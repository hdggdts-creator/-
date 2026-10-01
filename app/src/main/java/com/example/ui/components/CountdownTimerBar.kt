package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * TV Show Sports Countdown Timer Bar:
 * Displays remaining time (90s = 1.5 min), smoothly decreases,
 * and shifts color to Amber and Alert Red in the final seconds
 * to create authentic television excitement and tension.
 */
@Composable
fun CountdownTimerBar(
    remainingSeconds: Int,
    totalSeconds: Int,
    modifier: Modifier = Modifier
) {
    val progress = (remainingSeconds.toFloat() / totalSeconds.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
    val isDanger = remainingSeconds <= 20
    val isCritical = remainingSeconds <= 10

    val targetColor = when {
        isCritical -> BuzzerRed
        isDanger -> TrophyGoldBright
        else -> PitchGreen
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 300),
        label = "timer_color_animation"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 400, easing = LinearEasing),
        label = "timer_progress_animation"
    )

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isCritical) 1.5.dp else 1.dp,
                color = if (isDanger) animatedColor else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(10.dp)
            )
            .testTag("countdown_timer_bar"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 7.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isCritical) "🚨" else if (isDanger) "⏳" else "⏱️",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCritical) "الوقت ينفد بسرعة!" else if (isDanger) "اقتربت نهاية الوقت!" else "الوقت المتبقي للجولة:",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDanger) animatedColor else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isDanger) FontWeight.ExtraBold else FontWeight.Medium
                    )
                }

                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.labelMedium,
                    color = animatedColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = animatedColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
