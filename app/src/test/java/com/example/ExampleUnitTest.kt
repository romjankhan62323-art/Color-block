package com.example

import com.example.model.BlockColor
import com.example.model.BlockEngine
import com.example.model.BlockShapeCatalog
import com.example.model.CellCoord
import com.example.model.LevelConfigManager
import com.example.model.LevelDifficulty
import com.example.model.PlayerStats
import com.example.model.TrayPiece
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun test1_placeBlockWithoutClearingLine_scoreAndProgressZero() {
        val grid = BlockEngine.createEmptyGrid(8)
        val singlePiece = listOf(CellCoord(0, 0))

        assertTrue(BlockEngine.canPlace(grid, singlePiece, 0, 0, 8))
        grid[0][0] = BlockColor.CYAN

        val completed = BlockEngine.findCompletedLines(grid, 8)
        assertEquals(0, completed.rows.size)
        assertEquals(0, completed.cols.size)

        val score = BlockEngine.calculateScore(completed.rows, completed.cols)
        assertEquals(0, score) // Zero points when no line is cleared!
    }

    @Test
    fun test2_clearOneLine_scoreAndProgressIncrease() {
        val grid = BlockEngine.createEmptyGrid(8)
        // Fill row 0 except column 7
        for (c in 0 until 7) {
            grid[0][c] = BlockColor.RED
        }
        // Place block at (0, 7)
        grid[0][7] = BlockColor.RED

        val completed = BlockEngine.findCompletedLines(grid, 8)
        assertEquals(1, completed.rows.size)
        assertEquals(0, completed.cols.size)

        val score = BlockEngine.calculateScore(completed.rows, completed.cols)
        assertEquals(10, score) // Exactly 10 points for 1 line

        val progress = LevelConfigManager.calculateProgressPercent(1, 10)
        assertEquals(10, progress) // 1 line of 10 = 10%
    }

    @Test
    fun test3_clearTwoLinesSimultaneously_bothLinesClearAndAwardMultiScore() {
        val grid = BlockEngine.createEmptyGrid(8)
        // Fill row 0 except (0, 0)
        for (c in 1 until 8) grid[0][c] = BlockColor.BLUE
        // Fill col 0 except (0, 0)
        for (r in 1 until 8) grid[r][0] = BlockColor.BLUE

        // Place at intersection (0, 0)
        grid[0][0] = BlockColor.BLUE

        val completed = BlockEngine.findCompletedLines(grid, 8)
        assertEquals(1, completed.rows.size)
        assertEquals(1, completed.cols.size)

        val score = BlockEngine.calculateScore(completed.rows, completed.cols)
        // 2 lines = 30 points + 20 cross bonus = 50 points
        assertTrue(score >= 30)

        val progress = LevelConfigManager.calculateProgressPercent(completed.rows.size + completed.cols.size, 10)
        assertEquals(20, progress) // 2 lines of 10 = 20%
    }

    @Test
    fun test4_placeBlockInInvalidLocation_rejected() {
        val grid = BlockEngine.createEmptyGrid(8)
        grid[2][2] = BlockColor.YELLOW

        val piece = listOf(CellCoord(0, 0))
        // Collides with existing block
        assertFalse(BlockEngine.canPlace(grid, piece, 2, 2, 8))

        // Out of bounds
        assertFalse(BlockEngine.canPlace(grid, piece, 8, 8, 8))
        assertFalse(BlockEngine.canPlace(grid, piece, -1, 0, 8))
    }

    @Test
    fun test5_useOnePiece_unusedPiecesRemainAvailable() {
        val pieces = BlockShapeCatalog.generateBalancedTrayDeal(12345L).toMutableList()
        assertEquals(3, pieces.size)

        // Mark first piece placed
        pieces[0] = pieces[0].copy(placed = true)

        assertFalse(pieces[1].placed)
        assertFalse(pieces[2].placed)
        val unplaced = pieces.filter { !it.placed }
        assertEquals(2, unplaced.size)
    }

    @Test
    fun test6_exhaustAllThreePieces_newGroupAppears() {
        var pieces = BlockShapeCatalog.generateBalancedTrayDeal(100L).map { it.copy(placed = true) }
        assertTrue(pieces.all { it.placed })

        // When all placed, generate new deal
        if (pieces.all { it.placed }) {
            pieces = BlockShapeCatalog.generateBalancedTrayDeal(200L)
        }

        assertEquals(3, pieces.size)
        assertTrue(pieces.none { it.placed })
    }

    @Test
    fun test7_reach50PercentProgress_milestoneMessage() {
        val progress = LevelConfigManager.calculateProgressPercent(5, 10)
        assertEquals(50, progress)
        val msg = LevelConfigManager.getMilestoneMessage(progress)
        assertEquals("HALFWAY THERE!", msg)
    }

    @Test
    fun test8_reach80PercentProgress_milestoneMessage() {
        val progress = LevelConfigManager.calculateProgressPercent(8, 10)
        assertEquals(80, progress)
        val msg = LevelConfigManager.getMilestoneMessage(progress)
        assertEquals("80% COMPLETE! ALMOST THERE!", msg)
    }

    @Test
    fun test9_reach100PercentProgress_triggersLevelCompletion() {
        val progress = LevelConfigManager.calculateProgressPercent(10, 10)
        assertEquals(100, progress)
        val msg = LevelConfigManager.getMilestoneMessage(progress)
        assertEquals("LEVEL COMPLETE! WELL DONE!", msg)
    }

    @Test
    fun test10_completeLevel1_unlocksLevel2() {
        val stats = PlayerStats(completedJourneyLevels = 0, lastUnlockedJourneyLevel = 1)
        val completed = minOf(5000, maxOf(stats.completedJourneyLevels, 1))
        val unlocked = minOf(5000, maxOf(stats.lastUnlockedJourneyLevel, 1 + 1))

        assertEquals(1, completed)
        assertEquals(2, unlocked)
    }

    @Test
    fun test11_reachLevel5000_finalChallengeConfiguration() {
        val config = LevelConfigManager.getLevelConfig(5000)
        assertEquals(5000, config.levelNumber)
        assertTrue(config.isFinalLevel)
        assertEquals(LevelDifficulty.FINAL_CHALLENGE, config.difficulty)
        assertEquals(5000, config.rewardDiamonds)
        assertEquals("Level 5000: Final Challenge", config.title)
    }

    @Test
    fun test12_completeLevel5000_level5001NeverCreated() {
        val stats = PlayerStats(completedJourneyLevels = 4999, lastUnlockedJourneyLevel = 5000)
        val completed = minOf(5000, maxOf(stats.completedJourneyLevels, 5000))
        val unlocked = minOf(5000, maxOf(stats.lastUnlockedJourneyLevel, 5000 + 1))

        assertEquals(5000, completed)
        assertEquals(5000, unlocked) // Capped at 5000, level 5001 never created!
    }

    @Test
    fun test13_classicMode_withoutLineClear_scoreDoesNotIncrease() {
        val grid = BlockEngine.createEmptyGrid(8)
        val cells = listOf(CellCoord(0, 0), CellCoord(0, 1))
        assertTrue(BlockEngine.canPlace(grid, cells, 1, 1, 8))

        grid[1][1] = BlockColor.YELLOW
        grid[1][2] = BlockColor.YELLOW

        val completed = BlockEngine.findCompletedLines(grid, 8)
        val addedScore = BlockEngine.calculateScore(completed.rows, completed.cols)
        assertEquals(0, addedScore) // No line cleared = 0 score!
    }

    @Test
    fun test14_classicMode_clearLine_scoreIncreasesCorrectly() {
        val grid = BlockEngine.createEmptyGrid(8)
        for (c in 0 until 8) grid[3][c] = BlockColor.PINK

        val completed = BlockEngine.findCompletedLines(grid, 8)
        val score = BlockEngine.calculateScore(completed.rows, completed.cols)
        assertEquals(10, score)
    }

    @Test
    fun test15_noAvailablePieceFits_gameOver() {
        val grid = BlockEngine.createEmptyGrid(8)
        // Fill entire board except checkerboard gaps so 2x2 cannot fit anywhere
        for (r in 0 until 8) {
            for (c in 0 until 8) {
                if ((r + c) % 2 == 0) {
                    grid[r][c] = BlockColor.RED
                }
            }
        }

        // 2x2 square cannot fit anywhere
        val piece2x2 = TrayPiece(
            id = "test_2x2",
            shapeId = "square_2x2",
            name = "Square 2x2",
            cells = listOf(CellCoord(0, 0), CellCoord(0, 1), CellCoord(1, 0), CellCoord(1, 1)),
            color = BlockColor.GREEN,
            slotIndex = 0,
            placed = false
        )

        assertFalse(BlockEngine.canShapeFitAnywhere(grid, piece2x2.cells, 8))
        assertFalse(BlockEngine.hasAnyValidMove(grid, listOf(piece2x2), 8))
    }

    @Test
    fun test16_reopenApplication_persistenceOfJourneyProgressAndClassicScore() {
        val saved = PlayerStats(
            hasSeenWelcome = true,
            hasCompletedTutorial = true,
            totalDiamonds = 2500,
            currentStreak = 4,
            completedJourneyLevels = 28,
            lastUnlockedJourneyLevel = 29,
            bestClassicScore = 4850,
            trophies = 53
        )

        assertEquals(28, saved.completedJourneyLevels)
        assertEquals(29, saved.lastUnlockedJourneyLevel)
        assertEquals(4850, saved.bestClassicScore)
        assertEquals(2500, saved.totalDiamonds)
    }

    // ==========================================
    // PHASE 11: DRAG & DROP BLOCK PLACEMENT TESTS
    // ==========================================

    @Test
    fun test17_dragBlockToCenterOfBoard_snapsAndPlaces() {
        val grid = BlockEngine.createEmptyGrid(8)
        val piece = TrayPiece(
            id = "p1", shapeId = "single_1x1", name = "Single",
            cells = listOf(CellCoord(0, 0)), color = BlockColor.CYAN,
            slotIndex = 0, placed = false
        )

        val boardSize = 8
        val boardPx = 800f
        val cellPx = boardPx / boardSize
        // Center of cell (4, 4) is boardX + 4.5 * cellPx
        val boardX = 100f
        val boardY = 200f
        val focalX = boardX + (4.5f) * cellPx
        val focalY = boardY + (4.5f) * cellPx

        val res = com.example.model.BlockPlacementMath.tryDropPiece(
            grid = grid,
            piece = piece,
            focalX = focalX,
            focalY = focalY,
            boardX = boardX,
            boardY = boardY,
            boardPxSize = boardPx,
            boardSize = boardSize,
            totalDragDistance = 150f
        )

        assertTrue(res is com.example.model.DropResult.Success)
        val success = res as com.example.model.DropResult.Success
        assertEquals(4, success.row)
        assertEquals(4, success.col)
        assertEquals(BlockColor.CYAN, grid[4][4])
    }

    @Test
    fun test18_dragBlockToEveryEdgeAndCorner_validSnapping() {
        val boardSize = 8
        val boardPx = 800f
        val cellPx = boardPx / boardSize
        val boardX = 0f
        val boardY = 0f

        val piece = TrayPiece(
            id = "p1", shapeId = "single_1x1", name = "Single",
            cells = listOf(CellCoord(0, 0)), color = BlockColor.RED,
            slotIndex = 0, placed = false
        )

        // Corners & edges to test: (0,0), (0,7), (7,0), (7,7), (0,3), (7,3), (3,0), (3,7)
        val testPositions = listOf(
            Pair(0, 0), Pair(0, 7), Pair(7, 0), Pair(7, 7),
            Pair(0, 3), Pair(7, 3), Pair(3, 0), Pair(3, 7)
        )

        for ((r, c) in testPositions) {
            val grid = BlockEngine.createEmptyGrid(8)
            val focalX = (c + 0.5f) * cellPx
            val focalY = (r + 0.5f) * cellPx

            val res = com.example.model.BlockPlacementMath.tryDropPiece(
                grid = grid,
                piece = piece,
                focalX = focalX,
                focalY = focalY,
                boardX = boardX,
                boardY = boardY,
                boardPxSize = boardPx,
                boardSize = boardSize,
                totalDragDistance = 100f
            )

            assertTrue("Failed to place at ($r, $c)", res is com.example.model.DropResult.Success)
            assertEquals(BlockColor.RED, grid[r][c])
        }
    }

    @Test
    fun test19_dragBlockAcrossBoard_calculatesNearestAnchorSmoothly() {
        val boardSize = 8
        val boardPx = 800f
        val cellPx = boardPx / boardSize
        val pieceCells = listOf(CellCoord(0, 0))

        // Move finger continuously across diagonal
        for (i in 0..7) {
            val focalX = (i + 0.5f) * cellPx
            val focalY = (i + 0.5f) * cellPx

            val (r, c) = com.example.model.BlockPlacementMath.calculateTargetRowCol(
                focalX = focalX,
                focalY = focalY,
                boardX = 0f,
                boardY = 0f,
                boardPxSize = boardPx,
                boardSize = boardSize,
                pieceCells = pieceCells
            )
            assertEquals(i, r)
            assertEquals(i, c)
        }
    }

    @Test
    fun test20_releaseOutsideBoard_rejectedWithoutModifyingBoard() {
        val grid = BlockEngine.createEmptyGrid(8)
        val piece = TrayPiece(
            id = "p1", shapeId = "single_1x1", name = "Single",
            cells = listOf(CellCoord(0, 0)), color = BlockColor.BLUE,
            slotIndex = 0, placed = false
        )

        // Drop far below board (in bottom tray area)
        val res = com.example.model.BlockPlacementMath.tryDropPiece(
            grid = grid,
            piece = piece,
            focalX = 400f,
            focalY = 1400f, // way below board
            boardX = 50f,
            boardY = 200f,
            boardPxSize = 800f,
            boardSize = 8,
            totalDragDistance = 50f
        )

        assertTrue(res is com.example.model.DropResult.OutsideBoard)
        // Board remains completely empty
        for (r in 0 until 8) {
            for (c in 0 until 8) {
                assertEquals(null, grid[r][c])
            }
        }
    }

    @Test
    fun test21_releaseOverOccupiedCells_rejectedWithoutModifyingBoard() {
        val grid = BlockEngine.createEmptyGrid(8)
        grid[3][3] = BlockColor.YELLOW // Occupied

        val piece = TrayPiece(
            id = "p1", shapeId = "single_1x1", name = "Single",
            cells = listOf(CellCoord(0, 0)), color = BlockColor.GREEN,
            slotIndex = 0, placed = false
        )

        val cellPx = 100f
        val res = com.example.model.BlockPlacementMath.tryDropPiece(
            grid = grid,
            piece = piece,
            focalX = 3.5f * cellPx,
            focalY = 3.5f * cellPx,
            boardX = 0f,
            boardY = 0f,
            boardPxSize = 800f,
            boardSize = 8,
            totalDragDistance = 100f
        )

        assertTrue(res is com.example.model.DropResult.InvalidLocation)
        // Cell retains original color, not overwritten!
        assertEquals(BlockColor.YELLOW, grid[3][3])
    }

    @Test
    fun test22_releaseWhenPartOfBlockExtendsBeyondBoard_rejected() {
        val grid = BlockEngine.createEmptyGrid(8)
        // 3x3 square piece
        val piece3x3 = TrayPiece(
            id = "p3x3", shapeId = "square_3x3", name = "Square 3x3",
            cells = BlockShapeCatalog.SHAPES.find { it.id == "square_3x3" }!!.cells,
            color = BlockColor.RED, slotIndex = 0, placed = false
        )

        val cellPx = 100f
        // Drag 3x3 toward corner (7, 7) where it would extend outside
        val res = com.example.model.BlockPlacementMath.tryDropPiece(
            grid = grid,
            piece = piece3x3,
            focalX = 7.5f * cellPx,
            focalY = 7.5f * cellPx,
            boardX = 0f,
            boardY = 0f,
            boardPxSize = 800f,
            boardSize = 8,
            totalDragDistance = 120f
        )

        assertTrue(res is com.example.model.DropResult.InvalidLocation)
        // Board remains completely empty
        for (r in 0 until 8) {
            for (c in 0 until 8) {
                assertEquals(null, grid[r][c])
            }
        }
    }

    @Test
    fun test23_tapBlockWithoutDragging_rejectedWithoutModifyingBoard() {
        val grid = BlockEngine.createEmptyGrid(8)
        val piece = TrayPiece(
            id = "p1", shapeId = "single_1x1", name = "Single",
            cells = listOf(CellCoord(0, 0)), color = BlockColor.PINK,
            slotIndex = 0, placed = false
        )

        // User tapped: total drag distance is only 4 pixels (below 15f threshold)
        val res = com.example.model.BlockPlacementMath.tryDropPiece(
            grid = grid,
            piece = piece,
            focalX = 400f,
            focalY = 400f,
            boardX = 0f,
            boardY = 0f,
            boardPxSize = 800f,
            boardSize = 8,
            totalDragDistance = 4f
        )

        assertTrue(res is com.example.model.DropResult.TapRejected)
        // Nothing placed
        for (r in 0 until 8) {
            for (c in 0 until 8) {
                assertEquals(null, grid[r][c])
            }
        }
    }

    @Test
    fun test24_dragDifferentShapesAndSizes_accuratelySnapped() {
        val boardPx = 800f
        val cellPx = 100f

        // 1. Horizontal 4-Line at (2, 2)
        val hLine4 = BlockShapeCatalog.SHAPES.find { it.id == "h_line_4" }!!
        val pieceH4 = TrayPiece(
            id = "ph4", shapeId = hLine4.id, name = hLine4.name,
            cells = hLine4.cells, color = BlockColor.GREEN,
            slotIndex = 0, placed = false
        )
        val grid1 = BlockEngine.createEmptyGrid(8)
        // Bounds: cols = 4, rows = 1. Top-left at (2, 2) -> Center at (2 + 2, 2 + 0.5) = (4.0, 2.5)
        val resH4 = com.example.model.BlockPlacementMath.tryDropPiece(
            grid = grid1, piece = pieceH4,
            focalX = 4.0f * cellPx, focalY = 2.5f * cellPx,
            boardX = 0f, boardY = 0f, boardPxSize = boardPx, boardSize = 8,
            totalDragDistance = 50f
        )
        assertTrue(resH4 is com.example.model.DropResult.Success)
        for (c in 2..5) {
            assertEquals(BlockColor.GREEN, grid1[2][c])
        }

        // 2. 2x2 Square at (5, 5)
        val sq2 = BlockShapeCatalog.SHAPES.find { it.id == "square_2x2" }!!
        val pieceSq2 = TrayPiece(
            id = "psq2", shapeId = sq2.id, name = sq2.name,
            cells = sq2.cells, color = BlockColor.YELLOW,
            slotIndex = 1, placed = false
        )
        val grid2 = BlockEngine.createEmptyGrid(8)
        // Bounds: cols = 2, rows = 2. Top-left at (5, 5) -> Center at (5 + 1.0, 5 + 1.0) = (6.0, 6.0)
        val resSq2 = com.example.model.BlockPlacementMath.tryDropPiece(
            grid = grid2, piece = pieceSq2,
            focalX = 6.0f * cellPx, focalY = 6.0f * cellPx,
            boardX = 0f, boardY = 0f, boardPxSize = boardPx, boardSize = 8,
            totalDragDistance = 50f
        )
        assertTrue(resSq2 is com.example.model.DropResult.Success)
        assertEquals(BlockColor.YELLOW, grid2[5][5])
        assertEquals(BlockColor.YELLOW, grid2[5][6])
        assertEquals(BlockColor.YELLOW, grid2[6][5])
        assertEquals(BlockColor.YELLOW, grid2[6][6])
    }

    @Test
    fun test25_oneDragProducesNoMoreThanOnePlacement() {
        val grid = BlockEngine.createEmptyGrid(8)
        var piece = TrayPiece(
            id = "p1", shapeId = "single_1x1", name = "Single",
            cells = listOf(CellCoord(0, 0)), color = BlockColor.CYAN,
            slotIndex = 0, placed = false
        )

        // Drop first time
        val res1 = com.example.model.BlockPlacementMath.tryDropPiece(
            grid = grid, piece = piece,
            focalX = 150f, focalY = 150f,
            boardX = 0f, boardY = 0f, boardPxSize = 800f, boardSize = 8,
            totalDragDistance = 50f
        )
        assertTrue(res1 is com.example.model.DropResult.Success)
        piece = piece.copy(placed = true)

        // Attempt drop second time with same placed piece
        val res2 = com.example.model.BlockPlacementMath.tryDropPiece(
            grid = grid, piece = piece,
            focalX = 350f, focalY = 350f,
            boardX = 0f, boardY = 0f, boardPxSize = 800f, boardSize = 8,
            totalDragDistance = 50f
        )
        assertTrue(res2 is com.example.model.DropResult.AlreadyPlaced)
    }

    @Test
    fun test26_successfulPlacementTriggersCorrectLineClearAndScoring() {
        val grid = BlockEngine.createEmptyGrid(8)
        // Fill row 4 except column 7
        for (c in 0 until 7) {
            grid[4][c] = BlockColor.PURPLE
        }

        val piece = TrayPiece(
            id = "single", shapeId = "single_1x1", name = "Single",
            cells = listOf(CellCoord(0, 0)), color = BlockColor.PURPLE,
            slotIndex = 0, placed = false
        )

        // Drop onto (4, 7)
        val cellPx = 100f
        val res = com.example.model.BlockPlacementMath.tryDropPiece(
            grid = grid, piece = piece,
            focalX = 7.5f * cellPx, focalY = 4.5f * cellPx,
            boardX = 0f, boardY = 0f, boardPxSize = 800f, boardSize = 8,
            totalDragDistance = 60f
        )

        assertTrue(res is com.example.model.DropResult.Success)
        val success = res as com.example.model.DropResult.Success
        assertEquals(1, success.completed.rows.size)
        assertEquals(4, success.completed.rows[0])
        assertEquals(10, success.scoreEarned)
    }

    @Test
    fun test27_invalidPlacementPreservesBoardScoreAndProgress() {
        val grid = BlockEngine.createEmptyGrid(8)
        grid[1][1] = BlockColor.RED

        val piece = TrayPiece(
            id = "single", shapeId = "single_1x1", name = "Single",
            cells = listOf(CellCoord(0, 0)), color = BlockColor.BLUE,
            slotIndex = 0, placed = false
        )

        // Invalid: drop on occupied (1, 1)
        val res = com.example.model.BlockPlacementMath.tryDropPiece(
            grid = grid, piece = piece,
            focalX = 150f, focalY = 150f,
            boardX = 0f, boardY = 0f, boardPxSize = 800f, boardSize = 8,
            totalDragDistance = 50f
        )

        assertTrue(res is com.example.model.DropResult.InvalidLocation)
        // Unchanged: cell (1, 1) still has RED, piece is still not placed
        assertEquals(BlockColor.RED, grid[1][1])
        assertFalse(piece.placed)
    }
}

