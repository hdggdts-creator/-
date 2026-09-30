package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class HostMood(val emoji: String, val badgeColor: Color) {
    WELCOME("🎙️", TrophyGold),
    EXCITED("🔥", TrophyGoldBright),
    CELEBRATING("🎉", PitchGreenBright),
    SURPRISED("😱", NeonCyanBright),
    THINKING("🤔", TrophyGold),
    DRAMATIC("⏳", BuzzerRed)
}

@Composable
fun PresenterCard(
    speechText: String,
    hostMood: HostMood = HostMood.WELCOME,
    isSpeaking: Boolean = false,
    onSpeakClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("presenter_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = StadiumCard
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(TrophyGoldDark, StadiumBorder, PitchGreenDark)
            )
        )
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Host Avatar with animated mood
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(hostMood.badgeColor.copy(alpha = 0.35f), StadiumDark)
                        )
                    )
                    .border(2.dp, hostMood.badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = hostMood.emoji,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Speech Bubble
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "مقدم التحدي 🎙️",
                        style = MaterialTheme.typography.labelSmall,
                        color = TrophyGoldBright,
                        fontWeight = FontWeight.Bold
                    )

                    if (onSpeakClick != null) {
                        IconButton(
                            onClick = onSpeakClick,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("speak_button")
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                                contentDescription = "قراءة صوتية",
                                tint = if (isSpeaking) PitchGreenBright else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                AnimatedContent(
                    targetState = speechText,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "PresenterSpeech"
                ) { targetText ->
                    Text(
                        text = targetText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
