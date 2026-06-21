package com.example.learningpandaai.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4FD6C6),
    onPrimary = Color(0xFF00382F),
    primaryContainer = Color(0xFF0B5249),
    onPrimaryContainer = Color(0xFFB9F2EA),
    inversePrimary = BrandPrimary,
    secondary = Color(0xFFFFB59B),
    onSecondary = Color(0xFF5C1F0C),
    secondaryContainer = Color(0xFF73331E),
    onSecondaryContainer = Color(0xFFFFD9CC),
    tertiary = Color(0xFF4FD09A),
    onTertiary = Color(0xFF003824),
    tertiaryContainer = Color(0xFF123D2E),
    onTertiaryContainer = Color(0xFFBDEBD3),
    background = SurfaceBackgroundDark,
    onBackground = Color(0xFFEAF0ED),
    surface = SurfaceContainerDark,
    onSurface = Color(0xFFEAF0ED),
    surfaceVariant = SurfaceContainerDark,
    onSurfaceVariant = TextSecondaryDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = Color(0xFF293733),
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainerHighest = Color(0xFF293733),
    surfaceTint = BrandPrimary,
    inverseSurface = Color(0xFFEAF0ED),
    inverseOnSurface = TextPrimary,
    error = Color(0xFFF0938F),
    onError = Color(0xFF5C1A18),
    errorContainer = Color(0xFF5C2421),
    onErrorContainer = Color(0xFFFBD9D6),
    outline = Color(0xFF49544F),
    outlineVariant = Color(0xFF35403C),
    scrim = Color(0xFF000000),
)


private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = PureWhite,
    primaryContainer = BrandPrimaryContainer,
    onPrimaryContainer = TextOnChip,
    inversePrimary = Color(0xFF4FD6C6),
    secondary = BrandSecondary,
    onSecondary = PureWhite,
    secondaryContainer = BrandSecondaryContainer,
    onSecondaryContainer = TextOnSecondaryContainer,
    tertiary = StatusSuccess,
    onTertiary = PureWhite,
    tertiaryContainer = AccentMintContainer,
    onTertiaryContainer = TextOnTertiaryContainer,
    background = SurfaceBackground,
    onBackground = TextPrimary,
    surface = PureWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainer,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainerHighest = PureWhite,
    surfaceTint = BrandPrimary,
    inverseSurface = Color(0xFF3A332C),
    inverseOnSurface = Color(0xFFF6F1EA),
    error = StatusError,
    onError = PureWhite,
    errorContainer = StatusErrorContainer,
    onErrorContainer = StatusError,
    outline = BorderDefault,
    outlineVariant = BorderSubtle,
    scrim = Color(0xFF1C1B2E),
)

@Composable
fun LearningPandaAITheme(
    darkTheme : Boolean = isSystemInDarkTheme(),
    content : @Composable () -> Unit
){
    val colorScheme = if(darkTheme) DarkColorScheme else LightColorScheme
    val appColors = if(darkTheme) DarkAppColors else LightAppColors
    val view = LocalView.current

    if(!view.isInEditMode)
    {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            val windowInsetsController = WindowCompat.getInsetsController(window,view)
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NordicTypography,
            shapes = AppShapes,
            content = content
        )
    }
}