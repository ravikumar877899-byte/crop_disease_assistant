package com.aicropcare.app.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AgriGreenPrimary,
    onPrimary = AgriGreenOnPrimary,
    primaryContainer = AgriGreenPrimaryContainer,
    onPrimaryContainer = AgriGreenOnPrimaryContainer,
    secondary = AgriEarthSecondary,
    onSecondary = AgriEarthOnSecondary,
    secondaryContainer = AgriEarthSecondaryContainer,
    onSecondaryContainer = AgriEarthOnSecondaryContainer,
    tertiary = HarvestTertiary,
    onTertiary = HarvestOnTertiary,
    tertiaryContainer = HarvestTertiaryContainer,
    onTertiaryContainer = HarvestOnTertiaryContainer,
    background = AgriBackgroundLight,
    onBackground = AgriOnBackgroundLight,
    surface = AgriSurfaceLight,
    onSurface = AgriOnSurfaceLight,
    surfaceVariant = AgriSurfaceVariantLight,
    onSurfaceVariant = AgriOnSurfaceVariantLight,
    outline = AgriOutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = AgriGreenPrimaryDark,
    onPrimary = AgriGreenOnPrimaryDark,
    primaryContainer = AgriGreenPrimaryContainerDark,
    onPrimaryContainer = AgriGreenOnPrimaryContainerDark,
    secondary = AgriEarthSecondaryDark,
    onSecondary = AgriEarthOnSecondaryDark,
    secondaryContainer = AgriEarthSecondaryContainerDark,
    onSecondaryContainer = AgriEarthOnSecondaryContainerDark,
    tertiary = HarvestTertiaryDark,
    onTertiary = HarvestOnTertiaryDark,
    tertiaryContainer = HarvestTertiaryContainerDark,
    onTertiaryContainer = HarvestOnTertiaryContainerDark,
    background = AgriBackgroundDark,
    onBackground = AgriOnBackgroundDark,
    surface = AgriSurfaceDark,
    onSurface = AgriOnSurfaceDark,
    surfaceVariant = AgriSurfaceVariantDark,
    onSurfaceVariant = AgriOnSurfaceVariantDark,
    outline = AgriOutlineDark
)

@Composable
fun AICropCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Agriculture identity prioritized
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
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
