package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    val isP1 = activePlayerNumber == 1
    Box(
        modifier = modifier
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isP1) Player1Color else Player2Color)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isP1) "دور اللاعب الأول 🔵" else "دور اللاعب الثاني 🔴",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
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
            title = {
                Text(
                    text = "مغادرة التحدي؟ ⚽",
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "هل أنت متأكد من مغادرة هذه المباراة؟ ستفقد جميع نقاطك الحالية في هذه الجولة.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        viewModel.goToHome()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BuzzerRed)
                ) {
                    Text("خروج من المباراة", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExitDialog = false }) {
                    Text("متابعة اللعب", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("game_screen")
    ) {
        // Scoreboard Header with Two-Player turn & live dual scores
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

        // TV Show Dynamic Countdown Timer Bar (1.5 min per question/round)
        if (uiState.currentRound != GameRound.RESULTS) {
            Spacer(modifier = Modifier.height(8.dp))
            CountdownTimerBar(
                remainingSeconds = uiState.timerRemainingSeconds,
                totalSeconds = uiState.timerTotalSeconds
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Presenter Speech & Mood
        PresenterCard(
            speechText = uiState.hostSpeech,
            hostMood = uiState.hostMood,
            isSpeaking = uiState.isTtsEnabled,
            onSpeakClick = { viewModel.speakCurrentHostSpeech() }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Natural Scrollable Document Container:
        // Allows smooth scrolling when clubs list, timeline or clues are long
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AnimatedContent(
                targetState = uiState.currentRound,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(200)) + slideInVertically(animationSpec = tween(200)) { 20 })
                        .togetherWith(fadeOut(animationSpec = tween(150)) + slideOutVertically(animationSpec = tween(150)) { -20 })
                },
                label = "round_content_animation"
            ) { round ->
                when (round) {
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
            }
            Spacer(modifier = Modifier.height(32.dp))
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

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ClueCard(
            hints = whoAmI.hints,
            unlockedCount = uiState.unlockedHintsCount,
            onRevealNextHint = { viewModel.revealNextHintRound1() }
        )

        if (!uiState.round1Finished) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color) else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .testTag("round1_answer_box"),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (uiState.isTwoPlayerMode) {
                        PlayerTurnHeaderBadge(activePlayerNumber = uiState.activePlayerNumber)
                    }

                    Text(
                        text = "من هو هذا اللاعب؟ 🤔",
                        style = MaterialTheme.typography.titleMedium,
                        color = TrophyGoldBright,
                        fontWeight = FontWeight.ExtraBold
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
                            focusedBorderColor = if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color) else PitchGreen,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (uiState.round1ErrorFeedback != null) {
                        Spacer(modifier = Modifier.height(6.dp))
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
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PitchGreen,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تأكيد الإجابة",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.skipRound1() },
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("round1_skip_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = "كشف وتخطي",
                                style = MaterialTheme.typography.labelLarge
                            )
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
}

// -------------------------------------------------------------------
// ROUND 2: الرابط العجيب / مسيرة لاعب (Document Flow & No Overlapping)
// -------------------------------------------------------------------
@Composable
fun Round2Content(
    uiState: QuizUiState,
    viewModel: QuizViewModel
) {
    val career = uiState.selectedEpisode.careerPath

    // Natural Document Flow: Column container holding:
    // 1. Clubs Timeline
    // 2. Clear spacing
    // 3. Answer Box Card below it
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("round2_content_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step 1: Clubs Timeline in Chronological Order
        CareerTimelineView(
            clubs = career.clubs,
            extraClue = career.extraClue
        )

        // Step 2: Clear Margin / Spacing between timeline and answer box
        Spacer(modifier = Modifier.height(4.dp))

        // Step 3: Answer Box Card (Cleanly placed below the timeline, never overlapping)
        if (!uiState.round2Finished) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color) else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .testTag("round2_answer_box"),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (uiState.isTwoPlayerMode) {
                        PlayerTurnHeaderBadge(activePlayerNumber = uiState.activePlayerNumber)
                    }

                    Text(
                        text = "من هو اللاعب صاحب هذه المسيرة الكروية؟ ⚽",
                        style = MaterialTheme.typography.titleMedium,
                        color = TrophyGoldBright,
                        fontWeight = FontWeight.ExtraBold
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
                            focusedBorderColor = if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color) else PitchGreen,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (uiState.round2ErrorFeedback != null) {
                        Spacer(modifier = Modifier.height(6.dp))
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
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PitchGreen,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تأكيد الإجابة (10 نقاط)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.skipRound2() },
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("round2_skip_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = "كشف وتخطي",
                                style = MaterialTheme.typography.labelLarge
                            )
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
                title = "${playerPrefix}اللاعب صاحب المسيرة: ${career.playerName}",
                subtitle = "أندية تاريخية ومحطات خالدة • حصلت على $earnedScore نقاط",
                scoreEarned = earnedScore,
                triviaFact = career.triviaFact,
                onNextClick = { viewModel.nextRound() },
                nextRoundTitle = getNextButtonTitle(uiState, "الجولة الثالثة: التشكيلة الناقصة 📋")
            )
        }
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

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = lineup.matchTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = TrophyGoldBright,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${lineup.teamName} • ${lineup.year}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = lineup.formation,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyanBright,
                        fontWeight = FontWeight.ExtraBold,
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
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    TrophyGold.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = lineup.clueText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TrophyGoldBright
                    )
                }
            }
        }

        if (!uiState.round3Finished) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color) else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .testTag("round3_answer_box"),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (uiState.isTwoPlayerMode) {
                        PlayerTurnHeaderBadge(activePlayerNumber = uiState.activePlayerNumber)
                    }

                    Text(
                        text = "من هو اللاعب المجهول ❓ في هذه التشكيلة التاريخية؟",
                        style = MaterialTheme.typography.titleMedium,
                        color = TrophyGoldBright,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.round3Input,
                        onValueChange = { viewModel.onRound3InputChange(it) },
                        placeholder = { Text("اكتب اسم اللاعب الناقص في الخطة...", color = TextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("round3_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color) else PitchGreen,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (uiState.round3ErrorFeedback != null) {
                        Spacer(modifier = Modifier.height(6.dp))
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
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PitchGreen,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تأكيد الإجابة (10 نقاط)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.skipRound3() },
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("round3_skip_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = "كشف وتخطي",
                                style = MaterialTheme.typography.labelLarge
                            )
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
                subtitle = "${lineup.position} • القميص رقم ${lineup.kitNumber}",
                scoreEarned = earnedScore,
                triviaFact = lineup.historicalContext.ifBlank { lineup.triviaFact },
                onNextClick = { viewModel.nextRound() },
                nextRoundTitle = getNextButtonTitle(uiState, "الجولة الرابعة: تحدي المزاد 🔨")
            )
        }
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

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔨", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "شرط التحدي في المزاد:",
                        style = MaterialTheme.typography.labelMedium,
                        color = TrophyGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = auction.challengePrompt,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 22.sp
                )
            }
        }

        AuctionItemChips(
            targetCount = auction.targetCount,
            acceptedEntries = uiState.round4AcceptedEntries
        )

        if (!uiState.round4Finished) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color) else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .testTag("round4_answer_box"),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (uiState.isTwoPlayerMode) {
                        PlayerTurnHeaderBadge(activePlayerNumber = uiState.activePlayerNumber)
                    }

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
                            focusedBorderColor = if (uiState.isTwoPlayerMode) (if (uiState.activePlayerNumber == 1) Player1Color else Player2Color) else PitchGreen,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (uiState.round4Feedback != null) {
                        Spacer(modifier = Modifier.height(6.dp))
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
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PitchGreen,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "إضافة اسم (+2)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.finishAuction() },
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("round4_finish_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = TrophyGoldBright
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TrophyGold.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "اكتفيت بهذا القدر",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
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
                subtitle = "أحسنت! ذكرت ${uiState.round4AcceptedEntries.size} أسماء صحيحة وحصدت $earnedScore نقاط",
                scoreEarned = earnedScore,
                triviaFact = auction.canonicalDisplayList.take(6).joinToString(" • ").ifBlank { auction.triviaFact },
                onNextClick = { viewModel.nextRound() },
                nextRoundTitle = getNextButtonTitle(uiState, "الجولة الأخيرة: أسئلة السرعة ⚡")
            )
        }
    }
}

