package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RccDarkColorScheme = darkColorScheme(
    primary = TechOrange,
    onPrimary = TechBlackPure,
    primaryContainer = TechOrangeContainer,
    onPrimaryContainer = TechOrangeOnContainer,
    secondary = TechOrangeBright,
    onSecondary = TechBlackPure,
    secondaryContainer = TechSurfaceElevated,
    onSecondaryContainer = TechOrangeLight,
    tertiary = TechCyan,
    onTertiary = TechBlackPure,
    background = TechBlack,
    onBackground = TechTextPrimary,
    surface = TechSurface,
    onSurface = TechTextPrimary,
    surfaceVariant = TechSurfaceVariant,
    onSurfaceVariant = TechTextSecondary,
    outline = TechWhiteBorder,
    outlineVariant = TechWhiteBorderSubtle,
    error = TechRed,
    onError = TechWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Preserve RCC2000 intentional dark & orange styling
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = RccDarkColorScheme,
        typography = Typography,
        content = content
    )
}
