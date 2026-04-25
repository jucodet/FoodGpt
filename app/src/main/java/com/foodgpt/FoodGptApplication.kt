package com.foodgpt

import android.app.Application
import androidx.room.Room
import com.foodgpt.data.db.AppDatabase

/**
 * Point d’entrée process : la base Room est ouverte **lazy** (premier accès, hors thread UI recommandé)
 * pour éviter un blocage long dans [android.app.Activity.onCreate].
 */
class FoodGptApplication : Application() {

    val database: AppDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "foodgpt.db").build()
    }
}
