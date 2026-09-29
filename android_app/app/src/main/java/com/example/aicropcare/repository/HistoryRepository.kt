package com.example.aicropcare.repository

import android.content.Context
import android.util.Log
import com.example.aicropcare.data.database.HistoryDatabaseHelper
import com.example.aicropcare.data.models.ScanHistoryRecord
import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.network.PredictionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class HistoryRepository(private val context: Context) {

    private val dbHelper = HistoryDatabaseHelper(context)
    private val sessionManager = SessionManager(context)

    companion object {
        private const val TAG = "HistoryRepository"
    }

    suspend fun saveScan(
        response: PredictionResponse,
        originalImageFile: File?
    ): Long = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(timestamp))

            // Save persistent private copy of the image file
            var persistentImagePath: String? = null
            if (originalImageFile != null && originalImageFile.exists() && originalImageFile.length() > 0) {
                try {
                    val scansDir = File(context.filesDir, "scans")
                    if (!scansDir.exists()) {
                        scansDir.mkdirs()
                    }
                    val targetFile = File(scansDir, "scan_${timestamp}_${UUID.randomUUID().toString().take(6)}.jpg")
                    FileInputStream(originalImageFile).use { input ->
                        FileOutputStream(targetFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    if (targetFile.exists() && targetFile.length() > 0) {
                        persistentImagePath = targetFile.absolutePath
                        Log.d(TAG, "Saved image copy to $persistentImagePath (${targetFile.length()} bytes)")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to copy image to internal filesDir, using original path", e)
                    persistentImagePath = originalImageFile.absolutePath
                }
            }

            val isHealthy = response.isHealthy
            val affectedPct = if (isHealthy) 0 else {
                val rawPct = response.affectedPercentageValue
                if (rawPct > 0) rawPct
                else {
                    val conf = response.confidenceValue
                    ((conf * 0.45f).toInt() + 25).coerceIn(10, 95)
                }
            }

            val treatmentPlanJson = try {
                val plan = response.treatmentPlan ?: response.resolvedTreatmentPlan
                com.google.gson.Gson().toJson(plan)
            } catch (e: Exception) {
                ""
            }

            val record = ScanHistoryRecord(
                userId = sessionManager.userId,
                username = sessionManager.username ?: "Farmer",
                cropName = response.displayCrop,
                diseaseName = response.displayDisease,
                confidence = response.confidenceValue,
                affectedPercentage = affectedPct,
                severity = response.severity ?: response.parsedSeverity.name,
                riskLevel = response.riskLevel ?: response.parsedRiskLevel.name,
                riskReason = response.displayRiskReason ?: "",
                immediateAction = response.displayImmediateAction ?: "",
                rescanRecommended = response.isRescanAdvised,
                symptoms = response.displaySymptoms,
                treatment = response.displayTreatment,
                treatmentPlanJson = treatmentPlanJson,
                isHealthy = isHealthy,
                isClear = response.isClearValue,
                engine = response.engine ?: "Gemini Vision AI",
                imagePath = persistentImagePath,
                timestamp = timestamp,
                formattedDate = formattedDate
            )


            val dbStart = System.currentTimeMillis()
            val rowId = dbHelper.insertScan(record)
            val dbTimeMs = System.currentTimeMillis() - dbStart
            Log.d("AI_TIMING", "[AI TIMING] History save: $dbTimeMs ms (Record #$rowId)")
            rowId
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save scan record", e)
            -1L
        }

    }

    suspend fun getHistory(): List<ScanHistoryRecord> = withContext(Dispatchers.IO) {
        try {
            val list = dbHelper.getHistoryForUser(sessionManager.userId, sessionManager.username)
            Log.d(TAG, "Fetched ${list.size} history records")
            list
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch history records", e)
            emptyList()
        }
    }

    suspend fun deleteScan(id: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val imagePath = dbHelper.deleteScan(id)
            if (!imagePath.isNullOrBlank()) {
                val file = File(imagePath)
                if (file.exists() && file.absolutePath.contains("scans")) {
                    file.delete()
                }
            }
            Log.d(TAG, "Deleted scan record #$id")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete scan record #$id", e)
            false
        }
    }

    suspend fun clearAllHistory(): Boolean = withContext(Dispatchers.IO) {
        try {
            val imagePaths = dbHelper.clearAllForUser(sessionManager.userId, sessionManager.username)
            imagePaths.forEach { path ->
                if (path.isNotBlank()) {
                    val file = File(path)
                    if (file.exists() && file.absolutePath.contains("scans")) {
                        file.delete()
                    }
                }
            }
            Log.d(TAG, "Cleared all scan history (${imagePaths.size} images cleaned)")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear all scan history", e)
            false
        }
    }
}
