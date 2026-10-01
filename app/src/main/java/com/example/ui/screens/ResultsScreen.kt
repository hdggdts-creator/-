package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameRound
import com.example.ui.components.ElegantBrandWatermark
import com.example.ui.components.PresenterCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun ResultsScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showAnswersReview by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.goToHome()
    }

    val isTwoPlayer = uiState.isTwoPlayerMode
    val p1Score = uiState.player1Score
    val p2Score = uiState.player2Score
    val soloScore = uiState.totalScore

    fun shareResult() {
        try {
            val shareText = if (isTwoPlayer) {
                val winnerName = when {
                    p1Score > p2Score -> "اللاعب الأول 🔵"
                    p2Score > p1Score -> "اللاعب الثاني 🔴"
                    else -> "تعادل كروي أسطوري 🤝"
                }
                "⚽ انتهت مباراة تحدي الـ 30 كروي (وضع 1v1)!\n" +
                "النتيجة: اللاعب 1: $p1Score نقطة | اللاعب 2: $p2Score نقطة\n" +
                "الفائز: $winnerName 🏆\n\n" +
                "العب معنا الآن: https://t.me/Mos_mohh"
            } else {
                "⚽ حققت $soloScore من 50 نقطة في تطبيق «تحدي الـ 30 كروي»!\n" +
                "هل تستطيع كسر رقمي وتجاوز الـ 5 جولات؟\n\n" +
                "العب الآن: https://t.me/Mos_mohh"
            }
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "مشاركة نتيجة تحدي الـ 30")
            context.startActivity(shareIntent)
        } catch (_: Exception) {}
    }

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
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // -------------------------------------------------------------
        // Header Trophy / Winner Announcement (Clean Flat Design)
        // -------------------------------------------------------------
        item {
            if (isTwoPlayer) {
                // Two-Player Winner Announcement Card
                val isP1Winner = p1Score > p2Score
                val isP2Winner = p2Score > p1Score
                val isDraw = p1Score == p2Score

                val winnerTitle = when {
                    isP1Winner -> "🏆 فوز اللاعب الأول 🔵!"
                    isP2Winner -> "🏆 فوز اللاعب الثاني 🔴!"
                    else -> "🤝 تعادل كروي أسطوري!"
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
                        .border(
                            width = 1.dp,
                            color = winnerColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .testTag("two_player_winner_card"),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(2.dp, winnerColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (isDraw) "🤝" else "👑", fontSize = 30.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

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

                        // Score Comparison Duo Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Player 1 Box
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = if (isP1Winner) 1.5.dp else 1.dp,
                                        color = if (isP1Winner) Player1Color else MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isP1Winner) Player1Color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🔵", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "اللاعب الأول",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Player1Color,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$p1Score",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black,
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
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = if (isP2Winner) 1.5.dp else 1.dp,
                                        color = if (isP2Winner) Player2Color else MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isP2Winner) Player2Color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🔴", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "اللاعب الثاني",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Player2Color,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$p2Score",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black,
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
                // Solo Evaluation Card (Clean Flat Design)
                val performanceTier = when {
                    soloScore >= 45 -> "أسطورة كروية عالمية 🌟"
                    soloScore >= 35 -> "محلل تكتيكي عبقري ⚽"
                    soloScore >= 25 -> "خبير كروي متمكن 🎯"
                    soloScore >= 15 -> "متابع شغوف للساحرة المستديرة 📺"
                    else -> "بداية الطريق الكروي 👟"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = TrophyGold,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .testTag("solo_results_card"),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(2.dp, TrophyGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏅", fontSize = 30.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = performanceTier,
                            style = MaterialTheme.typography.titleLarge,
                            color = TrophyGoldBright,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$soloScore / 50 نقطة",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "تم تقييمك عبر 5 جولات كروية متكاملة",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Host closing comments
        item {
            PresenterCard(
                speechText = uiState.hostSpeech,
                hostMood = uiState.hostMood,
                isSpeaking = uiState.isTtsEnabled,
                onSpeakClick = { viewModel.speakCurrentHostSpeech() }
            )
        }

        // -------------------------------------------------------------
        // Round-by-Round Breakdown
        // -------------------------------------------------------------
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isTwoPlayer) "مقارنة الجولات وجهاً لوجه (Head to Head) ⚔️" else "تفاصيل أداء الجولات 📊",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    allRounds.forEach { round ->
                        val rScoreP1 = uiState.player1RoundScores[round] ?: 0
                        val rScoreP2 = uiState.player2RoundScores[round] ?: 0
                        val soloRoundScore = uiState.roundScores[round] ?: 0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = round.titleAr,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )

                            if (isTwoPlayer) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Player1Color.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Player1Color)
                                    ) {
                                        Text(
                                            text = "🔵 $rScoreP1",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Player1Color,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(text = "ضد", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Player2Color.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Player2Color)
                                    ) {
                                        Text(
                                            text = "🔴 $rScoreP2",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Player2Color,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PitchGreenDark.copy(alpha = 0.35f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PitchGreen.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "$soloRoundScore / 10 نقاط",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = PitchGreenBright,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Toggle Answers Review Button
        item {
            OutlinedButton(
                onClick = { showAnswersReview = !showAnswersReview },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("toggle_answers_review_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (showAnswersReview) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showAnswersReview) "إخفاء مراجعة الإجابات والمعلومات الكروية" else "عرض مراجعة جميع الإجابات والمعلومات الكروية 📜",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Detailed Answers Review List
        if (showAnswersReview) {
            items(uiState.roundResults) { result ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(10.dp)
                        ),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val playerTag = if (isTwoPlayer) {
                                if (result.playerNumber == 1) "اللاعب 1 🔵 • " else "اللاعب 2 🔴 • "
                            } else ""

                            Text(
                                text = "$playerTag${result.round.titleAr}",
                                style = MaterialTheme.typography.titleSmall,
                                color = TrophyGoldBright,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PitchGreenDark.copy(alpha = 0.35f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PitchGreen.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "+${result.scoreEarned} نقاط",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PitchGreenBright,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "الإجابة الصحيحة: ${result.correctAnswer}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (result.triviaNote.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "معلومة: ${result.triviaNote}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // Action Buttons: Play Again, Replay, Share & Home
        // -------------------------------------------------------------
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Play another random match
                Button(
                    onClick = { viewModel.startRandomMatch(uiState.isTwoPlayerMode) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("play_again_random_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PitchGreen,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Shuffle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTwoPlayer) "بدء مواجهة ثنائية جديدة ⚔️" else "لعب حلقة عشوائية أخرى 🎲",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Share Result Button
                Button(
                    onClick = { shareResult() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("share_result_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrophyGold,
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مشاركة نتيجة المباراة 📢",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "إعادة نفس الحلقة بأسئلة متجددة 🔁",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Go Home
                OutlinedButton(
                    onClick = { viewModel.goToHome() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("return_home_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "العودة للقائمة الرئيسية 🏠",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Telegram Watermark Footer (t.me/Mos_mohh)
        item {
            ElegantBrandWatermark()
        }
    }
}
