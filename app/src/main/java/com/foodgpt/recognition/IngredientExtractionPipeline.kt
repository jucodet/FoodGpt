package com.foodgpt.recognition

class IngredientExtractionPipeline {
    fun extractOrderedItems(rawText: String, confidence: Float): List<IngredientRecognitionItem> {
        return rawText
            .substringAfter(":", rawText)
            .split(",", ";")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .mapIndexed { index, token ->
                IngredientRecognitionItem(
                    position = index,
                    rawText = token,
                    normalizedText = token.lowercase(),
                    confidence = confidence,
                    languageTag = "auto",
                    isAllergenMarked = token.any { it.isUpperCase() } || token.contains("ble", ignoreCase = true)
                )
            }
    }
}
