package com.example.aicropcare.ui.main

import com.example.aicropcare.data.models.PredictionResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelUnitTest {
    @Test
    fun testHealthyStatusDetection() {
        val healthyPred = PredictionResponse(
            crop = "Tomato",
            disease = "Healthy",
            confidence = 96.5f
        )
        assertTrue(healthyPred.isHealthy)
        assertEquals(0, healthyPred.getAffectedPercentage())
    }

    @Test
    fun testDiseasedSeverityCalculation() {
        val diseasedPred = PredictionResponse(
            crop = "Tomato",
            disease = "Early Blight",
            confidence = 88.0f
        )
        assertTrue(!diseasedPred.isHealthy)
        assertTrue(diseasedPred.getAffectedPercentage() > 0)
    }
}
