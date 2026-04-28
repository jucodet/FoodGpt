package com.foodgpt.gemma4local

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

/**
 * Point d'integration avec l'API Gemma4 exposee par le telephone.
 * Cette implementation est volontairement stricte v1: pas de fallback.
 */
class AndroidGemma4LocalGateway : Gemma4LocalApiGateway, Gemma4LocalAvailabilityProbe {
    override suspend fun analyzeText(inputText: String): String {
        return withContext(Dispatchers.IO) {
            val body = JSONObject().put("inputText", inputText).toString()
            val responseBody = performJsonRequest(
                path = Gemma4LocalConfig.LOCAL_API_ANALYZE_PATH,
                method = "POST",
                requestBody = body
            )
            decodeAnalyzeResponse(responseBody)
        }
    }

    override suspend fun ping(): Boolean {
        return withContext(Dispatchers.IO) {
            runCatching {
                performJsonRequest(
                    path = Gemma4LocalConfig.LOCAL_API_PING_PATH,
                    method = "GET",
                    requestBody = null
                )
                true
            }.getOrDefault(false)
        }
    }

    private fun performJsonRequest(
        path: String,
        method: String,
        requestBody: String?
    ): String {
        val baseUrl = Gemma4LocalConfig.LOCAL_API_BASE_URL.trimEnd('/')
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        val connection = (URL("$baseUrl$normalizedPath").openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = Gemma4LocalConfig.AVAILABILITY_TIMEOUT_MS.toInt()
            readTimeout = Gemma4LocalConfig.DEFAULT_TIMEOUT_MS.toInt()
            setRequestProperty("Accept", "application/json")
            if (requestBody != null) {
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                doOutput = true
            }
        }

        return try {
            if (requestBody != null) {
                connection.outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
                    writer.write(requestBody)
                }
            }

            val statusCode = connection.responseCode
            val stream = if (statusCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            val responseText = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (statusCode !in 200..299) {
                throw IOException("HTTP $statusCode ${responseText.take(160)}")
            }
            responseText
        } finally {
            connection.disconnect()
        }
    }

    private fun decodeAnalyzeResponse(responseBody: String): String {
        val trimmed = responseBody.trim()
        if (trimmed.isEmpty()) return ""

        return try {
            val json = JSONObject(trimmed)
            when {
                json.optString("outputText").isNotBlank() -> json.optString("outputText").trim()
                json.optString("text").isNotBlank() -> json.optString("text").trim()
                json.optString("result").isNotBlank() -> json.optString("result").trim()
                else -> ""
            }
        } catch (_: JSONException) {
            trimmed
        }
    }
}
