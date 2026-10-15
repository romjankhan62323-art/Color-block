package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TwoHeartProgressBar(
    levelNumber: Int,
    progressPercent: Int,
    completedLines: Int,
    targetLines: Int,
    modifier: Modifier = Modifier
) {
    val clampedPercent = progressPercent.coerceIn(0, 100)

    // Left heart fills from 0% to 50% of overall level progress
    val leftHeartFillRatio = (clampedPercent / 50f).coerceIn(0f, 1f)
    // Right heart fills from 50% to 100% of overall level progress
    val rightHeartFillRatio = ((clampedPercent - 50f) / 50f).coerceIn(0f, 1f)

    val animatedLeftFill by animateFloatAsState(
        targetValue = leftHeartFillRatio,
        animationSpec = tween(durationMillis = 400),
        label = "leftHeartFill"
    )
    val animatedRightFill by animateFloatAsState(
        targetValue = rightHeartFillRatio,
        animationSpec = tween(durationMillis = 400),
        label = "rightHeartFill"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Level Title & Progress Text
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "LEVEL $levelNumber  •  ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFDE047),
                letterSpacing = 1.sp
            )
            Text(
                text = "$clampedPercent% COMPLETE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Two Connected Hearts Progress Meter
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.75f))
                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Heart 1 (Left Heart: 0% - 50%)
            HeartProgressIcon(
                fillRatio = animatedLeftFill,
                modifier = Modifier.size(width = 38.dp, height = 32.dp)
            )

            // Connecting link indicator
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (clampedPercent >= 50) Color(0xFFF43F5E) else Color.White.copy(alpha = 0.25f)
                    )
            )

            // Heart 2 (Right Heart: 50% - 100%)
            HeartProgressIcon(
                fillRatio = animatedRightFill,
                modifier = Modifier.size(width = 38.dp, height = 32.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Lines counter pill
            Text(
                text = "$completedLines/$targetLines Lines",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFBAE6FD)
            )
        }
    }
}

@Composable
fun HeartProgressIcon(
    fillRatio: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val heartPath = Path().apply {
            moveTo(w * 0.5f, h * 0.95f)
            cubicTo(w * 0.20f, h * 0.65f, 0f, h * 0.45f, 0f, h * 0.25f)
            cubicTo(0f, h * 0.10f, w * 0.12f, 0f, w * 0.28f, 0f)
            cubicTo(w * 0.38f, 0f, w * 0.45f, h * 0.10f, w * 0.5f, h * 0.20f)
            cubicTo(w * 0.55f, h * 0.10f, w * 0.62f, 0f, w * 0.72f, 0f)
            cubicTo(w * 0.88f, 0f, w, h * 0.10f, w, h * 0.25f)
            cubicTo(w, h * 0.45f, w * 0.80f, h * 0.65f, w * 0.5f, h * 0.95f)
            close()
        }

        // 1. Dark empty heart background
        drawPath(
            path = heartPath,
            color = Color(0xFF332042).copy(alpha = 0.85f)
        )

        // 2. Filled portion (bottom to top fill based on fillRatio)
        if (fillRatio > 0f) {
            val fillTopY = h * (1f - fillRatio)
            clipPath(heartPath) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF43F5E), Color(0xFFEC4899), Color(0xFFBE185D)),
                        startY = fillTopY,
                        endY = h
                    ),
                    topLeft = Offset(0f, fillTopY),
                    size = androidx.compose.ui.geometry.Size(w, h - fillTopY)
                )

                // Top glow line of the liquid fill
                drawLine(
                    color = Color.White.copy(alpha = 0.85f),
                    start = Offset(0f, fillTopY),
                    end = Offset(w, fillTopY),
                    strokeWidth = 3f
                )
            }
        }

        // 3. Heart outline border
        drawPath(
            path = heartPath,
            color = if (fillRatio >= 1f) Color(0xFFFDA4AF) else Color.White.copy(alpha = 0.4f),
            style = Stroke(width = 2.5f)
        )

        // 4. Specular gloss highlight on top-left lobe
        drawOval(
            color = Color.White.copy(alpha = 0.35f),
            topLeft = Offset(w * 0.16f, h * 0.08f),
            size = androidx.compose.ui.geometry.Size(w * 0.24f, h * 0.14f)
        )
    }
}
