package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.model.*
import com.example.data.repository.QuizDataProvider
import com.example.ui.components.HostMood
import com.example.util.ArabicTextNormalizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizUiState(
    val currentScreen: Screen = Screen.HOME,
    val selectedEpisode: QuizEpisode = QuizDataProvider.episodes[0],
    val currentRound: GameRound = GameRound.ROUND_1_WHO_AM_I,
    val totalScore: Int = 0,
    val roundScores: Map<GameRound, Int> = emptyMap(),
    val roundResults: List<RoundResult> = emptyList(),

    // Presenter state
    val hostSpeech: String = "أهلاً بكم في تحدي الـ 30 كروي! استعد لمعركة العقول الكروية!",
    val hostMood: HostMood = HostMood.WELCOME,

    // Round 1 (Who Am I)
    val unlockedHintsCount: Int = 1,
    val round1Input: String = "",
    val round1Finished: Boolean = false,
    val round1ErrorFeedback: String? = null,

    // Round 2 (Career Path)
    val round2Input: String = "",
    val round2Finished: Boolean = false,
    val round2ErrorFeedback: String? = null,

    // Round 3 (Lineup)
    val round3Input: String = "",
    val round3Finished: Boolean = false,
    val round3RevealedName: String? = null,
    val round3ErrorFeedback: String? = null,

    // Round 4 (Auction)
    val round4Input: String = "",
    val round4AcceptedEntries: List<String> = emptyList(),
    val round4Finished: Boolean = false,
    val round4Feedback: String? = null,

    // Round 5 (Speed)
    val speedQuestionIndex: Int = 0,
    val speedAnsweredChoice: Boolean? = null,
    val speedCorrectCount: Int = 0,
    val round5Finished: Boolean = false,

    // Audio & Settings
    val isSoundEnabled: Boolean = true,
    val isTtsEnabled: Boolean = true,
    val isTwoPlayerMode: Boolean = false,
    val activePlayerNumber: Int = 1,
    val player1Score: Int = 0,
    val player2Score: Int = 0
)

