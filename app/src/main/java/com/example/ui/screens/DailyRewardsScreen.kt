package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.GameAudioService
import com.example.data.GameStorage
import com.example.model.PlayerStats
import com.example.ui.components.Button3D
import com.example.ui.components.Button3DVariant

data class DailyRewardItem(val day: Int, val diamonds: Int)

@Composable
fun DailyRewardsScreen(
    stats: PlayerStats,
    onBack: () -> Unit,
    onRefreshStats: () -> Unit,
    audioService: GameAudioService,
    gameStorage: GameStorage,
    modifier: Modifier = Modifier
) {
    val claimedDays = remember { mutableStateListOf(1) }

    val days = listOf(
        DailyRewardItem(1, 50),
        DailyRewardItem(2, 75),
        DailyRewardItem(3, 100),
        DailyRewardItem(4, 150),
        DailyRewardItem(5, 200),
        DailyRewardItem(6, 250)
    )

    fun claimDay(day: Int, amount: Int) {
        if (!claimedDays.contains(day)) {
            audioService.playDiamondChime()
            gameStorage.addDiamonds(amount)
            claimedDays.add(day)
            onRefreshStats()
        }
    }

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
        Column(modifier = Modifier.fillMaxWidth()) {
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
                    text = "DAILY REWARDS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            Text(
                text = "LOGIN DAILY & GET REWARDS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFBAE6FD),
                letterSpacing = 1.5.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 6-Day Grid (3 cols x 2 rows)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0 until 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0 until 3) {
                        val item = days[row * 3 + col]
                        val isClaimed = claimedDays.contains(item.day)
                        val isNext = !isClaimed && item.day == 2

                        val shape = RoundedCornerShape(16.dp)

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(shape)
                                .background(
                                    when {
                                        isClaimed -> Color(0xFF064E3B).copy(alpha = 0.85f)
                                        isNext -> Color(0xFF1E3A8A).copy(alpha = 0.95f)
                                        else -> Color(0xFF0F172A).copy(alpha = 0.75f)
                                    }
                                )
                                .border(
                                    if (isNext) 2.dp else 1.dp,
                                    if (isNext) Color(0xFFFACC15) else Color.White.copy(alpha = 0.2f),
                                    shape
                                )
                                .clickable {
                                    if (isNext) {
                                        claimDay(item.day, item.diamonds)
                                    }
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Day ${item.day}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFBAE6FD)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            if (isClaimed) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            } else {
                                Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${item.diamonds}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Day 7 Grand Prize Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF4C1D95), Color(0xFF1E1B4B))
                    )
                )
                .border(2.dp, Color(0xFFFACC15), RoundedCornerShape(22.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_treasure_chest),
                    contentDescription = "Day 7 Grand Prize",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "DAY 7 GRAND PRIZE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFACC15),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "  500 Diamonds",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Streak Pill
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xFF451A03).copy(alpha = 0.85f))
                .border(1.dp, Color(0xFFF59E0B), CircleShape)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${stats.currentStreak} Day Streak",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFED7AA)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Claim & Continue
        Button3D(
            text = "CLAIM & CONTINUE",
            onClick = {
                if (!claimedDays.contains(2)) {
                    claimDay(2, 75)
                }
                onBack()
            },
            variant = Button3DVariant.GREEN,
            modifier = Modifier.fillMaxWidth(),
            height = 54.dp
        )
    }
}
