package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
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
import com.example.ui.theme.*

@Composable
fun ClueCard(
    hints: List<String>,
    unlockedCount: Int,
    onRevealNextHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cluePoints = listOf(10, 7, 5, 2)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("clues_container"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        hints.forEachIndexed { index, hintText ->
            val isUnlocked = index < unlockedCount
            val hintPoint = cluePoints.getOrElse(index) { 2 }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hint_card_$index"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) StadiumCardHover else StadiumCard.copy(alpha = 0.6f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isUnlocked) TrophyGoldDark else StadiumBorder
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isUnlocked) TrophyGold else StadiumBorder),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.titleSmall,
                            color = if (isUnlocked) StadiumDark else TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "التلميح رقم ${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isUnlocked) TrophyGoldBright else TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$hintPoint نقاط",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isUnlocked) PitchGreenBright else TextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        if (isUnlocked) {
                            Text(
                                text = hintText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                lineHeight = 21.sp
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "مغلق",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تلميح مغلق (سيكلف خصم في النقاط)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        if (unlockedCount < hints.size) {
            val nextPoints = cluePoints.getOrElse(unlockedCount) { 2 }
            OutlinedButton(
                onClick = onRevealNextHint,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reveal_hint_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TrophyGoldBright
                ),
                border = ButtonDefaults.outlinedButtonBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(TrophyGoldDark)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "طلب التلميح التالي رقم ${unlockedCount + 1} (تصبح الجولة بـ $nextPoints نقاط)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
