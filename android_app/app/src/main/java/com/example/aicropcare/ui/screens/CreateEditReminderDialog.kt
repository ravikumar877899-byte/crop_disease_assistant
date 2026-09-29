package com.example.aicropcare.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.aicropcare.R
import com.example.aicropcare.data.models.FarmingReminder
import com.example.aicropcare.data.models.ReminderType
import com.example.aicropcare.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditReminderDialog(
    initialReminder: FarmingReminder? = null,
    prefilledCrop: String? = null,
    prefilledType: ReminderType = ReminderType.OTHER,
    prefilledTitle: String? = null,
    prefilledNotes: String? = null,
    prefilledWeatherPrecaution: String? = null,
    prefilledTimestamp: Long? = null,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        cropName: String?,
        reminderType: ReminderType,
        timestamp: Long,
        formattedDate: String,
        formattedTime: String,
        notes: String?,
        weatherPrecaution: String?
    ) -> Unit
) {
    val context = LocalContext.current
    val isEditing = initialReminder != null

    var selectedType by remember {
        mutableStateOf(initialReminder?.reminderType ?: prefilledType)
    }
    var title by remember {
        mutableStateOf(initialReminder?.title ?: prefilledTitle ?: "")
    }
    var cropName by remember {
        mutableStateOf(initialReminder?.cropName ?: prefilledCrop ?: "")
    }
    var notes by remember {
        mutableStateOf(initialReminder?.notes ?: prefilledNotes ?: "")
    }
    var weatherPrecaution by remember {
        mutableStateOf(initialReminder?.weatherPrecaution ?: prefilledWeatherPrecaution ?: "")
    }

    val calendar = remember {
        Calendar.getInstance().apply {
            val ts = initialReminder?.reminderTimestamp ?: prefilledTimestamp
            if (ts != null && ts > System.currentTimeMillis()) {
                timeInMillis = ts
            } else {
                // Default: 2 hours from now or next morning
                add(Calendar.HOUR_OF_DAY, 2)
            }
        }
    }

    var selectedTimestamp by remember { mutableLongStateOf(calendar.timeInMillis) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    var displayDate by remember { mutableStateOf(dateFormat.format(calendar.time)) }
    var displayTime by remember { mutableStateOf(timeFormat.format(calendar.time)) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val errorTitleEmpty = stringResource(R.string.reminder_val_title_empty)
    val errorPastTime = stringResource(R.string.reminder_val_past_time)

    fun showDatePicker() {
        val currentCal = Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = selectedTimestamp
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                selectedTimestamp = newCal.timeInMillis
                displayDate = dateFormat.format(newCal.time)
                errorMessage = null
            },
            currentCal.get(Calendar.YEAR),
            currentCal.get(Calendar.MONTH),
            currentCal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }.show()
    }

    fun showTimePicker() {
        val currentCal = Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = selectedTimestamp
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                }
                selectedTimestamp = newCal.timeInMillis
                displayTime = timeFormat.format(newCal.time)
                errorMessage = null
            },
            currentCal.get(Calendar.HOUR_OF_DAY),
            currentCal.get(Calendar.MINUTE),
            false
        ).show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp)),
            color = AgriSurface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Dialog Title Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AgriPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.EditNote else Icons.Default.AlarmAdd,
                                contentDescription = null,
                                tint = AgriPrimaryDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = if (isEditing) stringResource(R.string.reminder_edit_title) else stringResource(R.string.reminder_create_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriPrimaryDark
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cd_close), tint = AgriTextSecondary)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AgriBorder)

                // Scrollable Form Fields
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Reminder Type Selector
                    Text(
                        text = stringResource(R.string.reminder_type_label),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val types = listOf(ReminderType.TREATMENT, ReminderType.RESCAN, ReminderType.IRRIGATION)
                        types.forEach { type ->
                            val isSelected = selectedType == type
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) type.color else AgriBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedType = type },
                                color = if (isSelected) type.color.copy(alpha = 0.12f) else AgriBgLight
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = type.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) type.color else AgriTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(type.titleRes),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) type.color else AgriTextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val types = listOf(ReminderType.FIELD_INSPECTION, ReminderType.DISEASE_MONITORING, ReminderType.OTHER)
                        types.forEach { type ->
                            val isSelected = selectedType == type
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) type.color else AgriBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedType = type },
                                color = if (isSelected) type.color.copy(alpha = 0.12f) else AgriBgLight
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = type.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) type.color else AgriTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(type.titleRes),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) type.color else AgriTextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // 2. Title Input
                    OutlinedTextField(
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AgriTextPrimary,
                            unfocusedTextColor = AgriTextPrimary
                        ),
                        value = title,
                        onValueChange = {
                            title = it
                            errorMessage = null
                        },
                        label = { Text(stringResource(R.string.reminder_title_label)) },
                        placeholder = { Text(stringResource(R.string.reminder_title_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Title, contentDescription = null, tint = AgriPrimary)
                        }
                    )

                    // 3. Crop Name Input (Optional)
                    OutlinedTextField(
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AgriTextPrimary,
                            unfocusedTextColor = AgriTextPrimary
                        ),
                        value = cropName,
                        onValueChange = { cropName = it },
                        label = { Text(stringResource(R.string.reminder_crop_label)) },
                        placeholder = { Text(stringResource(R.string.reminder_crop_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = AgriPrimary)
                        }
                    )

                    // 4. Date & Time Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Date selector
                        Surface(
                            modifier = Modifier
                                .weight(1.1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, AgriBorder, RoundedCornerShape(12.dp))
                                .clickable { showDatePicker() },
                            color = AgriBgLight
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = AgriPrimary, modifier = Modifier.size(20.dp))
                                Column {
                                    Text(stringResource(R.string.reminder_date_label), style = MaterialTheme.typography.labelSmall, color = AgriTextSecondary)
                                    Text(displayDate, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                                }
                            }
                        }

                        // Time selector
                        Surface(
                            modifier = Modifier
                                .weight(0.9f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, AgriBorder, RoundedCornerShape(12.dp))
                                .clickable { showTimePicker() },
                            color = AgriBgLight
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = AgriPrimary, modifier = Modifier.size(20.dp))
                                Column {
                                    Text(stringResource(R.string.reminder_time_label), style = MaterialTheme.typography.labelSmall, color = AgriTextSecondary)
                                    Text(displayTime, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                                }
                            }
                        }
                    }

                    // 5. Notes Input (Optional)
                    OutlinedTextField(
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AgriTextPrimary,
                            unfocusedTextColor = AgriTextPrimary
                        ),
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text(stringResource(R.string.reminder_notes_label)) },
                        placeholder = { Text(stringResource(R.string.reminder_notes_placeholder)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 70.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3,
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, tint = AgriTextSecondary)
                        }
                    )

                    // 6. Weather Precaution (if pre-filled or present)
                    if (weatherPrecaution.isNotBlank()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFFF7ED),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7AA))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudQueue,
                                    contentDescription = null,
                                    tint = Color(0xFFEA580C),
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = stringResource(R.string.reminder_weather_precaution_label),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEA580C)
                                    )
                                    Text(
                                        text = weatherPrecaution,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AgriTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Error Message (if any)
                    if (errorMessage != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = AgriDangerContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriDanger,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons: Cancel and Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.cancel), color = AgriTextSecondary)
                    }

                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                errorMessage = errorTitleEmpty
                                return@Button
                            }
                            if (selectedTimestamp <= System.currentTimeMillis() - 60000) {
                                errorMessage = errorPastTime
                                return@Button
                            }
                            onSave(
                                title.trim(),
                                cropName.trim().ifBlank { null },
                                selectedType,
                                selectedTimestamp,
                                displayDate,
                                displayTime,
                                notes.trim().ifBlank { null },
                                weatherPrecaution.trim().ifBlank { null }
                            )
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AgriPrimary)
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.AddAlarm,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEditing) stringResource(R.string.reminders_update_btn) else stringResource(R.string.reminders_save_btn),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
