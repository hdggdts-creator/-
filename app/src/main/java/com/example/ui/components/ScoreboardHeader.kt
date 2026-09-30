package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameRound
import com.example.ui.theme.*

val Player1Color = Color(0xFF38BDF8) // Electric Blue / Cyan
val Player2Color = Color(0xFFF43F5E) // Crimson Coral / Red

@Composable
fun ScoreboardHeader(
    currentRound: GameRound,
    currentScore: Int,
    maxScore: Int = 50,
    isTwoPlayerMode: Boolean = false,
    activePlayerNumber: Int = 1,
    player1Score: Int = 0,
    player2Score: Int = 0,
    isSoundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("scoreboard_header"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(MaterialTheme.colorScheme.outline)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Main Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("header_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Round Badge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (currentRound == GameRound.RESULTS) "نهاية التحدي 🏁" else "الجولة ${currentRound.roundNumber} من 5",
                        style = MaterialTheme.typography.labelSmall,
                        color = TrophyGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentRound.titleAr,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Sound toggle and solo score (if not 2-player mode)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isTwoPlayerMode) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, TrophyGoldDark, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🏆", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$currentScore",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TrophyGoldBright,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "/$maxScore",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    IconButton(
                        onClick = onToggleSound,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("header_sound_button")
                    ) {
                        Icon(
                            imageVector = if (isSoundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
                            contentDescription = "كتم/تشغيل الصوت",
                            tint = if (isSoundEnabled) PitchGreenBright else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Two-Player Turn & Live Dual Scores Banner
            if (isTwoPlayerMode && currentRound != GameRound.RESULTS) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Player 1 Pill
                        val isP1Active = activePlayerNumber == 1
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isP1Active) Player1Color.copy(alpha = 0.2f) else Color.Transparent)
                                .border(
                                    width = if (isP1Active) 2.dp else 1.dp,
                                    color = if (isP1Active) Player1Color else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔵", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "اللاعب 1: $player1Score",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isP1Active) Player1Color else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isP1Active) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        // Active Turn Banner
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isP1Active) Player1Color else Player2Color
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isP1Active) "دور اللاعب الأول 🔵" else "دور اللاعب الثاني 🔴",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        // Player 2 Pill
                        val isP2Active = activePlayerNumber == 2
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isP2Active) Player2Color.copy(alpha = 0.2f) else Color.Transparent)
                                .border(
                                    width = if (isP2Active) 2.dp else 1.dp,
                                    color = if (isP2Active) Player2Color else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔴", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "اللاعب 2: $player2Score",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isP2Active) Player2Color else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isP2Active) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
