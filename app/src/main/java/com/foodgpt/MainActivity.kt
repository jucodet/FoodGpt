package com.foodgpt

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.foodgpt.camera.CameraScreen
import com.foodgpt.camera.CameraViewModel
import com.foodgpt.camera.ScanState
import com.foodgpt.composition.Gemma4LocalCompositionEngine
import com.foodgpt.data.repository.ScanSessionRepository
import com.foodgpt.gemma4local.AndroidGemma4LocalGateway
import com.foodgpt.gemma4local.DeviceClassResolver
import com.foodgpt.gemma4local.Gemma4LocalAvailabilityChecker
import com.foodgpt.gemma4local.Gemma4LocalClient
import com.foodgpt.gemma4local.Gemma4LocalErrorMapper
import com.foodgpt.gemma4local.Gemma4LocalMetricsLogger
import com.foodgpt.gemma4local.Gemma4LocalRequestMapper
import com.foodgpt.permissions.CameraPermissionHandler
import com.foodgpt.recognition.AiEdgeGalleryRecognizer
import com.foodgpt.recognition.DeviceAiCapabilityDetector
import com.foodgpt.recognition.IngredientExtractionPipeline
import com.foodgpt.recognition.IngredientRecognitionCoordinator
import com.foodgpt.recognition.LocalOcrFallbackRecognizer
import com.foodgpt.recognition.RecognitionEngineSelector
import com.foodgpt.scan.TemporaryImageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val permissionHandler = CameraPermissionHandler()

    private lateinit var cameraViewModel: CameraViewModel

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!::cameraViewModel.isInitialized) return@registerForActivityResult
        if (granted) {
            cameraViewModel.onPermissionGranted()
        } else {
            cameraViewModel.onPermissionDenied()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as FoodGptApplication

        setContent {
            val imageManager = remember { TemporaryImageManager(applicationContext) }
            var uiReady by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                val coordinator = withContext(Dispatchers.IO) {
                    val db = app.database
                    val repository = ScanSessionRepository(db.scanSessionDao())
                    val capabilityDetector = DeviceAiCapabilityDetector(applicationContext)
                    IngredientRecognitionCoordinator(
                        engineSelector = RecognitionEngineSelector(
                            capabilityDetector = capabilityDetector,
                            aiEdgeGalleryRecognizer = AiEdgeGalleryRecognizer(),
                            localOcrFallbackRecognizer = LocalOcrFallbackRecognizer(applicationContext)
                        ),
                        extractionPipeline = IngredientExtractionPipeline(),
                        repository = repository
                    )
                }
                val localGateway = AndroidGemma4LocalGateway(applicationContext)
                val localClient = Gemma4LocalClient(
                    availabilityChecker = Gemma4LocalAvailabilityChecker(localGateway),
                    requestMapper = Gemma4LocalRequestMapper(),
                    errorMapper = Gemma4LocalErrorMapper(),
                    metricsLogger = Gemma4LocalMetricsLogger(),
                    deviceClassResolver = DeviceClassResolver(applicationContext),
                    gateway = localGateway
                )
                val compositionEngine = Gemma4LocalCompositionEngine(localClient)
                cameraViewModel = ViewModelProvider(
                    this@MainActivity,
                    CameraViewModel.factory(application, coordinator, compositionEngine)
                )[CameraViewModel::class.java]
                cameraViewModel.onLoginSucceeded()
                if (permissionHandler.hasCameraPermission(this@MainActivity)) {
                    cameraViewModel.onPermissionGranted()
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
                uiReady = true
            }
            if (!uiReady) {
                MaterialTheme {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                CameraScreen(
                    viewModel = cameraViewModel,
                    onCreateTempImage = { imageManager.createTempImageFile() },
                    onRequestCameraPermission = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    onOpenAppSettings = {
                        startActivity(permissionHandler.buildAppSettingsIntent(this@MainActivity))
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!::cameraViewModel.isInitialized) return
        if (permissionHandler.hasCameraPermission(this) &&
            cameraViewModel.scanState.value is ScanState.PermissionDenied
        ) {
            cameraViewModel.onPermissionGranted()
        }
    }
}
