package com.foodgpt.analysis.ingredientsegment

class IngredientSegmentBoundaryResolver {

    data class Resolution(
        val endIndexExclusive: Int,
        val fallbackMode: IngredientSegmentFallbackMode
    )

    fun resolveEnd(text: String, anchorIndex: Int): Resolution {
        val newlineIndex = text.indexOf('\n', startIndex = anchorIndex)
        return if (newlineIndex >= 0) {
            Resolution(
                endIndexExclusive = newlineIndex,
                fallbackMode = IngredientSegmentFallbackMode.NONE
            )
        } else {
            Resolution(
                endIndexExclusive = text.length,
                fallbackMode = IngredientSegmentFallbackMode.NO_NEWLINE_TO_EOF
            )
        }
    }
}
