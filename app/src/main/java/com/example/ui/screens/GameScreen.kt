package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameRound
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.QuizUiState
import com.example.ui.viewmodel.QuizViewModel

fun getNextButtonTitle(uiState: QuizUiState, defaultNextTitle: String): String {
    if (!uiState.isTwoPlayerMode) return defaultNextTitle
    return if (uiState.activePlayerNumber == 1) {
        "تسليم الدور للاعب الثاني 🔴 (الجولة ${uiState.currentRound.roundNumber})"
    } else {
        if (uiState.currentRound == GameRound.ROUND_5_SPEED) {
            "عرض النتيجة وتتويج الفائز 🏆"
        } else {
            "الانتقال للجولة ${uiState.currentRound.roundNumber + 1} ➡️ (دور اللاعب الأول 🔵)"
        }
    }
}

@Composable
fun PlayerTurnHeaderBadge(activePlayerNumber: Int, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (activePlayerNumber == 1) Player1Color else Player2Color,
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (activePlayerNumber == 1) "دور اللاعب الأول 🔵" else "دور اللاعب الثاني 🔴",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun GameScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("مغادرة التحدي؟", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من مغادرة هذه المباراة؟ ستفقد نقاطك الحالية.") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    viewModel.goToHome()
                }) {
                    Text("خروج", color = BuzzerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("متابعة اللعب", color = TrophyGoldBright)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("game_screen")
    ) {
        // Scoreboard Header with Two-Player turn & score support
        ScoreboardHeader(
            currentRound = uiState.currentRound,
            currentScore = uiState.totalScore,
            isTwoPlayerMode = uiState.isTwoPlayerMode,
            activePlayerNumber = uiState.activePlayerNumber,
            player1Score = uiState.player1Score,
            player2Score = uiState.player2Score,
            isSoundEnabled = uiState.isSoundEnabled,
            onToggleSound = { viewModel.toggleSound() },
            onBackClick = { showExitDialog = true }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Presenter Speech & Mood
        PresenterCard(
            speechText = uiState.hostSpeech,
            hostMood = uiState.hostMood,
            isSpeaking = uiState.isTtsEnabled,
            onSpeakClick = { viewModel.speakCurrentHostSpeech() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Round Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (uiState.currentRound) {
                GameRound.ROUND_1_WHO_AM_I -> {
                    Round1Content(uiState = uiState, viewModel = viewModel)
                }
                GameRound.ROUND_2_CAREER -> {
                    Round2Content(uiState = uiState, viewModel = viewModel)
                }
                GameRound.ROUND_3_LINEUP -> {
                    Round3Content(uiState = uiState, viewModel = viewModel)
                }
                GameRound.ROUND_4_AUCTION -> {
                    Round4Content(uiState = uiState, viewModel = viewModel)
                }
                GameRound.ROUND_5_SPEED -> {
                    Round5Content(uiState = uiState, viewModel = viewModel)
                }
                GameRound.RESULTS -> {
                    // Handled by Screen.RESULTS
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------------
// ROUND 1: من أنا؟
// -------------------------------------------------------------------
@Composable
fun Round1Content(
    uiState: QuizUiState,
    viewModel: QuizViewModel
) {
    val whoAmI = uiState.selectedEpisode.whoAmI

    ClueCard(
        hints = whoAmI.hints,
        unlockedCount = uiState.unlockedHintsCount,
        onRevealNextHint = { viewModel.revealNextHintRound1() }
    )

    if (!uiState.round1Finished) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = SolidColor(
                    if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color)
                    else MaterialTheme.colorScheme.outline
                )
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (uiState.isTwoPlayerMode) {
                    PlayerTurnHeaderBadge(activePlayerNumber = uiState.activePlayerNumber)
                }

                Text(
                    text = "من هو هذا اللاعب؟ 🤔",
                    style = MaterialTheme.typography.titleSmall,
                    color = TrophyGoldBright,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = uiState.round1Input,
                    onValueChange = { viewModel.onRound1InputChange(it) },
                    placeholder = { Text("اكتب اسم اللاعب هنا...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("round1_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color) else TrophyGoldBright,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (uiState.round1ErrorFeedback != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.round1ErrorFeedback,
                        style = MaterialTheme.typography.bodySmall,
                        color = BuzzerRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.submitRound1Answer() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("round1_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PitchGreenDark),
                        border = ButtonDefaults.outlinedButtonBorder().copy(
                            brush = SolidColor(PitchGreenBright)
                        )
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = PitchGreenBright)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تأكيد الإجابة", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = { viewModel.skipRound1() },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("round1_skip_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
                    ) {
                        Text("كشف وتخطي")
                    }
                }
            }
        }
    } else {
        val earnedScore = if (uiState.isTwoPlayerMode) {
            (if (uiState.activePlayerNumber == 1) uiState.player1RoundScores else uiState.player2RoundScores)[GameRound.ROUND_1_WHO_AM_I] ?: 0
        } else {
            uiState.roundScores[GameRound.ROUND_1_WHO_AM_I] ?: 0
        }

        val playerPrefix = if (uiState.isTwoPlayerMode) {
            if (uiState.activePlayerNumber == 1) "اللاعب الأول 🔵: " else "اللاعب الثاني 🔴: "
        } else ""

        RoundSuccessCard(
            title = "${playerPrefix}اللاعب هو: ${whoAmI.playerName} ${whoAmI.nationalityEmoji}",
            subtitle = "${whoAmI.position} • كُشف بعد ${uiState.unlockedHintsCount} تلميحات",
            scoreEarned = earnedScore,
            triviaFact = whoAmI.triviaFact,
            onNextClick = { viewModel.nextRound() },
            nextRoundTitle = getNextButtonTitle(uiState, "الجولة الثانية: الرابط العجيب ⏱️")
        )
    }
}

// -------------------------------------------------------------------
// ROUND 2: الرابط العجيب / مسيرة لاعب
// -------------------------------------------------------------------
@Composable
fun Round2Content(
    uiState: QuizUiState,
    viewModel: QuizViewModel
) {
    val career = uiState.selectedEpisode.careerPath

    CareerTimelineView(
        clubs = career.clubs,
        extraClue = career.extraClue
    )

    if (!uiState.round2Finished) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = SolidColor(
                    if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color)
                    else MaterialTheme.colorScheme.outline
                )
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (uiState.isTwoPlayerMode) {
                    PlayerTurnHeaderBadge(activePlayerNumber = uiState.activePlayerNumber)
                }

                Text(
                    text = "من هو اللاعب صاحب هذه المسيرة الكروية؟ ⚽",
                    style = MaterialTheme.typography.titleSmall,
                    color = TrophyGoldBright,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = uiState.round2Input,
                    onValueChange = { viewModel.onRound2InputChange(it) },
                    placeholder = { Text("اكتب اسم اللاعب صاحب المسيرة...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("round2_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color) else TrophyGoldBright,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (uiState.round2ErrorFeedback != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.round2ErrorFeedback,
                        style = MaterialTheme.typography.bodySmall,
                        color = BuzzerRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.submitRound2Answer() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("round2_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PitchGreenDark),
                        border = ButtonDefaults.outlinedButtonBorder().copy(
                            brush = SolidColor(PitchGreenBright)
                        )
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = PitchGreenBright)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تأكيد الإجابة (10 نقاط)", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = { viewModel.skipRound2() },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("round2_skip_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
                    ) {
                        Text("كشف وتخطي")
                    }
                }
            }
        }
    } else {
        val earnedScore = if (uiState.isTwoPlayerMode) {
            (if (uiState.activePlayerNumber == 1) uiState.player1RoundScores else uiState.player2RoundScores)[GameRound.ROUND_2_CAREER] ?: 0
        } else {
            uiState.roundScores[GameRound.ROUND_2_CAREER] ?: 0
        }

        val playerPrefix = if (uiState.isTwoPlayerMode) {
            if (uiState.activePlayerNumber == 1) "اللاعب الأول 🔵: " else "اللاعب الثاني 🔴: "
        } else ""

        RoundSuccessCard(
            title = "${playerPrefix}صاحب المسيرة: ${career.playerName}",
            subtitle = "أندية: ${career.clubs.joinToString(" ⬅️ ") { it.clubName }}",
            scoreEarned = earnedScore,
            triviaFact = career.triviaFact,
            onNextClick = { viewModel.nextRound() },
            nextRoundTitle = getNextButtonTitle(uiState, "الجولة الثالثة: التشكيلة الناقصة 📋")
        )
    }
}

// -------------------------------------------------------------------
// ROUND 3: التشكيلة الناقصة
// -------------------------------------------------------------------
@Composable
fun Round3Content(
    uiState: QuizUiState,
    viewModel: QuizViewModel
) {
    val lineup = uiState.selectedEpisode.lineup

    // Match Header Banner
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = lineup.matchTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = TrophyGoldBright,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${lineup.teamName} • ${lineup.year}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = lineup.formation,
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyanBright,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }

    // Tactical Pitch View
    FootballPitchView(
        players = lineup.players,
        revealedMysteryName = uiState.round3RevealedName
    )

    if (lineup.clueText.isNotBlank()) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder().copy(
                brush = SolidColor(MaterialTheme.colorScheme.outline)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "💡", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = lineup.clueText,
                    style = MaterialTheme.typography.bodySmall,
                    color = TrophyGoldBright
                )
            }
        }
    }

    if (!uiState.round3Finished) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = SolidColor(
                    if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color)
                    else MaterialTheme.colorScheme.outline
                )
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (uiState.isTwoPlayerMode) {
                    PlayerTurnHeaderBadge(activePlayerNumber = uiState.activePlayerNumber)
                }

                Text(
                    text = "من هو اللاعب المجهول ❓ في هذه التشكيلة التاريخية؟",
                    style = MaterialTheme.typography.titleSmall,
                    color = TrophyGoldBright,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = uiState.round3Input,
                    onValueChange = { viewModel.onRound3InputChange(it) },
                    placeholder = { Text("اكتب اسم اللاعب المجهول...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("round3_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color) else TrophyGoldBright,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (uiState.round3ErrorFeedback != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.round3ErrorFeedback,
                        style = MaterialTheme.typography.bodySmall,
                        color = BuzzerRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.submitRound3Answer() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("round3_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PitchGreenDark),
                        border = ButtonDefaults.outlinedButtonBorder().copy(
                            brush = SolidColor(PitchGreenBright)
                        )
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = PitchGreenBright)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تأكيد الإجابة (10 نقاط)", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = { viewModel.skipRound3() },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("round3_skip_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
                    ) {
                        Text("كشف وتخطي")
                    }
                }
            }
        }
    } else {
        val earnedScore = if (uiState.isTwoPlayerMode) {
            (if (uiState.activePlayerNumber == 1) uiState.player1RoundScores else uiState.player2RoundScores)[GameRound.ROUND_3_LINEUP] ?: 0
        } else {
            uiState.roundScores[GameRound.ROUND_3_LINEUP] ?: 0
        }

        val playerPrefix = if (uiState.isTwoPlayerMode) {
            if (uiState.activePlayerNumber == 1) "اللاعب الأول 🔵: " else "اللاعب الثاني 🔴: "
        } else ""

        RoundSuccessCard(
            title = "${playerPrefix}اللاعب المجهول: ${lineup.mysteryPlayerName}",
            subtitle = "مباراة: ${lineup.matchTitle}",
            scoreEarned = earnedScore,
            triviaFact = lineup.triviaFact,
            onNextClick = { viewModel.nextRound() },
            nextRoundTitle = getNextButtonTitle(uiState, "الجولة الرابعة: تحدي المزاد 🔨")
        )
    }
}

