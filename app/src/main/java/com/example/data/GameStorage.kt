package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.GameSettings
import com.example.model.PlayerStats
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("color_block_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_HAS_SEEN_WELCOME = "has_seen_welcome"
        private const val KEY_HAS_COMPLETED_TUTORIAL = "has_completed_tutorial"
        private const val KEY_TOTAL_DIAMONDS = "total_diamonds"
        private const val KEY_CURRENT_STREAK = "current_streak"
        private const val KEY_LAST_PLAYED_DATE = "last_played_date"
        private const val KEY_COMPLETED_JOURNEY_LEVELS = "completed_journey_levels"
        private const val KEY_LAST_UNLOCKED_JOURNEY_LEVEL = "last_unlocked_journey_level"
        private const val KEY_BEST_CLASSIC_SCORE = "best_classic_score"
        private const val KEY_TROPHIES = "trophies"
        private const val KEY_DAILY_REWARD_DAY = "daily_reward_day"
        private const val KEY_DAILY_LAST_CLAIMED = "daily_last_claimed"

        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_MUSIC_ENABLED = "music_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_REDUCED_MOTION = "reduced_motion"
        private const val KEY_LANGUAGE = "language"
    }

    fun getStats(): PlayerStats {
        return PlayerStats(
            hasSeenWelcome = prefs.getBoolean(KEY_HAS_SEEN_WELCOME, false),
            hasCompletedTutorial = prefs.getBoolean(KEY_HAS_COMPLETED_TUTORIAL, false),
            totalDiamonds = prefs.getInt(KEY_TOTAL_DIAMONDS, 1649),
            currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 1),
            lastPlayedDate = prefs.getString(KEY_LAST_PLAYED_DATE, "") ?: "",
            completedJourneyLevels = prefs.getInt(KEY_COMPLETED_JOURNEY_LEVELS, 0),
            lastUnlockedJourneyLevel = prefs.getInt(KEY_LAST_UNLOCKED_JOURNEY_LEVEL, 1),
            bestClassicScore = prefs.getInt(KEY_BEST_CLASSIC_SCORE, 0),
            trophies = prefs.getInt(KEY_TROPHIES, 25),
            dailyRewardLastClaimedDate = prefs.getString(KEY_DAILY_LAST_CLAIMED, "") ?: "",
            dailyRewardDay = prefs.getInt(KEY_DAILY_REWARD_DAY, 1)
        )
    }

    fun saveStats(stats: PlayerStats) {
        prefs.edit()
            .putBoolean(KEY_HAS_SEEN_WELCOME, stats.hasSeenWelcome)
            .putBoolean(KEY_HAS_COMPLETED_TUTORIAL, stats.hasCompletedTutorial)
            .putInt(KEY_TOTAL_DIAMONDS, stats.totalDiamonds)
            .putInt(KEY_CURRENT_STREAK, stats.currentStreak)
            .putString(KEY_LAST_PLAYED_DATE, stats.lastPlayedDate)
            .putInt(KEY_COMPLETED_JOURNEY_LEVELS, stats.completedJourneyLevels)
            .putInt(KEY_LAST_UNLOCKED_JOURNEY_LEVEL, stats.lastUnlockedJourneyLevel)
            .putInt(KEY_BEST_CLASSIC_SCORE, stats.bestClassicScore)
            .putInt(KEY_TROPHIES, stats.trophies)
            .putString(KEY_DAILY_LAST_CLAIMED, stats.dailyRewardLastClaimedDate)
            .putInt(KEY_DAILY_REWARD_DAY, stats.dailyRewardDay)
            .apply()
    }

    fun getSettings(): GameSettings {
        return GameSettings(
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
            musicEnabled = prefs.getBoolean(KEY_MUSIC_ENABLED, true),
            vibrationEnabled = prefs.getBoolean(KEY_VIBRATION_ENABLED, true),
            notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true),
            reducedMotion = prefs.getBoolean(KEY_REDUCED_MOTION, false),
            language = prefs.getString(KEY_LANGUAGE, "English") ?: "English"
        )
    }

    fun saveSettings(settings: GameSettings) {
        prefs.edit()
            .putBoolean(KEY_SOUND_ENABLED, settings.soundEnabled)
            .putBoolean(KEY_MUSIC_ENABLED, settings.musicEnabled)
            .putBoolean(KEY_VIBRATION_ENABLED, settings.vibrationEnabled)
            .putBoolean(KEY_NOTIFICATIONS_ENABLED, settings.notificationsEnabled)
            .putBoolean(KEY_REDUCED_MOTION, settings.reducedMotion)
            .putString(KEY_LANGUAGE, settings.language)
            .apply()
    }

    fun addDiamonds(amount: Int): PlayerStats {
        val current = getStats()
        val updated = current.copy(totalDiamonds = current.totalDiamonds + amount)
        saveStats(updated)
        return updated
    }

    fun spendDiamonds(amount: Int): Boolean {
        val current = getStats()
        if (current.totalDiamonds < amount) return false
        saveStats(current.copy(totalDiamonds = current.totalDiamonds - amount))
        return true
    }

    fun completeLevel(levelNum: Int, earnedDiamonds: Int = 300): PlayerStats {
        val current = getStats()
        val newCompleted = minOf(5000, maxOf(current.completedJourneyLevels, levelNum))
        val newUnlocked = minOf(5000, maxOf(current.lastUnlockedJourneyLevel, levelNum + 1))
        val updated = current.copy(
            completedJourneyLevels = newCompleted,
            lastUnlockedJourneyLevel = newUnlocked,
            totalDiamonds = current.totalDiamonds + earnedDiamonds,
            trophies = current.trophies + 1
        )
        saveStats(updated)
        return updated
    }

    fun updateClassicScore(newScore: Int): PlayerStats {
        val current = getStats()
        if (newScore > current.bestClassicScore) {
            val updated = current.copy(bestClassicScore = newScore)
            saveStats(updated)
            return updated
        }
        return current
    }

    fun recordPlaySession(): PlayerStats {
        val current = getStats()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val updated = if (current.lastPlayedDate != today) {
            current.copy(lastPlayedDate = today, currentStreak = current.currentStreak + 1)
        } else {
            current
        }
        saveStats(updated)
        return updated
    }

    fun resetToFirstLaunch(): PlayerStats {
        prefs.edit().clear().apply()
        return getStats()
    }
}
