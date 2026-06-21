package com.example.learningpandaai.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.learningpandaai.core.designsystem.theme.PlanFreeRing
import com.example.learningpandaai.core.designsystem.theme.PlanProRingEnd
import com.example.learningpandaai.core.designsystem.theme.PlanProRingMid
import com.example.learningpandaai.core.designsystem.theme.PlanProRingStart
import com.example.learningpandaai.core.designsystem.theme.PureWhite
import com.example.learningpandaai.core.designsystem.theme.SurfaceAvatar
import com.example.learningpandaai.core.util.PlanTier
import com.example.learningpandaai.core.util.ProfileImageUrlResolver

/**
 * Circular user avatar (photo or initial fallback). Wraps the avatar in a plan ring:
 * - **Pro / paid** → Gemini-style gradient ring
 * - **Free** → subtle neutral ring
 * - **Unknown / null plan** → plain avatar (no ring)
 */
@Composable
fun UserAvatar(
    imageUrl: String?,
    displayName: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    initialTextStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleSmall,
    initialFontSize: TextUnit? = null,
    plan: String? = null,
    contentDescription: String? = null
) {
    val ringThickness = (size * 0.06f).coerceIn(2.dp, 4.dp)
    val ringGap = (size * 0.04f).coerceIn(1.5.dp, 3.dp)

    when {
        PlanTier.isPremium(plan) -> {
            val ringBrush = Brush.sweepGradient(
                listOf(PlanProRingStart, PlanProRingMid, PlanProRingEnd, PlanProRingStart)
            )
            PlanRingAvatar(
                imageUrl = imageUrl,
                displayName = displayName,
                modifier = modifier,
                size = size,
                ringThickness = ringThickness,
                ringGap = ringGap,
                ringBrush = ringBrush,
                initialTextStyle = initialTextStyle,
                initialFontSize = initialFontSize,
                contentDescription = contentDescription
            )
        }
        !plan.isNullOrBlank() -> {
            PlanRingAvatar(
                imageUrl = imageUrl,
                displayName = displayName,
                modifier = modifier,
                size = size,
                ringThickness = ringThickness,
                ringGap = ringGap,
                ringColor = PlanFreeRing,
                initialTextStyle = initialTextStyle,
                initialFontSize = initialFontSize,
                contentDescription = contentDescription
            )
        }
        else -> {
            AvatarCircle(
                imageUrl = imageUrl,
                displayName = displayName,
                modifier = modifier.size(size),
                initialTextStyle = initialTextStyle,
                initialFontSize = initialFontSize,
                contentDescription = contentDescription
            )
        }
    }
}

@Composable
private fun PlanRingAvatar(
    imageUrl: String?,
    displayName: String,
    modifier: Modifier,
    size: Dp,
    ringThickness: Dp,
    ringGap: Dp,
    initialTextStyle: androidx.compose.ui.text.TextStyle,
    initialFontSize: TextUnit?,
    contentDescription: String?,
    ringBrush: Brush? = null,
    ringColor: Color? = null
) {
    val ringModifier = modifier
        .size(size)
        .then(
            when {
                ringBrush != null -> Modifier.border(width = ringThickness, brush = ringBrush, shape = CircleShape)
                ringColor != null -> Modifier.border(width = ringThickness, color = ringColor, shape = CircleShape)
                else -> Modifier
            }
        )
        .padding(ringThickness + ringGap)

    Box(
        modifier = ringModifier,
        contentAlignment = Alignment.Center
    ) {
        AvatarCircle(
            imageUrl = imageUrl,
            displayName = displayName,
            modifier = Modifier.fillMaxSize(),
            initialTextStyle = initialTextStyle,
            initialFontSize = initialFontSize,
            contentDescription = contentDescription
        )
    }
}

@Composable
private fun AvatarCircle(
    imageUrl: String?,
    displayName: String,
    modifier: Modifier,
    initialTextStyle: androidx.compose.ui.text.TextStyle,
    initialFontSize: TextUnit?,
    contentDescription: String?
) {
    val initial = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val clippedModifier = modifier.clip(CircleShape)
    val normalizedUrl = ProfileImageUrlResolver.resolve(imageUrl)

    if (normalizedUrl != null) {
        SubcomposeAsyncImage(
            model = normalizedUrl,
            contentDescription = contentDescription ?: "Profile photo",
            modifier = clippedModifier,
            contentScale = ContentScale.Crop,
            loading = {
                AvatarInitialFallback(
                    initial = initial,
                    textStyle = initialTextStyle,
                    fontSize = initialFontSize
                )
            },
            error = {
                AvatarInitialFallback(
                    initial = initial,
                    textStyle = initialTextStyle,
                    fontSize = initialFontSize
                )
            },
            success = { SubcomposeAsyncImageContent() }
        )
    } else {
        Box(
            modifier = clippedModifier.background(SurfaceAvatar),
            contentAlignment = Alignment.Center
        ) {
            AvatarInitialFallback(
                initial = initial,
                textStyle = initialTextStyle,
                fontSize = initialFontSize
            )
        }
    }
}

@Composable
private fun AvatarInitialFallback(
    initial: String,
    textStyle: androidx.compose.ui.text.TextStyle,
    fontSize: TextUnit?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceAvatar),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = textStyle,
            fontSize = fontSize ?: textStyle.fontSize,
            color = PureWhite,
            fontWeight = FontWeight.Bold
        )
    }
}
