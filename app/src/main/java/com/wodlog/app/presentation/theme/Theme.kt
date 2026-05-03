package com.wodlog.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8B1E2D),
    secondary = Color(0xFF3D5A5A),
    tertiary = Color(0xFF6B5A3A),
    background = Color(0xFFFFFBFF),
    surface = Color(0xFFFFFBFF)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB3BC),
    secondary = Color(0xFFA8CACA),
    tertiary = Color(0xFFD7C49A),
    background = Color(0xFF1B1B1F),
    surface = Color(0xFF1B1B1F)
)

@Composable
fun WodlogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
