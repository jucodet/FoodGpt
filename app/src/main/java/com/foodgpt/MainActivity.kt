package com.foodgpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.foodgpt.camera.CameraScreen
import com.foodgpt.camera.CameraViewModel
import com.foodgpt.data.db.AppDatabase
import com.foodgpt.data.repository.ScanSessionRepository
import com.foodgpt.scan.LocalOcrAnalyzer
import com.foodgpt.scan.ScanSessionCoordinator
import com.foodgpt.scan.TemporaryImageManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "foodgpt.db"
        ).build()
        val repository = ScanSessionRepository(db.scanSessionDao())
        val coordinator = ScanSessionCoordinator(LocalOcrAnalyzer(), repository)
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
