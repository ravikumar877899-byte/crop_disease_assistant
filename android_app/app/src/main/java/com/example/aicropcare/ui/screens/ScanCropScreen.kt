package com.example.aicropcare.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.aicropcare.network.PredictionResponse
import com.example.aicropcare.theme.*
import com.example.aicropcare.ui.components.PrimaryButton
import com.example.aicropcare.utils.ImageUtils
import com.example.aicropcare.viewmodel.ScanUiState
import com.example.aicropcare.viewmodel.ScanViewModel
import java.io.File

@Composable
fun ScanCropScreen(
    currentImageFile: File? = null,
    scanViewModel: ScanViewModel,
    onOpenCamera: () -> Unit,
    onImageSelected: (File) -> Unit,
    onAnalysisSuccess: (PredictionResponse, File) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scanUiState by scanViewModel.uiState.collectAsState()

    var activeImageFile by remember { mutableStateOf<File?>(currentImageFile) }
    var showPermissionRationale by remember { mutableStateOf(false) }

    // Sync if parent updates currentImageFile
    LaunchedEffect(currentImageFile) {
        if (currentImageFile != null) {
            activeImageFile = currentImageFile
        }
    }

    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val file = ImageUtils.copyUriToTempFile(context, uri)
            if (file != null) {
                activeImageFile = file
                onImageSelected(file)
                scanViewModel.resetState()
            } else {
                Toast.makeText(context, "Failed to load selected image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onOpenCamera()
        } else {
            showPermissionRationale = true
        }
    }

    fun requestCamera() {
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> {
                onOpenCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    val isAnalyzing = scanUiState is ScanUiState.Analyzing

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AgriBackground)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Section Header
            Text(
                text = "Scan Your Crop",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AgriPrimaryDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Capture a leaf photo with CameraX or pick an image from your gallery for real-time Gemini Vision AI analysis.",
                style = MaterialTheme.typography.bodyMedium,
                color = AgriTextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Permission Rationale Dialog
            if (showPermissionRationale) {
                AlertDialog(
                    onDismissRequest = { showPermissionRationale = false },
                    title = {
                        Text(
                            text = "Camera Permission Required",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriPrimaryDark
                        )
                    },
                    text = {
                        Text(
                            text = "AI Crop Care uses your device camera to scan and photograph affected crop leaves for disease detection.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextPrimary
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showPermissionRationale = false
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AgriPrimary)
                        ) {
                            Text("Grant Permission", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPermissionRationale = false }) {
                            Text("Cancel", color = AgriTextSecondary)
                        }
                    }
                )
            }

            // Image Preview or Upload Prompt Area
            if (activeImageFile != null && activeImageFile!!.exists()) {
                // Display Image Preview Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(2.dp, AgriPrimary, RoundedCornerShape(20.dp)),
                    color = AgriSurface,
                    shadowElevation = 3.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black)
                        ) {
                            AsyncImage(
                                model = activeImageFile,
                                contentDescription = "Crop Leaf Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp),
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = "✓ Leaf Ready",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriSuccessContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeImageFile!!.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriTextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = "Size: ${ImageUtils.formatFileSize(activeImageFile!!.length())}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AgriTextSecondary
                                )
                            }

                            IconButton(
                                onClick = {
                                    activeImageFile = null
                                    scanViewModel.resetState()
                                },
                                enabled = !isAnalyzing
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Photo",
                                    tint = AgriDanger
                                )
                            }
                        }
                    }
                }
            } else {
                // Upload Prompt Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(2.dp, AgriPrimaryLight, RoundedCornerShape(20.dp)),
                    color = AgriSurface,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(AgriPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Upload",
                                tint = AgriPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "No leaf photo selected",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriPrimaryDark
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Use the buttons below to open camera or upload from gallery",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons: Gallery & Camera
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Gallery Button
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    enabled = !isAnalyzing,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AgriPrimaryDark),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AgriPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (activeImageFile != null) "Change Gallery" else "Upload Gallery",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                // Camera Button (CameraX)
                Button(
                    onClick = { requestCamera() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    enabled = !isAnalyzing,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AgriPrimaryDark,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (activeImageFile != null) "Retake Photo" else "Open Camera",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message Banner (if any)
            if (scanUiState is ScanUiState.Error) {
                val errorMsg = (scanUiState as ScanUiState.Error).message
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, AgriDanger, RoundedCornerShape(12.dp)),
                    color = AgriDanger.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = AgriDanger,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = errorMsg,
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriDanger,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Analyze Crop Button
            PrimaryButton(
                text = if (isAnalyzing) "Analyzing Leaf..." else "🔍 Analyze Leaf with Gemini AI",
                onClick = {
                    val file = activeImageFile
                    if (file == null) {
                        Toast.makeText(context, "Please select or capture a crop leaf image first.", Toast.LENGTH_SHORT).show()
                    } else {
                        scanViewModel.analyzeCrop(file) { response ->
                            onAnalysisSuccess(response, file)
                        }
                    }
                },
                icon = Icons.Default.Search,
                enabled = activeImageFile != null && !isAnalyzing
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Photography Guidance
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, AgriBorder, RoundedCornerShape(16.dp)),
                color = AgriSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TipsAndUpdates,
                            contentDescription = null,
                            tint = AgriSecondaryDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Camera & Photo Tips for Farmers",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriPrimaryDark
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Use natural outdoor daylight without direct heavy glare.\n• Position the damaged or discolored leaf area inside the center of the frame.\n• Keep your phone steady when tapping the shutter button for sharp leaf vein details.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Full-screen Analyzing Progress Overlay
        if (isAnalyzing) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black.copy(alpha = 0.65f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clip(RoundedCornerShape(24.dp)),
                        color = AgriSurface,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(54.dp),
                                color = AgriPrimary,
                                strokeWidth = 4.dp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Analyzing Leaf...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriPrimaryDark
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Gemini Vision AI is inspecting plant tissue, pathology patterns, and leaf clarity...",
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
