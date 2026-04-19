package com.foodgpt.camera

sealed class ScanState {
    data object CameraReady : ScanState()
    data object Capturing : ScanState()
    data object Analyzing : ScanState()
    data class Success(val transcriptText: String) : ScanState()
    data class Error(val message: String) : ScanState()
    data object PermissionDenied : ScanState()
}
