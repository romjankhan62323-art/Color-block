package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BlockColor
import com.example.model.PlayerStats
import com.example.model.ScreenType
import com.example.ui.components.Button3D
import com.example.ui.components.Button3DVariant
import com.example.ui.components.ColorBlockView
import com.example.ui.components.GameLogo
import com.example.ui.components.StatCard

@Composable
fun HomeScreen(
    stats: PlayerStats,
    onNavigate: (ScreenType) -> Unit,
    modifier: Modifier = Modifier
) {
    val floatAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        floatAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF5335D2), Color(0xFF3575DF), Color(0xFF2994F2))
                )
            )
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP HEADER
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Top-Left 3D Trophy Button
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2563EB))
                        .border(2.dp, Color(0xFFFACC15), CircleShape)
                        .clickable { onNavigate(ScreenType.TROPHY) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophies",
                        tint = Color(0xFFFACC15),
                        modifier = Modifier.size(26.dp)
                    )
                }

                GameLogo(compact = true, showStar = true)

                // Top-Right Quick Settings Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                        .clickable { onNavigate(ScreenType.SETTINGS) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Stats Panel
            StatCard(
                diamonds = stats.totalDiamonds,
                streak = stats.currentStreak,
                levels = stats.completedJourneyLevels,
                onDiamondClick = { onNavigate(ScreenType.SHOP) }
            )
        }

        // CENTER HERO AREA: Giant PLAY Button + Floating Blocks
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Floating blocks around PLAY button
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 12.dp, y = (-6 + floatAnim.value * 12).dp)
                    .rotate(floatAnim.value * 10f)
            ) {
                ColorBlockView(color = BlockColor.PINK, size = 32.dp)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-12).dp, y = (4 - floatAnim.value * 12).dp)
                    .rotate(-floatAnim.value * 12f)
            ) {
                ColorBlockView(color = BlockColor.GREEN, size = 30.dp)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 20.dp, y = (4 - floatAnim.value * 10).dp)
                    .rotate(-floatAnim.value * 8f)
            ) {
                ColorBlockView(color = BlockColor.YELLOW, size = 28.dp)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-16).dp, y = (6 + floatAnim.value * 10).dp)
                    .rotate(floatAnim.value * 12f)
            ) {
                ColorBlockView(color = BlockColor.ORANGE, size = 34.dp)
            }

            // Giant PLAY Button
            Button3D(
                text = "PLAY",
                onClick = { onNavigate(ScreenType.JOURNEY) },
                variant = Button3DVariant.GREEN,
                height = 68.dp,
                modifier = Modifier.fillMaxWidth(0.72f),
                icon = {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
            )
        }

        // 3 MAIN GAME MODE BUTTONS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. CLASSIC MODE
            Button3D(
                text = "CLASSIC",
                subtitle = "Endless Block Puzzle",
                onClick = { onNavigate(ScreenType.CLASSIC_INTRO) },
                variant = Button3DVariant.GREEN,
                modifier = Modifier.fillMaxWidth(),
                height = 54.dp,
                icon = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.25f))
                            .padding(2.dp)
                    ) {
                        Column {
                            Row {
                                ColorBlockView(color = BlockColor.YELLOW, size = 9.dp)
                                ColorBlockView(color = BlockColor.RED, size = 9.dp)
                            }
                            Row {
                                ColorBlockView(color = BlockColor.CYAN, size = 9.dp)
                                ColorBlockView(color = BlockColor.GREEN, size = 9.dp)
                            }
                        }
                    }
                }
            )

            // 2. JOURNEY MODE
            Button3D(
                text = "JOURNEY",
                subtitle = "Explore 5,000 Levels",
                badge = if (stats.completedJourneyLevels > 0) "Continue!" else null,
                onClick = { onNavigate(ScreenType.JOURNEY) },
                variant = Button3DVariant.ORANGE,
                modifier = Modifier.fillMaxWidth(),
                height = 54.dp,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Journey",
                        tint = Color(0xFFFEF08A),
                        modifier = Modifier.size(26.dp)
                    )
                }
            )

            // 3. DRAWING BLOCK MODE
            Button3D(
                text = "DRAWING BLOCK",
                subtitle = "Build Pictures with Blocks",
                onClick = { onNavigate(ScreenType.DRAWING_INTRO) },
                variant = Button3DVariant.BLUE,
                modifier = Modifier.fillMaxWidth(),
                height = 54.dp,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Drawing Block",
                        tint = Color(0xFFBAE6FD),
                        modifier = Modifier.size(26.dp)
                    )
                }
            )
        }

        // BOTTOM 4 QUICK NAVIGATION BUTTONS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val navItems = listOf(
                Pair("Journey", Icons.Default.Map) to ScreenType.JOURNEY,
                Pair("Shop", Icons.Default.ShoppingBag) to ScreenType.SHOP,
                Pair("Cards", Icons.Default.GridView) to ScreenType.COLLECTION,
                Pair("Daily", Icons.Default.CardGiftcard) to ScreenType.DAILY_REWARDS
            )

            navItems.forEach { (meta, screen) ->
                val (title, icon) = meta
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1D4ED8), Color(0xFF1E3A8A))
                            )
                        )
                        .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .clickable { onNavigate(screen) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color(0xFFFACC15),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // CHALLENGE BANNER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F172A))
                    )
                )
                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                .clickable { onNavigate(ScreenType.JOURNEY) }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFD97706)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Challenge",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Challenge Yourself",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Reach 5000 Levels!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFACC15)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "VIEW",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFBAE6FD)
                )
            }
        }
    }
}
