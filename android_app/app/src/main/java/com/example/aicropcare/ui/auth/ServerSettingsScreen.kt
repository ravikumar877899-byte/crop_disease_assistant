package com.example.aicropcare.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aicropcare.R
import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.theme.*
import com.example.aicropcare.ui.components.AgriTopBar
import com.example.aicropcare.ui.components.GlassCard

@Composable
fun ServerSettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var currentUrl by remember { mutableStateOf(sessionManager.serverUrl) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBgLight)
    ) {
        AgriTopBar(
            title = stringResource(R.string.server_settings),
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Production Cloud Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AgriHealthyGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Production Backend (HTTPS)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Enables communication through secure public cloud hosting, allowing the app to work seamlessly over Mobile Data, College Wi-Fi, Home Wi-Fi, or any internet connection anywhere in the world.",
                    fontSize = 13.sp,
                    color = AgriTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = currentUrl,
                    onValueChange = { currentUrl = it },
                    label = { Text(stringResource(R.string.server_url)) },
                    leadingIcon = {
                        Icon(Icons.Default.Dns, contentDescription = null, tint = AgriGreenPrimary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AgriTextPrimary,
                        unfocusedTextColor = AgriTextPrimary,
                        cursorColor = AgriPrimaryDark,
                        focusedBorderColor = AgriPrimary,
                        unfocusedBorderColor = AgriBorder,
                        focusedContainerColor = AgriBackground,
                        unfocusedContainerColor = AgriBackground
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Quick Presets:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Presets
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(
                        onClick = { currentUrl = SessionManager.PRODUCTION_SERVER_URL },
                        label = { Text("🌐 Production (Render HTTPS Cloud)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SuggestionChip(
                            onClick = { currentUrl = SessionManager.EMULATOR_DEV_URL },
                            label = { Text("Emulator (10.0.2.2)") },
                            modifier = Modifier.weight(1f)
                        )
                        SuggestionChip(
                            onClick = { currentUrl = "http://10.186.23.74:5000/" },
                            label = { Text("Local Wi-Fi") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (currentUrl.isNotBlank()) {
                            sessionManager.serverUrl = currentUrl.trim()
                            Toast.makeText(context, "Backend URL updated successfully!", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text(
                        text = stringResource(R.string.save_server),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
