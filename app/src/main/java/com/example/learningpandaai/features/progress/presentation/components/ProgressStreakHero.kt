package com.example.learningpandaai.features.progress.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningpandaai.core.designsystem.theme.AppTheme
import com.example.learningpandaai.core.designsystem.theme.Spacing
import com.example.learningpandaai.features.progress.presentation.ProgressLabels

@Composable
fun ProgressStreakHero(
    labels: ProgressLabels,
    currentStreak: Int,
    longestStreak: Int,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val appColors = AppTheme.colors

    var animateIn by remember { mutableStateOf(false) }
    val numberScale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.82f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "streakNumberScale"
    )
    LaunchedEffect(currentStreak) {
        animateIn = false
        animateIn = true
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(colorScheme.surfaceVariant.copy(alpha = 0.65f))
            .padding(vertical = Spacing.xxxl, horizontal = Spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = null,
            tint = appColors.amber.copy(alpha = 0.12f),
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.Center)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Text(
                text = currentStreak.toString(),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 72.sp,
                    lineHeight = 72.sp
                ),
                color = if (currentStreak > 0) appColors.amber else colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(numberScale)
            )
            Text(
                text = labels.dayStreakSuffix,
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            if (longestStreak > 0) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(appColors.amberContainer.copy(alpha = 0.85f))
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = appColors.amber,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${labels.longestStreakTitle}: $longestStreak",
                        style = MaterialTheme.typography.labelLarge,
                        color = appColors.onAmberContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressStatsRow(
    chatSessionCount: Int,
    accuracyPercent: Int,
    activeDaysThisWeek: Int,
    chatSessionsLabel: String,
    accuracyLabel: String,
    activeDaysLabel: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        ProgressStatPill(
            value = chatSessionCount.toString(),
            label = chatSessionsLabel,
            modifier = Modifier.weight(1f)
        )
        ProgressStatPill(
            value = "$accuracyPercent%",
            label = accuracyLabel,
            modifier = Modifier.weight(1f)
        )
        ProgressStatPill(
            value = "$activeDaysThisWeek/7",
            label = activeDaysLabel,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ProgressStatPill(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val appColors = AppTheme.colors

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(colorScheme.surface)
            .padding(vertical = Spacing.lg, horizontal = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}
