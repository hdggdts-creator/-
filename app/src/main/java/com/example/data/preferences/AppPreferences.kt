package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("tahadi_el30_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_DARK_THEME = "is_dark_theme"
        private const val KEY_PLAYED_VARIANTS_PREFIX = "played_variants_"
    }

    var isDarkTheme: Boolean
        get() = prefs.getBoolean(KEY_IS_DARK_THEME, true) // Default to true (Stadium Dark)
        set(value) = prefs.edit().putBoolean(KEY_IS_DARK_THEME, value).apply()

    fun getRecentlyPlayedVariants(episodeId: String): List<Int> {
        val raw = prefs.getString("$KEY_PLAYED_VARIANTS_PREFIX$episodeId", "") ?: ""
        if (raw.isBlank()) return emptyList()
        return raw.split(",").mapNotNull { it.toIntOrNull() }
    }

    fun recordPlayedVariant(episodeId: String, variantIndex: Int, maxHistory: Int = 4) {
        val current = getRecentlyPlayedVariants(episodeId).toMutableList()
        current.remove(variantIndex)
        current.add(0, variantIndex)
        val trimmed = current.take(maxHistory)
        prefs.edit().putString("$KEY_PLAYED_VARIANTS_PREFIX$episodeId", trimmed.joinToString(",")).apply()
    }
}
