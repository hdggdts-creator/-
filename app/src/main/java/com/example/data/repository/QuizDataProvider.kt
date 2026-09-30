package com.example.data.repository

import com.example.data.model.*
import kotlin.random.Random

object QuizDataProvider {

    val episodePacks: List<EpisodePack> = listOf(
        EpisodePack(
            id = "ep_1",
            title = "الحلقة 1: كلاسيكو وأبطال أوروبا",
            subtitle = "أساطير العصر الذهبي ودوري أبطال أوروبا",
            era = "2000 - 2024",
            difficulty = "متوسط إلى صعب",
            iconEmoji = "🏆",
            variants = Episode1Data.variants
        ),
        EpisodePack(
            id = "ep_2",
            title = "الحلقة 2: ملحمة المونديال وأساطير التاريخ",
            subtitle = "كأس العالم، نهائي الماراكانا، وأرقام الأساطير القياسية",
            era = "1998 - 2022",
            difficulty = "صعب",
            iconEmoji = "🌍",
            variants = Episode2Data.variants
        ),
        EpisodePack(
            id = "ep_3",
            title = "الحلقة 3: ملحمة إسطنبول وسحر البريميرليج",
            subtitle = "أساطير الدوري الإنجليزي وأعظم ريمونتادا في تاريخ النهائيات",
            era = "2000 - 2023",
            difficulty = "للمتيمين الكرويين",
            iconEmoji = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
            variants = Episode3Data.variants
        ),
        EpisodePack(
            id = "ep_4",
            title = "الحلقة 4: ثلاثية الإنتر 2010 والملوك الأفارقة",
            subtitle = "تكتيك جوزيه مورينيو، دروغبا، وإيتو في قمة أوروبا",
            era = "2004 - 2020",
            difficulty = "صعب جداً",
            iconEmoji = "⚡",
            variants = Episode4Data.variants
        ),
        EpisodePack(
            id = "ep_5",
            title = "الحلقة 5: نهائي لوسيل 2022 وتاريخ المونديال",
            subtitle = "أعظم نهائي في تاريخ كرة القدم وصراع ميسي ومبابي",
            era = "2010 - 2024",
            difficulty = "متوسط إلى صعب",
            iconEmoji = "⭐",
            variants = Episode5Data.variants
        )
    )

    // Public list of default episodes
    val episodes: List<QuizEpisode> = episodePacks.map { it.toQuizEpisode(0) }

    /**
     * Anti-Repetition logic:
     * Selects [count] distinct variants for an episode from available unused variants.
     * When fewer than [count] variants remain unused in the 10-pack,
     * it gracefully pulls from the remaining pool (avoiding intra-session duplicates).
     */
    fun getDistinctVariantsForEpisode(
        episodeId: String,
        playedIndices: Set<Int>,
        count: Int = 1
    ): List<QuizEpisode> {
        val pack = episodePacks.find { it.id == episodeId } ?: episodePacks[0]
        val totalVariants = pack.variants.size // 10

        val available = (0 until totalVariants).filter { it !in playedIndices }.shuffled().toMutableList()
        val chosenIndices = mutableListOf<Int>()

        // Take from available
        while (chosenIndices.size < count && available.isNotEmpty()) {
            chosenIndices.add(available.removeAt(0))
        }

        // If we still need more (e.g. pool had only 1 left and we need 2 for two-player),
        // take from the remaining pool (excluding already chosen in this call)
        if (chosenIndices.size < count) {
            val remainingPool = (0 until totalVariants).filter { it !in chosenIndices }.shuffled()
            for (idx in remainingPool) {
                chosenIndices.add(idx)
                if (chosenIndices.size == count) break
            }
        }

        return chosenIndices.map { pack.toQuizEpisode(it) }
    }

    /**
     * Selects a single variant of the given episode that the user has NOT played recently.
     */
    fun getEpisodeWithAntiRepetition(episodeId: String, recentlyPlayed: Collection<Int>): QuizEpisode {
        return getDistinctVariantsForEpisode(episodeId, recentlyPlayed.toSet(), count = 1).first()
    }

    fun getRandomEpisodeWithAntiRepetition(recentlyPlayedMap: Map<String, Collection<Int>>): QuizEpisode {
        val randomPack = episodePacks.random()
        val history = recentlyPlayedMap[randomPack.id] ?: emptyList()
        return getEpisodeWithAntiRepetition(randomPack.id, history)
    }
}
