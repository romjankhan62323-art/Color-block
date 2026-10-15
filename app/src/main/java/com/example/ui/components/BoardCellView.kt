package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.model.BlockColor
import com.example.model.BoardCellVisualState

@Composable
fun BoardCellView(
    row: Int,
    col: Int,
    state: BoardCellVisualState,
    color: BlockColor?,
    previewColor: BlockColor?,
    isClearing: Boolean = false,
    isJustPlaced: Boolean = false,
    reducedMotion: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(1f) }

    LaunchedEffect(isClearing) {
        if (isClearing && !reducedMotion) {
            scaleAnim.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
            )
        } else {
            scaleAnim.snapTo(1f)
        }
    }

    LaunchedEffect(isJustPlaced) {
        if (isJustPlaced && !reducedMotion) {
            scaleAnim.animateTo(
                targetValue = 1.15f,
                animationSpec = tween(durationMillis = 100)
            )
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 140)
            )
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(Color(0xFF25224F), RectangleShape)
            .border(0.75.dp, Color(0xFF334155), RectangleShape),
        contentAlignment = Alignment.Center
    ) {
        // 1. Occupied State
        if (color != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(if (isClearing) scaleAnim.value else 1f)
            ) {
                ColorBlockView(color = color, modifier = Modifier.fillMaxSize())
            }
        }

        // 2. Valid Placement Preview (Translucent + Green glowing border)
        if (state == BoardCellVisualState.PREVIEW_VALID && previewColor != null && color == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, Color(0xFF6EE7B7), RectangleShape)
            ) {
                ColorBlockView(
                    color = previewColor,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF34D399).copy(alpha = 0.35f))
                )
            }
        }

        // 3. Invalid Placement Preview (Red/rose hazard tint)
        if (state == BoardCellVisualState.PREVIEW_INVALID) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF43F5E).copy(alpha = 0.40f))
                    .border(2.dp, Color(0xFFFB7185), RectangleShape)
            )
        }

        // 4. Tutorial / Spotlight Highlight
        if (state == BoardCellVisualState.HIGHLIGHT && color == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFACC15).copy(alpha = 0.30f))
                    .border(2.dp, Color(0xFFFACC15), RectangleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFFFDE047))
                )
            }
        }
    }
}
