package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.example.model.BoardCellVisualState
import com.example.model.CellCoord
import com.example.ui.components.BlockShapeView
import com.example.ui.components.BoardCellView
import com.example.ui.components.Button3D
import com.example.ui.components.Button3DVariant
import com.example.ui.components.ColorBlockView
import com.example.ui.components.LaserLineClearOverlay
import com.example.ui.components.ScoreHeart
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun TutorialScreen(
    onComplete: () -> Unit,
    onGoHome: () -> Unit,
    audioService: GameAudioService,
    gameStorage: GameStorage,
    modifier: Modifier = Modifier
) {
    var score by remember { mutableIntStateOf(0) }
    var secondsElapsed by remember { mutableIntStateOf(0) }
    var showHomeButton by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var hasInteracted by remember { mutableStateOf(false) }
    var isCleared by remember { mutableStateOf(false) }
    var showCompletionModal by remember { mutableStateOf(false) }
    var clearingRow by remember { mutableStateOf<Int?>(null) }
    var isJustPlaced by remember { mutableStateOf(false) }

    val targetRow = 5
    val targetCol = 4

    // Initial board with row 5 missing only (5, 4)
    val grid = remember {
        val initial = Array(8) { Array<BlockColor?>(8) { null } }
        initial[7][0] = BlockColor.BLUE
        initial[7][1] = BlockColor.BLUE
        initial[7][2] = BlockColor.PURPLE
        initial[7][5] = BlockColor.GREEN
        initial[7][6] = BlockColor.ORANGE
        initial[7][7] = BlockColor.ORANGE

        initial[6][1] = BlockColor.RED
        initial[6][2] = BlockColor.RED
        initial[6][5] = BlockColor.GREEN
        initial[6][6] = BlockColor.GREEN

        initial[5][0] = BlockColor.YELLOW
        initial[5][1] = BlockColor.YELLOW
        initial[5][2] = BlockColor.CYAN
        initial[5][3] = BlockColor.CYAN
        initial[5][4] = null // TARGET!
        initial[5][5] = BlockColor.PINK
        initial[5][6] = BlockColor.PINK
        initial[5][7] = BlockColor.YELLOW
        initial
    }

    // 15-second timer rule
    LaunchedEffect(Unit) {
        while (secondsElapsed < 15) {
            delay(1000)
            secondsElapsed++
        }
        showHomeButton = true
    }

    // Hand animation
    val handProgress = remember { Animatable(0f) }
    LaunchedEffect(hasInteracted) {
        if (!hasInteracted) {
            handProgress.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    // Board position in root for touch dragging calculations
    var boardPosition by remember { mutableStateOf(Offset.Zero) }
    var boardDimension by remember { mutableFloatStateOf(0f) }

    // Drag tracking for cyan single piece
    var isDraggingPiece by remember { mutableStateOf(false) }
    var fingerRootPosition by remember { mutableStateOf(Offset.Zero) }
    var traySlotPosition by remember { mutableStateOf(Offset.Zero) }
    var totalDragDistance by remember { mutableFloatStateOf(0f) }
    var previewTarget by remember { mutableStateOf(false) }
    var piecePlaced by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val touchLiftPx = with(density) { 72.dp.toPx() }

    // Placement executor
    fun executeDrop() {
        if (piecePlaced || isCleared) return
        hasInteracted = true
        piecePlaced = true
        grid[targetRow][targetCol] = BlockColor.CYAN
        isJustPlaced = true
        audioService.playTap()

        clearingRow = targetRow
        audioService.playLineClear()
        score += 100

        // Clear row after animation
        kotlinx.coroutines.GlobalScope.apply {}
    }

    LaunchedEffect(isJustPlaced) {
        if (isJustPlaced) {
            delay(400)
            for (c in 0 until 8) {
                grid[targetRow][c] = null
            }
            clearingRow = null
            isCleared = true
            delay(400)
            audioService.playFanfare()
            showCompletionModal = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF3B82F6), Color(0xFF2563EB))
                )
            )
            .padding(8.dp)
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
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showHomeButton) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2563EB))
                            .border(1.5.dp, Color.White, CircleShape)
                            .clickable(onClick = onGoHome),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White)
                    }
                } else {
                    Spacer(modifier = Modifier.size(40.dp))
                }

                ScoreHeart(score = score, label = "Score")

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                        .clickable { showSettings = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }

            // Banner
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFFACC15))
                    .border(2.dp, Color.White, CircleShape)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "DRAG THE BLOCK HERE!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A)
                )
            }

            // Game Board 8x8
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .aspectRatio(1f)
                    .clip(RectangleShape)
                    .background(Color(0xFF1E1B4B))
                    .border(3.dp, Color(0xFF1E40AF), RectangleShape)
                    .onGloballyPositioned { coordinates ->
                        boardPosition = coordinates.positionInRoot()
                        boardDimension = coordinates.size.width.toFloat()
                    }
                    .padding(2.dp)
            ) {
                val cellSize = maxWidth / 8

                // Grid Cells
                Column(modifier = Modifier.fillMaxSize()) {
                    for (r in 0 until 8) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (c in 0 until 8) {
                                val cellColor = grid[r][c]
                                val isTarget = r == targetRow && c == targetCol && !piecePlaced
                                val isClearingCell = clearingRow == r
                                val isPreview = previewTarget && r == targetRow && c == targetCol

                                val visualState = when {
                                    isClearingCell -> BoardCellVisualState.CLEARING
                                    isPreview -> BoardCellVisualState.PREVIEW_VALID
                                    isTarget -> BoardCellVisualState.HIGHLIGHT
                                    cellColor != null -> BoardCellVisualState.OCCUPIED
                                    else -> BoardCellVisualState.EMPTY
                                }

                                BoardCellView(
                                    row = r,
                                    col = c,
                                    state = visualState,
                                    color = cellColor,
                                    previewColor = if (isPreview || isTarget) BlockColor.CYAN else null,
                                    isClearing = isClearingCell,
                                    isJustPlaced = isJustPlaced && r == targetRow && c == targetCol,
                                    modifier = Modifier.size(cellSize)
                                )
                            }
                        }
                    }
                }

                // Laser Overlay
                LaserLineClearOverlay(
                    clearedRows = if (clearingRow != null) listOf(clearingRow!!) else emptyList(),
                    clearedCols = emptyList(),
                    boardSize = 8
                )

                // Animated Hand Demonstration
                if (!hasInteracted && !isCleared && boardDimension > 0) {
                    val p = handProgress.value
                    val startX = boardDimension * 0.2f
                    val startY = boardDimension * 0.95f
                    val endX = boardDimension * (targetCol + 0.5f) / 8f
                    val endY = boardDimension * (targetRow + 0.5f) / 8f

                    val currentX = startX + (endX - startX) * p
                    val currentY = startY + (endY - startY) * p

                    Canvas(
                        modifier = Modifier
                            .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
                            .size(36.dp)
                    ) {
                        drawCircle(color = Color(0xFFFDE047), radius = 18f)
                        drawCircle(color = Color.White, radius = 10f)
                    }
                }
            }

            // Available Pieces Tray
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AVAILABLE PIECES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFBAE6FD)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.9f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Piece 1: Cyan Single (True Drag-and-Drop)
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    piecePlaced -> Color.Transparent
                                    isDraggingPiece -> Color.Black.copy(alpha = 0.25f)
                                    else -> Color(0xFF06B6D4).copy(alpha = 0.25f)
                                }
                            )
                            .border(
                                1.5.dp,
                                when {
                                    piecePlaced -> Color.White.copy(alpha = 0.1f)
                                    isDraggingPiece -> Color.White.copy(alpha = 0.25f)
                                    else -> Color(0xFFFACC15)
                                },
                                RoundedCornerShape(12.dp)
                            )
                            .onGloballyPositioned { coordinates ->
                                traySlotPosition = coordinates.positionInRoot()
                            }
                            .pointerInput(piecePlaced) {
                                if (piecePlaced) return@pointerInput

                                detectDragGestures(
                                    onDragStart = { localOffset ->
                                        if (isCleared) return@detectDragGestures
                                        fingerRootPosition = Offset(traySlotPosition.x + localOffset.x, traySlotPosition.y + localOffset.y)
                                        totalDragDistance = 0f
                                        isDraggingPiece = true
                                        previewTarget = false
                                        audioService.playTap()
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        fingerRootPosition = Offset(fingerRootPosition.x + dragAmount.x, fingerRootPosition.y + dragAmount.y)
                                        totalDragDistance += kotlin.math.sqrt(dragAmount.x * dragAmount.x + dragAmount.y * dragAmount.y)

                                        if (boardDimension > 0f) {
                                            val focalX = fingerRootPosition.x
                                            val focalY = fingerRootPosition.y - touchLiftPx
                                            val cellPx = boardDimension / 8f
                                            val targetCellCenterX = boardPosition.x + (targetCol + 0.5f) * cellPx
                                            val targetCellCenterY = boardPosition.y + (targetRow + 0.5f) * cellPx
                                            val dx = focalX - targetCellCenterX
                                            val dy = focalY - targetCellCenterY
                                            val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                                            previewTarget = dist < cellPx * 1.25f
                                        }
                                    },
                                    onDragEnd = {
                                        if (totalDragDistance >= 15f && previewTarget) {
                                            executeDrop()
                                        } else {
                                            if (totalDragDistance >= 15f) {
                                                audioService.playInvalidDrop()
                                            }
                                        }
                                        isDraggingPiece = false
                                        previewTarget = false
                                        totalDragDistance = 0f
                                    },
                                    onDragCancel = {
                                        isDraggingPiece = false
                                        previewTarget = false
                                        totalDragDistance = 0f
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (piecePlaced) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Placed",
                                tint = Color(0xFF10B981).copy(alpha = 0.4f),
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Box(modifier = Modifier.scale(if (isDraggingPiece) 0.85f else 1f)) {
                                ColorBlockView(color = BlockColor.CYAN, size = 26.dp)
                            }
                        }
                    }

                    // Piece 2: Yellow 2x2
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        BlockShapeView(
                            cells = listOf(CellCoord(0, 0), CellCoord(0, 1), CellCoord(1, 0), CellCoord(1, 1)),
                            color = BlockColor.YELLOW,
                            cellSize = 18.dp
                        )
                    }

                    // Piece 3: Red 3-Line
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        BlockShapeView(
                            cells = listOf(CellCoord(0, 0), CellCoord(0, 1), CellCoord(0, 2)),
                            color = BlockColor.RED,
                            cellSize = 18.dp
                        )
                    }
                }
            }
        }

        // DRAGGING PIECE AVATAR
        if (isDraggingPiece) {
            val focalX = fingerRootPosition.x
            val focalY = fingerRootPosition.y - touchLiftPx
            val avatarSizeDp = 28.dp
            val avatarSizePx = with(density) { avatarSizeDp.toPx() }
            val avatarLeft = focalX - avatarSizePx / 2f
            val avatarTop = focalY - avatarSizePx / 2f

            Box(
                modifier = Modifier.offset {
                    IntOffset(avatarLeft.roundToInt(), avatarTop.roundToInt())
                }
            ) {
                ColorBlockView(
                    color = BlockColor.CYAN,
                    size = avatarSizeDp
                )
            }
        }

        // Settings Dialog during Tutorial
        if (showSettings) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                val shape = RoundedCornerShape(24.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(shape)
                        .background(Color(0xFF1E1B4B))
                        .border(2.dp, Color(0xFF38BDF8), shape)
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SETTINGS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { showSettings = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val settingsState = gameStorage.getSettings()

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF38BDF8))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sound", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (settingsState.soundEnabled) Color(0xFF22C55E) else Color.Gray)
                                .clickable {
                                    val updated = settingsState.copy(soundEnabled = !settingsState.soundEnabled)
                                    gameStorage.saveSettings(updated)
                                    audioService.soundEnabled = updated.soundEnabled
                                }
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            Text(if (settingsState.soundEnabled) "ON" else "OFF", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0xFF38BDF8))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Music", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (settingsState.musicEnabled) Color(0xFF22C55E) else Color.Gray)
                                .clickable {
                                    val updated = settingsState.copy(musicEnabled = !settingsState.musicEnabled)
                                    gameStorage.saveSettings(updated)
                                    audioService.musicEnabled = updated.musicEnabled
                                }
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            Text(if (settingsState.musicEnabled) "ON" else "OFF", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button3D(
                        text = "CLOSE",
                        onClick = { showSettings = false },
                        variant = Button3DVariant.ROYAL,
                        modifier = Modifier.fillMaxWidth(),
                        height = 46.dp
                    )
                }
            }
        }

        // Completion Modal
        if (showCompletionModal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                val cardShape = RoundedCornerShape(28.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(cardShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F172A))
                            )
                        )
                        .border(2.dp, Color(0xFFFACC15), cardShape)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GREAT JOB!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFACC15),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "You've learned the basics!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button3D(
                        text = "CONTINUE",
                        onClick = {
                            gameStorage.saveStats(
                                gameStorage.getStats().copy(hasCompletedTutorial = true, hasSeenWelcome = true)
                            )
                            onComplete()
                        },
                        variant = Button3DVariant.GREEN,
                        modifier = Modifier.fillMaxWidth(),
                        height = 54.dp
                    )
                }
            }
        }
    }
}
