package com.example.doaharianmuslimin.ui.theme

import android.app.Activity
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
import com.example.doaharianmuslimin.data.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryToscaDarkTheme,
    onPrimary = DarkBackground,
    primaryContainer = ToscaContainerDark,
    onPrimaryContainer = OnToscaContainerDark,
    secondary = AccentGold,
    onSecondary = DarkBackground,
    tertiary = PrimaryToscaLight,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceCard,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = CardBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTosca,
    onPrimary = Color.White,
    primaryContainer = ToscaContainerLight,
    onPrimaryContainer = OnToscaContainerLight,
    secondary = AccentGold,
    onSecondary = TextPrimaryLight,
    tertiary = PrimaryToscaLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = ToscaContainerLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = CardBorderLight
)

private val RedColorScheme = lightColorScheme(
    primary = PrimaryRed,
    onPrimary = Color.White,
    primaryContainer = RedContainerLight,
    onPrimaryContainer = OnRedContainerLight,
    secondary = AccentGold,
    onSecondary = TextPrimaryRed,
    tertiary = PrimaryRedLight,
    background = BackgroundRedLight,
    surface = SurfaceLight,
    surfaceVariant = RedContainerLight,
    onBackground = TextPrimaryRed,
    onSurface = TextPrimaryRed,
    onSurfaceVariant = TextSecondaryRed,
    outline = CardBorderRed
)

private val BlueColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = BlueContainerLight,
    onPrimaryContainer = OnBlueContainerLight,
    secondary = AccentGold,
    onSecondary = TextPrimaryBlue,
    tertiary = PrimaryBlueLight,
    background = BackgroundBlueLight,
    surface = SurfaceLight,
    surfaceVariant = BlueContainerLight,
    onBackground = TextPrimaryBlue,
    onSurface = TextPrimaryBlue,
    onSurfaceVariant = TextSecondaryBlue,
    outline = CardBorderBlue
)

@Composable
fun DoaHarianMusliminTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.RED -> RedColorScheme
        AppThemeMode.BLUE -> BlueColorScheme
        AppThemeMode.SYSTEM -> if (isSystemDark) DarkColorScheme else LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
