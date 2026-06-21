package com.example.learningpandaai.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Extended, theme-aware accent colors that aren't part of the Material [androidx.compose.material3.ColorScheme]
 * (amber for streaks/XP, mint for progress). These have explicit light and dark variants so the UI
 * looks intentional in both modes instead of reusing a single hardcoded tone.
 *
 * Access via [AppTheme.colors].
 */

@Immutable
data class AppColors(
    val amber: Color,
    val amberContainer: Color,
    val onAmberContainer: Color,
    val mint: Color,
    val mintContainer: Color,
    val onMintContainer: Color
)

val LightAppColors = AppColors(
    amber = Color(0xFFE5941A),
    amberContainer = Color(0xFFFCEFD2),
    onAmberContainer = Color(0xFF7A5210),
    mint = Color(0xFF1FA873),
    mintContainer = Color(0xFFD6F2E4),
    onMintContainer = Color(0xFF0F6B47)
)
val DarkAppColors = AppColors(
    amber = Color(0xFFF2B85A),
    amberContainer = Color(0xFF3D2F12),
    onAmberContainer = Color(0xFFF6DCAE),
    mint = Color(0xFF4FD09A),
    mintContainer = Color(0xFF123D2E),
    onMintContainer = Color(0xFFBDEBD3)
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }

object  AppTheme {
    val colors : AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current
}