// -------------------------------------------------------------------
// ROUND 5: أسئلة السرعة (صح أو خطأ)
// -------------------------------------------------------------------
@Composable
fun Round5Content(
    uiState: QuizUiState,
    viewModel: QuizViewModel
) {
    val speedRound = uiState.selectedEpisode.speed
    val currentQuestion = speedRound.questions.getOrNull(uiState.speedQuestionIndex)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (currentQuestion != null && !uiState.round5Finished) {
            if (uiState.isTwoPlayerMode) {
                PlayerTurnHeaderBadge(activePlayerNumber = uiState.activePlayerNumber)
            }

            SpeedQuestionCard(
                question = currentQuestion,
                currentIndex = uiState.speedQuestionIndex,
                totalCount = speedRound.questions.size,
                answeredChoice = uiState.speedAnsweredChoice,
                onAnswerSelected = { viewModel.answerSpeedQuestion(it) }
            )

            if (uiState.speedAnsweredChoice != null) {
                val isLast = uiState.speedQuestionIndex == speedRound.questions.size - 1

                val nextLabel = if (isLast) {
                    if (uiState.isTwoPlayerMode && uiState.activePlayerNumber == 1) {
                        "إنهاء دور اللاعب 1 وتسليم الدور للاعب الثاني 🔴"
                    } else {
                        "عرض النتيجة النهائية ومقارنة البطلين 🏆"
                    }
                } else {
                    "السؤال السريع التالي ➡️"
                }

                Button(
                    onClick = { viewModel.nextSpeedQuestion() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("next_speed_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PitchGreen,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = nextLabel,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        } else if (uiState.round5Finished && uiState.isTwoPlayerMode && uiState.activePlayerNumber == 1) {
            // Player 1 finished speed round in 2-player mode -> show handover card!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Player1Color,
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Player1Color.copy(alpha = 0.2f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "اللاعب الأول 🔵: +${uiState.player1RoundScores[GameRound.ROUND_5_SPEED] ?: 0} نقاط في السرعة",
                            style = MaterialTheme.typography.labelMedium,
                            color = Player1Color,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "انتهى دور اللاعب الأول في الجولة الخامسة!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "سلم الشاشة للاعب الثاني 🔴 ليخوض أسئلة السرعة الخاصة به ويحسم اللقب!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.nextRound() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("handover_p2_speed_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Player2Color,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "بدء دور اللاعب الثاني 🔴 (أسئلة السرعة)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------
// Round Success Card (Clean Flat Design)
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
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .testTag("round_success_card"),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PitchGreenDark.copy(alpha = 0.3f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PitchGreen.copy(alpha = 0.5f)),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "+$scoreEarned نقاط في الرصيد 🌟",
                    style = MaterialTheme.typography.labelMedium,
                    color = PitchGreenBright,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (triviaFact.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📌", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "معلومة كروية توثيقية:",
                                style = MaterialTheme.typography.labelMedium,
                                color = TrophyGoldBright,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = triviaFact,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("next_round_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PitchGreen,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = nextRoundTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
