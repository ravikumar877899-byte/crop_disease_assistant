package com.aicropcare.app.model

/**
 * Sealed hierarchy representing authentication and session lifecycle states.
 */
sealed interface AuthState {
    data object Idle : AuthState
    data object Loading : AuthState
    data object LoggedOut : AuthState
    data class LoggedIn(val profile: FarmerProfile) : AuthState
    data class Error(val message: String) : AuthState
}
