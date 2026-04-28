package com.foodgpt.analysis.ingredientsegment

import java.text.Normalizer
import java.util.Locale

class IngredientAnchorNormalizer {
    private val canonicalAnchorRegex = Regex("ingredients\\s*:", RegexOption.IGNORE_CASE)

    fun normalize(input: String): String {
        val noDiacritics = Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
        return noDiacritics.lowercase(Locale.ROOT)
    }

    fun findFirstAnchorIndex(text: String, anchorWord: String = "ingredients"): Int? {
        // First try the canonical list anchor to avoid intro sentence captures.
        val canonicalIndex = canonicalAnchorRegex.find(text)?.range?.first
        if (canonicalIndex != null) return canonicalIndex

        val normalizedText = normalize(text)
        val normalizedAnchor = normalize(anchorWord)
        val fallbackIndex = normalizedText.indexOf(normalizedAnchor)
        return if (fallbackIndex >= 0) fallbackIndex else null
    }
}
