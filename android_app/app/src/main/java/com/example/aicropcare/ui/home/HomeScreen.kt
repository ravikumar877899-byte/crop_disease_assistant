package com.example.aicropcare.ui.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aicropcare.*
import com.example.aicropcare.R
import com.example.aicropcare.data.DataRepository
import com.example.aicropcare.data.models.WeatherAdvisory
import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.theme.*
import com.example.aicropcare.ui.components.AgriTopBar
import com.example.aicropcare.ui.components.GlassCard
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigate: (NavigationKey) -> Unit,
    onToggleLanguage: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataRepository(context) }
    val sessionManager = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()

    var showLogoutDialog by remember { mutableStateOf(false) }
    val isTamil = sessionManager.isTamil
    val userName = sessionManager.userProfile?.name ?: if (isTamil) "விவசாயி" else "Farmer"
    val userLocation = sessionManager.userProfile?.location ?: if (isTamil) "தமிழ்நாடு" else "Tamil Nadu"

    // Weather advisory mock / live state
    val weather = remember {
        WeatherAdvisory(
            temperature = "29°C",
            humidity = "68%",
            condition = if (isTamil) "பகுதி மேகமூட்டம்" else "Partly Cloudy",
            advisory = if (isTamil)
                "இலை பரிசோதனைக்கு உகந்த வானிலை. அதிகாலை அல்லது மாலையில் தெளிப்பு மருந்துகளைப் பயன்படுத்துங்கள்."
            else
                "Optimal conditions for leaf disease screening. Ensure preventative sprays in the early morning."
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.logout)) },
            text = { Text(if (isTamil) "நிச்சயமாக வெளியேற விரும்புகிறீர்களா?" else "Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        coroutineScope.launch {
                            repository.logout()
                            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                            onNavigate(LoginNav)
                        }
                    }
                ) {
                    Text(stringResource(R.string.logout), color = AgriDangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBgLight)
    ) {
        // App Top Bar
        AgriTopBar(
            title = stringResource(R.string.home_title),
            subtitle = userLocation,
            isTamil = isTamil,
            onToggleLanguage = onToggleLanguage,
            actions = {
                IconButton(onClick = { onNavigate(ServerSettingsNav) }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
                IconButton(onClick = { showLogoutDialog = true }) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Farmer Welcome Banner
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White,
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(AgriGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = AgriGreenPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.greeting_farmer, userName),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = if (isTamil) "உங்கள் பண்ணை நலனை இன்று பரிசோதிக்கவும்" else "Check your farm health today",
                            fontSize = 13.sp,
                            color = AgriTextSecondary
                        )
                    }
                }
            }

            // Weather Advisory Glass Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp))
                    .border(1.dp, AgriGreenTertiary.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0FDF4)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = AgriWarningAmber,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = stringResource(R.string.weather_card_title),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextPrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AgriGreenPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = weather.condition,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = AgriGreenPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = weather.temperature, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = AgriTextPrimary)
                            Text(text = stringResource(R.string.weather_temp), fontSize = 12.sp, color = AgriTextMuted)
                        }
                        Divider(modifier = Modifier.height(36.dp).width(1.dp), color = AgriGreenTertiary.copy(alpha = 0.3f))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = weather.humidity, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = AgriTextPrimary)
                            Text(text = stringResource(R.string.weather_humidity), fontSize = 12.sp, color = AgriTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.8f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(18.dp))
                            Text(
                                text = weather.advisory,
                                fontSize = 12.sp,
                                color = AgriTextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Quick Diagnostic Section Header
            Text(
                text = stringResource(R.string.quick_actions),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            // Diagnostic Action Grid (2x2 Cards)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeActionCard(
                        title = stringResource(R.string.action_scan),
                        subtitle = stringResource(R.string.action_scan_desc),
                        icon = Icons.Default.CameraAlt,
                        gradient = listOf(AgriGreenPrimary, AgriGreenSecondary),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(ScanCropNav) }
                    )

                    HomeActionCard(
                        title = stringResource(R.string.action_upload),
                        subtitle = stringResource(R.string.action_upload_desc),
                        icon = Icons.Default.PhotoLibrary,
                        gradient = listOf(Color(0xFF0284C7), Color(0xFF0EA5E9)),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(UploadImageNav) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeActionCard(
                        title = stringResource(R.string.action_history),
                        subtitle = stringResource(R.string.action_history_desc),
                        icon = Icons.Default.History,
                        gradient = listOf(Color(0xFFD97706), Color(0xFFF59E0B)),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(HistoryNav) }
                    )

                    HomeActionCard(
                        title = stringResource(R.string.action_chatbot),
                        subtitle = stringResource(R.string.action_chatbot_desc),
                        icon = Icons.Default.SmartToy,
                        gradient = listOf(Color(0xFF7C3AED), Color(0xFF8B5CF6)),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(ChatbotNav) }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(150.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradient))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 14.sp,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
