package com.example.learningpandaai.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.designsystem.theme.PlanFreeBadge
import com.example.learningpandaai.core.designsystem.theme.PlanFreeOnBadge
import com.example.learningpandaai.core.designsystem.theme.PlanProBadge
import com.example.learningpandaai.core.designsystem.theme.PlanProOnBadge
import com.example.learningpandaai.core.util.PlanTier

/**
 * Small pill badge showing the user's subscription tier (Free, Pro, etc.).
 * Used next to names on Profile and in account sections.
 */
@Composable
fun PlanBadge(
    planDisplay: String,
    isPremium: Boolean,
    modifier: Modifier = Modifier
) {
    val background = if (isPremium) PlanProBadge else PlanFreeBadge
    val foreground = if (isPremium) PlanProOnBadge else PlanFreeOnBadge
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(background)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        if (isPremium) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = foreground,
                modifier = Modifier.size(12.dp)
            )
        }
        Text(
            text = planDisplay,
            style = MaterialTheme.typography.labelSmall,
            color = foreground,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Convenience overload: derives display name and premium flag from the backend plan code. */
@Composable
fun PlanBadge(
    planCode: String?,
    modifier: Modifier = Modifier
) {
    PlanBadge(
        planDisplay = PlanTier.displayName(planCode),
        isPremium = PlanTier.isPremium(planCode),
        modifier = modifier
    )
}
