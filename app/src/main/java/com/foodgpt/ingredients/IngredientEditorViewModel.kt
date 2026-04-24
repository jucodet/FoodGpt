package com.foodgpt.ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodgpt.data.repository.ValidatedIngredientRepository
import com.foodgpt.recognition.ValidateIngredientListCommand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IngredientEditorViewModel(
    private val validationUseCase: IngredientValidationUseCase,
    private val repository: ValidatedIngredientRepository
) : ViewModel() {
    private val _items = MutableStateFlow<List<String>>(emptyList())
    val items: StateFlow<List<String>> = _items.asStateFlow()

    fun setItems(items: List<String>) {
        _items.value = items
    }

    fun validateAndSave(scanId: String, editedByUser: Boolean, onValidated: (Boolean) -> Unit) {
        val command = ValidateIngredientListCommand(scanId, _items.value, editedByUser)
        val result = validationUseCase.validate(command)
        if (!result.accepted) {
            onValidated(false)
            return
        }
        viewModelScope.launch {
            repository.save(scanId, _items.value, editedByUser)
            onValidated(true)
        }
    }
}
