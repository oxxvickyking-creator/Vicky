package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VideoPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = VideoPurpleDark,
    onPrimaryContainer = VideoPurpleLight,
    secondary = VideoCyanSecondary,
    onSecondary = Color.Black,
    secondaryContainer = VideoCyanDark,
    onSecondaryContainer = VideoCyanLight,
    tertiary = VideoPinkAccent,
    onTertiary = Color.White,
    background = VideoDarkBg,
    onBackground = VideoTextPrimary,
    surface = VideoSurfaceDark,
    onSurface = VideoTextPrimary,
    surfaceVariant = VideoSurfaceVariantDark,
    onSurfaceVariant = VideoTextSecondary,
    outline = VideoCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Always use our bespoke cinematic dark video studio theme for high production value
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
