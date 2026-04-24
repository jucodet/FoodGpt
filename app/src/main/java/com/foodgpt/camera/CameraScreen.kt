package com.foodgpt.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onCreateTempImage: () -> File
) {
    val state by viewModel.scanState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .widthIn(max = 720.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Mode appareil photo", style = MaterialTheme.typography.headlineSmall)
        Text("Preview caméra (placeholder)")

        when (state) {
            ScanState.CameraReady -> {
                Button(onClick = { viewModel.startScan(onCreateTempImage()) }) {
                    Text("Scan")
                }
            }

            ScanState.Capturing, ScanState.Analyzing -> {
                CircularProgressIndicator()
                Text("Analyse en cours...")
            }

            is ScanState.Success -> {
                Text("Analyse terminée")
                Text((state as ScanState.Success).transcriptText)
                (state as ScanState.Success).items.forEach { Text("- $it") }
                Button(onClick = viewModel::onRetry) { Text("Nouveau scan") }
            }

            is ScanState.Error -> {
                Text("Erreur: ${(state as ScanState.Error).message}")
                Button(onClick = viewModel::onRetry) { Text("Réessayer") }
            }

            ScanState.PermissionDenied -> {
                Text("Permission caméra refusée")
                Button(onClick = viewModel::onRetry) { Text("Réessayer") }
            }
        }
    }
}
