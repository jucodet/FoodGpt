package com.foodgpt.core

object FeatureConfig {
    const val AUTO_VALIDATE_OCR_THRESHOLD = 0.70f
    const val DEFAULT_MAX_PROCESSING_MS = 8_000L

    /** Cible produit: aperçu visible rapidement (spec SC-001). */
    const val PREVIEW_START_TARGET_MS = 3_000L

    /** Feedback utilisateur post-capture (plan). */
    const val CAPTURE_FEEDBACK_TARGET_MS = 300L
}
