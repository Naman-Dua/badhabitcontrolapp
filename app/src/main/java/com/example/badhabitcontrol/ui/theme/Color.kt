package com.example.badhabitcontrol.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val CalmBackground = Color(0xFF101216)
val CalmSurface = Color(0xFF1A1D24)
val CalmSurfaceElevated = Color(0xFF242833)
val CalmBorder = Color(0xFF2E3340)

val TextPrimary = Color(0xFFE6E9EE)
val TextSecondary = Color(0xFF8D95A5)
val TextMuted = Color(0xFF5C6475)

val StatusClean = Color(0xFF3EB489)
val StatusUrgeManaged = Color(0xFFE5A93C)
val StatusRelapse = Color(0xFFD95C5C)
val IndicatorToday = Color(0xFF5A88C5)

val CalmDarkColorScheme = darkColorScheme(
    primary = TextPrimary,
    onPrimary = CalmBackground,
    secondary = TextSecondary,
    onSecondary = TextPrimary,
    background = CalmBackground,
    onBackground = TextPrimary,
    surface = CalmSurface,
    onSurface = TextPrimary,
    surfaceVariant = CalmSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = CalmBorder
)
