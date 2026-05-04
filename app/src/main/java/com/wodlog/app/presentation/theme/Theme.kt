package com.wodlog.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun WodlogTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) WodlogDarkColorScheme else WodlogLightColorScheme,
        typography = WodlogTypography,
        shapes = WodlogShapes,
        content = content
    )
}
