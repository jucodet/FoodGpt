package com.foodgpt.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onCreateTempImage: () -> File,
    onRequestCameraPermission: () -> Unit,
    onOpenAppSettings: () -> Unit
) {
    val state by viewModel.scanState.collectAsState()
    val previewSession by viewModel.previewSession.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .widthIn(max = 720.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Mode appareil photo", style = MaterialTheme.typography.headlineSmall)

        when (state) {
            ScanState.PermissionDenied -> {
                Text("L'accès à la caméra est nécessaire pour afficher l'aperçu réel.")
                Button(
                    onClick = onRequestCameraPermission,
                    modifier = Modifier.testTag("request_camera_permission")
                ) {
                    Text("Autoriser la caméra")
                }
                Button(
                    onClick = onOpenAppSettings,
                    modifier = Modifier.testTag("open_app_settings")
                ) {
                    Text("Ouvrir les paramètres")
                }
            }

            is ScanState.CameraUnavailable -> {
                Text("Caméra indisponible: ${(state as ScanState.CameraUnavailable).reason ?: "erreur inconnue"}")
                Button(onClick = viewModel::onRetry, modifier = Modifier.testTag("retry_camera")) {
                    Text("Réessayer")
                }
            }

            is ScanState.Success -> {
                Text("Analyse terminée")
                Text((state as ScanState.Success).transcriptText)
                (state as ScanState.Success).items.forEach { Text("- $it") }
                Button(onClick = viewModel::onRetry, modifier = Modifier.testTag("new_scan_button")) {
                    Text("Nouveau scan")
                }
            }
            is ScanState.Empty -> {
                Text((state as ScanState.Empty).message)
                Button(onClick = viewModel::onRetry, modifier = Modifier.testTag("retry_after_empty")) {
                    Text("Réessayer")
                }
            }

            is ScanState.Error -> {
                Text("Erreur: ${(state as ScanState.Error).message}")
                Button(onClick = viewModel::onRetry, modifier = Modifier.testTag("retry_after_error")) {
                    Text("Réessayer")
                }
            }

            ScanState.CameraReady,
            ScanState.PreviewInitializing,
            ScanState.PreviewActive,
            ScanState.Capturing,
            ScanState.Analyzing -> {
                key(previewSession) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                    ) {
                        CameraPreviewBox(
                            onPreviewViewCreated = { previewView ->
                                viewModel.attachPreview(previewView, lifecycleOwner)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                        if (state is ScanState.PreviewInitializing) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        if (state is ScanState.Capturing || state is ScanState.Analyzing) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator()
                                    Text("Analyse en cours…")
                                }
                            }
                        }
                    }
                }

                Text(
                    text = when (state) {
                        ScanState.PreviewActive -> "Aperçu caméra actif"
                        ScanState.PreviewInitializing -> "Démarrage de l'aperçu caméra…"
                        ScanState.Capturing -> "Capture en cours…"
                        ScanState.Analyzing -> "Traitement de l'image…"
                        else -> "Préparez le cadrage"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                if (state is ScanState.PreviewActive) {
                    Button(
                        onClick = { viewModel.capturePhoto(onCreateTempImage()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("capture_photo_button")
                    ) {
                        Text("Prendre la photo")
                    }
                }
            }
        }
    }
}
