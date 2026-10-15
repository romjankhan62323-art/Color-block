package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import android.app.Activity
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ads.UnityAdsManager
import com.example.ui.components.Button3D
import com.example.ui.components.Button3DVariant
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameAudioService
import com.example.data.GameStorage
import com.example.model.GameSettings

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateAbout: () -> Unit,
    onResetFirstLaunch: () -> Unit,
    audioService: GameAudioService,
    gameStorage: GameStorage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var settings by remember { mutableStateOf(gameStorage.getSettings()) }

    fun updateSettings(newSettings: GameSettings) {
        settings = newSettings
        gameStorage.saveSettings(newSettings)
        audioService.soundEnabled = newSettings.soundEnabled
        audioService.musicEnabled = newSettings.musicEnabled
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
                text = "SETTINGS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Settings items
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingToggleItem(
                icon = Icons.Default.VolumeUp,
                title = "Sound",
                subtitle = null,
                enabled = settings.soundEnabled,
                onToggle = { updateSettings(settings.copy(soundEnabled = !settings.soundEnabled)) }
            )

            SettingToggleItem(
                icon = Icons.Default.MusicNote,
                title = "Music",
                subtitle = null,
                enabled = settings.musicEnabled,
                onToggle = { updateSettings(settings.copy(musicEnabled = !settings.musicEnabled)) }
            )

            SettingToggleItem(
                icon = Icons.Default.Smartphone,
                title = "Vibration",
                subtitle = null,
                enabled = settings.vibrationEnabled,
                onToggle = { updateSettings(settings.copy(vibrationEnabled = !settings.vibrationEnabled)) }
            )

            SettingToggleItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                subtitle = null,
                enabled = settings.notificationsEnabled,
                onToggle = { updateSettings(settings.copy(notificationsEnabled = !settings.notificationsEnabled)) }
            )

            SettingToggleItem(
                icon = Icons.Default.Visibility,
                title = "Reduced Motion",
                subtitle = "Disable decorative animations",
                enabled = settings.reducedMotion,
                onToggle = { updateSettings(settings.copy(reducedMotion = !settings.reducedMotion)) }
            )

            SettingNavigationItem(
                icon = Icons.Default.Language,
                title = "Language",
                value = "English >",
                onClick = { Toast.makeText(context, "Language set to English (Default)", Toast.LENGTH_SHORT).show() }
            )

            SettingNavigationItem(
                icon = Icons.Default.Security,
                title = "Privacy Policy",
                value = ">",
                onClick = { Toast.makeText(context, "Privacy Policy: All game statistics are stored locally on your device.", Toast.LENGTH_SHORT).show() }
            )

            SettingNavigationItem(
                icon = Icons.Default.Description,
                title = "Terms & Conditions",
                value = ">",
                onClick = { Toast.makeText(context, "Terms & Conditions: Fair play casual puzzle mobile entertainment.", Toast.LENGTH_SHORT).show() }
            )

            SettingNavigationItem(
                icon = Icons.Default.Info,
                title = "About Us",
                value = ">",
                onClick = onNavigateAbout
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // UNITY ADS DIAGNOSTICS & TEST PANEL
        val sdkStatus by UnityAdsManager.sdkStatus.collectAsState()
        val isAdReady by UnityAdsManager.isAdReady.collectAsState()
        val totalRewards by UnityAdsManager.totalDeliveredRewards.collectAsState()
        val lastStatusMsg by UnityAdsManager.lastStatusMessage.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.9f))
                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Videocam,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "UNITY ADS (REWARDED)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                val statusColor = when (sdkStatus) {
                    UnityAdsManager.SdkStatus.INITIALIZED -> Color(0xFF10B981)
                    UnityAdsManager.SdkStatus.INITIALIZING -> Color(0xFFFACC15)
                    UnityAdsManager.SdkStatus.FAILED -> Color(0xFFEF4444)
                    UnityAdsManager.SdkStatus.NOT_INITIALIZED -> Color(0xFF94A3B8)
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.2f))
                        .border(1.dp, statusColor, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = sdkStatus.name,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Game ID:", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    Text(UnityAdsManager.GAME_ID, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Placement:", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    Text(UnityAdsManager.REWARDED_PLACEMENT_ID, color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Ad Ready:", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    Text(
                        if (isAdReady) "READY" else "LOADING...",
                        color = if (isAdReady) Color(0xFF34D399) else Color(0xFFFBBF24),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mode:", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    Text("Test Mode (Active)", color = Color(0xFFFACC15), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Rewards Delivered:", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    Text("$totalRewards verified", color = Color(0xFFBAE6FD), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Status: $lastStatusMsg",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button3D(
                    text = "WATCH TEST AD (+50)",
                    onClick = {
                        val act = context as? Activity
                        if (act != null) {
                            UnityAdsManager.showRewardedAd(
                                activity = act,
                                rewardAmount = 50,
                                onRewardEarned = { amount ->
                                    audioService.playDiamondChime()
                                    gameStorage.addDiamonds(amount)
                                    Toast.makeText(context, "Test reward verified: +$amount Diamonds!", Toast.LENGTH_SHORT).show()
                                },
                                onAdDismissed = {
                                    Toast.makeText(context, "Ad skipped. No reward granted.", Toast.LENGTH_SHORT).show()
                                },
                                onAdFailed = { err ->
                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    variant = Button3DVariant.GREEN,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF2563EB))
                        .clickable {
                            UnityAdsManager.loadRewardedAd(context)
                            Toast.makeText(context, "Reloading ad placement...", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reload",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Reset to First Launch button
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF881337).copy(alpha = 0.6f))
                .border(1.dp, Color(0xFFF43F5E).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .clickable(onClick = onResetFirstLaunch)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFFDA4AF), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Reset to First Launch (Test Flow)",
                color = Color(0xFFFDA4AF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SettingToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    enabled: Boolean,
    onToggle: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF0F172A).copy(alpha = 0.85f))
            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f), shape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (subtitle != null) {
                    Text(subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                }
            }
        }

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (enabled) Color(0xFF10B981) else Color(0xFF334155))
                .clickable(onClick = onToggle)
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (enabled) "ON" else "OFF",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun SettingNavigationItem(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF0F172A).copy(alpha = 0.85f))
            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Text(value, color = Color(0xFFBAE6FD), fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
}
