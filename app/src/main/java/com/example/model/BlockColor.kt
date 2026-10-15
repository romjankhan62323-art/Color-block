package com.example.model

import androidx.compose.ui.graphics.Color

enum class BlockColor(
    val displayName: String,
    val base: Color,
    val highlight: Color,
    val shadow: Color,
    val deepShadow: Color,
    val border: Color
) {
    RED(
        displayName = "Red",
        base = Color(0xFFEF4444),
        highlight = Color(0xFFFCA5A5),
        shadow = Color(0xFFB91C1C),
        deepShadow = Color(0xFF7F1D1D),
        border = Color(0xFFFECACA)
    ),
    ORANGE(
        displayName = "Orange",
        base = Color(0xFFF97316),
        highlight = Color(0xFFFDBA74),
        shadow = Color(0xFFC2410C),
        deepShadow = Color(0xFF7C2D12),
        border = Color(0xFFFED7AA)
    ),
    YELLOW(
        displayName = "Yellow",
        base = Color(0xFFFACC15),
        highlight = Color(0xFFFEF08A),
        shadow = Color(0xFFCA8A04),
        deepShadow = Color(0xFF854D0E),
        border = Color(0xFFFEF9C3)
    ),
    GREEN(
        displayName = "Green",
        base = Color(0xFF22C55E),
        highlight = Color(0xFF86EFAC),
        shadow = Color(0xFF15803D),
        deepShadow = Color(0xFF14532D),
        border = Color(0xFFBBF7D0)
    ),
    BLUE(
        displayName = "Blue",
        base = Color(0xFF3B82F6),
        highlight = Color(0xFF93C5FD),
        shadow = Color(0xFF1D4ED8),
        deepShadow = Color(0xFF1E3A8A),
        border = Color(0xFFBFDBFE)
    ),
    CYAN(
        displayName = "Cyan",
        base = Color(0xFF06B6D4),
        highlight = Color(0xFF67E8F9),
        shadow = Color(0xFF0E7490),
        deepShadow = Color(0xFF164E63),
        border = Color(0xFFCFFAFE)
    ),
    PURPLE(
        displayName = "Purple",
        base = Color(0xFFA855F7),
        highlight = Color(0xFFD8B4FE),
        shadow = Color(0xFF7E22CE),
        deepShadow = Color(0xFF581C87),
        border = Color(0xFFE9D5FF)
    ),
    PINK(
        displayName = "Pink",
        base = Color(0xFFF472B6),
        highlight = Color(0xFFFBCFE8),
        shadow = Color(0xFFBE185D),
        deepShadow = Color(0xFF831843),
        border = Color(0xFFFCE7F3)
    );

    companion object {
        val ALL = entries.toTypedArray()
    }
}
