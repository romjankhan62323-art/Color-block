package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Button3DVariant(
    val topGradient: Color,
    val midGradient: Color,
    val bottomGradient: Color,
    val shadowColor: Color
) {
    GREEN(Color(0xFF4ADE80), Color(0xFF22C55E), Color(0xFF10B981), Color(0xFF047857)),
    ORANGE(Color(0xFFFDE047), Color(0xFFF97316), Color(0xFFEA580C), Color(0xFFC2410C)),
    BLUE(Color(0xFF38BDF8), Color(0xFF2563EB), Color(0xFF1D4ED8), Color(0xFF1E40AF)),
    ROYAL(Color(0xFF3B82F6), Color(0xFF2563EB), Color(0xFF1D4ED8), Color(0xFF1E3A8A)),
    GLASS(Color(0x40FFFFFF), Color(0x30FFFFFF), Color(0x20FFFFFF), Color(0x60000000))
}

@Composable
fun Button3D(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: Button3DVariant = Button3DVariant.GREEN,
    subtitle: String? = null,
    badge: String? = null,
    icon: (@Composable () -> Unit)? = null,
    height: Dp = 56.dp,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressOffset by animateFloatAsState(targetValue = if (isPressed) 4f else 0f, label = "pressOffset")

    val shape = RoundedCornerShape(20.dp)
    val shadowHeight = 6.dp

    Box(
        modifier = modifier
            .offset(y = pressOffset.dp)
            .height(height)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Bottom 3D Extrusion
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = (shadowHeight.value - pressOffset).dp)
                .background(variant.shadowColor, shape)
        )

        // Main Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(variant.topGradient, variant.midGradient, variant.bottomGradient)
                    )
                )
                .border(1.5.dp, Color.White.copy(alpha = 0.5f), shape)
                .drawBehind {
                    // Top bevel glossy reflection
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.45f), Color.Transparent),
                            startY = 0f,
                            endY = size.height * 0.4f
                        ),
                        topLeft = Offset.Zero,
                        size = Size(size.width, size.height * 0.4f)
                    )
                }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        icon()
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = text.uppercase(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Optional Top-Right Badge
        if (badge != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .background(Color(0xFFEF4444), RoundedCornerShape(12.dp))
                    .border(1.5.dp, Color.White, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
