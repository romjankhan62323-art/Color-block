package com.example.model

data class GridCell(
    val row: Int,
    val col: Int,
    val color: BlockColor? = null,
    val isClearing: Boolean = false
)

object BlockEngine {
    fun createEmptyGrid(size: Int): Array<Array<BlockColor?>> {
        return Array(size) { Array(size) { null } }
    }

    fun canPlace(
        grid: Array<Array<BlockColor?>>,
        cells: List<CellCoord>,
        targetRow: Int,
        targetCol: Int,
        boardSize: Int = 8
    ): Boolean {
        for (coord in cells) {
            val r = targetRow + coord.r
            val c = targetCol + coord.c
            if (r !in 0 until boardSize || c !in 0 until boardSize) return false
            if (grid[r][c] != null) return false
        }
        return true
    }

    fun canShapeFitAnywhere(
        grid: Array<Array<BlockColor?>>,
        cells: List<CellCoord>,
        boardSize: Int = 8
    ): Boolean {
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                if (canPlace(grid, cells, r, c, boardSize)) return true
            }
        }
        return false
    }

    fun hasAnyValidMove(
        grid: Array<Array<BlockColor?>>,
        availablePieces: List<TrayPiece>,
        boardSize: Int = 8
    ): Boolean {
        val unplaced = availablePieces.filter { !it.placed }
        if (unplaced.isEmpty()) return true
        return unplaced.any { canShapeFitAnywhere(grid, it.cells, boardSize) }
    }

    fun findCompletedLines(
        grid: Array<Array<BlockColor?>>,
        boardSize: Int = 8
    ): CompletedLines {
        val rows = mutableListOf<Int>()
        val cols = mutableListOf<Int>()

        for (r in 0 until boardSize) {
            var full = true
            for (c in 0 until boardSize) {
                if (grid[r][c] == null) {
                    full = false
                    break
                }
            }
            if (full) rows.add(r)
        }

        for (c in 0 until boardSize) {
            var full = true
            for (r in 0 until boardSize) {
                if (grid[r][c] == null) {
                    full = false
                    break
                }
            }
            if (full) cols.add(c)
        }

        return CompletedLines(rows, cols)
    }

    /**
     * CRITICAL SCORE FIX:
     * Placing blocks alone awards ZERO line-clear points.
     * Score increases ONLY when lines are cleared!
     * 1 line = 10 pts, 2 lines = 30 pts, 3 lines = 60 pts, 4 lines = 100 pts, 5+ = 150 pts.
     */
    fun calculateScore(
        completedRows: List<Int>,
        completedCols: List<Int>
    ): Int {
        val totalLines = completedRows.size + completedCols.size
        if (totalLines == 0) return 0

        val linePoints = when (totalLines) {
            1 -> 10
            2 -> 30
            3 -> 60
            4 -> 100
            else -> 150 + (totalLines - 4) * 50
        }
        val crossBonus = if (completedRows.isNotEmpty() && completedCols.isNotEmpty()) 20 else 0
        return linePoints + crossBonus
    }

    fun calculateScore(
        pieceCellsCount: Int,
        completedRows: List<Int>,
        completedCols: List<Int>
    ): Int {
        // Zero points awarded for placing blocks alone
        return calculateScore(completedRows, completedCols)
    }
}

data class CompletedLines(
    val rows: List<Int>,
    val cols: List<Int>
)

sealed class DropResult {
    data class Success(
        val row: Int,
        val col: Int,
        val completed: CompletedLines,
        val scoreEarned: Int
    ) : DropResult()

    data class InvalidLocation(val row: Int, val col: Int) : DropResult()
    object OutsideBoard : DropResult()
    object TapRejected : DropResult()
    object AlreadyPlaced : DropResult()
}

object BlockPlacementMath {
    /**
     * Calculates the nearest board row and column anchor for a dragged piece.
     *
     * @param focalX The X coordinate of the dragged piece center in root space.
     * @param focalY The Y coordinate of the dragged piece center in root space (lifted above finger).
     * @param boardX The left position of the board in root space.
     * @param boardY The top position of the board in root space.
     * @param boardPxSize The pixel width/height of the square board.
     * @param boardSize The grid dimension (e.g. 8).
     * @param pieceCells The cells composing the piece shape.
     */
    fun calculateTargetRowCol(
        focalX: Float,
        focalY: Float,
        boardX: Float,
        boardY: Float,
        boardPxSize: Float,
        boardSize: Int,
        pieceCells: List<CellCoord>
    ): Pair<Int, Int> {
        if (boardPxSize <= 0f || boardSize <= 0) return Pair(-999, -999)
        val cellPx = boardPxSize / boardSize
        val bounds = BlockShapeCatalog.getBounds(pieceCells)

        val pieceTopLeftX = focalX - (bounds.cols * cellPx) / 2f
        val pieceTopLeftY = focalY - (bounds.rows * cellPx) / 2f

        val relX = pieceTopLeftX - boardX
        val relY = pieceTopLeftY - boardY

        val targetCol = Math.round(relX / cellPx).toInt()
        val targetRow = Math.round(relY / cellPx).toInt()
        return Pair(targetRow, targetCol)
    }

    /**
     * Determines whether the focal point is near the board (within margin).
     */
    fun isNearBoard(
        focalX: Float,
        focalY: Float,
        boardX: Float,
        boardY: Float,
        boardPxSize: Float,
        paddingPx: Float = 40f
    ): Boolean {
        if (boardPxSize <= 0f) return false
        val left = boardX - paddingPx
        val top = boardY - paddingPx
        val right = boardX + boardPxSize + paddingPx
        val bottom = boardY + boardPxSize + paddingPx
        return focalX in left..right && focalY in top..bottom
    }

    /**
     * Evaluates a drop attempt atomically.
     */
    fun tryDropPiece(
        grid: Array<Array<BlockColor?>>,
        piece: TrayPiece,
        focalX: Float,
        focalY: Float,
        boardX: Float,
        boardY: Float,
        boardPxSize: Float,
        boardSize: Int,
        totalDragDistance: Float,
        minDragDistance: Float = 15f
    ): DropResult {
        if (piece.placed) return DropResult.AlreadyPlaced
        if (totalDragDistance < minDragDistance) return DropResult.TapRejected

        val cellPx = boardPxSize / boardSize
        if (!isNearBoard(focalX, focalY, boardX, boardY, boardPxSize, cellPx * 0.8f)) {
            return DropResult.OutsideBoard
        }

        val (targetRow, targetCol) = calculateTargetRowCol(
            focalX, focalY, boardX, boardY, boardPxSize, boardSize, piece.cells
        )

        if (!BlockEngine.canPlace(grid, piece.cells, targetRow, targetCol, boardSize)) {
            return DropResult.InvalidLocation(targetRow, targetCol)
        }

        // Place into grid atomically
        for (coord in piece.cells) {
            grid[targetRow + coord.r][targetCol + coord.c] = piece.color
        }

        val completed = BlockEngine.findCompletedLines(grid, boardSize)
        val score = BlockEngine.calculateScore(completed.rows, completed.cols)
        return DropResult.Success(targetRow, targetCol, completed, score)
    }
}

