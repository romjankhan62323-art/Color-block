package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameAudioService
import com.example.data.GameStorage
import com.example.model.BlockColor
import com.example.model.BlockEngine
import com.example.model.BlockPlacementMath
import com.example.model.BlockShapeCatalog
import com.example.model.BoardCellVisualState
import com.example.model.CellCoord
import com.example.model.TrayPiece
import com.example.ui.components.BlockShapeView
import com.example.ui.components.BoardCellView
import com.example.ui.components.Button3D
import com.example.ui.components.Button3DVariant
import com.example.ui.components.LaserLineClearOverlay
import com.example.ui.components.ScoreHeart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DrawingGameScreen(
    onBack: () -> Unit,
    onHome: () -> Unit,
    audioService: GameAudioService,
    gameStorage: GameStorage,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var score by remember { mutableIntStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }

    var clearingRows by remember { mutableStateOf<List<Int>>(emptyList()) }
    var clearingCols by remember { mutableStateOf<List<Int>>(emptyList()) }
    var isClearing by remember { mutableStateOf(false) }
    var justPlacedCells by remember { mutableStateOf<List<CellCoord>>(emptyList()) }

    // 8x8 Target Template (Stylized Heart & Sparkle Art)
    val targetPicture = remember {
        val pattern = Array(8) { Array<BlockColor?>(8) { null } }
        pattern[0][1] = BlockColor.PINK; pattern[0][2] = BlockColor.PINK
        pattern[0][5] = BlockColor.PINK; pattern[0][6] = BlockColor.PINK

        pattern[1][0] = BlockColor.PINK; pattern[1][1] = BlockColor.RED; pattern[1][2] = BlockColor.RED; pattern[1][3] = BlockColor.PINK
        pattern[1][4] = BlockColor.PINK; pattern[1][5] = BlockColor.RED; pattern[1][6] = BlockColor.RED; pattern[1][7] = BlockColor.PINK

        pattern[2][0] = BlockColor.PINK; pattern[2][1] = BlockColor.RED; pattern[2][2] = BlockColor.RED; pattern[2][3] = BlockColor.RED
        pattern[2][4] = BlockColor.RED; pattern[2][5] = BlockColor.RED; pattern[2][6] = BlockColor.RED; pattern[2][7] = BlockColor.PINK

        pattern[3][1] = BlockColor.PINK; pattern[3][2] = BlockColor.RED; pattern[3][3] = BlockColor.YELLOW
        pattern[3][4] = BlockColor.YELLOW; pattern[3][5] = BlockColor.RED; pattern[3][6] = BlockColor.PINK

        pattern[4][2] = BlockColor.PINK; pattern[4][3] = BlockColor.YELLOW; pattern[4][4] = BlockColor.YELLOW; pattern[4][5] = BlockColor.PINK
        pattern[5][3] = BlockColor.PINK; pattern[5][4] = BlockColor.PINK

        pattern[6][1] = BlockColor.CYAN; pattern[6][6] = BlockColor.CYAN
        pattern[7][0] = BlockColor.CYAN; pattern[7][1] = BlockColor.CYAN; pattern[7][2] = BlockColor.CYAN
        pattern[7][5] = BlockColor.CYAN; pattern[7][6] = BlockColor.CYAN; pattern[7][7] = BlockColor.CYAN
        pattern
    }

    // 8x8 Grid
    val grid = remember { BlockEngine.createEmptyGrid(8) }

    val drawingPool = listOf(
        Pair(BlockColor.PINK, listOf(CellCoord(0, 0), CellCoord(0, 1))),
        Pair(BlockColor.RED, listOf(CellCoord(0, 0), CellCoord(0, 1), CellCoord(1, 0), CellCoord(1, 1))),
        Pair(BlockColor.YELLOW, listOf(CellCoord(0, 0), CellCoord(1, 0))),
        Pair(BlockColor.CYAN, listOf(CellCoord(0, 0), CellCoord(0, 1), CellCoord(0, 2))),
        Pair(BlockColor.PINK, listOf(CellCoord(0, 0), CellCoord(1, 0), CellCoord(1, 1))),
        Pair(BlockColor.RED, listOf(CellCoord(0, 0), CellCoord(0, 1))),
        Pair(BlockColor.CYAN, listOf(CellCoord(0, 0)))
    )

    fun dealDrawingPieces(seed: Long): List<TrayPiece> {
        return (0..2).map { idx ->
            val (col, cells) = drawingPool.random()
            TrayPiece(
                id = "dp_${seed}_$idx",
                shapeId = "dp_shape_$idx",
                name = "Art Piece",
                cells = cells,
                color = col,
                slotIndex = idx,
                placed = false
            )
        }
    }

    val trayPieces = remember {
        mutableStateListOf<TrayPiece>().apply {
            addAll(dealDrawingPieces(System.currentTimeMillis()))
        }
    }

    var draggingPiece by remember { mutableStateOf<TrayPiece?>(null) }
    var fingerRootPosition by remember { mutableStateOf(Offset.Zero) }
    var totalDragDistance by remember { mutableFloatStateOf(0f) }
    var previewRowCol by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var isPreviewValid by remember { mutableStateOf(false) }

    var boardPosition by remember { mutableStateOf(Offset.Zero) }
    var boardPxSize by remember { mutableFloatStateOf(0f) }
    val traySlotPositions = remember { mutableStateMapOf<String, Offset>() }

    val density = LocalDensity.current
    val touchLiftPx = with(density) { 72.dp.toPx() }

    fun placeDrawingPiece(piece: TrayPiece, targetRow: Int, targetCol: Int) {
        if (isClearing || isPaused) return
        if (!BlockEngine.canPlace(grid, piece.cells, targetRow, targetCol, 8)) return

        audioService.playTap()
        val coords = piece.cells.map { CellCoord(targetRow + it.r, targetCol + it.c) }

        var targetMatches = 0
        for (coord in coords) {
            grid[coord.r][coord.c] = piece.color
            if (targetPicture[coord.r][coord.c] == piece.color) {
                targetMatches++
            }
        }

        justPlacedCells = coords
        scope.launch {
            delay(250)
            justPlacedCells = emptyList()
        }

        val idx = trayPieces.indexOfFirst { it.id == piece.id }
        if (idx != -1) {
            trayPieces[idx] = trayPieces[idx].copy(placed = true)
        }

        draggingPiece = null
        previewRowCol = null
        isPreviewValid = false
        totalDragDistance = 0f

        val completed = BlockEngine.findCompletedLines(grid, 8)
        val addedScore = piece.cells.size * 15 + targetMatches * 50 + (completed.rows.size + completed.cols.size) * 200
        score += addedScore

        if (completed.rows.isNotEmpty() || completed.cols.isNotEmpty()) {
            isClearing = true
            clearingRows = completed.rows
            clearingCols = completed.cols
            audioService.playLineClear()

            scope.launch {
                delay(380)
                for (r in completed.rows) {
                    for (c in 0 until 8) {
                        grid[r][c] = null
                    }
                }
                for (c in completed.cols) {
                    for (r in 0 until 8) {
                        grid[r][c] = null
                    }
                }

                clearingRows = emptyList()
                clearingCols = emptyList()
                isClearing = false

                if (trayPieces.all { it.placed }) {
                    trayPieces.clear()
                    trayPieces.addAll(dealDrawingPieces(System.currentTimeMillis()))
                }
            }
        } else {
            if (trayPieces.all { it.placed }) {
                trayPieces.clear()
                trayPieces.addAll(dealDrawingPieces(System.currentTimeMillis()))
            }
        }

        if (score >= 600 && !isCompleted) {
            scope.launch {
                delay(400)
                audioService.playFanfare()
                gameStorage.addDiamonds(100)
                isCompleted = true
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF4A154B), Color(0xFF2D1B69), Color(0xFF1E1035))
                )
            )
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2563EB))
                        .border(1.5.dp, Color.White, CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                ScoreHeart(score = score, label = "Art Score")

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2563EB))
                        .border(1.5.dp, Color.White, CircleShape)
                        .clickable { isPaused = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Pause, contentDescription = "Pause", tint = Color.White)
                }
            }

            // Guide Pill
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF1E1B4B))
                    .border(1.dp, Color(0xFFEC4899), CircleShape)
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = Color(0xFFF472B6), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Match the Pixel Picture Guide!", color = Color(0xFFFBCFE8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Board Box
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .aspectRatio(1f)
                    .clip(RectangleShape)
                    .background(Color(0xFF0E0C28))
                    .border(3.dp, Color(0xFFEC4899), RectangleShape)
                    .onGloballyPositioned { coordinates ->
                        boardPosition = coordinates.positionInRoot()
                        boardPxSize = coordinates.size.width.toFloat()
                    }
                    .padding(2.dp)
            ) {
                val cellSize = maxWidth / 8

                Column(modifier = Modifier.fillMaxSize()) {
                    for (r in 0 until 8) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (c in 0 until 8) {
                                val cellColor = grid[r][c]
                                val targetHint = targetPicture[r][c]
                                val isRowClearing = clearingRows.contains(r)
                                val isColClearing = clearingCols.contains(c)
                                val isClearingCell = isRowClearing || isColClearing

                                val isPreview = previewRowCol?.let { (pr, pc) ->
                                    draggingPiece?.cells?.any { pr + it.r == r && pc + it.c == c } == true
                                } ?: false

                                val isJustPlaced = justPlacedCells.any { it.r == r && it.c == c }

                                val visualState = when {
                                    isClearingCell -> BoardCellVisualState.CLEARING
                                    isJustPlaced -> BoardCellVisualState.HIGHLIGHT
                                    isPreview -> if (isPreviewValid) BoardCellVisualState.PREVIEW_VALID else BoardCellVisualState.PREVIEW_INVALID
                                    cellColor != null -> BoardCellVisualState.OCCUPIED
                                    else -> BoardCellVisualState.EMPTY
                                }

                                Box(modifier = Modifier.size(cellSize)) {
                                    // Template hint square if empty
                                    if (cellColor == null && targetHint != null) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize(0.45f)
                                                .align(Alignment.Center)
                                                .background(targetHint.base.copy(alpha = 0.4f))
                                                .border(1.dp, Color.White.copy(alpha = 0.6f))
                                        )
                                    }

                                    BoardCellView(
                                        row = r,
                                        col = c,
                                        state = visualState,
                                        color = cellColor,
                                        previewColor = draggingPiece?.color,
                                        isClearing = isClearingCell,
                                        isJustPlaced = isJustPlaced,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }

                LaserLineClearOverlay(
                    clearedRows = clearingRows,
                    clearedCols = clearingCols,
                    boardSize = 8
                )
            }

            // Bottom Tray
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.9f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    trayPieces.forEach { piece ->
                        val isPieceDragging = draggingPiece?.id == piece.id

                        Box(
                            modifier = Modifier
                                .size(width = 96.dp, height = 76.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    when {
                                        piece.placed -> Color.Transparent
                                        isPieceDragging -> Color.Black.copy(alpha = 0.25f)
                                        else -> Color.Black.copy(alpha = 0.45f)
                                    }
                                )
                                .border(
                                    1.dp,
                                    when {
                                        piece.placed -> Color.White.copy(alpha = 0.1f)
                                        isPieceDragging -> Color.White.copy(alpha = 0.25f)
                                        else -> Color.White.copy(alpha = 0.2f)
                                    },
                                    RoundedCornerShape(12.dp)
                                )
                                .onGloballyPositioned { coordinates ->
                                    traySlotPositions[piece.id] = coordinates.positionInRoot()
                                }
                                .pointerInput(piece.id, piece.placed) {
                                    if (piece.placed) return@pointerInput

                                    detectDragGestures(
                                        onDragStart = { localOffset ->
                                            if (isClearing || isPaused) return@detectDragGestures
                                            val slotPos = traySlotPositions[piece.id] ?: Offset.Zero
                                            fingerRootPosition = Offset(slotPos.x + localOffset.x, slotPos.y + localOffset.y)
                                            totalDragDistance = 0f
                                            draggingPiece = piece
                                            previewRowCol = null
                                            isPreviewValid = false
                                            audioService.playTap()
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            fingerRootPosition = Offset(fingerRootPosition.x + dragAmount.x, fingerRootPosition.y + dragAmount.y)
                                            totalDragDistance += kotlin.math.sqrt(dragAmount.x * dragAmount.x + dragAmount.y * dragAmount.y)

                                            if (boardPxSize > 0f && draggingPiece?.id == piece.id) {
                                                val focalX = fingerRootPosition.x
                                                val focalY = fingerRootPosition.y - touchLiftPx
                                                val cellPx = boardPxSize / 8f
                                                val isNear = BlockPlacementMath.isNearBoard(
                                                    focalX, focalY, boardPosition.x, boardPosition.y, boardPxSize, cellPx * 0.8f
                                                )

                                                if (isNear) {
                                                    val (targetR, targetC) = BlockPlacementMath.calculateTargetRowCol(
                                                        focalX = focalX,
                                                        focalY = focalY,
                                                        boardX = boardPosition.x,
                                                        boardY = boardPosition.y,
                                                        boardPxSize = boardPxSize,
                                                        boardSize = 8,
                                                        pieceCells = piece.cells
                                                    )
                                                    previewRowCol = Pair(targetR, targetC)
                                                    isPreviewValid = BlockEngine.canPlace(
                                                        grid,
                                                        piece.cells,
                                                        targetR,
                                                        targetC,
                                                        8
                                                    )
                                                } else {
                                                    previewRowCol = null
                                                    isPreviewValid = false
                                                }
                                            }
                                        },
                                        onDragEnd = {
                                            if (draggingPiece?.id == piece.id) {
                                                if (totalDragDistance >= 15f && previewRowCol != null && isPreviewValid) {
                                                    val (r, c) = previewRowCol!!
                                                    placeDrawingPiece(piece, r, c)
                                                } else {
                                                    if (totalDragDistance >= 15f) {
                                                        audioService.playInvalidDrop()
                                                    }
                                                }
                                                draggingPiece = null
                                                previewRowCol = null
                                                isPreviewValid = false
                                                totalDragDistance = 0f
                                            }
                                        },
                                        onDragCancel = {
                                            draggingPiece = null
                                            previewRowCol = null
                                            isPreviewValid = false
                                            totalDragDistance = 0f
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (piece.placed) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Placed",
                                    tint = Color(0xFF10B981).copy(alpha = 0.4f),
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Box(modifier = Modifier.scale(if (isPieceDragging) 0.9f else 1f)) {
                                    BlockShapeView(
                                        cells = piece.cells,
                                        color = piece.color,
                                        cellSize = 20.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dragging piece avatar
        if (draggingPiece != null) {
            val piece = draggingPiece!!
            val bounds = remember(piece.id) { BlockShapeCatalog.getBounds(piece.cells) }
            val draggedCellSizeDp = 24.dp
            val draggedCellSizePx = with(density) { draggedCellSizeDp.toPx() }
            val avatarWidthPx = bounds.cols * draggedCellSizePx
            val avatarHeightPx = bounds.rows * draggedCellSizePx

            val focalX = fingerRootPosition.x
            val focalY = fingerRootPosition.y - touchLiftPx
            val avatarLeft = focalX - avatarWidthPx / 2f
            val avatarTop = focalY - avatarHeightPx / 2f

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            avatarLeft.roundToInt(),
                            avatarTop.roundToInt()
                        )
                    }
            ) {
                BlockShapeView(
                    cells = piece.cells,
                    color = piece.color,
                    cellSize = draggedCellSizeDp
                )
            }
        }

        // Pause Modal
        AnimatedVisibility(visible = isPaused, enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                val shape = RoundedCornerShape(24.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(shape)
                        .background(Color(0xFF1E1B4B))
                        .border(2.dp, Color(0xFFEC4899), shape)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("PAUSED", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Button3D(text = "RESUME", onClick = { isPaused = false }, variant = Button3DVariant.GREEN, modifier = Modifier.fillMaxWidth())
                    Button3D(text = "HOME", onClick = onHome, variant = Button3DVariant.ROYAL, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // Masterpiece Complete Dialog
        AnimatedVisibility(visible = isCompleted, enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                val shape = RoundedCornerShape(28.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .clip(shape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF831843), Color(0xFF1E1B4B))
                            )
                        )
                        .border(2.dp, Color(0xFFF472B6), shape)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEC4899)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
                    }

                    Text("MASTERPIECE COMPLETE!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFFDE047), textAlign = TextAlign.Center)
                    Text("Postcard Added to Collection!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Reward:   +100 Diamonds", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color(0xFFFACC15))

                    Button3D(text = "DRAW ANOTHER", onClick = onBack, variant = Button3DVariant.GREEN, modifier = Modifier.fillMaxWidth())
                    Button3D(text = "HOME", onClick = onHome, variant = Button3DVariant.ROYAL, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
