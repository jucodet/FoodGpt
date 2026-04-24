package com.foodgpt.ingredients

class RetryScanActionHandler {
    fun canRetryAutomatically(): Boolean = false
    fun canRetryManually(): Boolean = true
}
