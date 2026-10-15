package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BlockColor
import com.example.ui.components.Button3D
import com.example.ui.components.Button3DVariant
import com.example.ui.components.ColorBlockView

@Composable
fun WelcomePopup(
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(0.7f) }
    var isTapped by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // 7x7 Matrix for the signature block smiley face graphic
    val smileyGrid = listOf(
        listOf(0, 1, 1, 1, 1, 1, 0),
        listOf(1, 2, 4, 2, 4, 2, 1),
        listOf(1, 1, 4, 1, 4, 1, 1),
        listOf(1, 6, 1, 1, 1, 6, 1),
        listOf(1, 3, 3, 3, 3, 3, 1),
        listOf(0, 1, 3, 3, 3, 1, 0),
        listOf(0, 0, 1, 1, 1, 0, 0)
    )

    fun getColor(value: Int): BlockColor {
        return when (value) {
            1 -> BlockColor.YELLOW
            2 -> BlockColor.ORANGE
            3 -> BlockColor.PINK
            4 -> BlockColor.BLUE
            5 -> BlockColor.GREEN
            6 -> BlockColor.RED
            else -> BlockColor.YELLOW
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        val cardShape = RoundedCornerShape(32.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scaleAnim.value)
                .clip(cardShape)
                .background(Color(0xFFF0F9FF))
                .border(2.dp, Color.White.copy(alpha = 0.6f), cardShape),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Ribbon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF5335D2), Color(0xFF3575DF), Color(0xFF2994F2))
                        )
                    )
                    .padding(vertical = 14.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "WELCOME TO COLOR BLOCK!",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "READY TO PLAY?",
                        color = Color(0xFFBAE6FD),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }

            // Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Block-made Smiley Artwork
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.15f))
                        .border(1.5.dp, Color.White, RoundedCornerShape(20.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        smileyGrid.forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                row.forEach { cellVal ->
                                    if (cellVal != 0) {
                                        ColorBlockView(
                                            color = getColor(cellVal),
                                            size = 14.dp
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "A World Full of Colors Awaits You!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Let the Fun Begin!",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button3D(
                    text = "LET'S PLAY!",
                    onClick = {
                        if (!isTapped) {
                            isTapped = true
                            onPlayClick()
                        }
                    },
                    enabled = !isTapped,
                    variant = Button3DVariant.GREEN,
                    modifier = Modifier.fillMaxWidth(),
                    height = 54.dp
                )
            }
        }
    }
}
