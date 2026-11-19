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
    primary = CyanElectric,
    onPrimary = Color(0xFF041F27),
    primaryContainer = CyanContainerDark,
    onPrimaryContainer = Color(0xFFA5F3FC),
    secondary = EmeraldPulse,
    onSecondary = Color(0xFF032319),
    secondaryContainer = EmeraldContainerDark,
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = VioletOrbit,
    onTertiary = Color(0xFF1F0A4A),
    tertiaryContainer = VioletContainerDark,
    onTertiaryContainer = Color(0xFFDDD6FE),
    background = ObsidianBg,
    onBackground = Color(0xFFE2E8F0),
    surface = SlateSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = SlateOutline,
    error = CoralAlert,
    onError = Color(0xFF3F0B0B),
    errorContainer = CoralContainerDark,
    onErrorContainer = Color(0xFFFECACA)
)

private val LightColorScheme = lightColorScheme(
    primary = CyanDeep,
    onPrimary = Color.White,
    primaryContainer = CyanContainerLight,
    onPrimaryContainer = Color(0xFF083344),
    secondary = EmeraldDeep,
    onSecondary = Color.White,
    secondaryContainer = EmeraldContainerLight,
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = VioletDeep,
    onTertiary = Color.White,
    tertiaryContainer = VioletContainerLight,
    onTertiaryContainer = Color(0xFF2E1065),
    background = LightBg,
    onBackground = InkDark,
    surface = LightSurface,
    onSurface = InkDark,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = InkMuted,
    outline = LightOutline
)

@Composable
fun AntiGravityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        typography = AppTypography,
        content = content
    )
}
