package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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

private val LightColorScheme = lightColorScheme(
    primary = TrophyGoldDark,
    onPrimary = Color.White,
    primaryContainer = TrophyGoldBright,
    onPrimaryContainer = ArenaLightTextPrimary,
    secondary = PitchGreen,
    onSecondary = Color.White,
    secondaryContainer = PitchGreenBright.copy(alpha = 0.2f),
    onSecondaryContainer = PitchGreenDark,
    tertiary = NeonCyan,
    onTertiary = Color.White,
    background = ArenaLightBg,
    onBackground = ArenaLightTextPrimary,
    surface = ArenaLightCard,
    onSurface = ArenaLightTextPrimary,
    surfaceVariant = ArenaLightCardHover,
    onSurfaceVariant = ArenaLightTextSecondary,
    outline = ArenaLightBorder,
    error = BuzzerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
