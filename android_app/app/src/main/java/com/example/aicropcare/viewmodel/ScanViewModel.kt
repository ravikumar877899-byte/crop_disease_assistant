package com.example.aicropcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aicropcare.network.PredictionResponse
import com.example.aicropcare.repository.PredictionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class ScanUiState {
    data object Idle : ScanUiState()
    data object Analyzing : ScanUiState()
    data class Success(val result: PredictionResponse, val imageFile: File) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

class ScanViewModel(
    private val predictionRepository: PredictionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun analyzeCrop(
        imageFile: File,
        onSuccess: (PredictionResponse) -> Unit = {}
    ) {
        _uiState.value = ScanUiState.Analyzing
        viewModelScope.launch {
            val result = predictionRepository.analyzeCropLeaf(imageFile)
            result.onSuccess { response ->
                _uiState.value = ScanUiState.Success(response, imageFile)
                onSuccess(response)
            }.onFailure { exception ->
                _uiState.value = ScanUiState.Error(
                    exception.localizedMessage ?: "AI disease analysis failed. Please try again."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = ScanUiState.Idle
    }
}
