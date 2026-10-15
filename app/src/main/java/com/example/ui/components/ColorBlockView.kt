package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.BlockColor

@Composable
fun ColorBlockView(
    color: BlockColor,
    modifier: Modifier = Modifier,
    size: Dp? = null
) {
    val boxModifier = if (size != null) {
        modifier.size(size)
    } else {
        modifier.fillMaxSize()
    }

    Box(
        modifier = boxModifier
            .background(color.base, RectangleShape)
            .drawBehind {
                val w = this.size.width
                val h = this.size.height

                // 1. Upper highlight facet
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.65f), Color.White.copy(alpha = 0.10f)),
                        startY = 0f,
                        endY = h * 0.28f
                    ),
                    topLeft = Offset.Zero,
                    size = Size(w, h * 0.28f)
                )

                // 2. Left facet
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.40f), Color.Transparent),
                        startX = 0f,
                        endX = w * 0.20f
                    ),
                    topLeft = Offset.Zero,
                    size = Size(w * 0.20f, h)
                )

                // 3. Bottom shadow facet
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, color.deepShadow.copy(alpha = 0.85f)),
                        startY = h * 0.75f,
                        endY = h
                    ),
                    topLeft = Offset(0f, h * 0.75f),
                    size = Size(w, h * 0.25f)
                )

                // 4. Right shadow facet
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, color.shadow.copy(alpha = 0.80f)),
                        startX = w * 0.80f,
                        endX = w
                    ),
                    topLeft = Offset(w * 0.80f, 0f),
                    size = Size(w * 0.20f, h)
                )

                // 5. Specular sparkle square at top-left
                val sparkleSize = (w * 0.16f).coerceIn(3f, 8f)
                drawRect(
                    color = Color.White.copy(alpha = 0.85f),
                    topLeft = Offset(2f, 2f),
                    size = Size(sparkleSize, sparkleSize)
                )
            }
            .border(1.dp, Color.White.copy(alpha = 0.25f), RectangleShape)
    )
}
