package com.foodgpt

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.foodgpt.camera.CameraScreen
import com.foodgpt.camera.CameraViewModel
import com.foodgpt.camera.ScanState
import com.foodgpt.data.db.AppDatabase
import com.foodgpt.data.repository.ScanSessionRepository
import com.foodgpt.permissions.CameraPermissionHandler
import com.foodgpt.recognition.AiEdgeGalleryRecognizer
import com.foodgpt.recognition.DeviceAiCapabilityDetector
import com.foodgpt.recognition.IngredientExtractionPipeline
import com.foodgpt.recognition.IngredientRecognitionCoordinator
import com.foodgpt.recognition.LocalOcrFallbackRecognizer
import com.foodgpt.recognition.RecognitionEngineSelector
import com.foodgpt.scan.TemporaryImageManager

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

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "foodgpt.db"
        ).build()
        val repository = ScanSessionRepository(db.scanSessionDao())
        val capabilityDetector = DeviceAiCapabilityDetector(applicationContext)
        val coordinator = IngredientRecognitionCoordinator(
            engineSelector = RecognitionEngineSelector(
                capabilityDetector = capabilityDetector,
                aiEdgeGalleryRecognizer = AiEdgeGalleryRecognizer(),
                localOcrFallbackRecognizer = LocalOcrFallbackRecognizer()
            ),
            extractionPipeline = IngredientExtractionPipeline(),
            repository = repository
        )
        val imageManager = TemporaryImageManager(applicationContext)

        cameraViewModel = ViewModelProvider(
            this,
            CameraViewModel.factory(application, coordinator)
        )[CameraViewModel::class.java]

        if (permissionHandler.hasCameraPermission(this)) {
            cameraViewModel.onPermissionGranted()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            CameraScreen(
                viewModel = cameraViewModel,
                onCreateTempImage = { imageManager.createTempImageFile() },
                onRequestCameraPermission = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onOpenAppSettings = {
                    startActivity(permissionHandler.buildAppSettingsIntent(this))
                }
            )
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
