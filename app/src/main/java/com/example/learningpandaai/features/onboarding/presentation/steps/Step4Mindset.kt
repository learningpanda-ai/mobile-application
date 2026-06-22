package com.example.learningpandaai.features.onboarding.presentation.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.features.onboarding.presentation.OnboardingUiState
import com.example.learningpandaai.features.onboarding.presentation.components.OnboardingDimens
import com.example.learningpandaai.features.onboarding.presentation.components.OnboardingSectionCard
import com.example.learningpandaai.features.onboarding.presentation.components.OnboardingStepTitle

@Composable
fun Step4Mindset(
    uiState: OnboardingUiState,
    onFavoriteSubjectSelected: (String) -> Unit,
    onStudiesFeelingSelected: (String) -> Unit,
    onCareerIdeaSelected: (String) -> Unit,
    onDiscoverStrengthsSelected: (String) -> Unit
) {
    val favoriteSubjects =
        listOf("Maths", "Science", "English", "Social Science", "Computer", "Not sure yet")
    val studyFeelings =
        listOf("I enjoy learning", "It's okay", "I feel stressed", "I feel confused")
    val careerIdeas =
        listOf("Yes, I have an idea", "I have many options", "Not yet", "I feel confused")
    val strengthInterests = listOf("Yes, definitely!", "Maybe", "Not sure")

    Column(
        verticalArrangement = Arrangement.spacedBy(OnboardingDimens.SectionSpacing),
        modifier = Modifier.fillMaxWidth()
    ) {
        OnboardingStepTitle(
            title = "A little about you",
            subtitle = "Help us personalize your study experience and goals."
        )

        QuestionLayout(
            title = "Which subject do you like the most?",
            items = favoriteSubjects,
            selectedValue = uiState.favoriteSubject,
            onValueSelected = onFavoriteSubjectSelected
        )

        QuestionLayout(
            title = "How do you feel about studies right now?",
            items = studyFeelings,
            selectedValue = uiState.studiesFeeling,
            onValueSelected = onStudiesFeelingSelected
        )

        QuestionLayout(
            title = "Have you thought about your future career?",
            items = careerIdeas,
            selectedValue = uiState.careerIdea,
            onValueSelected = onCareerIdeaSelected
        )

        QuestionLayout(
            title = "Interested in discovering your strengths?",
            items = strengthInterests,
            selectedValue = uiState.discoverStrengths,
            onValueSelected = onDiscoverStrengthsSelected
        )
    }
}

@Composable
fun QuestionLayout(
    title: String,
    items: List<String>,
    selectedValue: String,
    onValueSelected: (String) -> Unit
) {
    OnboardingSectionCard(title = title) {
        ClassCustomGrid(
            items = items,
            selectedItem = selectedValue,
            onItemClick = onValueSelected
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Step4MindsetPreview() {
    com.example.learningpandaai.core.designsystem.theme.LearningPandaAITheme {
        Box(modifier = Modifier.padding(20.dp)) {
            Step4Mindset(
                uiState = OnboardingUiState(
                    favoriteSubject = "Maths",
                    studiesFeeling = "I enjoy learning",
                    careerIdea = "Yes, I have an idea",
                    discoverStrengths = "Yes, definitely!"
                ),
                onFavoriteSubjectSelected = {},
                onStudiesFeelingSelected = {},
                onCareerIdeaSelected = {},
                onDiscoverStrengthsSelected = {}
            )
        }
    }
}
