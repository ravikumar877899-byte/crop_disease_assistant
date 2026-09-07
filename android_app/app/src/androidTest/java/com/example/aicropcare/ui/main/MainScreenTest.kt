package com.example.aicropcare.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.aicropcare.theme.AICropCareTheme
import com.example.aicropcare.ui.components.StatusBadge
import org.junit.Rule
import org.junit.Test

class StatusBadgeTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testHealthyBadgeRenders() {
        composeTestRule.setContent {
            AICropCareTheme {
                StatusBadge(isHealthy = true, isTamil = false)
            }
        }
        composeTestRule.onNodeWithText("Healthy Crop").assertExists()
    }
}
