package com.example.data.repository

import com.example.data.model.EpisodeVariant

/**
 * Episode 1 Data: كلاسيكيات أوروبا ودوري الأبطال
 * Contains 10 completely distinct, historically verified sets (variants 0 to 9).
 */
object Episode1Data {
    val variants: List<EpisodeVariant> = ep1Variants0to4 + ep1Variants5to9
}
