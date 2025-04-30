package com.example.superid.ui.theme

import android.app.Activity
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
    primary = Blue30,
    secondary = Blue20,
    tertiary = Color.Red,
    background = D_Blue80,
    onBackground = Color.White,
    surface = D_Blue70,
    onSurface = Color.White,
    surfaceVariant = D_Blue60,
    onSurfaceVariant = Color.Gray,
    onPrimary = Color.Black,
    onSecondary = Color.DarkGray,
    onTertiary = Color.Black,
    primaryContainer = Blue30,
    secondaryContainer = Blue20,
    onPrimaryContainer = D_Blue70,
    onSecondaryContainer = D_Blue60
)

private val LightColorScheme = lightColorScheme(
    primary = D_Blue60,
    secondary = D_Blue40,
    tertiary = Color.Red,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.LightGray,
    onSurface = Color.Black,
    surfaceVariant = Blue30,
    onSurfaceVariant = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.LightGray,
    onTertiary = Color.White,
    primaryContainer = D_Blue60,
    onPrimaryContainer = Color.White,
    secondaryContainer = D_Blue40,
    onSecondaryContainer = Color.LightGray
)

@Composable
fun SuperIdTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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