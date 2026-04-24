package com.foodgpt.data.repository

import com.foodgpt.data.db.ValidatedIngredientDao
import com.foodgpt.data.db.ValidatedIngredientEntity

class ValidatedIngredientRepository(
    private val dao: ValidatedIngredientDao
) {
    suspend fun save(scanId: String, finalItems: List<String>, editedByUser: Boolean) {
        dao.upsert(
            ValidatedIngredientEntity(
                scanId = scanId,
                finalItemsSerialized = finalItems.joinToString("|"),
                editedByUser = editedByUser,
                validatedAt = System.currentTimeMillis()
            )
        )
    }
}