enum class Screen {
    HOME,
    GAME,
    RESULTS
}

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    val soundManager = SoundManager(application)

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        // Greet user on launch
        greetForCurrentRound()
    }

    fun startEpisode(episode: QuizEpisode, twoPlayerMode: Boolean = false) {
        _uiState.update {
            it.copy(
                selectedEpisode = episode,
                currentScreen = Screen.GAME,
                currentRound = GameRound.ROUND_1_WHO_AM_I,
                totalScore = 0,
                roundScores = emptyMap(),
                roundResults = emptyList(),
                unlockedHintsCount = 1,
                round1Input = "",
                round1Finished = false,
                round1ErrorFeedback = null,
                round2Input = "",
                round2Finished = false,
                round2ErrorFeedback = null,
                round3Input = "",
                round3Finished = false,
                round3RevealedName = null,
                round3ErrorFeedback = null,
                round4Input = "",
                round4AcceptedEntries = emptyList(),
                round4Finished = false,
                round4Feedback = null,
                speedQuestionIndex = 0,
                speedAnsweredChoice = null,
                speedCorrectCount = 0,
                round5Finished = false,
                isTwoPlayerMode = twoPlayerMode,
                player1Score = 0,
                player2Score = 0,
                activePlayerNumber = 1
            )
        }
        soundManager.playWhistle()
        greetForCurrentRound()
    }

    fun goToHome() {
        soundManager.stopSpeaking()
        _uiState.update {
            it.copy(currentScreen = Screen.HOME)
        }
    }

    fun toggleSound() {
        val next = !_uiState.value.isSoundEnabled
        soundManager.isSoundEnabled = next
        soundManager.isTtsEnabled = next
        _uiState.update { it.copy(isSoundEnabled = next, isTtsEnabled = next) }
    }

    fun speakCurrentHostSpeech() {
        soundManager.speak(_uiState.value.hostSpeech)
    }

    // ----------------------------------------------------
    // ROUND 1: من أنا؟ (Who Am I)
    // ----------------------------------------------------
    fun onRound1InputChange(text: String) {
        _uiState.update { it.copy(round1Input = text, round1ErrorFeedback = null) }
    }

    fun revealNextHintRound1() {
        val currentCount = _uiState.value.unlockedHintsCount
        val maxHints = _uiState.value.selectedEpisode.whoAmI.hints.size
        if (currentCount < maxHints) {
            val nextCount = currentCount + 1
            val nextSpeech = "إليك التلميح رقم $nextCount! ركز جيداً، الإجابة الآن أصبحت أسهل ولكن بنقاط أقل!"
            _uiState.update {
                it.copy(
                    unlockedHintsCount = nextCount,
                    hostSpeech = nextSpeech,
                    hostMood = HostMood.THINKING,
                    round1ErrorFeedback = null
                )
            }
            soundManager.playTick()
            soundManager.speak(nextSpeech)
        }
    }

    fun submitRound1Answer() {
        val state = _uiState.value
        val input = state.round1Input.trim()
        if (input.isBlank() || state.round1Finished) return

        val whoAmI = state.selectedEpisode.whoAmI
        val allAccepted = listOf(whoAmI.playerName) + whoAmI.alternativeNames
        val isCorrect = ArabicTextNormalizer.matchesAny(input, allAccepted)

        val pointsTable = listOf(10, 7, 5, 2)
        val pointsToAward = pointsTable.getOrElse(state.unlockedHintsCount - 1) { 2 }

        if (isCorrect) {
            soundManager.playCorrect()
            val speech = "يا رباه! إجابة صحيحة وفي مقتل! اللاعب هو أسطورتنا ${whoAmI.playerName}! حصلت على $pointsToAward نقاط كاملة! 🌟"
            soundManager.speak(speech)

            val updatedRoundScores = state.roundScores + (GameRound.ROUND_1_WHO_AM_I to pointsToAward)
            val newResult = RoundResult(
                round = GameRound.ROUND_1_WHO_AM_I,
                scoreEarned = pointsToAward,
                isSuccess = true,
                playerAnswer = input,
                correctAnswer = whoAmI.playerName,
                triviaNote = whoAmI.triviaFact
            )

            _uiState.update {
                it.copy(
                    round1Finished = true,
                    totalScore = state.totalScore + pointsToAward,
                    roundScores = updatedRoundScores,
                    roundResults = state.roundResults + newResult,
                    hostSpeech = speech,
                    hostMood = HostMood.CELEBRATING,
                    round1ErrorFeedback = null
                )
            }
        } else {
            soundManager.playWrong()
            val errorMsg = if (state.unlockedHintsCount < whoAmI.hints.size) {
                "إجابة غير صحيحة! يمكنك المحاولة مجدداً أو طلب التلميح التالي رقم ${state.unlockedHintsCount + 1}."
            } else {
                "للأسف إجابة خاطئة! استنفدت جميع التلميحات الأربعة."
            }
            val speech = "لا لا لا! إجابة خاطئة! ركز معي في التفاصيل!"
            soundManager.speak(speech)

            _uiState.update {
                it.copy(
                    round1ErrorFeedback = errorMsg,
                    hostSpeech = speech,
                    hostMood = HostMood.SURPRISED
                )
            }
        }
    }

    fun skipRound1() {
        val state = _uiState.value
        if (state.round1Finished) return
        val whoAmI = state.selectedEpisode.whoAmI
        soundManager.playWrong()
        val speech = "للأسف لم تعرف اللاعب! اللاعب المقصود هو الأسطورة ${whoAmI.playerName}. لا بأس، معوضة في الجولة القادمة!"
        soundManager.speak(speech)

        val updatedRoundScores = state.roundScores + (GameRound.ROUND_1_WHO_AM_I to 0)
        val newResult = RoundResult(
            round = GameRound.ROUND_1_WHO_AM_I,
            scoreEarned = 0,
            isSuccess = false,
            playerAnswer = "تخطي",
            correctAnswer = whoAmI.playerName,
            triviaNote = whoAmI.triviaFact
        )

        _uiState.update {
            it.copy(
                round1Finished = true,
                roundScores = updatedRoundScores,
                roundResults = state.roundResults + newResult,
                hostSpeech = speech,
                hostMood = HostMood.DRAMATIC
            )
        }
    }

    // ----------------------------------------------------
    // ROUND 2: الرابط العجيب / مسيرة لاعب
    // ----------------------------------------------------
    fun onRound2InputChange(text: String) {
        _uiState.update { it.copy(round2Input = text, round2ErrorFeedback = null) }
    }

    fun submitRound2Answer() {
        val state = _uiState.value
        val input = state.round2Input.trim()
        if (input.isBlank() || state.round2Finished) return

        val career = state.selectedEpisode.careerPath
        val allAccepted = listOf(career.playerName) + career.alternativeNames
        val isCorrect = ArabicTextNormalizer.matchesAny(input, allAccepted)

        if (isCorrect) {
            soundManager.playCorrect()
            val pointsToAward = 10
            val speech = "الله عليك يا فنان! صاحب هذه المسيرة التاريخية هو بالفعل ${career.playerName}! 10 نقاط مستحقة بجدارة! 🔥"
            soundManager.speak(speech)

            val updatedRoundScores = state.roundScores + (GameRound.ROUND_2_CAREER to pointsToAward)
            val newResult = RoundResult(
                round = GameRound.ROUND_2_CAREER,
                scoreEarned = pointsToAward,
                isSuccess = true,
                playerAnswer = input,
                correctAnswer = career.playerName,
                triviaNote = career.triviaFact
            )

            _uiState.update {
                it.copy(
                    round2Finished = true,
                    totalScore = state.totalScore + pointsToAward,
                    roundScores = updatedRoundScores,
                    roundResults = state.roundResults + newResult,
                    hostSpeech = speech,
                    hostMood = HostMood.CELEBRATING,
                    round2ErrorFeedback = null
                )
            }
        } else {
            soundManager.playWrong()
            val errorMsg = "إجابة خاطئة! هذا اللاعب لم يلعب لهذه الأندية بالترتيب الزمني المعروض."
            val speech = "خطأ! فكّر في الأندية وسنوات الانتقال مرة أخرى!"
            soundManager.speak(speech)

            _uiState.update {
                it.copy(
                    round2ErrorFeedback = errorMsg,
                    hostSpeech = speech,
                    hostMood = HostMood.SURPRISED
                )
            }
        }
    }

    fun skipRound2() {
        val state = _uiState.value
        if (state.round2Finished) return
        val career = state.selectedEpisode.careerPath
        soundManager.playWrong()
        val speech = "صاحب هذه المسيرة التاريخية هو الأسطورة ${career.playerName}! ننتقل الآن إلى المستطيل الأخضر!"
        soundManager.speak(speech)

        val updatedRoundScores = state.roundScores + (GameRound.ROUND_2_CAREER to 0)
        val newResult = RoundResult(
            round = GameRound.ROUND_2_CAREER,
            scoreEarned = 0,
            isSuccess = false,
            playerAnswer = "تخطي",
            correctAnswer = career.playerName,
            triviaNote = career.triviaFact
        )

        _uiState.update {
            it.copy(
                round2Finished = true,
                roundScores = updatedRoundScores,
                roundResults = state.roundResults + newResult,
                hostSpeech = speech,
                hostMood = HostMood.DRAMATIC
            )
        }
    }

    // ----------------------------------------------------
    // ROUND 3: التشكيلة الناقصة (Missing Lineup)
    // ----------------------------------------------------
    fun onRound3InputChange(text: String) {
        _uiState.update { it.copy(round3Input = text, round3ErrorFeedback = null) }
    }

    fun submitRound3Answer() {
        val state = _uiState.value
        val input = state.round3Input.trim()
        if (input.isBlank() || state.round3Finished) return

        val lineup = state.selectedEpisode.lineup
        val allAccepted = listOf(lineup.mysteryPlayerName) + lineup.alternativeNames
        val isCorrect = ArabicTextNormalizer.matchesAny(input, allAccepted)

        if (isCorrect) {
            soundManager.playCorrect()
            val pointsToAward = 10
            val speech = "عين الصقر! إجابة عبقرية! اللاعب المجهول في التشكيلة هو بالفعل ${lineup.mysteryPlayerName}! 10 نقاط في المرمى! ⚽"
            soundManager.speak(speech)

            val updatedRoundScores = state.roundScores + (GameRound.ROUND_3_LINEUP to pointsToAward)
            val newResult = RoundResult(
                round = GameRound.ROUND_3_LINEUP,
                scoreEarned = pointsToAward,
                isSuccess = true,
                playerAnswer = input,
                correctAnswer = lineup.mysteryPlayerName,
                triviaNote = lineup.triviaFact
            )

            _uiState.update {
                it.copy(
                    round3Finished = true,
                    round3RevealedName = lineup.mysteryPlayerName,
                    totalScore = state.totalScore + pointsToAward,
                    roundScores = updatedRoundScores,
                    roundResults = state.roundResults + newResult,
                    hostSpeech = speech,
                    hostMood = HostMood.CELEBRATING,
                    round3ErrorFeedback = null
                )
            }
        } else {
            soundManager.playWrong()
            val errorMsg = "إجابة خاطئة! هذا اللاعب لم يبدأ أساسياً في هذه المباراة التاريخية."
            val speech = "يا ساتر! إجابة خاطئة! فكّر في المركز والتكتيك!"
            soundManager.speak(speech)

            _uiState.update {
                it.copy(
                    round3ErrorFeedback = errorMsg,
                    hostSpeech = speech,
                    hostMood = HostMood.SURPRISED
                )
            }
        }
    }

    fun skipRound3() {
        val state = _uiState.value
        if (state.round3Finished) return
        val lineup = state.selectedEpisode.lineup
        soundManager.playWrong()
        val speech = "اللاعب المفقود في هذا النهائي التاريخي كان ${lineup.mysteryPlayerName}! والآن إلى التحدي الأقوى: المزاد!"
        soundManager.speak(speech)

        val updatedRoundScores = state.roundScores + (GameRound.ROUND_3_LINEUP to 0)
        val newResult = RoundResult(
            round = GameRound.ROUND_3_LINEUP,
            scoreEarned = 0,
            isSuccess = false,
            playerAnswer = "تخطي",
            correctAnswer = lineup.mysteryPlayerName,
            triviaNote = lineup.triviaFact
        )

        _uiState.update {
            it.copy(
                round3Finished = true,
                round3RevealedName = lineup.mysteryPlayerName,
                roundScores = updatedRoundScores,
                roundResults = state.roundResults + newResult,
                hostSpeech = speech,
                hostMood = HostMood.DRAMATIC
            )
        }
    }

    // ----------------------------------------------------
    // ROUND 4: تحدي المزاد (Auction Challenge)
    // ----------------------------------------------------
    fun onRound4InputChange(text: String) {
        _uiState.update { it.copy(round4Input = text, round4Feedback = null) }
    }

    fun submitAuctionItem() {
        val state = _uiState.value
        val input = state.round4Input.trim()
        if (input.isBlank() || state.round4Finished) return

        val auction = state.selectedEpisode.auction

        // Check if already entered
        val alreadyEntered = state.round4AcceptedEntries.any {
            ArabicTextNormalizer.matchesAny(input, listOf(it))
        }

        if (alreadyEntered) {
            soundManager.playWrong()
            _uiState.update {
                it.copy(round4Feedback = "لقد قمت بذكر اسم '$input' مسبقاً! اكتب اسماً آخر.")
            }
            return
        }

        // Check against valid answer database
        val isValid = ArabicTextNormalizer.matchesAny(input, auction.validAnswers)

        if (isValid) {
            soundManager.playCorrect()
            val newEntries = state.round4AcceptedEntries + input
            val isCompleted = newEntries.size >= auction.targetCount

            if (isCompleted) {
                finishAuction(newEntries)
            } else {
                val remaining = auction.targetCount - newEntries.size
                val speech = "اسم صحيح 100%! متبقي $remaining أسماء لتكتمل العلامة الكاملة!"
                soundManager.speak(speech)
                _uiState.update {
                    it.copy(
                        round4AcceptedEntries = newEntries,
                        round4Input = "",
                        round4Feedback = "إجابة صحيحة! (+2 نقاط)",
                        hostSpeech = speech,
                        hostMood = HostMood.EXCITED
                    )
                }
            }
        } else {
            soundManager.playWrong()
            _uiState.update {
                it.copy(
                    round4Feedback = "اسم غير صحيح لهذا التحدي! حاول مجدداً.",
                    hostSpeech = "لا لا، هذا الاسم لا يطابق شروط التحدي!",
                    hostMood = HostMood.THINKING
                )
            }
        }
    }

    fun finishAuction(finalEntries: List<String> = _uiState.value.round4AcceptedEntries) {
        val state = _uiState.value
        if (state.round4Finished) return

        val auction = state.selectedEpisode.auction
        val earnedScore = (finalEntries.size * 2).coerceAtMost(10)
        soundManager.playFanfare()

        val speech = if (earnedScore == 10) {
            "يا عيني على المزاد! أوفيت بوعدك وحققت 10 نقاط كاملة في التحدي! 👏🔥"
        } else {
            "أحسنت! نجحت في ذكر ${finalEntries.size} من أصل ${auction.targetCount}، وحصلت على $earnedScore نقاط!"
        }
        soundManager.speak(speech)

        val updatedRoundScores = state.roundScores + (GameRound.ROUND_4_AUCTION to earnedScore)
        val newResult = RoundResult(
            round = GameRound.ROUND_4_AUCTION,
            scoreEarned = earnedScore,
            isSuccess = earnedScore > 0,
            playerAnswer = finalEntries.joinToString("، "),
            correctAnswer = auction.challengePrompt,
            triviaNote = auction.triviaFact
        )

        _uiState.update {
            it.copy(
                round4Finished = true,
                round4AcceptedEntries = finalEntries,
                round4Input = "",
                totalScore = state.totalScore + earnedScore,
                roundScores = updatedRoundScores,
                roundResults = state.roundResults + newResult,
                hostSpeech = speech,
                hostMood = HostMood.CELEBRATING
            )
        }
    }

    // ----------------------------------------------------
    // ROUND 5: أسئلة السرعة (Speed Round)
    // ----------------------------------------------------
    fun answerSpeedQuestion(userAnswer: Boolean) {
        val state = _uiState.value
        if (state.round5Finished || state.speedAnsweredChoice != null) return

        val speedRound = state.selectedEpisode.speed
        val currentQ = speedRound.questions.getOrNull(state.speedQuestionIndex) ?: return

        val isCorrect = (userAnswer == currentQ.isTrue)
        val newCorrectCount = if (isCorrect) state.speedCorrectCount + 1 else state.speedCorrectCount

        if (isCorrect) {
            soundManager.playCorrect()
        } else {
            soundManager.playWrong()
        }

        _uiState.update {
            it.copy(
                speedAnsweredChoice = userAnswer,
                speedCorrectCount = newCorrectCount
            )
        }

        // Host commentary
        val hostMsg = if (isCorrect) "إجابة سريعة وصحيحة! (+2 نقاط)" else "خطأ في السرعة!"
        _uiState.update { it.copy(hostSpeech = hostMsg, hostMood = if (isCorrect) HostMood.EXCITED else HostMood.DRAMATIC) }
    }

    fun nextSpeedQuestion() {
        val state = _uiState.value
        val speedRound = state.selectedEpisode.speed
        val nextIndex = state.speedQuestionIndex + 1

        if (nextIndex < speedRound.questions.size) {
            _uiState.update {
                it.copy(
                    speedQuestionIndex = nextIndex,
                    speedAnsweredChoice = null,
                    hostSpeech = "السؤال رقم ${nextIndex + 1}: صح أم خطأ؟ ركّز وأجب بسرعة!",
                    hostMood = HostMood.THINKING
                )
            }
            soundManager.playTick()
        } else {
            // Finished speed round
            val speedPoints = (state.speedCorrectCount * 2).coerceAtMost(10)
            val updatedRoundScores = state.roundScores + (GameRound.ROUND_5_SPEED to speedPoints)
            val grandTotal = state.totalScore + speedPoints

            val speedSummary = "${state.speedCorrectCount} إجابات صحيحة من 5"
            val newResult = RoundResult(
                round = GameRound.ROUND_5_SPEED,
                scoreEarned = speedPoints,
                isSuccess = speedPoints >= 6,
                playerAnswer = speedSummary,
                correctAnswer = "5 معلومات كروية سريعة",
                triviaNote = "انتهت جولة السرعة الحاسمة بنجاح!"
            )

            soundManager.playFanfare()
            val finalVerdictSpeech = getFinalVerdictSpeech(grandTotal)
            soundManager.speak(finalVerdictSpeech)

            _uiState.update {
                it.copy(
                    round5Finished = true,
                    totalScore = grandTotal,
                    roundScores = updatedRoundScores,
                    roundResults = state.roundResults + newResult,
                    currentRound = GameRound.RESULTS,
                    currentScreen = Screen.RESULTS,
                    hostSpeech = finalVerdictSpeech,
                    hostMood = HostMood.CELEBRATING
                )
            }
        }
    }

    // ----------------------------------------------------
    // ROUND NAVIGATION
    // ----------------------------------------------------
    fun nextRound() {
        val current = _uiState.value.currentRound
        val next = when (current) {
            GameRound.ROUND_1_WHO_AM_I -> GameRound.ROUND_2_CAREER
            GameRound.ROUND_2_CAREER -> GameRound.ROUND_3_LINEUP
            GameRound.ROUND_3_LINEUP -> GameRound.ROUND_4_AUCTION
            GameRound.ROUND_4_AUCTION -> GameRound.ROUND_5_SPEED
            GameRound.ROUND_5_SPEED -> GameRound.RESULTS
            GameRound.RESULTS -> GameRound.RESULTS
        }

        if (next == GameRound.RESULTS) {
            _uiState.update { it.copy(currentRound = next, currentScreen = Screen.RESULTS) }
        } else {
            _uiState.update { it.copy(currentRound = next) }
            greetForCurrentRound()
        }
    }

    private fun greetForCurrentRound() {
        val state = _uiState.value
        val round = state.currentRound
        val speech = when (round) {
            GameRound.ROUND_1_WHO_AM_I -> {
                "أهلاً بك في الجولة الأولى: فقرة 'من أنا؟'! أمامك 4 تلميحات متدرجة للاعب كروي، التلميح الأول بـ 10 نقاط كاملة! فكّر وأجب الآن!"
            }
            GameRound.ROUND_2_CAREER -> {
                "والآن مع الجولة الثانية: 'الرابط العجيب ومسيرة لاعب'! أمامك الأندية التي لعب لها نجمنا بالترتيب الزمني! من هو هذا اللاعب؟"
            }
            GameRound.ROUND_3_LINEUP -> {
                "الجولة الثالثة: 'التشكيلة الناقصة'! مباراة تاريخية خالدة، وتشكيلة تكتيكية كاملة إلا نجماً واحداً هو 'اللاعب المجهول ❓'! اكشف هويته!"
            }
            GameRound.ROUND_4_AUCTION -> {
                "الجولة الرابعة: 'تحدي المزاد'! التحدي هو: ${state.selectedEpisode.auction.challengePrompt}! اكتب الأسماء الصحيحة لتحصد 10 نقاط!"
            }
            GameRound.ROUND_5_SPEED -> {
                "الجولة الخامسة والأخيرة: 'أسئلة السرعة'! 5 معلومات كروية حاسمة، أجب بـ (صح) أو (خطأ)! كل ثانية لها ثمن!"
            }
            GameRound.RESULTS -> {
                getFinalVerdictSpeech(state.totalScore)
            }
        }
        _uiState.update { it.copy(hostSpeech = speech, hostMood = HostMood.WELCOME) }
        soundManager.speak(speech)
    }

    private fun getFinalVerdictSpeech(totalScore: Int): String {
        return when {
            totalScore >= 45 -> "ما هذا الأداء التاريخي! $totalScore من 50! أنت أسطورة الأساطير وموسوعة كروية تمشي على قدمين! مكانك في الاستوديو التحليلي بجدارة! 🏆👑"
            totalScore >= 35 -> "أداء استثنائي جداً! حققت $totalScore من 50! متابع من الطراز الرفيع وخبير في تاريخ المستديرة وتفاصيلها! كفو والله! 🥇🔥"
            totalScore >= 25 -> "أداء جيد جداً! $totalScore من 50! معلوماتك الكروية قوية ولكن بعض التفاصيل والتلميحات الصعبة فاجأتك! 🥈⚽"
            else -> "حصلت على $totalScore من 50! كانت محاولة شجاعة وتحدياً نارياً، تحتاج لمراجعة مباريات الأبطال والمونديال لتكتسح التحدي القادم! 📺⚽"
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
