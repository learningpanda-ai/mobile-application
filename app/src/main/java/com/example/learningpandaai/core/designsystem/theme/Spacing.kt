package com.example.learningpandaai.core.designsystem.theme

import androidx.compose.ui.unit.dp

/**
 * Centralized spacing scale so layouts stay symmetric and consistent across screens.
 * Use these instead of scattering magic dp values everywhere.
 */
object Spacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp

    /** Default horizontal screen gutter. */
    val screen = 20.dp

    /** Vertical gap between major sections on a screen. */
    val section = 20.dp
}