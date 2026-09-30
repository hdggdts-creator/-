package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AuctionItemChips(
    targetCount: Int,
    acceptedEntries: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("auction_slots_container")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الأسماء المكتملة (${acceptedEntries.size} / $targetCount)",
                style = MaterialTheme.typography.titleSmall,
                color = TrophyGoldBright,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${acceptedEntries.size * 2} من 10 نقاط",
                style = MaterialTheme.typography.labelMedium,
                color = PitchGreenBright,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Slots grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0 until targetCount) {
                val isFilled = i < acceptedEntries.size
                val playerName = if (isFilled) acceptedEntries[i] else null

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(68.dp)
                        .testTag("auction_slot_$i"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFilled) PitchGreenDark.copy(alpha = 0.8f) else StadiumDark
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isFilled) PitchGreenBright else StadiumBorder
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isFilled) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "صحيح",
                                tint = PitchGreenBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = playerName.orEmpty(),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        } else {
                            Text(
                                text = "${i + 1}",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "متبقي",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
