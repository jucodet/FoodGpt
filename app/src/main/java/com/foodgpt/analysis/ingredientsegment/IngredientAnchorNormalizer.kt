package com.foodgpt.analysis.ingredientsegment

import java.text.Normalizer
import java.util.Locale

class IngredientAnchorNormalizer {

    fun normalize(input: String): String {
        val noDiacritics = Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
        return noDiacritics.lowercase(Locale.ROOT)
    }

    fun findFirstAnchorIndex(text: String, anchorWord: String = "ingredients"): Int? {
        val normalizedText = normalize(text)
        val normalizedAnchor = normalize(anchorWord)
        val index = normalizedText.indexOf(normalizedAnchor)
        return if (index >= 0) index else null
    }
}
