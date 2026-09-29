package com.example.aicropcare.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.aicropcare.R
import com.example.aicropcare.data.models.FarmingReminder
import com.example.aicropcare.data.models.ReminderType
import com.example.aicropcare.repository.ReminderRepository
import com.example.aicropcare.theme.*
import kotlinx.coroutines.launch

enum class ReminderTab {
    TODAY, UPCOMING, COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@android.annotation.SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun FarmingRemindersScreen(
    onBack: () -> Unit,
    reminderRepository: ReminderRepository? = null,
    prefilledCrop: String? = null,
    prefilledType: ReminderType = ReminderType.OTHER,
    prefilledTitle: String? = null,
    prefilledNotes: String? = null,
    prefilledWeatherPrecaution: String? = null,
    prefilledTimestamp: Long? = null,
    autoOpenCreateDialog: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { reminderRepository ?: ReminderRepository(context) }

    var selectedTab by remember { mutableStateOf(ReminderTab.TODAY) }
    var todayReminders by remember { mutableStateOf<List<FarmingReminder>>(emptyList()) }
    var upcomingReminders by remember { mutableStateOf<List<FarmingReminder>>(emptyList()) }
    var completedReminders by remember { mutableStateOf<List<FarmingReminder>>(emptyList()) }

    var showCreateDialog by remember { mutableStateOf(autoOpenCreateDialog) }
    var reminderToEdit by remember { mutableStateOf<FarmingReminder?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Request POST_NOTIFICATIONS permission once if needed
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }
    
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun loadData() {
        isLoading = true
        coroutineScope.launch {
            todayReminders = repository.getTodayReminders()
            upcomingReminders = repository.getUpcomingReminders()
            completedReminders = repository.getCompletedReminders()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    val currentList = when (selectedTab) {
        ReminderTab.TODAY -> todayReminders
        ReminderTab.UPCOMING -> upcomingReminders
        ReminderTab.COMPLETED -> completedReminders
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reminders_screen_title), fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriGreenPrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    reminderToEdit = null
                    showCreateDialog = true
                },
                containerColor = AgriGreenPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.reminders_add_btn))
            }
        },
        containerColor = AgriBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TabButton(
                    text = stringResource(R.string.reminders_tab_today),
                    isSelected = selectedTab == ReminderTab.TODAY,
                    count = todayReminders.size,
                    onClick = { selectedTab = ReminderTab.TODAY }
                )
                TabButton(
                    text = stringResource(R.string.reminders_tab_upcoming),
                    isSelected = selectedTab == ReminderTab.UPCOMING,
                    count = upcomingReminders.size,
                    onClick = { selectedTab = ReminderTab.UPCOMING }
                )
                TabButton(
                    text = stringResource(R.string.reminders_tab_completed),
                    isSelected = selectedTab == ReminderTab.COMPLETED,
                    count = completedReminders.size,
                    onClick = { selectedTab = ReminderTab.COMPLETED }
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AgriGreenPrimary)
                }
            } else if (currentList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.EventNote,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.reminders_empty_title),
                            fontSize = 16.sp,
                            color = AgriTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentList) { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onToggleComplete = {
                                coroutineScope.launch {
                                    val newStatus = !reminder.isCompleted
                                    repository.toggleComplete(reminder, newStatus)
                                    loadData()
                                }
                            },
                            onEdit = {
                                reminderToEdit = reminder
                                showCreateDialog = true
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    repository.deleteReminder(reminder.id)
                                    loadData()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateEditReminderDialog(
            initialReminder = reminderToEdit,
            prefilledCrop = prefilledCrop,
            prefilledType = prefilledType,
            prefilledTitle = prefilledTitle,
            prefilledNotes = prefilledNotes,
            prefilledWeatherPrecaution = prefilledWeatherPrecaution,
            prefilledTimestamp = prefilledTimestamp,
            onDismiss = { showCreateDialog = false },
            onSave = { title, crop, type, timestamp, date, time, notes, weather ->
                coroutineScope.launch {
                    if (reminderToEdit == null) {
                        repository.createReminder(title, crop, type, timestamp, date, time, notes, weather)
                    } else {
                        repository.updateReminder(reminderToEdit!!.id, title, crop, type, timestamp, date, time, notes, weather)
                    }
                    showCreateDialog = false
                    loadData()
                }
            }
        )
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, count: Int, onClick: () -> Unit) {
    val bgColor = if (isSelected) AgriGreenPrimary else Color.Transparent
    val textColor = if (isSelected) Color.White else AgriTextSecondary

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        if (count > 0) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White.copy(alpha = 0.2f) else AgriGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    color = if (isSelected) Color.White else AgriGreenPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ReminderCard(
    reminder: FarmingReminder,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val alpha = if (reminder.isCompleted) 0.6f else 1.0f
    
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (reminder.isCompleted) AgriGreenPrimary else Color.White)
                    .border(2.dp, if (reminder.isCompleted) AgriGreenPrimary else Color.LightGray, CircleShape)
                    .clickable { onToggleComplete() },
                contentAlignment = Alignment.Center
            ) {
                if (reminder.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Content
            Column(modifier = Modifier.weight(1f).padding(top = 2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = reminder.reminderType.icon,
                        contentDescription = null,
                        tint = reminder.reminderType.color.copy(alpha = alpha),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(id = reminder.reminderType.titleRes),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = reminder.reminderType.color.copy(alpha = alpha)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reminder.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriPrimaryDark.copy(alpha = alpha)
                )
                if (!reminder.cropName.isNullOrBlank()) {
                    Text(
                        text = stringResource(R.string.reminder_crop_label) + " " + reminder.cropName,
                        fontSize = 13.sp,
                        color = AgriTextPrimary.copy(alpha = alpha)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = AgriTextSecondary.copy(alpha = alpha), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = " · ",
                        fontSize = 12.sp,
                        color = AgriTextSecondary.copy(alpha = alpha)
                    )
                }
                
                if (!reminder.notes.isNullOrBlank() || !reminder.weatherPrecaution.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AgriGreenContainer.copy(alpha = if (reminder.isCompleted) 0.3f else 0.5f))
                            .padding(10.dp)
                    ) {
                        Column {
                            if (!reminder.notes.isNullOrBlank()) {
                                Text(text = reminder.notes, fontSize = 13.sp, color = AgriTextPrimary.copy(alpha = alpha))
                            }
                            if (!reminder.weatherPrecaution.isNullOrBlank()) {
                                if (!reminder.notes.isNullOrBlank()) Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.CloudQueue, contentDescription = null, tint = AgriWarningAmber.copy(alpha = alpha), modifier = Modifier.size(14.dp).padding(top = 2.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = reminder.weatherPrecaution, fontSize = 12.sp, color = AgriTextPrimary.copy(alpha = alpha))
                                }
                            }
                        }
                    }
                }
            }
            
            // Actions
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.reminder_edit_title), tint = AgriTextSecondary, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.reminder_delete_title), tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

