package com.example.model

enum class ScreenType {
    SPLASH,
    WELCOME_POPUP,
    TUTORIAL,
    LOADING,
    HOME,
    TROPHY,
    JOURNEY,
    GAME_LEVEL,
    TREASURE_BOX,
    SHOP,
    COLLECTION,
    DAILY_REWARDS,
    SETTINGS,
    ABOUT,
    CLASSIC_INTRO,
    DRAWING_INTRO,
    DRAWING_GAME
}

enum class GameMode {
    JOURNEY,
    CLASSIC,
    DRAWING
}

data class PlayerStats(
    val hasSeenWelcome: Boolean = false,
    val hasCompletedTutorial: Boolean = false,
    val totalDiamonds: Int = 1649,
    val currentStreak: Int = 1,
    val lastPlayedDate: String = "",
    val completedJourneyLevels: Int = 0,
    val lastUnlockedJourneyLevel: Int = 1,
    val bestClassicScore: Int = 0,
    val trophies: Int = 25,
    val dailyRewardLastClaimedDate: String = "",
    val dailyRewardDay: Int = 1
)

data class GameSettings(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val reducedMotion: Boolean = false,
    val language: String = "English"
)

enum class BoardCellVisualState {
    EMPTY,
    OCCUPIED,
    PREVIEW_VALID,
    PREVIEW_INVALID,
    CLEARING,
    HIGHLIGHT
}
