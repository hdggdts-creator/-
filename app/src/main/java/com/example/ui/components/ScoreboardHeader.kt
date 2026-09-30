package com.example.ui.components

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameRound
import com.example.ui.theme.*

@Composable
fun ScoreboardHeader(
    currentRound: GameRound,
    currentScore: Int,
    maxScore: Int = 50,
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
            containerColor = StadiumCard
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(StadiumBorder)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(StadiumDark)
                    .testTag("header_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "الرجوع",
                    tint = TextPrimary,
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
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Score Pill & Audio toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Score Box
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(StadiumDark)
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
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(StadiumDark)
                        .testTag("header_sound_button")
                ) {
                    Icon(
                        imageVector = if (isSoundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
                        contentDescription = "كتم/تشغيل الصوت",
                        tint = if (isSoundEnabled) PitchGreenBright else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
