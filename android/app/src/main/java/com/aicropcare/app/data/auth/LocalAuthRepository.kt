package com.aicropcare.app.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.aicropcare.app.model.AuthState
import com.aicropcare.app.model.FarmerProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

class LocalAuthRepository(
    context: Context
) : AuthRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        restoreSession()
    }

    private fun restoreSession() {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (isLoggedIn) {
            val profile = readCurrentProfile()
            _authState.value = AuthState.LoggedIn(profile)
        } else {
            _authState.value = AuthState.LoggedOut
        }
    }

    override suspend fun checkSession(): Boolean = withContext(Dispatchers.IO) {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (isLoggedIn) {
            val profile = readCurrentProfile()
            _authState.value = AuthState.LoggedIn(profile)
            true
        } else {
            _authState.value = AuthState.LoggedOut
            false
        }
    }

    override suspend fun register(
        fullName: String,
        mobileNumber: String,
        email: String,
        password: String
    ): Result<FarmerProfile> = withContext(Dispatchers.IO) {
        try {
            _authState.value = AuthState.Loading

            val existingEmail = prefs.getString(KEY_EMAIL, null)
            val existingMobile = prefs.getString(KEY_MOBILE, null)

            if ((existingEmail != null && existingEmail.equals(email, ignoreCase = true)) ||
                (existingMobile != null && existingMobile == mobileNumber)
            ) {
                val errorMsg = "An account with this mobile number or email already exists."
                _authState.value = AuthState.Error(errorMsg)
                return@withContext Result.failure(Exception(errorMsg))
            }

            val newId = UUID.randomUUID().toString()
            val hashedPassword = hashPassword(password)

            val profile = FarmerProfile(
                id = newId,
                fullName = fullName.trim(),
                mobileNumber = mobileNumber.trim(),
                email = email.trim().lowercase(),
                farmLocation = "",
                farmSize = 0.0,
                farmSizeUnit = "Acres",
                preferredLanguage = "English"
            )

            prefs.edit()
                .putString(KEY_ID, profile.id)
                .putString(KEY_FULL_NAME, profile.fullName)
                .putString(KEY_MOBILE, profile.mobileNumber)
                .putString(KEY_EMAIL, profile.email)
                .putString(KEY_PASSWORD_HASH, hashedPassword)
                .putString(KEY_FARM_LOCATION, profile.farmLocation)
                .putFloat(KEY_FARM_SIZE, profile.farmSize.toFloat())
                .putString(KEY_FARM_UNIT, profile.farmSizeUnit)
                .putString(KEY_LANGUAGE, profile.preferredLanguage)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply()

            _authState.value = AuthState.LoggedIn(profile)
            Result.success(profile)
        } catch (e: Exception) {
            val errorMsg = "Failed to create account. Please try again."
            _authState.value = AuthState.Error(errorMsg)
            Result.failure(Exception(errorMsg))
        }
    }

    override suspend fun login(
        identifier: String,
        password: String
    ): Result<FarmerProfile> = withContext(Dispatchers.IO) {
        try {
            _authState.value = AuthState.Loading

            val savedMobile = prefs.getString(KEY_MOBILE, null)
            val savedEmail = prefs.getString(KEY_EMAIL, null)
            val savedHash = prefs.getString(KEY_PASSWORD_HASH, null)

            val trimmedId = identifier.trim()
            val isMobileMatch = savedMobile != null && savedMobile == trimmedId
            val isEmailMatch = savedEmail != null && savedEmail.equals(trimmedId, ignoreCase = true)

            if ((!isMobileMatch && !isEmailMatch) || savedHash == null) {
                val errorMsg = "Account not found. Please check your credentials or create a new account."
                _authState.value = AuthState.Error(errorMsg)
                return@withContext Result.failure(Exception(errorMsg))
            }

            val inputHash = hashPassword(password)
            if (inputHash != savedHash) {
                val errorMsg = "Incorrect password. Please try again."
                _authState.value = AuthState.Error(errorMsg)
                return@withContext Result.failure(Exception(errorMsg))
            }

            val profile = readCurrentProfile()
            prefs.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply()

            _authState.value = AuthState.LoggedIn(profile)
            Result.success(profile)
        } catch (e: Exception) {
            val errorMsg = "Login failed. Please try again."
            _authState.value = AuthState.Error(errorMsg)
            Result.failure(Exception(errorMsg))
        }
    }

    override suspend fun updateProfile(
        profile: FarmerProfile
    ): Result<FarmerProfile> = withContext(Dispatchers.IO) {
        try {
            prefs.edit()
                .putString(KEY_FULL_NAME, profile.fullName.trim())
                .putString(KEY_MOBILE, profile.mobileNumber.trim())
                .putString(KEY_EMAIL, profile.email.trim().lowercase())
                .putString(KEY_FARM_LOCATION, profile.farmLocation.trim())
                .putFloat(KEY_FARM_SIZE, profile.farmSize.toFloat())
                .putString(KEY_FARM_UNIT, profile.farmSizeUnit)
                .putString(KEY_LANGUAGE, profile.preferredLanguage)
                .apply()

            _authState.value = AuthState.LoggedIn(profile)
            Result.success(profile)
        } catch (e: Exception) {
            val errorMsg = "Failed to update profile."
            Result.failure(Exception(errorMsg))
        }
    }

    override suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()
            _authState.value = AuthState.LoggedOut
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun readCurrentProfile(): FarmerProfile {
        return FarmerProfile(
            id = prefs.getString(KEY_ID, "") ?: "",
            fullName = prefs.getString(KEY_FULL_NAME, "Farmer") ?: "Farmer",
            mobileNumber = prefs.getString(KEY_MOBILE, "") ?: "",
            email = prefs.getString(KEY_EMAIL, "") ?: "",
            farmLocation = prefs.getString(KEY_FARM_LOCATION, "") ?: "",
            farmSize = prefs.getFloat(KEY_FARM_SIZE, 0.0f).toDouble(),
            farmSizeUnit = prefs.getString(KEY_FARM_UNIT, "Acres") ?: "Acres",
            preferredLanguage = prefs.getString(KEY_LANGUAGE, "English") ?: "English"
        )
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val PREFS_NAME = "ai_crop_care_auth_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_ID = "farmer_id"
        private const val KEY_FULL_NAME = "farmer_full_name"
        private const val KEY_MOBILE = "farmer_mobile"
        private const val KEY_EMAIL = "farmer_email"
        private const val KEY_PASSWORD_HASH = "farmer_password_hash"
        private const val KEY_FARM_LOCATION = "farmer_location"
        private const val KEY_FARM_SIZE = "farmer_farm_size"
        private const val KEY_FARM_UNIT = "farmer_farm_unit"
        private const val KEY_LANGUAGE = "farmer_preferred_language"
    }
}
