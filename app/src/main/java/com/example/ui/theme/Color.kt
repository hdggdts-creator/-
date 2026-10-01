package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================================================
// Classic Stadium Dark Palette (Clean, Flat, Familiar)
// ============================================================================
val StadiumDark = Color(0xFF0F172A)          // Classic Slate Navy (900)
val StadiumCard = Color(0xFF1E293B)          // Solid Slate Card (800)
val StadiumCardHover = Color(0xFF334155)     // Slate Card Hover (700)
val StadiumBorder = Color(0xFF334155)        // Clean Flat Border

// ============================================================================
// Classic Arena Light Palette (Clean, Flat, Familiar)
// ============================================================================
val ArenaLightBg = Color(0xFFF8FAFC)         // Slate 50 Light Background
val ArenaLightCard = Color(0xFFFFFFFF)       // Crisp White Card
val ArenaLightCardHover = Color(0xFFF1F5F9)  // Slate 100 Hover
val ArenaLightBorder = Color(0xFFCBD5E1)     // Crisp Clean Border
val ArenaLightTextPrimary = Color(0xFF0F172A)
val ArenaLightTextSecondary = Color(0xFF475569)
val ArenaLightTextMuted = Color(0xFF64748B)

// Shared Solid Football Accents
val PitchGreen = Color(0xFF10B981)           // Classic Emerald Pitch Green
val PitchGreenBright = Color(0xFF34D399)     // Light Green
val PitchGreenDark = Color(0xFF047857)       // Deep Turf Green

val TrophyGold = Color(0xFFF59E0B)           // Classic Amber Gold
val TrophyGoldBright = Color(0xFFFBBF24)     // Bright Amber
val TrophyGoldDark = Color(0xFFB45309)       // Deep Bronze Amber
val TrophyGoldRich = TrophyGold

val NeonCyan = Color(0xFF0284C7)             // Sky Blue
val NeonCyanBright = Color(0xFF38BDF8)

val BuzzerRed = Color(0xFFEF4444)            // Classic Red
val BuzzerRedDark = Color(0xFFB91C1C)

// Multiplayer Turn Colors (Flat & Distinct)
val Player1Color = Color(0xFF0284C7)         // Sky Blue
val Player2Color = Color(0xFFE11D48)         // Rose Red
val Player1Blue = Player1Color
val Player2Red = Player2Color
val Player1BlueLight = Color(0xFF38BDF8)
val Player2RedLight = Color(0xFFFB7185)
val Player1Glow = Player1Color.copy(alpha = 0.15f)
val Player2Glow = Player2Color.copy(alpha = 0.15f)

// Flat Gradients (Clean single-tone brushes for compatibility)
val Player1Gradient = Brush.horizontalGradient(listOf(Player1Color, Player1Color))
val Player2Gradient = Brush.horizontalGradient(listOf(Player2Color, Player2Color))
val GoldGradient = Brush.horizontalGradient(listOf(TrophyGold, TrophyGold))
val EmeraldGradient = Brush.horizontalGradient(listOf(PitchGreen, PitchGreen))

// Typography Text Colors
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val TextPrimaryDark = TextPrimary
val TextSecondaryDark = TextSecondary
val TextMutedDark = TextMuted

val TextPrimaryLight = ArenaLightTextPrimary
val TextSecondaryLight = ArenaLightTextSecondary
val TextMutedLight = ArenaLightTextMuted

// Tactical Pitch Drawing
val PitchGrassLight = Color(0xFF154B2A)
val PitchGrassDark = Color(0xFF0D331C)
val PitchLine = Color(0x75FFFFFF)

// Compatibility aliases
val StadiumNavyDark = StadiumDark
val StadiumNavyCard = StadiumCard
val StadiumNavyCardElevated = StadiumCardHover
val StadiumNavyBorder = StadiumBorder
val ArenaOffWhiteBg = ArenaLightBg
val ArenaCardWhite = ArenaLightCard
val ArenaCardElevated = ArenaLightCardHover
val ArenaBorderLight = ArenaLightBorder
val EmeraldGreen = PitchGreen
val EmeraldGreenLight = PitchGreenBright
val EmeraldGreenDark = PitchGreenDark
val EmeraldGreenSurface = Color(0xFFECFDF5)
