package com.foodgpt.composition

import android.content.Context
import java.io.File
import java.io.FileNotFoundException

sealed class GemmaModelLocation {
    data class Ready(val modelFile: File) : GemmaModelLocation()
    data object NotFound : GemmaModelLocation()
    data class LoadFailed(val reason: String) : GemmaModelLocation()
}

/**
 * Vérifie la présence du modèle dans `assets/gemma/` et le **matérialise** sur le système de fichiers
 * (LiteRT-LM exige un chemin fichier absolu, pas `asset://`).
 */
class GemmaModelLocator(private val context: Context) {

    fun resolve(): GemmaModelLocation {
        val assetPath = GemmaModelPaths.expectedAssetPath()
        return try {
            context.assets.openFd(assetPath).use { fd ->
                if (fd.declaredLength <= 0L) {
                    return GemmaModelLocation.LoadFailed("empty_model_asset")
                }
            }
            val outDir = File(context.filesDir, GemmaModelPaths.ASSET_DIRECTORY).apply { mkdirs() }
            val outFile = File(outDir, GemmaModelPaths.EXPECTED_MODEL_FILENAME)
            context.assets.open(assetPath).use { input ->
                outFile.outputStream().use { output -> input.copyTo(output) }
            }
            if (!outFile.exists() || outFile.length() == 0L) {
                GemmaModelLocation.LoadFailed("materialize_failed")
            } else {
                GemmaModelLocation.Ready(outFile)
            }
        } catch (_: FileNotFoundException) {
            GemmaModelLocation.NotFound
        } catch (e: Exception) {
            GemmaModelLocation.LoadFailed(e.message ?: "asset_open_failed")
        }
    }
}
