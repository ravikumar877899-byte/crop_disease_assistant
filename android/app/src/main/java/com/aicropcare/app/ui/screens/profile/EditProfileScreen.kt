package com.aicropcare.app.ui.screens.profile

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
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.aicropcare.app.ui.components.PrimaryButton
import com.aicropcare.app.ui.components.SectionTitle

@Composable
fun EditProfileScreen(
    viewModel: AuthViewModel,
    onSaveSuccess: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formState by viewModel.editProfileState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar(
            title = "Edit Farmer Profile",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionTitle(title = "Personal Information")

            // Full Name
            AuthTextField(
                value = formState.fullName,
                onValueChange = viewModel::onEditNameChanged,
                label = "Full Name",
                placeholder = "Enter your full name",
                leadingIcon = Icons.Filled.Person,
                errorMessage = formState.nameError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Mobile Number
            AuthTextField(
                value = formState.mobileNumber,
                onValueChange = viewModel::onEditMobileChanged,
                label = "Mobile Number",
                placeholder = "Enter your mobile number",
                leadingIcon = Icons.Filled.Phone,
                errorMessage = formState.mobileError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Email
            AuthTextField(
                value = formState.email,
                onValueChange = viewModel::onEditEmailChanged,
                label = "Email Address",
                placeholder = "Enter your email address",
                leadingIcon = Icons.Filled.Email,
                errorMessage = formState.emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(title = "Farm Information")

            // Farm Location
            AuthTextField(
                value = formState.farmLocation,
                onValueChange = viewModel::onEditFarmLocationChanged,
                label = "Farm Location / Village / District",
                placeholder = "e.g. Madurai, Tamil Nadu",
                leadingIcon = Icons.Filled.LocationOn,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Farm Size
            AuthTextField(
                value = formState.farmSize,
                onValueChange = viewModel::onEditFarmSizeChanged,
                label = "Farm Size",
                placeholder = "e.g. 5.0",
                leadingIcon = Icons.Filled.Landscape,
                errorMessage = formState.farmSizeError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Farm Unit Selector (Acres / Hectares)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Unit:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                listOf("Acres", "Hectares").forEach { unit ->
                    val isSelected = formState.farmSizeUnit == unit
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onEditFarmUnitChanged(unit) },
                        label = { Text(unit) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Changes Button
            PrimaryButton(
                text = "Save Changes",
                isLoading = formState.isSaving,
                onClick = {
                    viewModel.saveProfile(onSuccess = onSaveSuccess)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
