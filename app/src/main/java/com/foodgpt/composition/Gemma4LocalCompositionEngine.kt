package com.foodgpt.composition

import com.foodgpt.gemma4local.Gemma4LocalClient
import com.foodgpt.gemma4local.model.AnalyseTextuelleStatus

class Gemma4LocalCompositionEngine(
    private val localClient: Gemma4LocalClient
) : CompositionAnalysisEngine {
    override suspend fun analyze(
        rawText: String,
        maxInferenceMs: Long,
        onStreamPartial: ((String) -> Unit)?
    ): AnalyzeCompositionResult {
        val localResult = localClient.analyze(rawText)
        return if (localResult.status == AnalyseTextuelleStatus.SUCCESS && !localResult.outputText.isNullOrBlank()) {
            val parsed = GemmaBilanParser.parse(localResult.outputText)
            if (parsed != null) AnalyzeCompositionResult.BilanSuccess(parsed)
            else AnalyzeCompositionResult.CompositionLimit(CompositionMessages.COMPOSITION_LIMIT_GENERIC)
        } else {
            AnalyzeCompositionResult.GemmaError(
                GemmaErrorCode.GEMMA_NOT_FOUND,
                localResult.userMessage.ifBlank { CompositionMessages.GEMMA_LOAD_FAILED_USER }
            )
        }
    }
}
