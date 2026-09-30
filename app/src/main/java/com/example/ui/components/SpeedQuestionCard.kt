package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.example.data.model.SpeedItem
import com.example.ui.theme.*

@Composable
fun SpeedQuestionCard(
    question: SpeedItem,
    currentIndex: Int,
    totalCount: Int,
    answeredChoice: Boolean?,
    onAnswerSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("speed_question_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = StadiumCard
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(StadiumBorder)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: question counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚡", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "السؤال ${currentIndex + 1} من $totalCount",
                        style = MaterialTheme.typography.titleSmall,
                        color = TrophyGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StadiumDark
                ) {
                    Text(
                        text = "2 نقطتان",
                        style = MaterialTheme.typography.labelSmall,
                        color = PitchGreenBright,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Statement
            Text(
                text = question.statement,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // True / False Buttons or Answer Feedback
            if (answeredChoice == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // True Button (Green)
                    Button(
                        onClick = { onAnswerSelected(true) },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("speed_true_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PitchGreenDark
                        ),
                        border = ButtonDefaults.outlinedButtonBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(PitchGreenBright)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = PitchGreenBright,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "صــح",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // False Button (Red)
                    Button(
                        onClick = { onAnswerSelected(false) },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("speed_false_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BuzzerRedDark
                        ),
                        border = ButtonDefaults.outlinedButtonBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(BuzzerRed)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = BuzzerRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "خـطـأ",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                val isCorrect = answeredChoice == question.isTrue
                val feedbackBg = if (isCorrect) PitchGreenDark else BuzzerRedDark
                val feedbackBorder = if (isCorrect) PitchGreenBright else BuzzerRed

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = feedbackBg.copy(alpha = 0.85f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(feedbackBorder)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isCorrect) "إجابة صحيحة! (+2 نقاط) 👏" else "إجابة خاطئة! ❌",
                            style = MaterialTheme.typography.titleSmall,
                            color = if (isCorrect) PitchGreenBright else BuzzerRed,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
