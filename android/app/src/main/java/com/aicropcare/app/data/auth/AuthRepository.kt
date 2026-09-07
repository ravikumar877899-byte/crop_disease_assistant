package com.aicropcare.app.data.auth

import com.aicropcare.app.model.AuthState
import com.aicropcare.app.model.FarmerProfile
import kotlinx.coroutines.flow.StateFlow

/**
 * Authentication Repository abstraction.
 * Allows effortless transition from LocalAuthRepository to RemoteAuthRepository (Flask Backend).
 */
interface AuthRepository {
    val authState: StateFlow<AuthState>

    suspend fun checkSession(): Boolean

    suspend fun register(
        fullName: String,
        mobileNumber: String,
        email: String,
        password: String
    ): Result<FarmerProfile>

    suspend fun login(
        identifier: String,
        password: String
    ): Result<FarmerProfile>

    suspend fun updateProfile(
        profile: FarmerProfile
    ): Result<FarmerProfile>

    suspend fun logout(): Result<Unit>
}
