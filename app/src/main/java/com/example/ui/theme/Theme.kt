package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = IndoNavyDarkPrimary,
    onPrimary = IndoNavyDarkOnPrimary,
    primaryContainer = IndoNavyDarkContainer,
    onPrimaryContainer = Color(0xFFD6E4F7),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFF452B00),
    secondaryContainer = Color(0xFF633F00),
    onSecondaryContainer = Color(0xFFFFECC4),
    tertiary = Color(0xFF81C784),
    background = IndoDarkBackground,
    surface = IndoDarkSurface,
    surfaceVariant = IndoDarkSurfaceVariant,
    onBackground = Color(0xFFECEFF1),
    onSurface = Color(0xFFECEFF1),
    error = Color(0xFFEF5350)
)

private val LightColorScheme = lightColorScheme(
    primary = IndoNavyPrimary,
    onPrimary = IndoNavyOnPrimary,
    primaryContainer = IndoNavyContainer,
    onPrimaryContainer = IndoOnNavyContainer,
    secondary = IndoGoldSecondary,
    onSecondary = IndoGoldOnSecondary,
    secondaryContainer = IndoGoldContainer,
    onSecondaryContainer = IndoOnGoldContainer,
    tertiary = IndoGreenSuccess,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = Color(0xFF191C1E),
    onSurface = Color(0xFF191C1E),
    outline = OutlineLight,
    error = IndoRedDanger
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use branded colors by default for strong identity
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
