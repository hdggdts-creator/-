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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameRound
import com.example.ui.components.HostMood
import com.example.ui.components.Player1Color
import com.example.ui.components.Player2Color
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

    val isTwoPlayer = uiState.isTwoPlayerMode
    val p1Score = uiState.player1Score
    val p2Score = uiState.player2Score
    val soloScore = uiState.totalScore

    val allRounds = listOf(
        GameRound.ROUND_1_WHO_AM_I,
        GameRound.ROUND_2_CAREER,
        GameRound.ROUND_3_LINEUP,
        GameRound.ROUND_4_AUCTION,
        GameRound.ROUND_5_SPEED
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("results_screen"),
        contentPadding = PaddingValues(top = 20.dp, bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // -------------------------------------------------------------
        // Header Trophy / Winner Announcement
        // -------------------------------------------------------------
        item {
            if (isTwoPlayer) {
                // Two-Player Winner Announcement Card
                val isP1Winner = p1Score > p2Score
                val isP2Winner = p2Score > p1Score
                val isDraw = p1Score == p2Score

                val winnerTitle = when {
                    isP1Winner -> "🏆 فوز مستحق للاعب الأول 🔵!"
                    isP2Winner -> "🏆 فوز مستحق للاعب الثاني 🔴!"
                    else -> "🤝 تعادل كروي أسطوري ومثير!"
                }

                val winnerSubtitle = when {
                    isP1Winner -> "تألق اللاعب الأول وتفوق في حصد النقاط بفضل دقته وسرعته!"
                    isP2Winner -> "اكتسح اللاعب الثاني وتفوق ببراعة في الجولات الحاسمة!"
                    else -> "مباراة للتاريخ! تكافؤ تام في الموسوعة والمعلومات الكروية!"
                }

                val winnerColor = when {
                    isP1Winner -> Player1Color
                    isP2Winner -> Player2Color
                    else -> TrophyGoldBright
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("two_player_winner_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(Player1Color, TrophyGold, Player2Color)
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(winnerColor.copy(alpha = 0.2f))
                                .border(2.5.dp, winnerColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (isDraw) "🤝" else "👑", fontSize = 36.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = winnerTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = winnerColor,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = winnerSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Score Comparison Duo
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Player 1 Box
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isP1Winner) Player1Color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = SolidColor(if (isP1Winner) Player1Color else MaterialTheme.colorScheme.outline)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🔵", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "اللاعب الأول",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Player1Color,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$p1Score",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isP1Winner) Player1Color else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "من 50 نقطة",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Player 2 Box
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isP2Winner) Player2Color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = SolidColor(if (isP2Winner) Player2Color else MaterialTheme.colorScheme.outline)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🔴", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "اللاعب الثاني",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Player2Color,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$p2Score",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isP2Winner) Player2Color else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "من 50 نقطة",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Solo Mode Trophy Card
                val (ratingTitle, ratingBadge, ratingColor) = when {
                    soloScore >= 45 -> Triple("أسطورة الأساطير (موسوعة كروية خارقة)", "🏆", TrophyGoldBright)
                    soloScore >= 35 -> Triple("محلل عالمي وخبير كروي رفيع", "🥇", TrophyGold)
                    soloScore >= 25 -> Triple("عاشق حقيقي ومتابع وفيّ للمستديرة", "🥈", PitchGreenBright)
                    else -> Triple("تحتاج لمراجعة مباريات الأبطال والتاريخ!", "⚽", MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("final_trophy_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(TrophyGold, PitchGreenDark, NeonCyan)
                        )
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(3.dp, ratingColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = ratingBadge, fontSize = 42.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "النتيجة النهائية للتحدي",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$soloScore",
                                fontSize = 54.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TrophyGoldBright
                            )
                            Text(
                                text = " / 50",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = SolidColor(ratingColor)
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

        // -------------------------------------------------------------
        // Presenter Commentary
        // -------------------------------------------------------------
        item {
            PresenterCard(
                speechText = uiState.hostSpeech,
                hostMood = HostMood.CELEBRATING,
                isSpeaking = uiState.isTtsEnabled,
                onSpeakClick = { viewModel.speakCurrentHostSpeech() }
            )
        }

        // -------------------------------------------------------------
        // Round by Round Head-to-Head / Breakdown
        // -------------------------------------------------------------
        item {
            Text(
                text = if (isTwoPlayer) "مقارنة الجولات وجهاً لوجه ⚔️" else "تفاصيل الجولات الخمس 📊",
                style = MaterialTheme.typography.titleMedium,
                color = TrophyGoldBright,
                fontWeight = FontWeight.Bold
            )
        }

        items(allRounds) { round ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("round_result_${round.roundNumber}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = SolidColor(MaterialTheme.colorScheme.outline)
                )
            ) {
                if (isTwoPlayer) {
                    val s1 = uiState.player1RoundScores[round] ?: 0
                    val s2 = uiState.player2RoundScores[round] ?: 0

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${round.roundNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TrophyGoldBright,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = round.titleAr,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Head-to-Head scores
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // P1 score
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Player1Color.copy(alpha = 0.2f),
                                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Player1Color))
                            ) {
                                Text(
                                    text = "🔵 $s1",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Player1Color,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "ضد",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // P2 score
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Player2Color.copy(alpha = 0.2f),
                                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Player2Color))
                            ) {
                                Text(
                                    text = "🔴 $s2",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Player2Color,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    val score = uiState.roundScores[round] ?: 0
                    val maxScore = 10
                    val isSuccess = score >= 5

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
                                    .background(if (isSuccess) PitchGreenDark else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${round.roundNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSuccess) PitchGreenBright else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = round.titleAr,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (score > 0) PitchGreenDark else MaterialTheme.colorScheme.surfaceVariant
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
        }

        // -------------------------------------------------------------
        // Toggle Answers Review
        // -------------------------------------------------------------
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

        // -------------------------------------------------------------
        // Answers Review List
        // -------------------------------------------------------------
        if (showAnswersReview) {
            items(uiState.roundResults) { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (result.triviaNote.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "معلومة: ${result.triviaNote}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // Action Buttons
        // -------------------------------------------------------------
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
                        brush = SolidColor(PitchGreenBright)
                    )
                ) {
                    Icon(imageVector = Icons.Default.Shuffle, contentDescription = null, tint = PitchGreenBright)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTwoPlayer) "بدء مواجهة ثنائية جديدة ⚔️" else "لعب حلقة عشوائية أخرى 🎲",
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
                    Text(text = "إعادة نفس الحلقة بأسئلة جديدة 🔁", fontWeight = FontWeight.Bold)
                }

                // Go Home
                OutlinedButton(
                    onClick = { viewModel.goToHome() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("return_home_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "العودة للقائمة الرئيسية 🏠", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
