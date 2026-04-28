package com.foodgpt.gemma4local

import kotlinx.coroutines.withTimeoutOrNull

fun interface Gemma4LocalAvailabilityProbe {
    suspend fun ping(): Boolean
}

class Gemma4LocalAvailabilityChecker(
    private val probe: Gemma4LocalAvailabilityProbe
) {
    suspend fun isAvailable(): Boolean {
        val ok = withTimeoutOrNull(Gemma4LocalConfig.AVAILABILITY_TIMEOUT_MS) {
            probe.ping()
        }
        return ok == true
    }
}
