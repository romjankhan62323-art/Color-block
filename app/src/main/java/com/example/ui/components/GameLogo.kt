package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameLogo(
    compact: Boolean = false,
    showStar: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colorLetters = listOf(
        Pair("C", Color(0xFFE8312A)),
        Pair("O", Color(0xFFF57C1C)),
        Pair("L", Color(0xFFFBC02D)),
        Pair("O", Color(0xFF22C55E)),
        Pair("R", Color(0xFF0EA5E9))
    )

    val colorFontSize = if (compact) 32.sp else 46.sp
    val blockFontSize = if (compact) 22.sp else 34.sp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                colorLetters.forEach { (char, color) ->
                    Text(
                        text = char,
                        fontSize = colorFontSize,
                        fontWeight = FontWeight.Black,
                        color = color,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Yellow star near 'R'
            if (showStar) {
                Canvas(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 10.dp, y = (-4).dp)
                        .size(16.dp)
                ) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val outerR = size.width / 2
                    val innerR = outerR * 0.45f
                    val path = Path()

                    for (i in 0 until 10) {
                        val angle = (i * 36 - 90) * (Math.PI / 180f)
                        val r = if (i % 2 == 0) outerR else innerR
                        val x = (cx + cos(angle) * r).toFloat()
                        val y = (cy + sin(angle) * r).toFloat()
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    path.close()

                    drawPath(path = path, color = Color(0xFFFACC15))
                    drawPath(path = path, color = Color.White, style = Stroke(width = 2f))
                }
            }
        }

        Text(
            text = "BLOCK",
            fontSize = blockFontSize,
            fontWeight = FontWeight.Black,
            style = androidx.compose.ui.text.TextStyle(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFF1F5F9), Color(0xFFFFFFFF), Color(0xFFCBD5E1))
                ),
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black.copy(alpha = 0.45f),
                    offset = Offset(0f, 3f),
                    blurRadius = 5f
                )
            ),
            letterSpacing = if (compact) 4.sp else 6.sp,
            modifier = Modifier.offset(y = (-4).dp)
        )
    }
}
