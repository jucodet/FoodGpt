package com.foodgpt.analysis

object AnalysisInputBuilder {
    fun buildSegmentPayload(segmentText: String): String {
        return segmentText.trim()
    }
}
