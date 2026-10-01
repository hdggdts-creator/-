package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Standard click modifier with natural Android ripple feedback.
 * Replaces bouncy scale micro-interactions with flat, clean, responsive click.
 */
fun Modifier.bouncyClickable(
    pressedScale: Float = 1.0f,
    onClick: () -> Unit
): Modifier = this.clickable(onClick = onClick)

/**
 * Clean turn indicator modifier (Flat, stable, no distracting scale pulse).
 */
fun Modifier.turnPulseEffect(
    enabled: Boolean = true,
    minScale: Float = 1.0f,
    maxScale: Float = 1.0f
): Modifier = this

/**
 * Clean Flat Card (replaces Glassmorphism / LuxuryGlassCard).
 * Familiar 12.dp rounded corners, flat solid background, and crisp 1.dp border.
 */
@Composable
fun LuxuryGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    borderColor: Color = MaterialTheme.colorScheme.outline,
    glowColor: Color = Color.Transparent,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 2.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.border(width = 1.dp, color = borderColor, shape = shape),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

/**
 * Clean Flat Button (replaces complex gradient buttons).
 * Standard height, solid sports accent color, 10.dp corner radius, clean flat style.
 */
@Composable
fun LuxuryGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconEmoji: String? = null,
    gradient: Brush = EmeraldGradient,
    textColor: Color = Color.White,
    enabled: Boolean = true,
    testTag: String = "flat_action_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .testTag(testTag)
            .height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PitchGreen,
            contentColor = textColor,
            disabledContainerColor = PitchGreenDark.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 1.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconEmoji != null) {
                Text(text = iconEmoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            )
        }
    }
}

/**
 * Clean Flat Telegram Watermark footer for t.me/Mos_mohh.
 * Familiar, practical, and clear.
 */
@Composable
fun ElegantBrandWatermark(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val openTelegram = {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Mos_mohh"))
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .clickable { openTelegram() }
                .testTag("footer_author_credits"),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0284C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✈️", fontSize = 9.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "حقوق صانع اللعبة:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "t.me/Mos_mohh",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyanBright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}
