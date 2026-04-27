package com.foodgpt.gemma4local

/**
 * Point d'integration avec l'API Gemma4 exposee par le telephone.
 * Cette implementation est volontairement stricte v1: pas de fallback.
 */
class AndroidGemma4LocalGateway : Gemma4LocalApiGateway, Gemma4LocalAvailabilityProbe {
    override suspend fun analyzeText(inputText: String): String {
        throw IllegalStateException("API locale Gemma4 non connectee")
    }

    override suspend fun ping(): Boolean = false
}
