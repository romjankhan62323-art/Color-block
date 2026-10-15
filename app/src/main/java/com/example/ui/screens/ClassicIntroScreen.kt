package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BlockColor
import com.example.ui.components.Button3D
import com.example.ui.components.Button3DVariant
import com.example.ui.components.ColorBlockView
import com.example.ui.components.ScoreHeart

@Composable
fun ClassicIntroScreen(
    bestScore: Int,
    onPlay: (boardSize: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var boardDimension by remember { mutableIntStateOf(8) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF5335D2), Color(0xFF3575DF), Color(0xFF2994F2))
                )
            )
            .padding(14.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2563EB))
                    .border(1.5.dp, Color.White, CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Text(
                text = "CLASSIC MODE",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.size(40.dp))
        }

        // Center Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScoreHeart(score = bestScore, label = "Best Score")

            Spacer(modifier = Modifier.height(14.dp))

            // 3x3 Block Cluster
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                    .border(2.dp, Color(0xFF10B981), RoundedCornerShape(22.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        ColorBlockView(color = BlockColor.GREEN, size = 26.dp)
                        ColorBlockView(color = BlockColor.YELLOW, size = 26.dp)
                        ColorBlockView(color = BlockColor.BLUE, size = 26.dp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        ColorBlockView(color = BlockColor.ORANGE, size = 26.dp)
                        ColorBlockView(color = BlockColor.RED, size = 26.dp)
                        ColorBlockView(color = BlockColor.PINK, size = 26.dp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        ColorBlockView(color = BlockColor.CYAN, size = 26.dp)
                        ColorBlockView(color = BlockColor.PURPLE, size = 26.dp)
                        ColorBlockView(color = BlockColor.GREEN, size = 26.dp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ENDLESS PUZZLE",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Text(
                text = "PURE BLOCK MATCHING",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF6EE7B7),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Grid Size Selector (8x8 vs 9x9)
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SELECT BOARD GRID",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFBAE6FD),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val shape = RoundedCornerShape(14.dp)

                    // 8x8
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(shape)
                            .background(if (boardDimension == 8) Color(0xFF10B981) else Color.White.copy(alpha = 0.1f))
                            .border(
                                if (boardDimension == 8) 2.dp else 1.dp,
                                if (boardDimension == 8) Color.White else Color.White.copy(alpha = 0.2f),
                                shape
                            )
                            .clickable { boardDimension = 8 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "8 × 8 Grid",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    // 9x9
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(shape)
                            .background(if (boardDimension == 9) Color(0xFF10B981) else Color.White.copy(alpha = 0.1f))
                            .border(
                                if (boardDimension == 9) 2.dp else 1.dp,
                                if (boardDimension == 9) Color.White else Color.White.copy(alpha = 0.2f),
                                shape
                            )
                            .clickable { boardDimension = 9 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "9 × 9 Grid",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Info rows
            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.65f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFFACC15), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("No time limits — think at your own pace!", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.65f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Clear rows and columns with electric laser blasts.", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Play Button
        Button3D(
            text = "PLAY $boardDimension × $boardDimension",
            onClick = { onPlay(boardDimension) },
            variant = Button3DVariant.GREEN,
            modifier = Modifier.fillMaxWidth(0.9f),
            height = 56.dp,
            icon = {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
        )
    }
}
