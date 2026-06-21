package com.example.learningpandaai.features.progress.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.designsystem.theme.AppTheme
import com.example.learningpandaai.core.designsystem.theme.Spacing
import com.example.learningpandaai.features.progress.presentation.WeeklyCheckInDay

@Composable
fun ProgressWeeklyCheckInsSection(
    title: String,
    subtitle: String,
    days: List<WeeklyCheckInDay>,
    activeDayCheckedIn: String,
    activeDayMissed: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val appColors = AppTheme.colors
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(colorScheme.surface)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { expanded = !expanded }
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
            ) + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                HorizontalDivider(color = colorScheme.outlineVariant.copy(alpha = 0.35f))
                days.forEachIndexed { index, day ->
                    WeeklyDayRow(
                        day = day,
                        isSelected = selectedIndex == index,
                        onClick = {
                            selectedIndex = if (selectedIndex == index) -1 else index
                        }
                    )
                    AnimatedVisibility(visible = selectedIndex == index) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .background(
                                    if (day.isActive) {
                                        appColors.mintContainer.copy(alpha = 0.55f)
                                    } else {
                                        colorScheme.errorContainer.copy(alpha = 0.35f)
                                    }
                                )
                                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            Icon(
                                imageVector = if (day.isActive) Icons.Filled.Check else Icons.Filled.Close,
                                contentDescription = null,
                                tint = if (day.isActive) appColors.mint else colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (day.isActive) activeDayCheckedIn else activeDayMissed,
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyDayRow(
    day: WeeklyCheckInDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val appColors = AppTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(
                if (isSelected) {
                    colorScheme.primaryContainer.copy(alpha = 0.35f)
                } else {
                    colorScheme.surfaceVariant.copy(alpha = 0.35f)
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = day.fullLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = day.dateShort,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = if (day.isActive) Icons.Filled.Check else Icons.Filled.Close,
            contentDescription = null,
            tint = if (day.isActive) appColors.mint else colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.size(20.dp)
        )
    }
}
