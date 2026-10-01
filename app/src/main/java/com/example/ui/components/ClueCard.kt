package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
                    .border(
                        width = 1.dp,
                        color = if (isUnlocked) TrophyGold else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .testTag("hint_card_$index"),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 2.dp else 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge Number
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                if (isUnlocked) TrophyGold else MaterialTheme.colorScheme.outline
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isUnlocked) Color.Black else TextMuted,
                            fontWeight = FontWeight.ExtraBold
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
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isUnlocked) TrophyGoldBright else TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isUnlocked) PitchGreenDark.copy(alpha = 0.3f) else Color.Transparent,
                                border = if (isUnlocked) androidx.compose.foundation.BorderStroke(1.dp, PitchGreen.copy(alpha = 0.5f)) else null
                            ) {
                                Text(
                                    text = "$hintPoint نقاط",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isUnlocked) PitchGreenBright else TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        if (isUnlocked) {
                            Text(
                                text = hintText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 20.sp
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "مقفل",
                                    tint = TextMuted.copy(alpha = 0.7f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "مغلق • يفتح بخصم من النقاط",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action button to reveal next clue
        if (unlockedCount < hints.size) {
            val nextPoints = cluePoints.getOrElse(unlockedCount) { 2 }
            Button(
                onClick = onRevealNextHint,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("reveal_next_hint_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = TrophyGoldBright
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, TrophyGold.copy(alpha = 0.6f)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = TrophyGoldBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "كشف التلميح التالي ($nextPoints نقاط)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
