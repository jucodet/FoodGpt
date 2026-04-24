package com.foodgpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.foodgpt.camera.CameraScreen
import com.foodgpt.camera.TemporaryImageManager
import com.foodgpt.camera.CameraViewModel
import com.foodgpt.data.db.AppDatabase
import com.foodgpt.data.repository.ScanSessionRepository
import com.foodgpt.recognition.AiEdgeGalleryRecognizer
import com.foodgpt.recognition.DeviceAiCapabilityDetector
import com.foodgpt.recognition.IngredientExtractionPipeline
import com.foodgpt.recognition.IngredientRecognitionCoordinator
import com.foodgpt.recognition.LocalOcrFallbackRecognizer
import com.foodgpt.recognition.RecognitionEngineSelector

class MainActivity : ComponentActivity() {
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
        val vm = CameraViewModel(coordinator)

        setContent {
            CameraScreen(
                viewModel = vm,
                onCreateTempImage = { imageManager.createTempImageFile() }
            )
        }
    }
}
