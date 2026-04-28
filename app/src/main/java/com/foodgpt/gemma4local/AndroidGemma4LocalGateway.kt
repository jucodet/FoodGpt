package com.foodgpt.gemma4local

import android.content.Context
import com.foodgpt.composition.GemmaModelLocation
import com.foodgpt.composition.GemmaModelLocator
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Point d'integration local Gemma4 via runtime LLM on-device (LiteRT-LM).
 * Cette implementation evite toute dependance a un endpoint HTTP local.
 */
class AndroidGemma4LocalGateway(
    private val context: Context,
    private val modelLocator: GemmaModelLocator = GemmaModelLocator(context)
) : Gemma4LocalApiGateway, Gemma4LocalAvailabilityProbe {

    override suspend fun analyzeText(inputText: String): String {
        return withContext(Dispatchers.IO) {
            val future = CompletableFuture.supplyAsync({
                runAnalyze(inputText)
            }, inferenceExecutor)
            try {
                future.get(Gemma4LocalConfig.DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            } catch (e: TimeoutException) {
                future.cancel(true)
                throw e
            } catch (e: ExecutionException) {
                val cause = e.cause
                if (cause is Exception) {
                    throw cause
                }
                throw IllegalStateException("Execution Gemma4 locale en echec.")
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                future.cancel(true)
                throw TimeoutException("Execution Gemma4 interrompue.")
            }
        }
    }

    private fun runAnalyze(inputText: String): String {
        val locatedModel = modelLocator.resolve()
        val modelFile = (locatedModel as? GemmaModelLocation.Ready)?.modelFile
            ?: throw IllegalStateException("Modele Gemma local indisponible")

        val engine = Engine(
            EngineConfig(
                modelPath = modelFile.absolutePath,
                backend = Backend.CPU(),
                cacheDir = context.cacheDir.absolutePath
            )
        )
        return try {
            engine.initialize()
            val systemInstruction = Contents.of(
                "Tu analyses des listes d'ingredients alimentaires. " +
                    "Reponds uniquement avec les sections ###LISTE puis ###ANALYSE."
            )
            val conversationConfig = ConversationConfig(systemInstruction = systemInstruction)
            val prompt = "Texte capture (OCR):\n${inputText.trim().take(Gemma4LocalConfig.MAX_INPUT_CHARS)}"
            engine.createConversation(conversationConfig).use { conversation ->
                textFromMessage(conversation.sendMessage(prompt)).trim()
            }
        } finally {
            engine.close()
        }
    }

    override suspend fun ping(): Boolean {
        return withContext(Dispatchers.IO) {
            runCatching {
                Backend.CPU()
                true
            }.getOrDefault(false)
        }
    }

    private fun textFromMessage(message: Message): String =
        message.contents.contents
            .asSequence()
            .filterIsInstance<Content.Text>()
            .map { it.text }
            .joinToString("\n")

    companion object {
        private val inferenceExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    }
}
