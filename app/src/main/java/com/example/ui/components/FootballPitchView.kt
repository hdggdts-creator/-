package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PitchPlayer
import com.example.ui.theme.*

@Composable
fun FootballPitchView(
    players: List<PitchPlayer>,
    revealedMysteryName: String? = null,
    onMysteryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Pulse animation for mystery player
    val infiniteTransition = rememberInfiniteTransition(label = "MysteryPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, StadiumBorder, RoundedCornerShape(16.dp))
            .testTag("football_pitch_view")
    ) {
        val totalWidth = maxWidth
        val totalHeight = maxHeight

        // 1. Draw Football Field on Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val strokeW = 2.dp.toPx()
            val lineColor = Color(0x75FFFFFF)

            // Field Background with alternating grass stripes
            val stripeCount = 6
            val stripeHeight = h / stripeCount
            for (i in 0 until stripeCount) {
                val stripeColor = if (i % 2 == 0) PitchGrassDark else PitchGrassLight
                drawRect(
                    color = stripeColor,
                    topLeft = Offset(0f, i * stripeHeight),
                    size = Size(w, stripeHeight)
                )
            }

            // Outer pitch border
            val padding = 12.dp.toPx()
            val fieldRect = Size(w - padding * 2, h - padding * 2)
            drawRect(
                color = lineColor,
                topLeft = Offset(padding, padding),
                size = fieldRect,
                style = Stroke(width = strokeW)
            )

            // Halfway line
            val midY = h / 2
            drawLine(
                color = lineColor,
                start = Offset(padding, midY),
                end = Offset(w - padding, midY),
                strokeWidth = strokeW
            )

            // Center circle
            val centerRadius = 36.dp.toPx()
            drawCircle(
                color = lineColor,
                radius = centerRadius,
                center = Offset(w / 2, midY),
                style = Stroke(width = strokeW)
            )
            drawCircle(
                color = lineColor,
                radius = 3.dp.toPx(),
                center = Offset(w / 2, midY)
            )

            // Bottom Penalty Area (Goalkeeper side)
            val penaltyWidth = w * 0.55f
            val penaltyHeight = h * 0.16f
            val penaltyX = (w - penaltyWidth) / 2
            drawRect(
                color = lineColor,
                topLeft = Offset(penaltyX, h - padding - penaltyHeight),
                size = Size(penaltyWidth, penaltyHeight),
                style = Stroke(width = strokeW)
            )

            // Top Penalty Area (Attacking side)
            drawRect(
                color = lineColor,
                topLeft = Offset(penaltyX, padding),
                size = Size(penaltyWidth, penaltyHeight),
                style = Stroke(width = strokeW)
            )
        }

        // 2. Position Players over the tactical pitch
        players.forEach { player ->
            val playerX = (totalWidth.value * player.xPercent).dp - 32.dp
            // yPercent: 0.0 is GK bottom, 1.0 is Striker top
            val playerY = (totalHeight.value * (1f - player.yPercent)).dp - 30.dp

            Box(
                modifier = Modifier
                    .offset(x = playerX.coerceIn(0.dp, totalWidth - 64.dp), y = playerY.coerceIn(0.dp, totalHeight - 64.dp))
                    .width(64.dp),
                contentAlignment = Alignment.Center
            ) {
                if (player.isMystery) {
                    val displayName = revealedMysteryName ?: "اللاعب المجهول"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onMysteryClick() }
                            .testTag("mystery_player_node")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .scale(if (revealedMysteryName == null) pulseScale else 1.0f)
                                .clip(CircleShape)
                                .background(if (revealedMysteryName == null) TrophyGold else PitchGreen)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (revealedMysteryName == null) "❓" else "⭐",
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TrophyGoldDark.copy(alpha = 0.9f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = TrophyGoldBright,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(StadiumCardHover)
                                .border(1.5.dp, TextPrimary.copy(alpha = 0.8f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = player.number.ifEmpty { player.position },
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(1.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(StadiumDark.copy(alpha = 0.85f))
                                .padding(horizontal = 3.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = player.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                fontSize = 9.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
