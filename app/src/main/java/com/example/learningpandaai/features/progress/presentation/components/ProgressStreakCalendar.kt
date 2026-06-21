package com.example.learningpandaai.features.progress.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningpandaai.core.designsystem.theme.AppTheme
import com.example.learningpandaai.core.designsystem.theme.Spacing
import com.example.learningpandaai.features.progress.presentation.CalendarDayCell
import com.example.learningpandaai.features.progress.presentation.CalendarStreakMark

@Composable
fun ProgressStreakCalendar(
    title: String,
    monthLabel: String,
    weekHeaders: List<String>,
    days: List<CalendarDayCell>,
    canGoToPreviousMonth: Boolean,
    canGoToNextMonth: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(colorScheme.surfaceVariant.copy(alpha = 0.85f))
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth, enabled = canGoToPreviousMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous month",
                    tint = if (canGoToPreviousMonth) {
                        colorScheme.onSurface
                    } else {
                        colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                    }
                )
            }

            AnimatedContent(
                targetState = monthLabel,
                transitionSpec = {
                    fadeIn(spring(stiffness = Spring.StiffnessMedium)) togetherWith
                        fadeOut(spring(stiffness = Spring.StiffnessMedium))
                },
                label = "calendarMonth"
            ) { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(onClick = onNextMonth, enabled = canGoToNextMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next month",
                    tint = if (canGoToNextMonth) {
                        colorScheme.onSurface
                    } else {
                        colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                    }
                )
            }
        }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cellSize = ((maxWidth - Spacing.sm * 6) / 7).coerceIn(32.dp, 44.dp)
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weekHeaders.forEach { header ->
                        Text(
                            text = header,
                            modifier = Modifier.size(cellSize),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                days.chunked(7).forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        week.forEach { cell ->
                            CalendarDay(
                                cell = cell,
                                cellSize = cellSize,
                                modifier = Modifier.size(cellSize)
                            )
                        }
                        repeat(7 - week.size) {
                            Box(modifier = Modifier.size(cellSize))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    cell: CalendarDayCell,
    cellSize: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val appColors = AppTheme.colors
    val day = cell.dayOfMonth

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (day == null) return@Box

        when (cell.streakMark) {
            CalendarStreakMark.TODAY,
            CalendarStreakMark.TODAY_STREAK -> {
                val showFlame = cell.streakMark == CalendarStreakMark.TODAY_STREAK
                Box(
                    modifier = Modifier
                        .size(cellSize * 0.82f)
                        .clip(CircleShape)
                        .background(colorScheme.primary.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (showFlame) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = appColors.amber,
                                modifier = Modifier.size((cellSize * 0.38f).coerceAtLeast(12.dp))
                            )
                            Text(
                                text = day.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            text = day.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            CalendarStreakMark.STREAK -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = appColors.amber,
                        modifier = Modifier.size((cellSize * 0.42f).coerceAtLeast(14.dp))
                    )
                    Text(
                        text = day.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = appColors.amber,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            CalendarStreakMark.INACTIVE -> {
                Text(
                    text = day.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                    textAlign = TextAlign.Center
                )
            }
            CalendarStreakMark.FUTURE -> {
                Text(
                    text = day.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    textAlign = TextAlign.Center
                )
            }
            CalendarStreakMark.BLANK -> Unit
        }
    }
}
