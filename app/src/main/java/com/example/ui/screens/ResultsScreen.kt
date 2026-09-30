package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameRound
import com.example.data.model.RoundResult
import com.example.data.repository.QuizDataProvider
import com.example.ui.components.HostMood
import com.example.ui.components.PresenterCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun ResultsScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAnswersReview by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.goToHome()
    }

    val totalScore = uiState.totalScore
    val (ratingTitle, ratingBadge, ratingColor) = when {
        totalScore >= 45 -> Triple("أسطورة الأساطير (موسوعة كروية خارقة)", "🏆", TrophyGoldBright)
        totalScore >= 35 -> Triple("محلل عالمي وخبير كروي رفيع", "🥇", TrophyGold)
        totalScore >= 25 -> Triple("عاشق حقيقي ومتابع وفيّ للمستديرة", "🥈", PitchGreenBright)
        else -> Triple("تحتاج لمراجعة مباريات الأبطال والتاريخ!", "⚽", TextSecondary)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("results_screen"),
        contentPadding = PaddingValues(top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Grand Final Score Trophy Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("final_trophy_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = StadiumCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(TrophyGold, PitchGreenDark, NeonCyan)
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(TrophyGoldDark.copy(alpha = 0.35f), StadiumDark.copy(alpha = 0.9f))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(StadiumCardHover)
                                .border(3.dp, ratingColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = ratingBadge, fontSize = 42.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "النتيجة النهائية للتحدي",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$totalScore",
                                fontSize = 54.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TrophyGoldBright
                            )
                            Text(
                                text = " / 50",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StadiumCardHover,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(ratingColor)
                            )
                        ) {
                            Text(
                                text = ratingTitle,
                                style = MaterialTheme.typography.titleSmall,
                                color = ratingColor,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Presenter Grand Commentary
        item {
            PresenterCard(
                speechText = uiState.hostSpeech,
                hostMood = HostMood.CELEBRATING,
                isSpeaking = uiState.isTtsEnabled,
                onSpeakClick = { viewModel.speakCurrentHostSpeech() }
            )
        }

        // Section Title: Round by Round Breakdown
        item {
            Text(
                text = "تفاصيل الجولات الخمس 📊",
                style = MaterialTheme.typography.titleMedium,
                color = TrophyGoldBright,
                fontWeight = FontWeight.Bold
            )
        }

        // Rounds Breakdown List
        val allRounds = listOf(
            GameRound.ROUND_1_WHO_AM_I,
            GameRound.ROUND_2_CAREER,
            GameRound.ROUND_3_LINEUP,
            GameRound.ROUND_4_AUCTION,
            GameRound.ROUND_5_SPEED
        )

        items(allRounds) { round ->
            val score = uiState.roundScores[round] ?: 0
            val maxScore = 10
            val isSuccess = score >= 5

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("round_result_${round.roundNumber}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = StadiumCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(StadiumBorder)
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isSuccess) PitchGreenDark else StadiumDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${round.roundNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSuccess) PitchGreenBright else TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = round.titleAr,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Score pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (score > 0) PitchGreenDark else StadiumDark
                    ) {
                        Text(
                            text = "$score / $maxScore نقاط",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (score > 0) PitchGreenBright else BuzzerRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Toggle Answers Review
        item {
            OutlinedButton(
                onClick = { showAnswersReview = !showAnswersReview },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("toggle_review_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TrophyGoldBright)
            ) {
                Icon(
                    imageVector = if (showAnswersReview) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (showAnswersReview) "إخفاء مراجعة الإجابات والمعلومات الكروية" else "مراجعة جميع الإجابات والمعلومات الكروية 📜",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Answers Review Details
        if (showAnswersReview) {
            items(uiState.roundResults) { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = StadiumCardHover)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = result.round.titleAr,
                                style = MaterialTheme.typography.titleSmall,
                                color = TrophyGoldBright,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "+${result.scoreEarned} نقاط",
                                style = MaterialTheme.typography.labelSmall,
                                color = PitchGreenBright,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "الإجابة الصحيحة: ${result.correctAnswer}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (result.triviaNote.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "معلومة: ${result.triviaNote}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Play another random match
                Button(
                    onClick = {
                        viewModel.startRandomMatch(uiState.isTwoPlayerMode)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("play_again_random_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PitchGreenDark),
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(PitchGreenBright)
                    )
                ) {
                    Icon(imageVector = Icons.Default.Shuffle, contentDescription = null, tint = PitchGreenBright)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "لعب حلقة عشوائية أخرى 🎲",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Replay same episode with fresh non-repeating variant
                OutlinedButton(
                    onClick = {
                        viewModel.startEpisodeWithAntiRepetition(uiState.selectedEpisode.id, uiState.isTwoPlayerMode)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("replay_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TrophyGoldBright)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "إعادة نفس الحلقة 🔁", fontWeight = FontWeight.Bold)
                }

                // Go Home
                OutlinedButton(
                    onClick = { viewModel.goToHome() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("return_home_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "العودة للقائمة الرئيسية 🏠", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
