package com.foodgpt.gemma4local

import android.util.Log
import com.foodgpt.analysis.AnalysisLatencyTracker
import com.foodgpt.gemma4local.model.ApiCallMetric

class Gemma4LocalMetricsLogger {
    fun log(metric: ApiCallMetric) {
        AnalysisLatencyTracker.record(metric)
        Log.i(
            TAG,
            "requestId=${metric.requestId} outcome=${metric.outcome} latencyMs=${metric.latencyMs} " +
                "errorType=${metric.errorType} deviceClass=${metric.deviceClass}"
        )
    }

    companion object {
        private const val TAG = "Gemma4LocalMetrics"
    }
}
