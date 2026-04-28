package com.foodgpt.camera

data class CameraUiState(
    val segmentPreview: String = "",
    val requiresSegmentConfirmation: Boolean = false
)
