package com.foodgpt.composition

/**
 * Post-valide le bilan par rapport au texte source (US2 — pas d’ingrédients manifestement hors texte).
 */
object CompositionResultValidator {

    /** Rejet explicite d’un bilan « vide » présenté comme complet (FR-012). */
    fun rejectEmptyStructure(bilan: CompositionBilan): AnalyzeCompositionResult.CompositionLimit? {
        if (bilan.ingredientLines.isEmpty() || bilan.compositionAnalysis.isBlank()) {
            return AnalyzeCompositionResult.CompositionLimit(CompositionMessages.COMPOSITION_LIMIT_GENERIC)
        }
        return null
    }

    fun validateAgainstSource(bilan: CompositionBilan, rawText: String): AnalyzeCompositionResult {
        val normalizedSource = rawText.lowercase()
        val suspicious = bilan.ingredientLines.any { line ->
            val token = line.lowercase().trim()
            token.length >= 4 && !normalizedSource.contains(token)
        }
        if (suspicious) {
            return AnalyzeCompositionResult.CompositionLimit(
                CompositionMessages.COMPOSITION_LIMIT_GENERIC
            )
        }
        return AnalyzeCompositionResult.BilanSuccess(bilan)
    }
}
