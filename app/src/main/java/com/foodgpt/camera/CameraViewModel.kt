package com.foodgpt.camera

import android.app.Application
import android.os.SystemClock
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.foodgpt.core.FeatureConfig
import com.foodgpt.ingredients.ExtractedIngredientMapper
import com.foodgpt.ingredients.RetryScanActionHandler
import com.foodgpt.ingredients.ScanFailureMessageBuilder
import com.foodgpt.permissions.CameraPermissionHandler
import com.foodgpt.recognition.IngredientRecognitionCoordinator
import com.foodgpt.recognition.ScanFailureClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class CameraViewModel(
    application: Application,
    private val coordinator: IngredientRecognitionCoordinator?,
    private val permissionHandler: CameraPermissionHandler = CameraPermissionHandler(),
    private val mapper: ExtractedIngredientMapper = ExtractedIngredientMapper(),
    private val failureClassifier: ScanFailureClassifier = ScanFailureClassifier(),
    private val failureMessageBuilder: ScanFailureMessageBuilder = ScanFailureMessageBuilder(),
    private val retryHandler: RetryScanActionHandler = RetryScanActionHandler(),
    private val captureController: CameraCaptureController = CameraCaptureController(application.applicationContext)
) : AndroidViewModel(application) {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.CameraReady)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _previewSession = MutableStateFlow(0)
    val previewSession: StateFlow<Int> = _previewSession.asStateFlow()

    private var bindJob: Job? = null
    private var inFlightScan = false

    fun onPermissionDenied() {
        bindJob?.cancel()
        captureController.unbind()
        _scanState.value = ScanState.PermissionDenied
    }

    fun onPermissionGranted() {
        if (!permissionHandler.hasCameraPermission(getApplication())) {
            onPermissionDenied()
            return
        }
        bindJob?.cancel()
        captureController.unbind()
        _previewSession.value += 1
        _scanState.value = ScanState.CameraReady
    }

    fun onRetry() {
        bindJob?.cancel()
        captureController.unbind()
        inFlightScan = false
        _previewSession.value += 1
        _scanState.value = if (permissionHandler.hasCameraPermission(getApplication())) {
            ScanState.CameraReady
        } else {
            ScanState.PermissionDenied
        }
    }

    fun attachPreview(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        if (!permissionHandler.hasCameraPermission(getApplication())) {
            _scanState.value = ScanState.PermissionDenied
            return
        }
        bindJob?.cancel()
        bindJob = viewModelScope.launch {
            _scanState.value = ScanState.PreviewInitializing
            val bindStart = SystemClock.elapsedRealtime()
            val result = withContext(Dispatchers.Main) {
                captureController.bind(lifecycleOwner, previewView)
            }
            val bindMs = SystemClock.elapsedRealtime() - bindStart
            Log.d(
                TAG,
                "preview_bind_total_ms=$bindMs target_ms=${FeatureConfig.PREVIEW_START_TARGET_MS}"
            )
            if (!isActive) return@launch
            _scanState.value = if (result.isSuccess) {
                ScanState.PreviewActive
            } else {
                ScanState.CameraUnavailable(result.exceptionOrNull()?.message)
            }
        }
    }

    fun capturePhoto(outputFile: File) {
        if (inFlightScan) return
        if (_scanState.value != ScanState.PreviewActive) return
        val scanCoordinator = coordinator ?: return

        inFlightScan = true
        _scanState.value = ScanState.Capturing
        viewModelScope.launch {
            try {
                val captureResult = withContext(Dispatchers.Main) {
                    captureController.captureToFile(outputFile)
                }
                if (captureResult.isFailure) {
                    _scanState.value = ScanState.Error(
                        captureResult.exceptionOrNull()?.message ?: "Échec de la capture"
                    )
                    return@launch
                }

                _scanState.value = ScanState.Analyzing
                val result = scanCoordinator.runRecognition(outputFile)
                _scanState.value = if (result.outcome == "success" || result.outcome == "partial") {
                    val uiItems = mapper.toUi(result.items)
                    ScanState.Success(
                        transcriptText = result.items.joinToString(", ") { it.normalizedText },
                        items = uiItems.map { it.text }
                    )
                } else if (result.outcome == "empty") {
                    ScanState.Empty(result.userMessage.ifBlank { "Aucun texte détecté" })
                } else {
                    val code = failureClassifier.classify(result.ocrConfidenceGlobal, result.items.size)
                    val message = failureMessageBuilder.build(code)
                    if (!retryHandler.canRetryManually()) {
                        ScanState.Error("Relance manuelle indisponible")
                    } else {
                        ScanState.Error(message)
                    }
                }
            } finally {
                captureController.unbind()
                inFlightScan = false
            }
        }
    }

    override fun onCleared() {
        bindJob?.cancel()
        captureController.shutdown()
        super.onCleared()
    }

    companion object {
        private const val TAG = "CameraViewModel"

        fun factory(
            application: Application,
            coordinator: IngredientRecognitionCoordinator?
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(CameraViewModel::class.java))
                    return CameraViewModel(application, coordinator) as T
                }
            }
        }
    }
}
