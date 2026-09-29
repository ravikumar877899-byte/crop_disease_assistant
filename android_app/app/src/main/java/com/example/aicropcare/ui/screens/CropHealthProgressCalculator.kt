package com.example.aicropcare.ui.screens

import com.example.aicropcare.data.models.ScanHistoryRecord

/**
 * Result summary for crop health progress.
 */
enum class CropHealthSummary {
    IMPROVING,
    STABLE,
    NEEDS_ATTENTION,
    HEALTHY,
    NO_DATA
}

data class CropHealthProgressResult(
    val summary: CropHealthSummary,
    val latestRecord: ScanHistoryRecord?,
    val previousRecord: ScanHistoryRecord?,
    val totalScans: Int
)

/**
 * Calculates the crop health progress based on the sequence of scan records.
 * The records must be ordered chronologically (oldest first). The function
 * applies the ±5 % tolerance rules and the mandatory Healthy rule.
 */
object CropHealthProgressCalculator {
    fun calculateProgress(records: List<ScanHistoryRecord>): CropHealthProgressResult {
        if (records.isEmpty()) {
            return CropHealthProgressResult(CropHealthSummary.NO_DATA, null, null, 0)
        }
        // Ensure chronological order (oldest -> newest)
        val sorted = records.sortedBy { it.timestamp }
        val total = sorted.size
        val latest = sorted.last()
        // Healthy rule overrides everything - must NEVER be classified as Stable or Improving
        val isHealthyRecord = latest.isHealthy ||
                latest.diseaseName.equals("Healthy", ignoreCase = true) ||
                (latest.severity.equals("HEALTHY", ignoreCase = true) && latest.affectedPercentage == 0)

        if (isHealthyRecord) {
            return CropHealthProgressResult(CropHealthSummary.HEALTHY, latest, if (total > 1) sorted[total - 2] else null, total)
        }
        if (total == 1) {
            // Single non‑healthy scan is considered STABLE
            return CropHealthProgressResult(CropHealthSummary.STABLE, latest, null, total)
        }
        val previous = sorted[total - 2]
        val diff = latest.affectedPercentage - previous.affectedPercentage
        val summary = when {
            diff < -5 -> CropHealthSummary.IMPROVING
            diff > 5 -> CropHealthSummary.NEEDS_ATTENTION
            else -> CropHealthSummary.STABLE
        }
        return CropHealthProgressResult(summary, latest, previous, total)
    }
}
