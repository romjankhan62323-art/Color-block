package com.example.model

enum class LevelDifficulty(val label: String) {
    BEGINNER("Beginner"),
    EASY_MEDIUM("Easy-Medium"),
    MEDIUM("Medium"),
    HARD("Hard"),
    VERY_HARD("Very Hard"),
    EXPERT("Expert"),
    MASTER("Master"),
    EXTREME("Extreme"),
    FINAL_CHALLENGE("Final Challenge")
}

data class LevelConfig(
    val levelNumber: Int,
    val targetLines: Int,
    val difficulty: LevelDifficulty,
    val rewardDiamonds: Int,
    val isFinalLevel: Boolean = false,
    val title: String = "Level $levelNumber"
)

object LevelConfigManager {
    const val MAX_LEVEL = 5000

    fun getLevelConfig(level: Int): LevelConfig {
        val clampedLevel = level.coerceIn(1, MAX_LEVEL)
        val isFinal = clampedLevel == MAX_LEVEL

        val difficulty = when (clampedLevel) {
            in 1..50 -> LevelDifficulty.BEGINNER
            in 51..200 -> LevelDifficulty.EASY_MEDIUM
            in 201..500 -> LevelDifficulty.MEDIUM
            in 501..1000 -> LevelDifficulty.HARD
            in 1001..2000 -> LevelDifficulty.VERY_HARD
            in 2001..3000 -> LevelDifficulty.EXPERT
            in 3001..4000 -> LevelDifficulty.MASTER
            in 4001..4999 -> LevelDifficulty.EXTREME
            else -> LevelDifficulty.FINAL_CHALLENGE
        }

        val targetLines = when (clampedLevel) {
            1 -> 10
            2 -> 12
            3 -> 15
            4 -> 18
            5 -> 22
            6 -> 26
            7 -> 30
            8 -> 35
            9 -> 40
            10 -> 45
            in 11..50 -> 45 + (clampedLevel - 10)
            in 51..200 -> 85 + ((clampedLevel - 50) * 0.4).toInt()
            in 201..500 -> 145 + ((clampedLevel - 200) * 0.3).toInt()
            in 501..1000 -> 235 + ((clampedLevel - 500) * 0.2).toInt()
            in 1001..2000 -> 335 + ((clampedLevel - 1000) * 0.15).toInt()
            in 2001..3000 -> 485 + ((clampedLevel - 2000) * 0.1).toInt()
            in 3001..4000 -> 585 + ((clampedLevel - 3000) * 0.1).toInt()
            in 4001..4999 -> 685 + ((clampedLevel - 4000) * 0.1).toInt()
            else -> 800 // Level 5000 Final Challenge
        }

        val reward = when {
            isFinal -> 5000
            clampedLevel % 100 == 0 -> 1000
            clampedLevel % 50 == 0 -> 500
            clampedLevel % 10 == 0 -> 250
            else -> 100
        }

        val title = when {
            isFinal -> "Level 5000: Final Challenge"
            clampedLevel % 50 == 0 -> "Milestone Level $clampedLevel"
            else -> "Level $clampedLevel"
        }

        return LevelConfig(
            levelNumber = clampedLevel,
            targetLines = targetLines,
            difficulty = difficulty,
            rewardDiamonds = reward,
            isFinalLevel = isFinal,
            title = title
        )
    }

    /**
     * Exact integer formula:
     * progressPercent = min(100, floor((completedLines * 100) / targetLines))
     */
    fun calculateProgressPercent(completedLines: Int, targetLines: Int): Int {
        if (targetLines <= 0) return 100
        val percent = (completedLines * 100L / targetLines).toInt()
        return percent.coerceIn(0, 100)
    }

    /**
     * Line clear message based on line count
     */
    fun getLineClearMessage(clearedLinesCount: Int): String? {
        return when (clearedLinesCount) {
            1 -> "LINE CLEAR!"
            2 -> "DOUBLE CLEAR!"
            3 -> "TRIPLE CLEAR!"
            4 -> "AMAZING CLEAR!"
            in 5..Int.MAX_VALUE -> "INCREDIBLE COMBO!"
            else -> null
        }
    }

    /**
     * Milestone message triggered when crossing 10%, 20%, ..., 100%
     */
    fun getMilestoneMessage(progressPercent: Int): String? {
        return when (progressPercent) {
            in 100..Int.MAX_VALUE -> "LEVEL COMPLETE! WELL DONE!"
            in 90..99 -> "90% COMPLETE! ONE MORE PUSH!"
            in 80..89 -> "80% COMPLETE! ALMOST THERE!"
            in 70..79 -> "70% COMPLETE! GREAT WORK!"
            in 60..69 -> "60% COMPLETE! KEEP GOING!"
            in 50..59 -> "HALFWAY THERE!"
            in 40..49 -> "40% COMPLETE!"
            in 30..39 -> "30% COMPLETE!"
            in 20..29 -> "20% COMPLETE!"
            in 10..19 -> "10% COMPLETE!"
            else -> null
        }
    }
}
