package com.example.aicropcare.ui.screens

import com.example.aicropcare.data.models.ScanHistoryRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CropHealthProgressCalculatorTest {

    private fun createRecord(
        id: Long = 1,
        crop: String = "Tomato",
        disease: String = "Early Blight",
        affectedPct: Int = 40,
        severity: String = "MODERATE",
        risk: String = "MEDIUM",
        isHealthy: Boolean = false,
        timestamp: Long = 1000L
    ): ScanHistoryRecord {
        return ScanHistoryRecord(
            id = id,
            cropName = crop,
            diseaseName = disease,
            confidence = 90.0f,
            affectedPercentage = affectedPct,
            severity = severity,
            riskLevel = risk,
            symptoms = "",
            treatment = "",
            isHealthy = isHealthy,
            timestamp = timestamp,
            formattedDate = "2026-09-21"
        )
    }

    @Test
    fun testNoScans_returnsNoData() {
        val result = CropHealthProgressCalculator.calculateProgress(emptyList())
        assertEquals(CropHealthSummary.NO_DATA, result.summary)
        assertNull(result.latestRecord)
        assertNull(result.previousRecord)
        assertEquals(0, result.totalScans)
    }

    @Test
    fun testSingleNonHealthyScan_returnsStable() {
        val r1 = createRecord(id = 1, affectedPct = 30, isHealthy = false)
        val result = CropHealthProgressCalculator.calculateProgress(listOf(r1))
        assertEquals(CropHealthSummary.STABLE, result.summary)
        assertNotNull(result.latestRecord)
        assertNull(result.previousRecord)
        assertEquals(1, result.totalScans)
    }

    @Test
    fun testSingleHealthyScan_returnsHealthy() {
        val r1 = createRecord(
            id = 1,
            disease = "Healthy",
            affectedPct = 0,
            severity = "HEALTHY",
            risk = "NO RISK",
            isHealthy = true
        )
        val result = CropHealthProgressCalculator.calculateProgress(listOf(r1))
        assertEquals(CropHealthSummary.HEALTHY, result.summary)
        assertEquals(1, result.totalScans)
    }

    @Test
    fun testDecreaseGreaterThanFivePoints_returnsImproving() {
        val r1 = createRecord(id = 1, affectedPct = 40, timestamp = 1000L)
        val r2 = createRecord(id = 2, affectedPct = 30, timestamp = 2000L) // diff = -10 < -5
        val result = CropHealthProgressCalculator.calculateProgress(listOf(r1, r2))
        assertEquals(CropHealthSummary.IMPROVING, result.summary)
        assertEquals(2, result.totalScans)
        assertEquals(30, result.latestRecord?.affectedPercentage)
    }

    @Test
    fun testIncreaseGreaterThanFivePoints_returnsNeedsAttention() {
        val r1 = createRecord(id = 1, affectedPct = 20, timestamp = 1000L)
        val r2 = createRecord(id = 2, affectedPct = 30, timestamp = 2000L) // diff = +10 > 5
        val result = CropHealthProgressCalculator.calculateProgress(listOf(r1, r2))
        assertEquals(CropHealthSummary.NEEDS_ATTENTION, result.summary)
        assertEquals(2, result.totalScans)
        assertEquals(30, result.latestRecord?.affectedPercentage)
    }

    @Test
    fun testChangeWithinFivePoints_returnsStable() {
        val r1 = createRecord(id = 1, affectedPct = 25, timestamp = 1000L)
        val r2 = createRecord(id = 2, affectedPct = 28, timestamp = 2000L) // diff = +3 within [-5, 5]
        val result = CropHealthProgressCalculator.calculateProgress(listOf(r1, r2))
        assertEquals(CropHealthSummary.STABLE, result.summary)

        val r3 = createRecord(id = 3, affectedPct = 22, timestamp = 3000L) // diff = -6 from 28 -> improving
        val result2 = CropHealthProgressCalculator.calculateProgress(listOf(r2, r3))
        assertEquals(CropHealthSummary.IMPROVING, result2.summary)

        val r4 = createRecord(id = 4, affectedPct = 27, timestamp = 4000L) // diff = 27 - 22 = +5 -> boundary stable
        val result3 = CropHealthProgressCalculator.calculateProgress(listOf(r3, r4))
        assertEquals(CropHealthSummary.STABLE, result3.summary)
    }

    @Test
    fun testHealthyOverride_healthyMustNeverBecomeStableOrImproving() {
        // Scan 1: 40% disease
        val r1 = createRecord(id = 1, affectedPct = 40, timestamp = 1000L)
        // Scan 2: 20% disease (would normally be improving)
        val r2 = createRecord(id = 2, affectedPct = 20, timestamp = 2000L)
        // Scan 3: Healthy (0% affected)
        val r3 = createRecord(
            id = 3,
            disease = "Healthy",
            affectedPct = 0,
            severity = "HEALTHY",
            risk = "NO RISK",
            isHealthy = true,
            timestamp = 3000L
        )

        val result = CropHealthProgressCalculator.calculateProgress(listOf(r1, r2, r3))
        // Must be HEALTHY and NEVER IMPROVING or STABLE
        assertEquals(CropHealthSummary.HEALTHY, result.summary)
        assertEquals(3, result.totalScans)
        assertEquals("Healthy", result.latestRecord?.diseaseName)
    }

    @Test
    fun testChronologicalSorting_handlesOutOfOrderTimestamps() {
        val r1 = createRecord(id = 1, affectedPct = 50, timestamp = 3000L) // newest
        val r2 = createRecord(id = 2, affectedPct = 20, timestamp = 1000L) // oldest
        // Sorted: 20 -> 50, diff = +30 -> NEEDS_ATTENTION
        val result = CropHealthProgressCalculator.calculateProgress(listOf(r1, r2))
        assertEquals(CropHealthSummary.NEEDS_ATTENTION, result.summary)
        assertEquals(50, result.latestRecord?.affectedPercentage)
        assertEquals(20, result.previousRecord?.affectedPercentage)
    }
}
