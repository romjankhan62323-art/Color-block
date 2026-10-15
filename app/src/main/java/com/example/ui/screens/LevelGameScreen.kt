package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.model.GameMode
import com.example.model.LevelConfigManager
import com.example.model.TrayPiece
import com.example.ui.components.BlockShapeView
import com.example.ui.components.BoardCellView
import com.example.ui.components.Button3D
import com.example.ui.components.Button3DVariant
import com.example.ui.components.LaserLineClearOverlay
import com.example.ui.components.ScoreHeart
import com.example.ui.components.TwoHeartProgressBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun LevelGameScreen(
    levelNumber: Int = 1,
    targetScore: Int = 1500,
    mode: GameMode = GameMode.JOURNEY,
    boardSize: Int = 8,
    onBack: () -> Unit,
    onHome: () -> Unit,
    audioService: GameAudioService,
    gameStorage: GameStorage,
    onWatchAd: (((onSuccess: () -> Unit) -> Unit))? = null,
    modifier: Modifier = Modifier
) {
    val isClassic = mode == GameMode.CLASSIC
    val scope = rememberCoroutineScope()

    // Level configuration for Journey mode (levels 1 to 5000)
    var currentLevelNum by remember { mutableIntStateOf(levelNumber.coerceIn(1, 5000)) }
    var levelConfig by remember(currentLevelNum) {
        mutableStateOf(LevelConfigManager.getLevelConfig(currentLevelNum))
    }

    // Scores & Progress
    var score by remember { mutableIntStateOf(0) }
    var bestScore by remember { mutableIntStateOf(gameStorage.getStats().bestClassicScore) }
    var showNewBestBanner by remember { mutableStateOf(false) }

    // Journey Mode lines cleared count
    var completedLinesCount by remember { mutableIntStateOf(0) }
    val progressPercent = if (!isClassic) {
        LevelConfigManager.calculateProgressPercent(completedLinesCount, levelConfig.targetLines)
    } else {
        0
    }

    // Milestone & clear message toasts
    var activeMessage by remember { mutableStateOf<String?>(null) }
    var lastCelebratedMilestone by remember { mutableIntStateOf(0) }

    // Lifecycle states
    var isPaused by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    // Line clearing animation states
    var clearingRows by remember { mutableStateOf<List<Int>>(emptyList()) }
    var clearingCols by remember { mutableStateOf<List<Int>>(emptyList()) }
    var isClearing by remember { mutableStateOf(false) }
    var justPlacedCells by remember { mutableStateOf<List<CellCoord>>(emptyList()) }

    // 2D Grid
    val grid = remember { BlockEngine.createEmptyGrid(boardSize) }

    // Exactly 3 Tray Pieces
    val trayPieces = remember {
        mutableStateListOf<TrayPiece>().apply {
            addAll(BlockShapeCatalog.generateBalancedTrayDeal())
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

    fun showToastMessage(msg: String, durationMs: Long = 1400) {
        activeMessage = msg
        scope.launch {
            delay(durationMs)
            if (activeMessage == msg) {
                activeMessage = null
            }
        }
    }

    // Check game over
    fun verifyMoves() {
        if (!BlockEngine.hasAnyValidMove(grid, trayPieces, boardSize)) {
            audioService.playInvalidDrop()
            isGameOver = true
        }
    }

    // Execute placement
    fun placePiece(piece: TrayPiece, targetRow: Int, targetCol: Int) {
        if (isClearing || isGameOver || isPaused || isCompleted) return
        if (!BlockEngine.canPlace(grid, piece.cells, targetRow, targetCol, boardSize)) return

        audioService.playTap()
        val coords = piece.cells.map { CellCoord(targetRow + it.r, targetCol + it.c) }

        // Update board
        for (coord in coords) {
            grid[coord.r][coord.c] = piece.color
        }

        justPlacedCells = coords
        scope.launch {
            delay(250)
            justPlacedCells = emptyList()
        }

        // Mark placed in tray
        val pieceIndex = trayPieces.indexOfFirst { it.id == piece.id }
        if (pieceIndex != -1) {
            trayPieces[pieceIndex] = trayPieces[pieceIndex].copy(placed = true)
        }

        draggingPiece = null
        previewRowCol = null
        isPreviewValid = false
        totalDragDistance = 0f

        // Detect completed lines
        val completed = BlockEngine.findCompletedLines(grid, boardSize)
        val numCleared = completed.rows.size + completed.cols.size

        if (numCleared > 0) {
            // CRITICAL SCORE FIX: Score increases ONLY when lines are cleared!
            val addedScore = BlockEngine.calculateScore(completed.rows, completed.cols)
            score += addedScore

            // Line clear feedback message
            LevelConfigManager.getLineClearMessage(numCleared)?.let { clearMsg ->
                showToastMessage(clearMsg)
            }

            // Classic Mode Best Score tracking
            if (isClassic) {
                if (score > bestScore) {
                    if (bestScore > 0 && !showNewBestBanner) {
                        showNewBestBanner = true
                        showToastMessage("NEW BEST SCORE!")
                    }
                    bestScore = score
                    gameStorage.updateClassicScore(score)
                }
            } else {
                // Journey Mode Progress: increases by actual number of lines cleared
                completedLinesCount += numCleared
                val newPercent = LevelConfigManager.calculateProgressPercent(
                    completedLinesCount,
                    levelConfig.targetLines
                )

                // Check milestone crossing (10%, 20%, ..., 90%)
                val currentDecade = (newPercent / 10) * 10
                if (currentDecade in 10..90 && currentDecade > lastCelebratedMilestone) {
                    lastCelebratedMilestone = currentDecade
                    LevelConfigManager.getMilestoneMessage(currentDecade)?.let { milestoneMsg ->
                        showToastMessage(milestoneMsg)
                    }
                }
            }

            isClearing = true
            clearingRows = completed.rows
            clearingCols = completed.cols
            audioService.playLineClear()
            if (numCleared > 1) {
                audioService.playCombo(numCleared)
            }

            scope.launch {
                delay(380)
                // Clear cells
                for (r in completed.rows) {
                    for (c in 0 until boardSize) {
                        grid[r][c] = null
                    }
                }
                for (c in completed.cols) {
                    for (r in 0 until boardSize) {
                        grid[r][c] = null
                    }
                }

                clearingRows = emptyList()
                clearingCols = emptyList()
                isClearing = false

                // Deal new pieces if tray all placed
                if (trayPieces.all { it.placed }) {
                    trayPieces.clear()
                    trayPieces.addAll(BlockShapeCatalog.generateBalancedTrayDeal())
                }

                // Check Journey Level 100% completion
                if (!isClassic && completedLinesCount >= levelConfig.targetLines && !isCompleted) {
                    audioService.playFanfare()
                    gameStorage.completeLevel(currentLevelNum, levelConfig.rewardDiamonds)
                    gameStorage.recordPlaySession()
                    isCompleted = true
                } else {
                    verifyMoves()
                }
            }
        } else {
            // NO LINES CLEARED:
            // Score remains unchanged!
            // Journey progress remains unchanged!
            if (trayPieces.all { it.placed }) {
                trayPieces.clear()
                trayPieces.addAll(BlockShapeCatalog.generateBalancedTrayDeal())
            }
            verifyMoves()
        }
    }

    // Restart game session
    fun restartSession() {
        score = 0
        completedLinesCount = 0
        lastCelebratedMilestone = 0
        showNewBestBanner = false
        activeMessage = null
        isPaused = false
        isGameOver = false
        isCompleted = false
        isClearing = false
        clearingRows = emptyList()
        clearingCols = emptyList()
        draggingPiece = null
        previewRowCol = null
        isPreviewValid = false
        totalDragDistance = 0f

        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                grid[r][c] = null
            }
        }

        trayPieces.clear()
        trayPieces.addAll(BlockShapeCatalog.generateBalancedTrayDeal())
    }

    // Proceed to next level in Journey mode
    fun proceedToNextLevel() {
        if (currentLevelNum < LevelConfigManager.MAX_LEVEL) {
            currentLevelNum++
            levelConfig = LevelConfigManager.getLevelConfig(currentLevelNum)
            restartSession()
        } else {
            onHome()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF5335D2), Color(0xFF3575DF), Color(0xFF2994F2))
                )
            )
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Header: Back button, Score / Two-Heart progress, Pause button
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
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isClassic) {
                    // Classic Mode: Live score heart with Best score
                    ScoreHeart(
                        score = score,
                        label = "Best: $bestScore"
                    )
                } else {
                    // Journey Mode: Two-Heart Level Progress Bar
                    TwoHeartProgressBar(
                        levelNumber = currentLevelNum,
                        progressPercent = progressPercent,
                        completedLines = completedLinesCount,
                        targetLines = levelConfig.targetLines
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2563EB))
                        .border(1.5.dp, Color.White, CircleShape)
                        .clickable { isPaused = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Exciting Line Clear & Milestone Message Banner
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = activeMessage != null,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    activeMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFFACC15))
                                .border(1.5.dp, Color.White, CircleShape)
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = msg,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }
            }

            // Main Board Box
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .aspectRatio(1f)
                    .clip(RectangleShape)
                    .background(Color(0xFF1E1B4B))
                    .border(3.dp, Color(0xFF1E40AF), RectangleShape)
                    .onGloballyPositioned { coordinates ->
                        boardPosition = coordinates.positionInRoot()
                        boardPxSize = coordinates.size.width.toFloat()
                    }
                    .padding(2.dp)
            ) {
                val cellSize = maxWidth / boardSize

                // Grid Cells
                Column(modifier = Modifier.fillMaxSize()) {
                    for (r in 0 until boardSize) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (c in 0 until boardSize) {
                                val cellColor = grid[r][c]
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

                                BoardCellView(
                                    row = r,
                                    col = c,
                                    state = visualState,
                                    color = cellColor,
                                    previewColor = draggingPiece?.color,
                                    isClearing = isClearingCell,
                                    isJustPlaced = isJustPlaced,
                                    modifier = Modifier.size(cellSize)
                                )
                            }
                        }
                    }
                }

                // Laser Line Clear Overlay
                LaserLineClearOverlay(
                    clearedRows = clearingRows,
                    clearedCols = clearingCols,
                    boardSize = boardSize
                )
            }

            // Bottom Tray with exactly 3 Pieces
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
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
                                            if (isClearing || isGameOver || isPaused || isCompleted) return@detectDragGestures
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
                                                val cellPx = boardPxSize / boardSize
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
                                                        boardSize = boardSize,
                                                        pieceCells = piece.cells
                                                    )
                                                    previewRowCol = Pair(targetR, targetC)
                                                    isPreviewValid = BlockEngine.canPlace(
                                                        grid,
                                                        piece.cells,
                                                        targetR,
                                                        targetC,
                                                        boardSize
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
                                                    placePiece(piece, r, c)
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
                                        cellSize = 18.dp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bottom Reward Bar
                Row(
                    modifier = Modifier.fillMaxWidth(0.96f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF9333EA))
                            .clickable {
                                audioService.playDiamondChime()
                                gameStorage.addDiamonds(10)
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("  +10", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2563EB))
                            .clickable {
                                onWatchAd?.invoke { }
                            }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Watch Ad ", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // DRAGGING PIECE AVATAR
        if (draggingPiece != null) {
            val piece = draggingPiece!!
            val bounds = remember(piece.id) { BlockShapeCatalog.getBounds(piece.cells) }
            val draggedCellSizeDp = 22.dp
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

        // PAUSE DIALOG
        AnimatedVisibility(
            visible = isPaused,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                val shape = RoundedCornerShape(28.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(shape)
                        .background(Color(0xFF1E1B4B))
                        .border(2.dp, Color(0xFF38BDF8), shape)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "PAUSED",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Button3D(
                        text = "CONTINUE",
                        onClick = { isPaused = false },
                        variant = Button3DVariant.GREEN,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button3D(
                        text = "RESTART",
                        onClick = { restartSession() },
                        variant = Button3DVariant.ORANGE,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button3D(
                        text = "HOME",
                        onClick = onHome,
                        variant = Button3DVariant.ROYAL,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // GAME OVER DIALOG (Section 6 & 10: Dark Plum Gradient #2E1A29 -> #1F121C, NO SPACE LEFT)
        AnimatedVisibility(
            visible = isGameOver,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
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
                                colors = listOf(Color(0xFF2E1A29), Color(0xFF1F121C))
                            )
                        )
                        .border(2.dp, Color(0xFFF43F5E), shape)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "NO SPACE LEFT",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFDA4AF),
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "GAME OVER",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    // Final Score and Best Score
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "FINAL SCORE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBAE6FD)
                        )
                        Text(
                            text = String.format("%,d", score),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFACC15)
                        )

                        if (isClassic) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "BEST SCORE: ${String.format("%,d", bestScore)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6EE7B7)
                            )
                        }
                    }

                    if (onWatchAd != null) {
                        Button3D(
                            text = "REVIVE (WATCH AD)",
                            onClick = {
                                onWatchAd.invoke {
                                    val midStart = maxOf(0, (boardSize / 2) - 1)
                                    val midEnd = minOf(boardSize - 1, midStart + 1)
                                    for (r in midStart..midEnd) {
                                        for (c in 0 until boardSize) {
                                            grid[r][c] = null
                                        }
                                    }
                                    for (c in midStart..midEnd) {
                                        for (r in 0 until boardSize) {
                                            grid[r][c] = null
                                        }
                                    }
                                    trayPieces.clear()
                                    trayPieces.addAll(BlockShapeCatalog.generateBalancedTrayDeal())
                                    isGameOver = false
                                }
                            },
                            variant = Button3DVariant.ORANGE,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button3D(
                        text = "TRY AGAIN",
                        onClick = { restartSession() },
                        variant = Button3DVariant.GREEN,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button3D(
                        text = "HOME",
                        onClick = onHome,
                        variant = Button3DVariant.ROYAL,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // JOURNEY LEVEL COMPLETE MODAL (Sections 11 & 13)
        AnimatedVisibility(
            visible = isCompleted && !isClassic,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                val shape = RoundedCornerShape(28.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(shape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F172A))
                            )
                        )
                        .border(2.dp, Color(0xFFFACC15), shape)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD97706)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    if (levelConfig.isFinalLevel) {
                        // Level 5000 Grand Finale Screen
                        Text(
                            text = "CONGRATULATIONS!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFACC15),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "YOU COMPLETED ALL 5000 LEVELS!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "YOU ARE A COLOR BLOCK MASTER!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6EE7B7),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Final Grand Reward:   +5,000 Diamonds",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFACC15)
                        )

                        Button3D(
                            text = "HOME",
                            onClick = onHome,
                            variant = Button3DVariant.GREEN,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // Standard Level Complete
                        Text(
                            text = "LEVEL COMPLETE!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFACC15),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "WELL DONE!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Completed Level $currentLevelNum (${levelConfig.targetLines} Lines)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBAE6FD)
                        )
                        Text(
                            text = "Reward:   +${levelConfig.rewardDiamonds} Diamonds",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFACC15)
                        )

                        Button3D(
                            text = "CONTINUE",
                            onClick = { proceedToNextLevel() },
                            variant = Button3DVariant.GREEN,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
