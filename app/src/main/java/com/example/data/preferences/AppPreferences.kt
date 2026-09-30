package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("tahadi_el30_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_DARK_THEME = "is_dark_theme"
        private const val KEY_PLAYED_VARIANTS_SET_PREFIX = "played_set_v4_"
    }

    var isDarkTheme: Boolean
        get() = prefs.getBoolean(KEY_IS_DARK_THEME, true) // Default to true (Stadium Dark)
        set(value) = prefs.edit().putBoolean(KEY_IS_DARK_THEME, value).apply()

    /**
     * Returns the set of variant indices (0..9) already consumed for a given episode.
     */
    fun getPlayedVariants(episodeId: String): Set<Int> {
        val raw = prefs.getString("$KEY_PLAYED_VARIANTS_SET_PREFIX$episodeId", "") ?: ""
        if (raw.isBlank()) return emptySet()
        return raw.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
    }

    /**
     * Alias for compatibility
     */
    fun getPlayedVariantsSet(episodeId: String): Set<Int> = getPlayedVariants(episodeId)

    fun getRecentlyPlayedVariants(episodeId: String): List<Int> = getPlayedVariants(episodeId).toList()

    /**
     * Records new variant indices as played.
     * If the total consumed reaches or exceeds [totalAvailable] (10 by default),
     * the pool is automatically cleared so the user starts a fresh randomized cycle.
     */
    fun recordPlayedVariants(episodeId: String, newIndices: Collection<Int>, totalAvailable: Int = 10) {
        val current = getPlayedVariants(episodeId).toMutableSet()
        current.addAll(newIndices)

        if (current.size >= totalAvailable) {
            // All 10 variants have been consumed! Reset pool cycle.
            prefs.edit().remove("$KEY_PLAYED_VARIANTS_SET_PREFIX$episodeId").apply()
        } else {
            prefs.edit().putString("$KEY_PLAYED_VARIANTS_SET_PREFIX$episodeId", current.joinToString(",")).apply()
        }
    }

    fun recordPlayedVariant(episodeId: String, index: Int, totalAvailable: Int = 10) {
        recordPlayedVariants(episodeId, listOf(index), totalAvailable)
    }

    /**
     * Manually resets the played variants for an episode.
     */
    fun resetPlayedVariants(episodeId: String) {
        prefs.edit().remove("$KEY_PLAYED_VARIANTS_SET_PREFIX$episodeId").apply()
    }
}
