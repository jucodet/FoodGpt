package com.foodgpt.recognition

import android.os.SystemClock

class RecognitionTimingHelper {
    private var startMs: Long = 0L

    fun start() {
        startMs = SystemClock.elapsedRealtime()
    }

    fun elapsedMs(): Long = SystemClock.elapsedRealtime() - startMs
}
