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
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlayerStats

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val trophies: Int,
    val level: Int,
    val avatar: String,
    val isUser: Boolean = false
)

@Composable
fun TrophyRankingScreen(
    stats: PlayerStats,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ranks = listOf(
        Pair("Bronze", Color(0xFF92400E)) to true,
        Pair("Silver", Color(0xFF64748B)) to true,
        Pair("Gold", Color(0xFFEAB308)) to true,
        Pair("Diamond", Color(0xFF0284C7)) to false,
        Pair("Hero", Color(0xFFE11D48)) to false,
        Pair("Master", Color(0xFF7C3AED)) to false,
        Pair("Grandmaster", Color(0xFF059669)) to false
    )

    val leaderboard = listOf(
        LeaderboardEntry(1, "Dreamer", 5280, 4200, " "),
        LeaderboardEntry(2, "Legend", 4870, 3980, " "),
        LeaderboardEntry(3, "Galaxy", 4520, 3650, " "),
        LeaderboardEntry(4, "You", stats.trophies * 100 + 1120, maxOf(120, stats.completedJourneyLevels), " ", isUser = true),
        LeaderboardEntry(5, "Star", 3180, 2800, " "),
        LeaderboardEntry(6, "Nova", 2950, 2600, " ")
    )

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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFACC15), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TROPHY & RANKING",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.size(40.dp))
        }

        // Top 4 Stat Tiles
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Level
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.75f))
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("LEVEL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFBAE6FD))
                        Text("${maxOf(120, stats.completedJourneyLevels)}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }

            // Diamonds
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.75f))
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("DIAMONDS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFBAE6FD))
                        Text(String.format("%,d", stats.totalDiamonds), fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Streak
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.75f))
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("STREAK", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFED7AA))
                        Text("${stats.currentStreak} Day", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }

            // Trophies
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.75f))
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFACC15), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("TROPHIES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFEF08A))
                        Text("${stats.trophies}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }

        // Best Record & Current Rank
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1E1B4B).copy(alpha = 0.8f))
                    .border(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFACC15), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Best Record", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFFACC15))
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Level 1500", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF451A03).copy(alpha = 0.75f))
                    .border(1.5.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = Color(0xFFFACC15), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Current Rank", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFFACC15))
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Gold", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFFACC15))
                }
            }
        }

        // Rank Badges
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "RANK PROGRESS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFBAE6FD),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ranks.forEach { (rankData, active) ->
                    val (name, color) = rankData
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    if (active) 2.dp else 1.dp,
                                    if (active) Color(0xFFFACC15) else Color.Gray,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MilitaryTech, contentDescription = name, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = name,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (active) Color.White else Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Leaderboard
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.9f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("# PLAYER", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFBAE6FD))
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text("TROPHIES", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFBAE6FD))
                    Text("LEVEL", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFBAE6FD))
                }
            }

            leaderboard.forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (entry.isUser) Color(0xFFF59E0B).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f)
                        )
                        .border(
                            1.dp,
                            if (entry.isUser) Color(0xFFFACC15) else Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${entry.rank}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.width(20.dp)
                        )
                        Text(entry.avatar, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = entry.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (entry.isUser) Color(0xFFFDE047) else Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Text(
                            text = "${entry.trophies}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFACC15)
                        )
                        Text(
                            text = "${entry.level}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBAE6FD),
                            modifier = Modifier.width(36.dp)
                        )
                    }
                }
            }
        }
    }
}
