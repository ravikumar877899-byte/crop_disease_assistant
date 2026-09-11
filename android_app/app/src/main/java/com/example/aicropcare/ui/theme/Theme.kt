package com.example.aicropcare.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AgriPrimary,
    onPrimary = AgriTextOnPrimary,
    primaryContainer = AgriPrimaryContainer,
    onPrimaryContainer = AgriOnPrimaryContainer,
    secondary = AgriSecondary,
    secondaryContainer = AgriSecondaryContainer,
    background = AgriBackground,
    surface = AgriSurface,
    surfaceVariant = AgriSurfaceVariant,
    onBackground = AgriTextPrimary,
    onSurface = AgriTextPrimary,
    outline = AgriBorder,
    error = AgriDanger
)

private val DarkColorScheme = darkColorScheme(
    primary = AgriPrimaryLight,
    onPrimary = AgriTextOnPrimary,
    primaryContainer = AgriPrimaryDark,
    onPrimaryContainer = AgriPrimaryContainer,
    secondary = AgriSecondary,
    background = Color(0xFF121814),
    surface = Color(0xFF1E2620),
    onBackground = Color(0xFFF1F5F2),
    onSurface = Color(0xFFF1F5F2),
    outline = Color(0xFF2C3E32)
)

@Composable
fun AICropCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
