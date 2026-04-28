package com.foodgpt.gemma4local

import android.content.Context
import android.net.Uri
import com.foodgpt.composition.GemmaModelPaths
import java.io.File
import java.io.FileOutputStream

class GemmaModelImportManager(
    private val context: Context
) {
    fun importFromUri(uri: Uri, overwriteExisting: Boolean = false): Boolean {
        val target = targetModelFile()
        if (!overwriteExisting && hasLocalModel()) {
            persistModelUri(uri)
            return true
        }
        val temp = File(target.parentFile, "${target.name}.tmp")
        return runCatching {
            context.contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input) { "Impossible de lire le modele selectionne." }
                FileOutputStream(temp).use { output ->
                    input.copyTo(output)
                    output.fd.sync()
                }
            }
            require(temp.length() > 0L) { "Le fichier modele est vide." }
            if (target.exists() && !target.delete()) {
                throw IllegalStateException("Impossible de remplacer le modele local.")
            }
            if (!temp.renameTo(target)) {
                temp.copyTo(target, overwrite = true)
                temp.delete()
            }
            persistModelUri(uri)
            true
        }.getOrElse {
            temp.delete()
            false
        }
    }

    fun persistedModelUri(): Uri? =
        prefs().getString(PREF_MODEL_URI, null)?.let(Uri::parse)

    fun hasLocalModel(): Boolean {
        val target = targetModelFile()
        return target.isFile && target.length() > 0L
    }

    private fun persistModelUri(uri: Uri) {
        prefs().edit().putString(PREF_MODEL_URI, uri.toString()).apply()
    }

    private fun prefs() =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun targetModelFile(): File {
        val outDir = File(context.filesDir, GemmaModelPaths.ASSET_DIRECTORY).apply { mkdirs() }
        return File(outDir, GemmaModelPaths.EXPECTED_MODEL_FILENAME)
    }

    companion object {
        private const val PREFS_NAME = "gemma_model_import"
        private const val PREF_MODEL_URI = "persisted_model_uri"
    }
}
