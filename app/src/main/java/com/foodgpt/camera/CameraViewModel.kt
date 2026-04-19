package com.foodgpt.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodgpt.scan.ScanSessionCoordinator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class CameraViewModel(
    private val coordinator: ScanSessionCoordinator? = null
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
            val result = scanCoordinator.runScan(tempImageFile)
            _scanState.value = if (result.status == "success") {
                ScanState.Success(result.transcriptText.orEmpty())
            } else {
                ScanState.Error(result.errorCode ?: "Erreur d'analyse")
            }
            inFlightScan = false
        }
    }
}
