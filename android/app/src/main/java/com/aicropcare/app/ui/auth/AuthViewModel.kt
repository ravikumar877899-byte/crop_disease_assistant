package com.aicropcare.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aicropcare.app.data.auth.AuthRepository
import com.aicropcare.app.model.AuthState
import com.aicropcare.app.model.FarmerProfile
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Form Validation States
data class RegisterFormState(
    val fullName: String = "",
    val mobileNumber: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val mobileError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false
)

data class LoginFormState(
    val identifier: String = "",
    val password: String = "",
    val identifierError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false
)

data class EditProfileFormState(
    val fullName: String = "",
    val mobileNumber: String = "",
    val email: String = "",
    val farmLocation: String = "",
    val farmSize: String = "",
    val farmSizeUnit: String = "Acres",
    val preferredLanguage: String = "English",
    val nameError: String? = null,
    val mobileError: String? = null,
    val emailError: String? = null,
    val farmSizeError: String? = null,
    val isSaving: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState

    private val _registerState = MutableStateFlow(RegisterFormState())
    val registerState: StateFlow<RegisterFormState> = _registerState.asStateFlow()

    private val _loginState = MutableStateFlow(LoginFormState())
    val loginState: StateFlow<LoginFormState> = _loginState.asStateFlow()

    private val _editProfileState = MutableStateFlow(EditProfileFormState())
    val editProfileState: StateFlow<EditProfileFormState> = _editProfileState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    init {
        // Populate edit profile whenever auth state updates to LoggedIn
        viewModelScope.launch {
            authState.collect { state ->
                if (state is AuthState.LoggedIn) {
                    populateEditProfileForm(state.profile)
                }
            }
        }
    }

    // --- REGISTRATION ACTIONS ---
    fun onRegisterNameChanged(name: String) {
        _registerState.value = _registerState.value.copy(fullName = name, nameError = null)
    }

    fun onRegisterMobileChanged(mobile: String) {
        val filtered = mobile.filter { it.isDigit() }.take(10)
        _registerState.value = _registerState.value.copy(mobileNumber = filtered, mobileError = null)
    }

    fun onRegisterEmailChanged(email: String) {
        _registerState.value = _registerState.value.copy(email = email, emailError = null)
    }

    fun onRegisterPasswordChanged(password: String) {
        _registerState.value = _registerState.value.copy(password = password, passwordError = null)
    }

    fun onRegisterConfirmPasswordChanged(confirmPassword: String) {
        _registerState.value = _registerState.value.copy(confirmPassword = confirmPassword, confirmPasswordError = null)
    }

    fun register(onSuccess: () -> Unit) {
        val state = _registerState.value
        var isValid = true

        var nameErr: String? = null
        var mobileErr: String? = null
        var emailErr: String? = null
        var passErr: String? = null
        var confirmErr: String? = null

        if (state.fullName.trim().isEmpty()) {
            nameErr = "Full name cannot be empty"
            isValid = false
        }

        val indianMobileRegex = Regex("^[6-9]\\d{9}$")
        if (state.mobileNumber.isEmpty()) {
            mobileErr = "Mobile number is required"
            isValid = false
        } else if (!indianMobileRegex.matches(state.mobileNumber)) {
            mobileErr = "Enter a valid 10-digit mobile number (e.g. 9876543210)"
            isValid = false
        }

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (state.email.isNotEmpty() && !emailRegex.matches(state.email.trim())) {
            emailErr = "Enter a valid email address"
            isValid = false
        }

        if (state.password.isEmpty()) {
            passErr = "Password cannot be empty"
            isValid = false
        } else if (state.password.length < 6) {
            passErr = "Password must be at least 6 characters"
            isValid = false
        }

        if (state.confirmPassword != state.password) {
            confirmErr = "Passwords do not match"
            isValid = false
        }

        _registerState.value = state.copy(
            nameError = nameErr,
            mobileError = mobileErr,
            emailError = emailErr,
            passwordError = passErr,
            confirmPasswordError = confirmErr
        )

        if (!isValid) return

        viewModelScope.launch {
            _registerState.value = _registerState.value.copy(isLoading = true)
            val result = authRepository.register(
                fullName = state.fullName,
                mobileNumber = state.mobileNumber,
                email = state.email,
                password = state.password
            )
            _registerState.value = _registerState.value.copy(isLoading = false)

            result.onSuccess {
                _registerState.value = RegisterFormState() // Reset
                _uiEvent.emit("Account created successfully!")
                onSuccess()
            }.onFailure { err ->
                _uiEvent.emit(err.message ?: "Failed to create account")
            }
        }
    }

    // --- LOGIN ACTIONS ---
    fun onLoginIdentifierChanged(identifier: String) {
        _loginState.value = _loginState.value.copy(identifier = identifier, identifierError = null)
    }

    fun onLoginPasswordChanged(password: String) {
        _loginState.value = _loginState.value.copy(password = password, passwordError = null)
    }

    fun login(onSuccess: () -> Unit) {
        val state = _loginState.value
        var isValid = true

        var idErr: String? = null
        var passErr: String? = null

        if (state.identifier.trim().isEmpty()) {
            idErr = "Please enter your mobile number or email"
            isValid = false
        }

        if (state.password.isEmpty()) {
            passErr = "Please enter your password"
            isValid = false
        }

        _loginState.value = state.copy(
            identifierError = idErr,
            passwordError = passErr
        )

        if (!isValid) return

        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true)
            val result = authRepository.login(
                identifier = state.identifier,
                password = state.password
            )
            _loginState.value = _loginState.value.copy(isLoading = false)

            result.onSuccess {
                _loginState.value = LoginFormState() // Reset
                _uiEvent.emit("Welcome back!")
                onSuccess()
            }.onFailure { err ->
                _uiEvent.emit(err.message ?: "Login failed")
            }
        }
    }

    // --- EDIT PROFILE ACTIONS ---
    fun populateEditProfileForm(profile: FarmerProfile) {
        _editProfileState.value = EditProfileFormState(
            fullName = profile.fullName,
            mobileNumber = profile.mobileNumber,
            email = profile.email,
            farmLocation = profile.farmLocation,
            farmSize = if (profile.farmSize > 0.0) profile.farmSize.toString() else "",
            farmSizeUnit = profile.farmSizeUnit,
            preferredLanguage = profile.preferredLanguage
        )
    }

    fun onEditNameChanged(name: String) {
        _editProfileState.value = _editProfileState.value.copy(fullName = name, nameError = null)
    }

    fun onEditMobileChanged(mobile: String) {
        val filtered = mobile.filter { it.isDigit() }.take(10)
        _editProfileState.value = _editProfileState.value.copy(mobileNumber = filtered, mobileError = null)
    }

    fun onEditEmailChanged(email: String) {
        _editProfileState.value = _editProfileState.value.copy(email = email, emailError = null)
    }

    fun onEditFarmLocationChanged(location: String) {
        _editProfileState.value = _editProfileState.value.copy(farmLocation = location)
    }

    fun onEditFarmSizeChanged(size: String) {
        _editProfileState.value = _editProfileState.value.copy(farmSize = size, farmSizeError = null)
    }

    fun onEditFarmUnitChanged(unit: String) {
        _editProfileState.value = _editProfileState.value.copy(farmSizeUnit = unit)
    }

    fun onEditLanguageChanged(language: String) {
        _editProfileState.value = _editProfileState.value.copy(preferredLanguage = language)
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val state = _editProfileState.value
        var isValid = true

        var nameErr: String? = null
        var mobileErr: String? = null
        var emailErr: String? = null
        var sizeErr: String? = null

        if (state.fullName.trim().isEmpty()) {
            nameErr = "Full name cannot be empty"
            isValid = false
        }

        val indianMobileRegex = Regex("^[6-9]\\d{9}$")
        if (state.mobileNumber.isNotEmpty() && !indianMobileRegex.matches(state.mobileNumber)) {
            mobileErr = "Enter a valid 10-digit mobile number"
            isValid = false
        }

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (state.email.isNotEmpty() && !emailRegex.matches(state.email.trim())) {
            emailErr = "Enter a valid email address"
            isValid = false
        }

        val parsedSize = state.farmSize.toDoubleOrNull()
        if (state.farmSize.isNotEmpty() && (parsedSize == null || parsedSize < 0)) {
            sizeErr = "Enter a valid farm size"
            isValid = false
        }

        _editProfileState.value = state.copy(
            nameError = nameErr,
            mobileError = mobileErr,
            emailError = emailErr,
            farmSizeError = sizeErr
        )

        if (!isValid) return

        val currentProfile = (authState.value as? AuthState.LoggedIn)?.profile ?: FarmerProfile()
        val updatedProfile = currentProfile.copy(
            fullName = state.fullName.trim(),
            mobileNumber = state.mobileNumber.trim(),
            email = state.email.trim().lowercase(),
            farmLocation = state.farmLocation.trim(),
            farmSize = parsedSize ?: 0.0,
            farmSizeUnit = state.farmSizeUnit,
            preferredLanguage = state.preferredLanguage
        )

        viewModelScope.launch {
            _editProfileState.value = _editProfileState.value.copy(isSaving = true)
            val result = authRepository.updateProfile(updatedProfile)
            _editProfileState.value = _editProfileState.value.copy(isSaving = false)

            result.onSuccess {
                _uiEvent.emit("Profile updated successfully!")
                onSuccess()
            }.onFailure { err ->
                _uiEvent.emit(err.message ?: "Failed to update profile")
            }
        }
    }

    // --- LOGOUT ACTION ---
    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _uiEvent.emit("Logged out successfully")
            onLoggedOut()
        }
    }

    fun showPlaceholderMessage(message: String) {
        viewModelScope.launch {
            _uiEvent.emit(message)
        }
    }
}

class AuthViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
