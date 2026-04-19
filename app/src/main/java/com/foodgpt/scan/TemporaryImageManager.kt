package com.foodgpt.scan

import android.content.Context
import java.io.File
import java.util.UUID

class TemporaryImageManager(private val context: Context) {
    fun createTempImageFile(): File {
        val fileName = "scan_${UUID.randomUUID()}.jpg"
        return File(context.cacheDir, fileName)
    }

    fun cleanupTempImage(file: File?): Boolean {
        if (file == null) return true
        if (!file.exists()) return true
        return file.delete()
    }
}
