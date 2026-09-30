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
}
