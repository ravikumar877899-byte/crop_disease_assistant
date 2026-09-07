package com.aicropcare.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicropcare.app.model.CropItem
import com.aicropcare.app.model.Language
import com.aicropcare.app.model.ScanRecord
import com.aicropcare.app.repository.AppRepository
import com.aicropcare.app.repository.InMemoryAppRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val selectedLanguageCode: String = "en",
    val languages: List<Language> = emptyList(),
    val crops: List<CropItem> = emptyList(),
    val scanHistory: List<ScanRecord> = emptyList(),
    val isOnboardingCompleted: Boolean = false,
    val isLanguageSelected: Boolean = false,
    val isLoading: Boolean = false
)

class MainViewModel(
    private val repository: AppRepository = InMemoryAppRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val supported = repository.getSupportedLanguages()
        _uiState.value = _uiState.value.copy(
            languages = supported
        )

        viewModelScope.launch {
            repository.getSelectedLanguage().collect { langCode ->
                val updatedLanguages = repository.getSupportedLanguages().map {
                    it.copy(isSelected = it.code == langCode)
                }
                _uiState.value = _uiState.value.copy(
                    selectedLanguageCode = langCode,
                    languages = updatedLanguages
                )
            }
        }

        viewModelScope.launch {
            repository.getCrops().collect { cropsList ->
                _uiState.value = _uiState.value.copy(crops = cropsList)
            }
        }

        viewModelScope.launch {
            repository.getScanHistory().collect { historyList ->
                _uiState.value = _uiState.value.copy(scanHistory = historyList)
            }
        }
    }

    fun selectLanguage(code: String) {
        viewModelScope.launch {
            repository.setSelectedLanguage(code)
            _uiState.value = _uiState.value.copy(isLanguageSelected = true)
        }
    }

    fun completeOnboarding() {
        _uiState.value = _uiState.value.copy(isOnboardingCompleted = true)
    }

    fun showPlaceholderMessage(actionName: String) {
        viewModelScope.launch {
            _userMessage.emit("$actionName will be available in the next phase.")
        }
    }
}
