package com.example.mappic_v3.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    tertiary = BlueTertiary,

    background = BackgroundLight,
    surface = SurfaceLight,

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,

    onBackground = TextPrimary,
    onSurface = TextPrimary,

    error = ErrorRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueSecondary,
    secondary = BluePrimary,
    tertiary = BlueTertiary,

    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),

    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,

    onBackground = Color.White,
    onSurface = Color.White,

    error = ErrorRed,
    onError = Color.Black
)

@Composable
fun MappicTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
