package com.example.aicropcare.repository

import com.example.aicropcare.data.models.ScanHistoryRecord
import com.example.aicropcare.ui.screens.CropHealthProgressCalculator
import com.example.aicropcare.ui.screens.CropHealthProgressResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CropHealthProgressRepository(private val historyRepository: HistoryRepository) {

    suspend fun getDistinctCrops(): List<String> = withContext(Dispatchers.IO) {
        val allRecords = historyRepository.getHistory()
        allRecords.map { it.cropName.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
    }

    suspend fun getRecordsForCrop(cropName: String): List<ScanHistoryRecord> = withContext(Dispatchers.IO) {
        val allRecords = historyRepository.getHistory()
        allRecords.filter { it.cropName.equals(cropName, ignoreCase = true) }
            .sortedBy { it.timestamp }
    }

    suspend fun getProgressForCrop(cropName: String): CropHealthProgressResult = withContext(Dispatchers.IO) {
        val sorted = getRecordsForCrop(cropName)
        CropHealthProgressCalculator.calculateProgress(sorted)
    }
}
