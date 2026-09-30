package com.example.util

object ArabicTextNormalizer {

    fun normalize(text: String): String {
        var clean = text.trim().lowercase()

        // Remove diacritics / tashkeel
        clean = clean.replace(Regex("[\u064B-\u0652\u0670]"), "")
        // Remove Tatweel / Kashida
        clean = clean.replace("ـ", "")

        // Normalize Alif forms
        clean = clean.replace(Regex("[أإآٱ]"), "ا")
        // Normalize Taa Marbuta
        clean = clean.replace("ة", "ه")
        // Normalize Yaa / Alif Maqsura
        clean = clean.replace("ى", "ي")

        // Remove punctuation, dashes, dots
        clean = clean.replace(Regex("[\\p{Punct}&&[^_-]]"), "")
        clean = clean.replace(Regex("[_-]"), " ")

        // Collapse multiple whitespaces
        clean = clean.replace(Regex("\\s+"), " ").trim()

        return clean
    }

    /**
     * Checks if the user's input matches any of the accepted names,
     * taking into account Arabic normalization and common naming prefixes like "ال".
     */
    fun matchesAny(userInput: String, acceptedNames: List<String>): Boolean {
        val userNorm = normalize(userInput)
        if (userNorm.isBlank()) return false

        val userWithoutAl = if (userNorm.startsWith("ال") && userNorm.length > 3) {
            userNorm.removePrefix("ال")
        } else userNorm

        for (candidate in acceptedNames) {
            val candNorm = normalize(candidate)
            if (userNorm == candNorm) return true
            if (userWithoutAl == candNorm) return true

            val candWithoutAl = if (candNorm.startsWith("ال") && candNorm.length > 3) {
                candNorm.removePrefix("ال")
            } else candNorm

            if (userNorm == candWithoutAl || userWithoutAl == candWithoutAl) return true

            // Substring match for long names if user entered only famous family name or nickname
            // e.g. user entered "زيدان" or "ميسي" or "دروغبا" and candidate is "زين الدين زيدان"
            val tokens = candNorm.split(" ")
            if (tokens.any { it == userNorm || it == userWithoutAl }) {
                if (userNorm.length >= 3) return true
            }
        }
        return false
    }
}
