package com.foodgpt.composition

import android.content.Context
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Inférence locale Gemma via **LiteRT-LM** ([litertlm-android](https://github.com/google-ai-edge/LiteRT-LM)).
 * Le modèle doit être au format `.litertlm` dans `assets/gemma/` (voir [GemmaModelPaths]).
 */
class LiteRtGemmaEngine(
    private val context: Context,
    private val locator: GemmaModelLocator
) : CompositionAnalysisEngine {

    override suspend fun analyze(rawText: String, maxInferenceMs: Long): AnalyzeCompositionResult =
        withContext(Dispatchers.Default) {
            if (rawText.isBlank()) {
                return@withContext AnalyzeCompositionResult.CompositionLimit(CompositionMessages.COMPOSITION_LIMIT_GENERIC)
            }
            when (val located = locator.resolve()) {
                GemmaModelLocation.NotFound -> AnalyzeCompositionResult.GemmaError(
                    GemmaErrorCode.GEMMA_NOT_FOUND,
                    CompositionMessages.GEMMA_NOT_FOUND_USER
                )
                is GemmaModelLocation.LoadFailed -> AnalyzeCompositionResult.GemmaError(
                    GemmaErrorCode.GEMMA_LOAD_FAILED,
                    CompositionMessages.GEMMA_LOAD_FAILED_USER
                )
                is GemmaModelLocation.Ready -> runLitertLm(located.modelFile, rawText, maxInferenceMs)
            }
        }

    private fun runLitertLm(
        modelFile: File,
        rawText: String,
        @Suppress("UNUSED_PARAMETER") maxInferenceMs: Long
    ): AnalyzeCompositionResult {
        return try {
            val engineConfig = EngineConfig(
                modelPath = modelFile.absolutePath,
                backend = Backend.CPU(),
                cacheDir = context.cacheDir.absolutePath
            )
            Engine(engineConfig).use { engine ->
                engine.initialize()
                val systemInstruction = buildString {
                    appendLine("Tu analyses des listes d'ingrédients alimentaires (contexte UE, français).")
                    appendLine("Tu ne dois pas inventer d'ingrédients absents du texte source.")
                    appendLine("Réponds uniquement avec la structure suivante (marqueurs exacts) :")
                    appendLine("###LISTE")
                    appendLine("- ingrédient 1")
                    appendLine("- ingrédient 2")
                    appendLine("###ANALYSE")
                    appendLine("Un paragraphe factuel et prudent sur la composition et les effets santé connus ; nuancer si doute.")
                    appendLine("Aucun texte avant la ligne ###LISTE.")
                }
                val conversationConfig = ConversationConfig(
                    systemInstruction = Contents.of(systemInstruction)
                )
                engine.createConversation(conversationConfig).use { conversation ->
                    val userMessage = buildString {
                        appendLine("Texte capturé (OCR) :")
                        appendLine(rawText.trim().take(12_000))
                    }
                    val response = conversation.sendMessage(userMessage)
                    val text = response.contents.contents
                        .asSequence()
                        .filterIsInstance<Content.Text>()
                        .map { it.text }
                        .joinToString("\n")
                        .trim()
                    if (text.isEmpty()) {
                        AnalyzeCompositionResult.CompositionLimit(CompositionMessages.COMPOSITION_LIMIT_GENERIC)
                    } else {
                        val bilan = GemmaBilanParser.parse(text)
                        if (bilan == null) {
                            AnalyzeCompositionResult.CompositionLimit(CompositionMessages.COMPOSITION_LIMIT_GENERIC)
                        } else {
                            AnalyzeCompositionResult.BilanSuccess(bilan)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            AnalyzeCompositionResult.GemmaError(
                GemmaErrorCode.GEMMA_LOAD_FAILED,
                "${CompositionMessages.GEMMA_LOAD_FAILED_USER} (${e.javaClass.simpleName})"
            )
        }
    }
}
