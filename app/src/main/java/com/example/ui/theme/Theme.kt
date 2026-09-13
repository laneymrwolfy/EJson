package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = EJsonYellow,
    onPrimary = Color(0xFF1A1400),
    primaryContainer = EJsonGoldContainer,
    onPrimaryContainer = EJsonGoldOnContainer,
    secondary = EJsonCyan,
    onSecondary = Color(0xFF002026),
    secondaryContainer = Color(0xFF003640),
    onSecondaryContainer = Color(0xFF8CE5F5),
    tertiary = EJsonGreen,
    onTertiary = Color(0xFF002204),
    tertiaryContainer = EJsonGreenContainer,
    onTertiaryContainer = Color(0xFFA5D6A7),
    background = EJsonDarkBg,
    onBackground = EJsonTextPrimary,
    surface = EJsonDarkSurface,
    onSurface = EJsonTextPrimary,
    surfaceVariant = EJsonDarkSurfaceVariant,
    onSurfaceVariant = EJsonTextSecondary,
    outline = EJsonDarkBorder,
    error = EJsonRed,
    onError = Color.White,
    errorContainer = EJsonRedContainer,
    onErrorContainer = Color(0xFFFFCDD2)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF946800),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDF9E),
    onPrimaryContainer = Color(0xFF2E1F00),
    secondary = Color(0xFF00677D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB5EBFF),
    onSecondaryContainer = Color(0xFF001F27),
    tertiary = Color(0xFF2E6C31),
    onTertiary = Color.White,
    background = Color(0xFFF9F9FC),
    onBackground = Color(0xFF181A20),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF181A20),
    surfaceVariant = Color(0xFFE8E9F0),
    onSurfaceVariant = Color(0xFF4A4E58),
    outline = Color(0xFFC4C7D0),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun EJsonTheme(
    themeMode: String = "dark", // "dark", "light", "system"
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        "light" -> false
        "system" -> systemInDark
        else -> true // default to dark for Minecraft editor feel
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
