package com.luissedan0.demetergarterdistanceclub.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF3F7D4A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD5E6C2),
    onPrimaryContainer = Color(0xFF17351C),
    secondary = Color(0xFF9FC486),
    onSecondary = Color(0xFF1F3319),
    secondaryContainer = Color(0xFFF2F0AE),
    onSecondaryContainer = Color(0xFF393113),
    tertiary = Color(0xFFFFE500),
    onTertiary = Color(0xFF443C00),
    background = Color(0xFFDDE7FA),
    onBackground = Color(0xFF1B1C20),
    surface = Color(0xFFFAFBFF),
    onSurface = Color(0xFF1B1C20),
    surfaceVariant = Color(0xFFE7EEDB),
    onSurfaceVariant = Color(0xFF4F5A4C),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun DemeterTrackerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
