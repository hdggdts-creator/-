package com.example

import com.example.data.repository.QuizDataProvider
import com.example.util.ArabicTextNormalizer
import org.junit.Assert.*
import org.junit.Test

class QuizLogicUnitTest {

    @Test
    fun testArabicNormalizerMatching() {
        val accepted = listOf("لوكا مودريتش", "مودريتش", "luka modric")

        // Variations of Alif and prefixes
        assertTrue(ArabicTextNormalizer.matchesAny("مودريتش", accepted))
        assertTrue(ArabicTextNormalizer.matchesAny("المودريتش", accepted))
        assertTrue(ArabicTextNormalizer.matchesAny("لوكا مودريتش", accepted))
        assertTrue(ArabicTextNormalizer.matchesAny("Luka Modric", accepted))
        assertTrue(ArabicTextNormalizer.matchesAny("  مودريتش  ", accepted))

        assertFalse(ArabicTextNormalizer.matchesAny("ميسي", accepted))
    }

    @Test
    fun testEpisodesCountAndCompleteness() {
        val episodes = QuizDataProvider.episodes
        assertTrue("Should have at least 5 episodes", episodes.size >= 5)

        for (ep in episodes) {
            assertEquals("Who Am I should have 4 hints", 4, ep.whoAmI.hints.size)
            assertTrue("Career path should have at least 4 clubs", ep.careerPath.clubs.size >= 4)
            assertEquals("Lineup should have 11 players", 11, ep.lineup.players.size)
            assertTrue("Lineup should contain exactly 1 mystery player", ep.lineup.players.count { it.isMystery } == 1)
            assertEquals("Speed round should have 5 questions", 5, ep.speed.questions.size)
            assertTrue("Auction round should have valid answers", ep.auction.validAnswers.isNotEmpty())
        }
    }

    @Test
    fun testEpisodePacksHave5VariantsEach() {
        val packs = QuizDataProvider.episodePacks
        assertEquals("Should have 5 episode packs", 5, packs.size)

        for (pack in packs) {
            assertEquals("Each pack should have exactly 5 variants", 5, pack.variants.size)
            for (v in pack.variants) {
                assertEquals(4, v.whoAmI.hints.size)
                assertTrue(v.careerPath.clubs.size >= 3)
                assertEquals(11, v.lineup.players.size)
                assertEquals(1, v.lineup.players.count { it.isMystery })
                assertEquals(5, v.speed.questions.size)
                assertTrue(v.auction.validAnswers.isNotEmpty())
            }
        }
    }

    @Test
    fun testAntiRepetitionAlgorithm() {
        val epId = "ep_1"
        val history = listOf(0, 1, 2)
        val freshEpisode = QuizDataProvider.getEpisodeWithAntiRepetition(epId, history)
        assertTrue(
            "Selected variant index should not be in recently played",
            freshEpisode.variantIndex !in history
        )
    }
}
