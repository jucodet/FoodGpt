package com.foodgpt.gemma4local

import com.foodgpt.BuildConfig

object Gemma4LocalConfig {
    const val DEFAULT_TIMEOUT_MS: Long = 6_000
    const val AVAILABILITY_TIMEOUT_MS: Long = 1_500
    const val MAX_INPUT_CHARS: Int = 12_000
    val LOCAL_API_BASE_URL: String = BuildConfig.GEMMA_LOCAL_API_BASE_URL
    val LOCAL_API_PING_PATH: String = BuildConfig.GEMMA_LOCAL_API_PING_PATH
    val LOCAL_API_ANALYZE_PATH: String = BuildConfig.GEMMA_LOCAL_API_ANALYZE_PATH
}
