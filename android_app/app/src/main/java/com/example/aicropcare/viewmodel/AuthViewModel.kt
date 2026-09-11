package com.example.aicropcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aicropcare.network.UserDto
import com.example.aicropcare.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val message: String, val user: UserDto? = null) : AuthUiState
    data class Error(val errorMessage: String) : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun login(username: String, password: String, onSuccess: () -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter both username and password.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.login(username.trim(), password)
            result.fold(
                onSuccess = { response ->
                    _uiState.value = AuthUiState.Success(
                        message = response.message,
                        user = response.user
                    )
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(
                        error.message ?: "Invalid credentials. Please try again."
                    )
                }
            )
        }
    }

    fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit
    ) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Please fill in all required fields.")
            return
        }

        if (!email.contains("@") || !email.contains(".")) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address.")
            return
        }

        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters long.")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = AuthUiState.Error("Passwords do not match.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.register(username.trim(), email.trim(), password)
            result.fold(
                onSuccess = { response ->
                    _uiState.value = AuthUiState.Success(
                        message = response.message,
                        user = response.user
                    )
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(
                        error.message ?: "Registration failed. Please try again."
                    )
                }
            )
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState.Idle
            onLoggedOut()
        }
    }
}
