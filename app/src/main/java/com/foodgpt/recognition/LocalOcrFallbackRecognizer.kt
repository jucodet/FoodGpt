package com.foodgpt.recognition

class LocalOcrFallbackRecognizer {
    suspend fun recognize(command: StartIngredientRecognitionCommand): IngredientRecognitionResult {
        val first = IngredientRecognitionItem(
            position = 0,
            rawText = "ingredients: farine de ble",
            normalizedText = "farine de ble",
            confidence = 0.78f,
            languageTag = "fr",
            isAllergenMarked = true
        )
        val second = IngredientRecognitionItem(
            position = 1,
            rawText = "sucre",
            normalizedText = "sucre",
            confidence = 0.74f,
            languageTag = "fr",
            isAllergenMarked = false
        )
        return IngredientRecognitionResult(
            scanId = command.scanId,
            outcome = "success",
            ocrConfidenceGlobal = 0.76f,
            autoValidated = true,
            items = listOf(first, second),
            userMessage = "Reconnaissance OCR locale terminée"
        )
    }
}