// -------------------------------------------------------------------
// ROUND 4: تحدي المزاد
// -------------------------------------------------------------------
@Composable
fun Round4Content(
    uiState: QuizUiState,
    viewModel: QuizViewModel
) {
    val auction = uiState.selectedEpisode.auction

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(TrophyGoldDark)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (uiState.isTwoPlayerMode) {
                PlayerTurnHeaderBadge(activePlayerNumber = uiState.activePlayerNumber)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔨", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "شرط التحدي في المزاد:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TrophyGoldBright,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = auction.challengePrompt,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp
            )
        }
    }

    AuctionItemChips(
        targetCount = auction.targetCount,
        acceptedEntries = uiState.round4AcceptedEntries
    )

    if (!uiState.round4Finished) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = SolidColor(
                    if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color)
                    else MaterialTheme.colorScheme.outline
                )
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "اكتب اسماً يطابق التحدي:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.round4Input,
                    onValueChange = { viewModel.onRound4InputChange(it) },
                    placeholder = { Text("اكتب الاسم هنا...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("round4_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color) else TrophyGoldBright,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (uiState.round4Feedback != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.round4Feedback,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (uiState.round4Feedback.contains("صحيحة")) PitchGreenBright else BuzzerRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.submitAuctionItem() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("round4_add_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PitchGreenDark),
                        border = ButtonDefaults.outlinedButtonBorder().copy(
                            brush = SolidColor(PitchGreenBright)
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = PitchGreenBright)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إضافة اسم (+2 نقاط)", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = { viewModel.finishAuction() },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("round4_finish_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TrophyGoldBright)
                    ) {
                        Text("اكتفيت بهذا القدر")
                    }
                }
            }
        }
    } else {
        val earnedScore = if (uiState.isTwoPlayerMode) {
            (if (uiState.activePlayerNumber == 1) uiState.player1RoundScores else uiState.player2RoundScores)[GameRound.ROUND_4_AUCTION] ?: 0
        } else {
            uiState.roundScores[GameRound.ROUND_4_AUCTION] ?: 0
        }

        val playerPrefix = if (uiState.isTwoPlayerMode) {
            if (uiState.activePlayerNumber == 1) "اللاعب الأول 🔵: " else "اللاعب الثاني 🔴: "
        } else ""

        RoundSuccessCard(
            title = "${playerPrefix}انتهى المزاد!",
            subtitle = "نجحت في ذكر: ${uiState.round4AcceptedEntries.joinToString(" • ")}",
            scoreEarned = earnedScore,
            triviaFact = auction.triviaFact,
            onNextClick = { viewModel.nextRound() },
            nextRoundTitle = getNextButtonTitle(uiState, "الجولة الخامسة: أسئلة السرعة ⚡")
        )
    }
}

