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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameAudioService
import com.example.data.GameStorage
import com.example.model.PlayerStats
import com.example.ui.components.Button3D
import com.example.ui.components.Button3DVariant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ShopPowerUp(
    val id: String,
    val name: String,
    val price: Int,
    val icon: String,
    val desc: String
)

@Composable
fun ShopScreen(
    stats: PlayerStats,
    onBack: () -> Unit,
    onRefreshStats: () -> Unit,
    audioService: GameAudioService,
    gameStorage: GameStorage,
    onWatchAdForDiamonds: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var activeTab by remember { mutableStateOf("powerups") }
    var purchaseMsg by remember { mutableStateOf<String?>(null) }

    val powerUps = listOf(
        ShopPowerUp("extra_moves", "Extra Moves", 100, " ", "Adds 5 extra placement moves"),
        ShopPowerUp("remove_row", "Remove Row", 150, " ", "Instantly clears any chosen row"),
        ShopPowerUp("bomb", "Bomb", 200, " ", "Explodes a 3x3 block section"),
        ShopPowerUp("hammer", "Hammer", 250, " ", "Smash any stubborn single block"),
        ShopPowerUp("shuffle", "Shuffle", 300, " ", "Rerolls current available pieces"),
        ShopPowerUp("color_switch", "Color Switch", 400, " ", "Transforms blocks to same color")
    )

    fun buyItem(item: ShopPowerUp) {
        if (stats.totalDiamonds < item.price) {
            purchaseMsg = "Not enough diamonds!"
            scope.launch {
                delay(2000)
                purchaseMsg = null
            }
            return
        }

        val success = gameStorage.spendDiamonds(item.price)
        if (success) {
            audioService.playDiamondChime()
            purchaseMsg = "Purchased ${item.name}!"
            onRefreshStats()
            scope.launch {
                delay(2000)
                purchaseMsg = null
            }
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

            Text(
                text = "SHOP",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )

            // Diamonds counter with + button
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .border(1.dp, Color(0xFF38BDF8), CircleShape)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format("%,d", stats.totalDiamonds),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                        .clickable {
                            onWatchAdForDiamonds?.invoke()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Free Diamonds Rewarded Ad Banner (Optional)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF047857), Color(0xFF10B981))
                    )
                )
                .border(1.dp, Color(0xFF6EE7B7).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable { onWatchAdForDiamonds?.invoke() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Videocam,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Watch Ad for Free Diamonds",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Earn +50 Diamonds",
                        color = Color(0xFFD1FAE5),
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFACC15))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Diamond,
                        contentDescription = null,
                        tint = Color(0xFF1E293B),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "+50",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.35f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                Pair("blocks", "Blocks"),
                Pair("powerups", "Power-Ups"),
                Pair("avatars", "Avatars"),
                Pair("themes", "Themes")
            )

            tabs.forEach { (key, label) ->
                val isActive = activeTab == key
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isActive) Color(0xFF2563EB) else Color.Transparent)
                        .clickable {
                            audioService.playTap()
                            activeTab = key
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isActive) Color.White else Color(0xFFBAE6FD)
                    )
                }
            }
        }

        // Purchase message toast
        if (purchaseMsg != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFACC15))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = purchaseMsg!!,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Power-Ups List
        if (activeTab == "powerups") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                powerUps.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF1E1B4B))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.icon, fontSize = 22.sp)
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Text(item.desc, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFBAE6FD))
                                Text("  ${item.price}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFFFACC15))
                            }
                        }

                        Button3D(
                            text = "BUY",
                            onClick = { buyItem(item) },
                            variant = Button3DVariant.GREEN,
                            height = 42.dp,
                            modifier = Modifier.width(76.dp)
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(" ", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("More Items Coming Soon!", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text("Unlock special skins by completing Journey levels.", fontSize = 12.sp, color = Color(0xFFBAE6FD))
                }
            }
        }
    }
}
