package com.example.learningpandaai.core.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

// Friendly, generously rounded corners are core to the "soft ed-tech" feel.
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// Named semantic shapes for direct use in components/screens.
val ShapeCard: Shape = RoundedCornerShape(20.dp)
val ShapeCardLarge: Shape = RoundedCornerShape(24.dp)
val ShapeButton: Shape = RoundedCornerShape(16.dp)
val ShapeField: Shape = RoundedCornerShape(14.dp)
val ShapeWell: Shape = RoundedCornerShape(14.dp)
val ShapePill: Shape = CircleShape
