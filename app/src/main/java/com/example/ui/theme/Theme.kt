package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TrophyGold,
    onPrimary = StadiumDark,
    primaryContainer = TrophyGoldDark,
    onPrimaryContainer = TrophyGoldBright,
    secondary = PitchGreen,
    onSecondary = Color.White,
    secondaryContainer = PitchGreenDark,
    onSecondaryContainer = PitchGreenBright,
    tertiary = NeonCyan,
    onTertiary = StadiumDark,
    background = StadiumDark,
    onBackground = TextPrimary,
    surface = StadiumCard,
    onSurface = TextPrimary,
    surfaceVariant = StadiumCardHover,
    onSurfaceVariant = TextSecondary,
    outline = StadiumBorder,
    error = BuzzerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Stadium Game Show is best in dark mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
