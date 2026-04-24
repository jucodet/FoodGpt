package com.foodgpt.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodgpt.ingredients.ExtractedIngredientMapper
import com.foodgpt.ingredients.ScanFailureMessageBuilder
import com.foodgpt.ingredients.RetryScanActionHandler
import com.foodgpt.recognition.IngredientRecognitionCoordinator
import com.foodgpt.recognition.ScanFailureClassifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class CameraViewModel(
    private val coordinator: IngredientRecognitionCoordinator? = null,
    private val mapper: ExtractedIngredientMapper = ExtractedIngredientMapper(),
    private val failureClassifier: ScanFailureClassifier = ScanFailureClassifier(),
    private val failureMessageBuilder: ScanFailureMessageBuilder = ScanFailureMessageBuilder(),
    private val retryHandler: RetryScanActionHandler = RetryScanActionHandler()
) : ViewModel() {
    private val _scanState = MutableStateFlow<ScanState>(ScanState.CameraReady)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private var inFlightScan = false

    fun onPermissionDenied() {
        _scanState.value = ScanState.PermissionDenied
    }

    fun onPermissionGranted() {
        _scanState.value = ScanState.CameraReady
    }

    fun onRetry() {
        inFlightScan = false
        _scanState.value = ScanState.CameraReady
    }

    fun startScan(tempImageFile: File) {
        if (inFlightScan) return
        val scanCoordinator = coordinator ?: return

        inFlightScan = true
        _scanState.value = ScanState.Capturing
        viewModelScope.launch {
            _scanState.value = ScanState.Analyzing
            val result = scanCoordinator.runRecognition(tempImageFile)
            _scanState.value = if (result.outcome == "success" || result.outcome == "partial") {
                val uiItems = mapper.toUi(result.items)
                ScanState.Success(
                    transcriptText = result.items.joinToString(", ") { it.normalizedText },
                    items = uiItems.map { it.text }
                )
            } else {
                val code = failureClassifier.classify(result.ocrConfidenceGlobal, result.items.size)
                val message = failureMessageBuilder.build(code)
                if (!retryHandler.canRetryManually()) {
                    ScanState.Error("Relance manuelle indisponible")
                } else {
                    ScanState.Error(message)
                }
            }
            inFlightScan = false
        }
    }
}
