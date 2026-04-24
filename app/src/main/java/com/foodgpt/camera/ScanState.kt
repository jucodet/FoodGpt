package com.foodgpt.camera

sealed class ScanState {
    data object CameraReady : ScanState()
    data object Capturing : ScanState()
    data object Analyzing : ScanState()
    data class Success(val transcriptText: String, val items: List<String> = emptyList()) : ScanState()
    data class Error(val message: String) : ScanState()
    data object PermissionDenied : ScanState()
}
