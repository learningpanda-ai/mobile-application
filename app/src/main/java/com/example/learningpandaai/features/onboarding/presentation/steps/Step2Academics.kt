package com.example.learningpandaai.features.onboarding.presentation.steps

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.features.onboarding.presentation.OnboardingUiState
import com.example.learningpandaai.features.onboarding.presentation.components.OnboardingDimens
import com.example.learningpandaai.features.onboarding.presentation.components.OnboardingStepTitle

@Composable
fun Step2Academics(
    uiState: OnboardingUiState,
    onClassSelected: (String) -> Unit,
    onBoardSelected: (String) -> Unit
) {
    val classes = listOf("Class 8", "Class 9", "Class 10", "Class 11", "Class 12")
    val boards = listOf("CBSE", "ICSE", "IGCSE", "IB", "State Board")

    Column(
        verticalArrangement = Arrangement.spacedBy(OnboardingDimens.SectionSpacing),
        modifier = Modifier.fillMaxWidth()
    ) {
        OnboardingStepTitle(
            title = "What grade are you in?",
            subtitle = "Choose class and board so your lessons match your school track."
        )

        Column(verticalArrangement = Arrangement.spacedBy(OnboardingDimens.FieldSpacing)) {
            Text(
                text = "Class",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            ClassCustomGrid(
                items = classes,
                selectedItem = uiState.selectedClass,
                onItemClick = onClassSelected
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(OnboardingDimens.FieldSpacing)) {
            Text(
                text = "School board",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            ClassCustomGrid(
                items = boards,
                selectedItem = uiState.selectedBoard,
                onItemClick = onBoardSelected
            )
        }
    }
}

@Composable
fun ClassCustomGrid(
    items: List<String>,
    selectedItem: String,
    onItemClick: (String) -> Unit
) {
    val groupedRows = items.chunked(2)

    Column(
        verticalArrangement = Arrangement.spacedBy(OnboardingDimens.FieldSpacing),
        modifier = Modifier.fillMaxWidth()
    ) {
        groupedRows.forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(OnboardingDimens.FieldSpacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { item ->
                    SelectablePillChip(
                        label = item,
                        isSelected = selectedItem == item,
                        onClick = { onItemClick(item) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun SelectablePillChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val pillShape = CircleShape

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorScheme.secondaryContainer
        } else {
            colorScheme.surface
        },
        animationSpec = tween(durationMillis = 220),
        label = "chipContainer"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) colorScheme.secondary else colorScheme.outline,
        animationSpec = tween(durationMillis = 220),
        label = "chipBorder"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) colorScheme.onSecondaryContainer else colorScheme.onSurface,
        animationSpec = tween(durationMillis = 220),
        label = "chipContent"
    )

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(pillShape)
            .background(containerColor)
            .border(
                width = if (isSelected) {
                    OnboardingDimens.ChipBorderSelected
                } else {
                    OnboardingDimens.ChipBorderDefault
                },
                color = borderColor,
                shape = pillShape
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Step2AcademicsPreview() {
    com.example.learningpandaai.core.designsystem.theme.LearningPandaAITheme {
        Box(modifier = Modifier.padding(20.dp)) {
            Step2Academics(
                uiState = OnboardingUiState(
                    selectedClass = "Class 10",
                    selectedBoard = "CBSE"
                ),
                onClassSelected = {},
                onBoardSelected = {}
            )
        }
    }
}
