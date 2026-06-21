package com.example.learningpandaai.features.onboarding.presentation.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.features.onboarding.presentation.OnboardingUiState
import com.example.learningpandaai.features.onboarding.presentation.components.OnboardingDimens
import com.example.learningpandaai.features.onboarding.presentation.components.OnboardingStepTitle

@Composable
fun Step3Subjects(
    uiState: OnboardingUiState,
    onSubjectToggled: (String) -> Unit
) {
    val subjects = listOf(
        "Economics",
        "Geography",
        "History",
        "Mathematics",
        "Political Science",
        "Science"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(OnboardingDimens.SectionSpacing),
        modifier = Modifier.fillMaxWidth()
    ) {
        OnboardingStepTitle(
            title = "Which subjects need help?",
            subtitle = "Select subjects you want your AI tutor to prioritize."
        )

        SubjectsCustomGrid(
            items = subjects,
            selectedItems = uiState.selectedSubjects,
            onItemClick = onSubjectToggled
        )
    }
}

@Composable
fun SubjectsCustomGrid(
    items: List<String>,
    selectedItems: Set<String>,
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
                        isSelected = selectedItems.contains(item),
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

@Preview(showBackground = true)
@Composable
fun Step3SubjectsPreview() {
    com.example.learningpandaai.core.designsystem.theme.LearningPandaAITheme {
        Box(modifier = Modifier.padding(20.dp)) {
            Step3Subjects(
                uiState = OnboardingUiState(
                    selectedSubjects = setOf("Mathematics", "Science")
                ),
                onSubjectToggled = {}
            )
        }
    }
}
