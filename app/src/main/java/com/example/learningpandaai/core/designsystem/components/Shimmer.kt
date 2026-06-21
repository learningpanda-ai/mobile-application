package com.example.learningpandaai.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.designsystem.theme.SurfaceContainer
import com.example.learningpandaai.core.designsystem.theme.BorderSubtle
import com.example.learningpandaai.core.designsystem.theme.SurfaceBackgroundDark

/**
 * Applies an animated linear gradient shimmer over [BorderSubtle] / [SurfaceContainer] tones.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1_200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            BorderSubtle,
            SurfaceContainer,
            BorderSubtle,
            SurfaceBackgroundDark.copy(alpha = 0.08f),
            BorderSubtle
        ),
        start = Offset(translate - 400f, translate - 400f),
        end = Offset(translate, translate)
    )

    background(brush)
}

/**
 * Clipped placeholder block for skeleton layouts.
 */
fun Modifier.shimmerPlaceholder(shape: Shape = RoundedCornerShape(8.dp)): Modifier =
    clip(shape).shimmerEffect()
