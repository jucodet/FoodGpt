package com.foodgpt.camera

import android.content.Context
import java.io.File
import java.util.UUID

class TemporaryImageManager(private val context: Context) {
    fun createTempImageFile(): File {
        return File(context.cacheDir, "scan_${UUID.randomUUID()}.jpg")
    }

    fun cleanupTempImage(file: File?): Boolean {
        if (file == null) return true
        if (!file.exists()) return true
        return file.delete()
    }
}
