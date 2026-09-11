package com.example.aicropcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aicropcare.repository.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface ConnectionUiState {
    data object Idle : ConnectionUiState
    data object Loading : ConnectionUiState
    data class Success(val message: String, val timestamp: String) : ConnectionUiState
    data class Error(val errorMessage: String) : ConnectionUiState
}

class ConnectionViewModel(
    private val repository: ApiRepository = ApiRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConnectionUiState>(ConnectionUiState.Idle)
    val uiState: StateFlow<ConnectionUiState> = _uiState.asStateFlow()

    init {
        checkConnection()
    }

    fun checkConnection() {
        viewModelScope.launch {
            _uiState.value = ConnectionUiState.Loading
            val result = repository.checkHealth()
            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            result.fold(
                onSuccess = { response ->
                    _uiState.value = ConnectionUiState.Success(
                        message = response.message,
                        timestamp = time
                    )
                },
                onFailure = { error ->
                    _uiState.value = ConnectionUiState.Error(
                        errorMessage = "Unable to connect to AI Crop Care server (${error.localizedMessage ?: "Offline"})."
                    )
                }
            )
        }
    }

    fun testMobileApi() {
        viewModelScope.launch {
            _uiState.value = ConnectionUiState.Loading
            val result = repository.testMobileConnection()
            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            result.fold(
                onSuccess = { response ->
                    _uiState.value = ConnectionUiState.Success(
                        message = "${response.message} • ${response.app ?: "AI CROP CARE"}",
                        timestamp = time
                    )
                },
                onFailure = { error ->
                    _uiState.value = ConnectionUiState.Error(
                        errorMessage = "Unable to connect to AI Crop Care server: ${error.localizedMessage ?: "Offline"}"
                    )
                }
            )
        }
    }
}
