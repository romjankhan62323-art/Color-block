package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreHeart(
    score: Int,
    label: String = "Score",
    modifier: Modifier = Modifier
) {
    val heartbeat = remember { Animatable(1f) }
    val scoreBump = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        heartbeat.animateTo(
            targetValue = 1.07f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(score) {
        if (score > 0) {
            scoreBump.animateTo(1.22f, tween(120))
            scoreBump.animateTo(1f, tween(200))
        }
    }

    val scoreStr = String.format("%,d", score)
    val fontSize = when {
        scoreStr.length <= 3 -> 24.sp
        scoreStr.length <= 5 -> 20.sp
        else -> 16.sp
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFFBCFE8),
            letterSpacing = 1.5.sp
        )

        Box(
            modifier = Modifier
                .size(width = 96.dp, height = 76.dp)
                .scale(heartbeat.value * scoreBump.value),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(width = 90.dp, height = 72.dp)) {
                val w = size.width
                val h = size.height

                val path = Path().apply {
                    moveTo(w * 0.5f, h * 0.95f)
                    cubicTo(w * 0.20f, h * 0.65f, 0f, h * 0.45f, 0f, h * 0.25f)
                    cubicTo(0f, h * 0.10f, w * 0.12f, 0f, w * 0.28f, 0f)
                    cubicTo(w * 0.38f, 0f, w * 0.45f, h * 0.10f, w * 0.5f, h * 0.20f)
                    cubicTo(w * 0.55f, h * 0.10f, w * 0.62f, 0f, w * 0.72f, 0f)
                    cubicTo(w * 0.88f, 0f, w, h * 0.10f, w, h * 0.25f)
                    cubicTo(w, h * 0.45f, w * 0.80f, h * 0.65f, w * 0.5f, h * 0.95f)
                    close()
                }

                // Heart gradient fill
                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF43F5E), Color(0xFFEC4899), Color(0xFFBE185D))
                    )
                )

                // Outline glow stroke
                drawPath(
                    path = path,
                    color = Color(0xFFFDA4AF),
                    style = Stroke(width = 4f)
                )

                // Top left glossy shine
                drawOval(
                    color = Color.White.copy(alpha = 0.40f),
                    topLeft = Offset(w * 0.15f, h * 0.10f),
                    size = androidx.compose.ui.geometry.Size(w * 0.25f, h * 0.16f)
                )
            }

            Text(
                text = scoreStr,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
