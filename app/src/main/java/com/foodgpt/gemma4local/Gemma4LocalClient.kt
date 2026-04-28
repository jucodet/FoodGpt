package com.foodgpt.gemma4local

import android.os.SystemClock
import com.foodgpt.gemma4local.model.AnalyseTextuelleErrorType
import com.foodgpt.gemma4local.model.AnalyseTextuelleResult
import com.foodgpt.gemma4local.model.AnalyseTextuelleStatus
import com.foodgpt.gemma4local.model.ApiCallMetric
import kotlinx.coroutines.withTimeout

fun interface Gemma4LocalApiGateway {
    suspend fun analyzeText(inputText: String): String
}

class Gemma4LocalClient(
    private val availabilityChecker: Gemma4LocalAvailabilityChecker,
    private val requestMapper: Gemma4LocalRequestMapper,
    private val errorMapper: Gemma4LocalErrorMapper,
    private val metricsLogger: Gemma4LocalMetricsLogger,
    private val deviceClassResolver: DeviceClassResolver,
    private val gateway: Gemma4LocalApiGateway
) {
    suspend fun analyze(rawText: String): AnalyseTextuelleResult {
        val request = requestMapper.map(rawText, sourceScreen = "camera")
        val started = SystemClock.elapsedRealtime()

        if (!availabilityChecker.isAvailable()) {
            val latency = SystemClock.elapsedRealtime() - started
            val result = AnalyseTextuelleResult(
                requestId = request.requestId,
                status = AnalyseTextuelleStatus.FAILED,
                errorType = AnalyseTextuelleErrorType.API_UNAVAILABLE,
                userMessage = Gemma4LocalMessages.API_UNAVAILABLE
            )
            metricsLogger.log(
                ApiCallMetric(
                    requestId = request.requestId,
                    outcome = AnalyseTextuelleStatus.FAILED,
                    latencyMs = latency,
                    errorType = AnalyseTextuelleErrorType.API_UNAVAILABLE,
                    deviceClass = deviceClassResolver.resolve()
                )
            )
            return result
        }

        return try {
            val output = withTimeout(Gemma4LocalConfig.DEFAULT_TIMEOUT_MS) {
                gateway.analyzeText(request.inputText)
            }.trim()
            val latency = SystemClock.elapsedRealtime() - started
            val result = if (output.isNotEmpty()) {
                AnalyseTextuelleResult(
                    requestId = request.requestId,
                    status = AnalyseTextuelleStatus.SUCCESS,
                    outputText = output
                )
            } else {
                AnalyseTextuelleResult(
                    requestId = request.requestId,
                    status = AnalyseTextuelleStatus.FAILED,
                    errorType = AnalyseTextuelleErrorType.INVALID_RESPONSE,
                    userMessage = Gemma4LocalMessages.INVALID_RESPONSE
                )
            }
            metricsLogger.log(
                ApiCallMetric(
                    requestId = request.requestId,
                    outcome = result.status,
                    latencyMs = latency,
                    errorType = result.errorType,
                    deviceClass = deviceClassResolver.resolve()
                )
            )
            result
        } catch (t: Throwable) {
            val mapped = errorMapper.map(t)
            val latency = SystemClock.elapsedRealtime() - started
            val failed = AnalyseTextuelleResult(
                requestId = request.requestId,
                status = AnalyseTextuelleStatus.FAILED,
                errorType = mapped.errorType,
                userMessage = mapped.userMessage
            )
            metricsLogger.log(
                ApiCallMetric(
                    requestId = request.requestId,
                    outcome = AnalyseTextuelleStatus.FAILED,
                    latencyMs = latency,
                    errorType = mapped.errorType,
                    deviceClass = deviceClassResolver.resolve()
                )
            )
            failed
        }
    }
}
