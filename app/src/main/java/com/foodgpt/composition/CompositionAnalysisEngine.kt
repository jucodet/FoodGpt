package com.foodgpt.composition

interface CompositionAnalysisEngine {
    suspend fun analyze(
        rawText: String,
        maxInferenceMs: Long
    ): AnalyzeCompositionResult
}
