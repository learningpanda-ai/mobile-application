package com.example.learningpandaai.core.designsystem.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.designsystem.theme.BrandPrimary
import com.example.learningpandaai.core.designsystem.theme.BrandPrimaryDark
import com.example.learningpandaai.core.designsystem.theme.BrandSecondary
import com.example.learningpandaai.core.designsystem.theme.BrandSecondaryDark
import com.example.learningpandaai.core.designsystem.theme.ButtonDark
import com.example.learningpandaai.core.designsystem.theme.ButtonDarkPressed
import com.example.learningpandaai.core.designsystem.theme.PureWhite
import com.example.learningpandaai.core.designsystem.theme.ShapeButton
import com.example.learningpandaai.core.designsystem.theme.TextOnDarkButton

enum class PandaButtonVariant { Primary, Secondary, Outline, Dark }

/**
 * Chunky, playful primary button with a "3D" base rim that the face presses into,
 * giving the tactile feedback common in modern ed-tech apps.
 *
 * Pass width via [modifier] (e.g. `Modifier.fillMaxWidth()`).
 */
@Composable
fun PandaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    variant: PandaButtonVariant = PandaButtonVariant.Primary
) {
    val colorScheme = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val isEnabled = enabled && !loading

    val faceColor: Color
    val baseColor: Color
    val contentColor: Color
    when (variant) {
        PandaButtonVariant.Primary -> {
            faceColor = BrandPrimary
            baseColor = BrandPrimaryDark
            contentColor = PureWhite
        }
        PandaButtonVariant.Secondary -> {
            faceColor = BrandSecondary
            baseColor = BrandSecondaryDark
            contentColor = PureWhite
        }
        PandaButtonVariant.Outline -> {
            faceColor = colorScheme.surface
            baseColor = colorScheme.outlineVariant
            contentColor = colorScheme.onSurface
        }
        PandaButtonVariant.Dark -> {
            faceColor = ButtonDark
            baseColor = ButtonDarkPressed
            contentColor = TextOnDarkButton
        }
    }

    val depth = 4.dp
    val depthPx = with(LocalDensity.current) { depth.toPx() }
    // Animate a GPU translation (no relayout) so the press feels instant and smooth.
    val pressTranslation by animateFloatAsState(
        targetValue = if (pressed && isEnabled) depthPx else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "buttonPress"
    )

    val faceAlpha = if (isEnabled) 1f else 0.45f

    Box(
        modifier = modifier
            .clip(ShapeButton)
            .background(baseColor.copy(alpha = faceAlpha))
            // Click target lives on the stable outer surface so taps register
            // instantly and aren't affected by the press translation.
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = isEnabled,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = depth)
                .graphicsLayer { translationY = pressTranslation }
                .clip(ShapeButton)
                .background(faceColor.copy(alpha = faceAlpha))
                .then(
                    if (variant == PandaButtonVariant.Outline) {
                        Modifier.border(1.5.dp, colorScheme.outline, ShapeButton)
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 20.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = contentColor,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
