package com.wodlog.app.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal val WodlogDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB84D),
    onPrimary = Color(0xFF241300),
    primaryContainer = Color(0xFF4D3100),
    onPrimaryContainer = Color(0xFFFFDDA6),
    inversePrimary = Color(0xFF8A5A00),
    secondary = Color(0xFF4ED3C4),
    onSecondary = Color(0xFF003731),
    secondaryContainer = Color(0xFF004F47),
    onSecondaryContainer = Color(0xFF76F7E8),
    tertiary = Color(0xFFB9A7FF),
    onTertiary = Color(0xFF27185A),
    tertiaryContainer = Color(0xFF3E2F72),
    onTertiaryContainer = Color(0xFFE6DEFF),
    background = Color(0xFF0D0F12),
    onBackground = Color(0xFFE6E8EC),
    surface = Color(0xFF15181D),
    onSurface = Color(0xFFE6E8EC),
    surfaceVariant = Color(0xFF252A31),
    onSurfaceVariant = Color(0xFFC5CAD3),
    surfaceTint = Color(0xFFFFB84D),
    inverseSurface = Color(0xFFE6E8EC),
    inverseOnSurface = Color(0xFF252A31),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF3B0000),
    errorContainer = Color(0xFF6E1111),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF6F7682),
    outlineVariant = Color(0xFF343B45),
    scrim = Color(0xCC000000),
    surfaceBright = Color(0xFF30343B),
    surfaceDim = Color(0xFF0D0F12),
    surfaceContainerLowest = Color(0xFF090B0D),
    surfaceContainerLow = Color(0xFF111419),
    surfaceContainer = Color(0xFF181C22),
    surfaceContainerHigh = Color(0xFF20252C),
    surfaceContainerHighest = Color(0xFF2A3038)
)

internal val WodlogLightColorScheme = lightColorScheme(
    primary = Color(0xFF8A5A00),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDDA6),
    onPrimaryContainer = Color(0xFF2B1700),
    inversePrimary = Color(0xFFFFB84D),
    secondary = Color(0xFF006A60),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF7AF7E8),
    onSecondaryContainer = Color(0xFF00201C),
    tertiary = Color(0xFF5B4A91),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE6DEFF),
    onTertiaryContainer = Color(0xFF1A0D45),
    background = Color(0xFFF8F9FB),
    onBackground = Color(0xFF191C20),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191C20),
    surfaceVariant = Color(0xFFE1E4EA),
    onSurfaceVariant = Color(0xFF434A54),
    surfaceTint = Color(0xFF8A5A00),
    inverseSurface = Color(0xFF2E3136),
    inverseOnSurface = Color(0xFFF0F1F4),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF747B85),
    outlineVariant = Color(0xFFC4CAD3),
    scrim = Color(0xCC000000),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFD8DADF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF2F3F6),
    surfaceContainer = Color(0xFFECEEF2),
    surfaceContainerHigh = Color(0xFFE6E8EC),
    surfaceContainerHighest = Color(0xFFE0E2E7)
)

internal object WodlogStatusColor {
    val Success = Color(0xFF45D483)
    val SuccessContainer = Color(0xFF073D24)
    val Warning = Color(0xFFFFC857)
    val WarningContainer = Color(0xFF4D3700)
}
