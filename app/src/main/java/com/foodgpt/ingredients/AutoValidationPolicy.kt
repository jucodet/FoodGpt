package com.foodgpt.ingredients

import com.foodgpt.core.FeatureConfig

class AutoValidationPolicy {
    fun shouldAutoValidate(ocrConfidenceGlobal: Float): Boolean {
        return ocrConfidenceGlobal >= FeatureConfig.AUTO_VALIDATE_OCR_THRESHOLD
    }
}
