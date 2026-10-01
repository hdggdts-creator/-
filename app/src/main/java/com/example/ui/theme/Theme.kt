package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PitchGreen,
    onPrimary = Color.White,
    primaryContainer = PitchGreenDark,
    onPrimaryContainer = PitchGreenBright,
    secondary = TrophyGold,
    onSecondary = Color.Black,
    secondaryContainer = TrophyGoldDark,
    onSecondaryContainer = TrophyGoldBright,
    tertiary = NeonCyan,
    onTertiary = Color.White,
    background = StadiumDark,
    onBackground = TextPrimaryDark,
    surface = StadiumCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = StadiumCardHover,
    onSurfaceVariant = TextSecondaryDark,
    outline = StadiumBorder,
    error = BuzzerRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PitchGreen,
    onPrimary = Color.White,
    primaryContainer = EmeraldGreenSurface,
    onPrimaryContainer = PitchGreenDark,
    secondary = TrophyGold,
    onSecondary = Color.Black,
    secondaryContainer = TrophyGoldBright.copy(alpha = 0.2f),
    onSecondaryContainer = TrophyGoldDark,
    tertiary = NeonCyan,
    onTertiary = Color.White,
    background = ArenaLightBg,
    onBackground = TextPrimaryLight,
    surface = ArenaLightCard,
    onSurface = TextPrimaryLight,
    surfaceVariant = ArenaLightCardHover,
    onSurfaceVariant = TextSecondaryLight,
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
        typography = AppTypography,
        content = content
    )
}
