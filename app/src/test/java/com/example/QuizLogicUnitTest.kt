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
            assertTrue("Career path should have at least 3 clubs", ep.careerPath.clubs.size >= 3)
            assertEquals("Lineup should have 11 players", 11, ep.lineup.players.size)
            assertTrue("Lineup should contain exactly 1 mystery player", ep.lineup.players.count { it.isMystery } == 1)
            assertEquals("Speed round should have 5 questions", 5, ep.speed.questions.size)
        }
    }

    @Test
    fun testEpisodePacksHave10VariantsEach() {
        val packs = QuizDataProvider.episodePacks
        assertEquals("Should have 5 episode packs", 5, packs.size)

        for (pack in packs) {
            assertEquals("Each pack must have exactly 10 variants", 10, pack.variants.size)
            for (v in pack.variants) {
                assertEquals(4, v.whoAmI.hints.size)
                assertTrue(v.careerPath.clubs.size >= 3)
                assertEquals(11, v.lineup.players.size)
                assertEquals(1, v.lineup.players.count { it.isMystery })
                assertEquals(5, v.speed.questions.size)
                assertTrue(
                    v.auction.validAnswers.isNotEmpty() || v.auction.acceptableAnswers.isNotEmpty()
                )
            }
        }
    }

    @Test
    fun testAntiRepetitionSingleAndTwoPlayer() {
        val epId = "ep_1"

        // Single player: exclude already played
        val played = setOf(0, 1, 2, 3)
        val singleResult = QuizDataProvider.getDistinctVariantsForEpisode(epId, played, count = 1)
        assertEquals(1, singleResult.size)
        assertTrue(
            "Selected variant index must not be in played set",
            singleResult[0].variantIndex !in played
        )

        // Two player: returns 2 distinct unplayed variants
        val twoPlayerResults = QuizDataProvider.getDistinctVariantsForEpisode(epId, played, count = 2)
        assertEquals(2, twoPlayerResults.size)
        assertNotEquals(
            "Player 1 and Player 2 must receive different variants",
            twoPlayerResults[0].variantIndex,
            twoPlayerResults[1].variantIndex
        )
        assertTrue(twoPlayerResults[0].variantIndex !in played)
        assertTrue(twoPlayerResults[1].variantIndex !in played)
    }
}
