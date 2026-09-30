package com.example.data.model

data class ClubStep(
    val clubName: String,
    val period: String,
    val countryFlag: String = "⚽"
)

data class PlayerHintRound(
    val id: String,
    val title: String = "من أنا؟",
    val playerName: String,
    val alternativeNames: List<String>,
    val hints: List<String>, // 4 progressive hints from hardest to easiest
    val triviaFact: String,
    val nationalityEmoji: String = "🌍",
    val position: String = ""
)

data class PitchPlayer(
    val name: String,
    val position: String,
    val xPercent: Float, // 0.0 (left) to 1.0 (right) on tactical pitch
    val yPercent: Float, // 0.0 (goalkeeper bottom) to 1.0 (striker top)
    val number: String = "",
    val isMystery: Boolean = false
)

data class LineupRound(
    val id: String,
    val matchTitle: String,
    val teamName: String,
    val year: String,
    val formation: String,
    val players: List<PitchPlayer>,
    val mysteryPlayerName: String,
    val alternativeNames: List<String>,
    val clueText: String = "",
    val triviaFact: String
)

data class CareerPathRound(
    val id: String,
    val title: String = "الرابط العجيب / مسيرة لاعب",
    val playerName: String,
    val alternativeNames: List<String>,
    val clubs: List<ClubStep>,
    val extraClue: String = "",
    val triviaFact: String
)

data class AuctionRound(
    val id: String,
    val challengePrompt: String,
    val targetCount: Int = 5,
    val validAnswers: List<String>,
    val triviaFact: String
)

data class SpeedItem(
    val id: String,
    val statement: String,
    val isTrue: Boolean,
    val explanation: String
)

data class SpeedRound(
    val id: String,
    val title: String = "أسئلة السرعة (صح أو خطأ)",
    val questions: List<SpeedItem>
)

data class QuizEpisode(
    val id: String,
    val title: String,
    val subtitle: String,
    val era: String,
    val difficulty: String,
    val iconEmoji: String,
    val whoAmI: PlayerHintRound,
    val careerPath: CareerPathRound,
    val lineup: LineupRound,
    val auction: AuctionRound,
    val speed: SpeedRound
)

enum class GameRound(val roundNumber: Int, val titleAr: String, val maxScore: Int) {
    ROUND_1_WHO_AM_I(1, "فقرة من أنا؟", 10),
    ROUND_2_CAREER(2, "الرابط العجيب", 10),
    ROUND_3_LINEUP(3, "التشكيلة الناقصة", 10),
    ROUND_4_AUCTION(4, "تحدي المزاد", 10),
    ROUND_5_SPEED(5, "أسئلة السرعة", 10),
    RESULTS(6, "التقييم النهائي", 50)
}

data class RoundResult(
    val round: GameRound,
    val scoreEarned: Int,
    val maxScore: Int = 10,
    val isSuccess: Boolean,
    val playerAnswer: String = "",
    val correctAnswer: String = "",
    val triviaNote: String = ""
)
