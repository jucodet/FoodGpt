package com.foodgpt.camera

import java.time.Instant

data class CaptureAction(
    val actionId: String,
    val sessionId: String,
    val triggeredAt: Instant,
    val sourceControl: String,
    val status: String,
    val ignoreReason: String? = null
)
