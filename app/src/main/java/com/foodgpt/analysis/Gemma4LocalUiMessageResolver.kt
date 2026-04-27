package com.foodgpt.analysis

import com.foodgpt.composition.CompositionMessages
import com.foodgpt.composition.GemmaErrorCode

object Gemma4LocalUiMessageResolver {
    fun resolve(code: GemmaErrorCode, backendMessage: String): String {
        return when (code) {
            GemmaErrorCode.GEMMA_NOT_FOUND -> "API locale Gemma4 indisponible sur cet appareil."
            GemmaErrorCode.GEMMA_TIMEOUT -> CompositionMessages.GEMMA_TIMEOUT_USER
            GemmaErrorCode.GEMMA_LOAD_FAILED -> backendMessage.ifBlank {
                CompositionMessages.GEMMA_LOAD_FAILED_USER
            }
        }
    }
}
