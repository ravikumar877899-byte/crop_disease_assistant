package com.aicropcare.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aicropcare.app.ui.auth.AuthViewModel
import com.aicropcare.app.ui.components.AppTopBar
import com.aicropcare.app.ui.components.AuthTextField
import com.aicropcare.app.ui.components.PasswordField
import com.aicropcare.app.ui.components.PrimaryButton

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val registerState by viewModel.registerState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar(
            title = "Farmer Registration",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Create Your Account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Enter your details to start monitoring your crops with AI",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name
            AuthTextField(
                value = registerState.fullName,
                onValueChange = viewModel::onRegisterNameChanged,
                label = "Full Name",
                placeholder = "Enter your full name",
                leadingIcon = Icons.Filled.Person,
                errorMessage = registerState.nameError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Mobile Number
            AuthTextField(
                value = registerState.mobileNumber,
                onValueChange = viewModel::onRegisterMobileChanged,
                label = "Mobile Number",
                placeholder = "Enter your mobile number",
                leadingIcon = Icons.Filled.Phone,
                errorMessage = registerState.mobileError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Email
            AuthTextField(
                value = registerState.email,
                onValueChange = viewModel::onRegisterEmailChanged,
                label = "Email Address (Optional)",
                placeholder = "Enter your email address",
                leadingIcon = Icons.Filled.Email,
                errorMessage = registerState.emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Password
            PasswordField(
                value = registerState.password,
                onValueChange = viewModel::onRegisterPasswordChanged,
                label = "Password",
                placeholder = "Create a password (min. 6 characters)",
                leadingIcon = Icons.Filled.Lock,
                errorMessage = registerState.passwordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Confirm Password
            PasswordField(
                value = registerState.confirmPassword,
                onValueChange = viewModel::onRegisterConfirmPasswordChanged,
                label = "Confirm Password",
                placeholder = "Confirm your password",
                leadingIcon = Icons.Filled.Lock,
                errorMessage = registerState.confirmPasswordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Create Account Button
            PrimaryButton(
                text = "Create Account",
                isLoading = registerState.isLoading,
                onClick = {
                    viewModel.register(onSuccess = onRegisterSuccess)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Already have account
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
