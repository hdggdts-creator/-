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
     * Anti-Repetition algorithm:
     * Selects a variant of the given episode that the user has NOT played recently.
     */
    fun getEpisodeWithAntiRepetition(episodeId: String, recentlyPlayed: List<Int>): QuizEpisode {
        val pack = episodePacks.find { it.id == episodeId } ?: episodePacks[0]
        val totalVariants = pack.variants.size

        // Find available variant indices not in recently played
        val available = (0 until totalVariants).filter { it !in recentlyPlayed }

        val selectedIndex = if (available.isNotEmpty()) {
            available.random()
        } else {
            // If all 5 variants have been seen, pick the least recently played
            val candidate = (0 until totalVariants).minByOrNull { idx ->
                val pos = recentlyPlayed.indexOf(idx)
                if (pos == -1) 0 else pos
            } ?: Random.nextInt(totalVariants)
            candidate
        }

        return pack.toQuizEpisode(selectedIndex)
    }

    fun getRandomEpisodeWithAntiRepetition(recentlyPlayedMap: Map<String, List<Int>>): QuizEpisode {
        val randomPack = episodePacks.random()
        val history = recentlyPlayedMap[randomPack.id] ?: emptyList()
        return getEpisodeWithAntiRepetition(randomPack.id, history)
    }
}
