package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.PlayerStats

data class LevelNode(
    val num: Int,
    val xFraction: Float,
    val yFraction: Float,
    val stars: Int = 0,
    val hasChest: Boolean = false,
    val unlocked: Boolean = false
)

@Composable
fun JourneyMapScreen(
    stats: PlayerStats,
    onBack: () -> Unit,
    onSelectLevel: (Int) -> Unit,
    onOpenTreasure: () -> Unit,
    onWatchAd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pageStartLevel by remember {
        mutableIntStateOf((((stats.lastUnlockedJourneyLevel - 1) / 10) * 10 + 1).coerceIn(1, 4991))
    }

    val positions = listOf(
        Pair(0.28f, 0.08f),
        Pair(0.72f, 0.16f),
        Pair(0.32f, 0.26f),
        Pair(0.68f, 0.36f),
        Pair(0.30f, 0.46f),
        Pair(0.70f, 0.56f),
        Pair(0.32f, 0.66f),
        Pair(0.68f, 0.76f),
        Pair(0.35f, 0.86f),
        Pair(0.65f, 0.94f)
    )

    val levels = (0 until 10).map { i ->
        val num = pageStartLevel + i
        val pos = positions[i]
        LevelNode(
            num = num,
            xFraction = pos.first,
            yFraction = pos.second,
            stars = if (stats.completedJourneyLevels >= num) 3 else 0,
            hasChest = (num % 5 == 0),
            unlocked = stats.lastUnlockedJourneyLevel >= num
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A192F))
    ) {
        // TOP HEADER
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(Color(0xFF1E3A8A).copy(alpha = 0.95f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2563EB))
                    .border(1.5.dp, Color.White, CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Stat pills
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "  ${stats.totalDiamonds}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFBAE6FD)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "  ${stats.currentStreak}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFED7AA)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "  ${stats.completedJourneyLevels}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFA7F3D0)
                    )
                }
            }
        }

        // SCROLLABLE MAP VIEWPORT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp, bottom = 56.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Journey Title
            Column(
                modifier = Modifier.padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "JOURNEY",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "5,000 LEVELS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFACC15),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (pageStartLevel > 1) Color(0xFF2563EB) else Color.White.copy(alpha = 0.1f))
                            .clickable(enabled = pageStartLevel > 1) {
                                pageStartLevel = (pageStartLevel - 10).coerceAtLeast(1)
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("◀ Prev", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "Levels $pageStartLevel – ${minOf(5000, pageStartLevel + 9)}",
                        color = Color(0xFFBAE6FD),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (pageStartLevel < 4991) Color(0xFF2563EB) else Color.White.copy(alpha = 0.1f))
                            .clickable(enabled = pageStartLevel < 4991) {
                                pageStartLevel = (pageStartLevel + 10).coerceAtMost(4991)
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Next ▶", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Sydney Opera House Preview Card
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                    .border(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 80.dp, height = 54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_sydney_opera),
                        contentDescription = "Sydney Opera House",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Sydney Opera House",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Unlock at Level 50",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFACC15)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Map Area with Background Image & Level Nodes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(820.dp)
            ) {
                // Background Island Image
                Image(
                    painter = painterResource(id = R.drawable.bg_journey_island),
                    contentDescription = "Journey Map Island",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.85f
                )

                // Dashed S-Path Trail
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f), 0f)

                    for (i in 0 until levels.size - 1) {
                        val n1 = levels[i]
                        val n2 = levels[i + 1]
                        val start = Offset(w * n1.xFraction, h * n1.yFraction)
                        val end = Offset(w * n2.xFraction, h * n2.yFraction)

                        drawLine(
                            color = Color.White.copy(alpha = 0.6f),
                            start = start,
                            end = end,
                            strokeWidth = 5f,
                            pathEffect = pathEffect
                        )
                    }
                }

                // Level Nodes
                levels.forEach { node ->
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val nodeX = maxWidth * node.xFraction - 28.dp
                        val nodeY = maxHeight * node.yFraction - 28.dp

                        Column(
                            modifier = Modifier
                                .offset(x = nodeX, y = nodeY)
                                .clickable {
                                    if (node.unlocked) {
                                        onSelectLevel(node.num)
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (node.unlocked) {
                                            Brush.verticalGradient(
                                                colors = listOf(Color(0xFFFDE047), Color(0xFFF59E0B), Color(0xFFD97706))
                                            )
                                        } else {
                                            Brush.verticalGradient(
                                                colors = listOf(Color(0xFF475569), Color(0xFF1E293B))
                                            )
                                        }
                                    )
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (node.unlocked) {
                                    Text(
                                        text = "${node.num}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF0F172A)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // Stars below node
                            Row(
                                modifier = Modifier.padding(top = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(1.dp)
                            ) {
                                repeat(3) { starIdx ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (starIdx < node.stars) Color(0xFFFACC15) else Color.White.copy(alpha = 0.3f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }

                        // Treasure Box marker on Level 4
                        if (node.hasChest) {
                            Box(
                                modifier = Modifier
                                    .offset(x = nodeX + 44.dp, y = nodeY - 14.dp)
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFD97706))
                                    .border(1.5.dp, Color.White, RoundedCornerShape(8.dp))
                                    .clickable(onClick = onOpenTreasure),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = " ", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }

        // BOTTOM REWARD BAR
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xFF0A192F).copy(alpha = 0.95f))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF9333EA), Color(0xFF4F46E5))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .clickable(onClick = onOpenTreasure)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "   +10",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF2563EB), Color(0xFF0284C7))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .clickable(onClick = onWatchAd)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Watch Ad ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
