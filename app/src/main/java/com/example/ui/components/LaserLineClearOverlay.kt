package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun LaserLineClearOverlay(
    clearedRows: List<Int>,
    clearedCols: List<Int>,
    boardSize: Int = 8,
    modifier: Modifier = Modifier
) {
    if (clearedRows.isEmpty() && clearedCols.isEmpty()) return

    val progress = remember { Animatable(0f) }

    LaunchedEffect(clearedRows, clearedCols) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellW = size.width / boardSize
            val cellH = size.height / boardSize
            val p = progress.value

            // 1. Cleared Rows
            for (row in clearedRows) {
                val y = row * cellH
                val laserX = -size.width * 0.2f + (size.width * 1.4f) * p

                // Row laser glow fill
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFEC4899).copy(alpha = (1f - p) * 0.5f),
                            Color(0xFFFACC15).copy(alpha = (1f - p) * 0.7f),
                            Color(0xFFEC4899).copy(alpha = (1f - p) * 0.5f)
                        )
                    ),
                    topLeft = Offset(0f, y),
                    size = Size(size.width, cellH)
                )

                // Laser borders
                drawLine(
                    color = Color(0xFFFACC15).copy(alpha = (1f - p * 0.5f)),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 5f
                )
                drawLine(
                    color = Color(0xFFFACC15).copy(alpha = (1f - p * 0.5f)),
                    start = Offset(0f, y + cellH),
                    end = Offset(size.width, y + cellH),
                    strokeWidth = 5f
                )

                // Traveling laser pulse head
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Color.White, Color.Transparent),
                        startX = laserX - 60f,
                        endX = laserX + 60f
                    ),
                    topLeft = Offset(laserX - 60f, y),
                    size = Size(120f, cellH)
                )
            }

            // 2. Cleared Columns
            for (col in clearedCols) {
                val x = col * cellW
                val laserY = -size.height * 0.2f + (size.height * 1.4f) * p

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFEC4899).copy(alpha = (1f - p) * 0.5f),
                            Color(0xFFFACC15).copy(alpha = (1f - p) * 0.7f),
                            Color(0xFFEC4899).copy(alpha = (1f - p) * 0.5f)
                        )
                    ),
                    topLeft = Offset(x, 0f),
                    size = Size(cellW, size.height)
                )

                drawLine(
                    color = Color(0xFFFACC15).copy(alpha = (1f - p * 0.5f)),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 5f
                )
                drawLine(
                    color = Color(0xFFFACC15).copy(alpha = (1f - p * 0.5f)),
                    start = Offset(x + cellW, 0f),
                    end = Offset(x + cellW, size.height),
                    strokeWidth = 5f
                )

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.White, Color.Transparent),
                        startY = laserY - 60f,
                        endY = laserY + 60f
                    ),
                    topLeft = Offset(x, laserY - 60f),
                    size = Size(cellW, 120f)
                )
            }
        }
    }
}
