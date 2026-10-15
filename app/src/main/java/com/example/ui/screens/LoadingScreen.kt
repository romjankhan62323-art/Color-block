package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BlockColor
import com.example.ui.components.ColorBlockView
import com.example.ui.components.GameLogo
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    onComplete: () -> Unit,
    targetSeconds: Int = 21,
    modifier: Modifier = Modifier
) {
    var elapsedSeconds by remember { mutableFloatStateOf(0f) }
    var isFast by remember { androidx.compose.runtime.mutableStateOf(false) }

    val dancingColors = listOf(
        BlockColor.RED,
        BlockColor.ORANGE,
        BlockColor.YELLOW,
        BlockColor.GREEN,
        BlockColor.CYAN,
        BlockColor.BLUE,
        BlockColor.PURPLE,
        BlockColor.PINK
    )

    LaunchedEffect(isFast) {
        val stepMs = 100L
        while (elapsedSeconds < targetSeconds) {
            delay(stepMs)
            val increment = if (isFast) 1.0f else (stepMs / 1000f)
            elapsedSeconds = (elapsedSeconds + increment).coerceAtMost(targetSeconds.toFloat())
        }
        delay(200)
        onComplete()
    }

    val progressPercent = ((elapsedSeconds / targetSeconds) * 100).toInt().coerceIn(0, 100)
    val remainingSeconds = (targetSeconds - elapsedSeconds).toInt().coerceAtLeast(0)
    val activeBlockIndex = ((elapsedSeconds * 3).toInt()) % dancingColors.size

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF5335D2), Color(0xFF3575DF), Color(0xFF2994F2))
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Top Fast-Forward Bar
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.3f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF38BDF8), CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Loading: ${remainingSeconds}s",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    .clickable { isFast = true }
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Fast Load",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Fast Load",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Center Brand & Taglines
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GameLogo(compact = false, showStar = true)

            Spacer(modifier = Modifier.height(24.dp))

            // Tagline: "30 minutes a day"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "30 MINUTES ",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFACC15)
                )
                Text(
                    text = "A DAY",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Message: "Less Stress, Better Sleep" with curved glowing underline
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Less Stress, Better Sleep",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = Color.White
                )

                Canvas(modifier = Modifier.size(width = 170.dp, height = 12.dp)) {
                    val path = Path().apply {
                        moveTo(10f, 6f)
                        quadraticTo(size.width / 2, 12f, size.width - 10f, 6f)
                    }
                    drawPath(
                        path = path,
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF38BDF8), Color(0xFFFACC15), Color(0xFF38BDF8))
                        ),
                        style = Stroke(width = 6f)
                    )
                }
            }
        }

        // Bottom Dancing Blocks and Progress Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.85f)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 8 Animated dancing blocks
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                dancingColors.forEachIndexed { index, color ->
                    val isActive = index == activeBlockIndex
                    Box(
                        modifier = Modifier
                            .offset(y = if (isActive) (-8).dp else 0.dp)
                    ) {
                        ColorBlockView(
                            color = color,
                            size = if (isActive) 26.dp else 22.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressPercent / 100f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF34D399), Color(0xFF38BDF8), Color(0xFFFACC15))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Loading Color Block...",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBAE6FD)
                )
                Text(
                    text = "$progressPercent%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}
