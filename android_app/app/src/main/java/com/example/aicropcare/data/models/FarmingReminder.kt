package com.example.aicropcare.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.aicropcare.R

enum class ReminderType(
    val titleRes: Int,
    val icon: ImageVector,
    val color: Color
) {
    TREATMENT(R.string.reminder_type_treatment, Icons.Default.MedicalServices, Color(0xFF2E7D32)),
    RESCAN(R.string.reminder_type_rescan, Icons.Default.DocumentScanner, Color(0xFF1565C0)),
    IRRIGATION(R.string.reminder_type_irrigation, Icons.Default.WaterDrop, Color(0xFF0288D1)),
    FIELD_INSPECTION(R.string.reminder_type_inspection, Icons.Default.Visibility, Color(0xFFE65100)),
    DISEASE_MONITORING(R.string.reminder_type_monitoring, Icons.Default.Coronavirus, Color(0xFFC2185B)),
    OTHER(R.string.reminder_type_other, Icons.AutoMirrored.Filled.EventNote, Color(0xFF5E35B1));

    companion object {
        fun fromString(name: String?): ReminderType {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: OTHER
        }
    }
}

data class FarmingReminder(
    val id: Long = 0,
    val userId: Int,
    val title: String,
    val cropName: String? = null,
    val reminderType: ReminderType = ReminderType.OTHER,
    val reminderTimestamp: Long,
    val formattedDate: String,
    val formattedTime: String,
    val notes: String? = null,
    val weatherPrecaution: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