// -------------------------------------------------------------------
// ROUND 5: أسئلة السرعة
// -------------------------------------------------------------------
@Composable
fun Round5Content(
    uiState: QuizUiState,
    viewModel: QuizViewModel
) {
    val speed = uiState.selectedEpisode.speed
    val currentQuestion = speed.questions.getOrNull(uiState.speedQuestionIndex)

    if (uiState.isTwoPlayerMode && !uiState.round5Finished) {
        PlayerTurnHeaderBadge(activePlayerNumber = uiState.activePlayerNumber)
    }

    if (currentQuestion != null && !uiState.round5Finished) {
        SpeedQuestionCard(
            question = currentQuestion,
            currentIndex = uiState.speedQuestionIndex,
            totalCount = speed.questions.size,
            answeredChoice = uiState.speedAnsweredChoice,
            onAnswerSelected = { viewModel.answerSpeedQuestion(it) }
        )

        if (uiState.speedAnsweredChoice != null) {
            val isLast = uiState.speedQuestionIndex == speed.questions.size - 1
            Button(
                onClick = { viewModel.nextSpeedQuestion() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("speed_next_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TrophyGoldDark),
                border = ButtonDefaults.outlinedButtonBorder().copy(
                    brush = SolidColor(TrophyGoldBright)
                )
            ) {
                val nextLabel = if (isLast) {
                    if (uiState.isTwoPlayerMode && uiState.activePlayerNumber == 1) {
                        "إنهاء أسئلة اللاعب الأول وتسليم الدور للاعب الثاني 🔴"
                    } else {
                        "عرض النتيجة النهائية ومقارنة البطلين 🏆"
                    }
                } else {
                    "السؤال السريع التالي ➡️"
                }
                Text(
                    text = nextLabel,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else if (uiState.round5Finished && uiState.isTwoPlayerMode && uiState.activePlayerNumber == 1) {
        // Player 1 finished speed round in 2-player mode -> show handover card!
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = SolidColor(Player1Color)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Player1Color.copy(alpha = 0.2f),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "اللاعب الأول 🔵: +${uiState.player1RoundScores[GameRound.ROUND_5_SPEED] ?: 0} نقاط في السرعة",
                        style = MaterialTheme.typography.labelMedium,
                        color = Player1Color,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Text(
                    text = "انتهى دور اللاعب الأول في الجولة الخامسة!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "سلم الهاتف أو الشاشة للاعب الثاني 🔴 ليخوض أسئلة السرعة الخاصة به ويحسم اللقب!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { viewModel.nextRound() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("handover_p2_speed_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PitchGreenDark),
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        brush = SolidColor(PitchGreenBright)
                    )
                ) {
                    Text(
                        text = "بدء دور اللاعب الثاني 🔴 (أسئلة السرعة)",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = PitchGreenBright)
                }
            }
        }
    }
}

// -------------------------------------------------------------------
// SHARED ROUND SUCCESS CARD
// -------------------------------------------------------------------
@Composable
fun RoundSuccessCard(
    title: String,
    subtitle: String,
    scoreEarned: Int,
    triviaFact: String,
    onNextClick: () -> Unit,
    nextRoundTitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("round_success_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(PitchGreenDark)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = PitchGreenDark,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "+$scoreEarned نقاط في الرصيد",
                    style = MaterialTheme.typography.labelMedium,
                    color = PitchGreenBright,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TrophyGoldBright,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (triviaFact.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = SolidColor(MaterialTheme.colorScheme.outline)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📌", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "معلومة كروية إضافية:",
                                style = MaterialTheme.typography.labelSmall,
                                color = TrophyGoldBright,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = triviaFact,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("next_round_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PitchGreenDark),
                border = ButtonDefaults.outlinedButtonBorder().copy(
                    brush = SolidColor(PitchGreenBright)
                )
            ) {
                Text(
                    text = nextRoundTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = PitchGreenBright)
            }
        }
    }
}
