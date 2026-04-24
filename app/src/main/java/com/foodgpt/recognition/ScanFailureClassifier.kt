package com.foodgpt.recognition

class ScanFailureClassifier {
    fun classify(confidence: Float, textLength: Int): String {
        if (confidence < 0.35f) return "blur"
        if (textLength < 6) return "incomplete"
        return "low-contrast"
    }
}
