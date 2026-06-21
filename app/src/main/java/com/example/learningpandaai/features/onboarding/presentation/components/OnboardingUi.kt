package com.example.learningpandaai.features.onboarding.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

object OnboardingDimens {
    val ScreenCardCorner = 16.dp
    val SectionCardCorner = 16.dp
    val FieldCorner = 14.dp
    val SectionSpacing = 20.dp
    val FieldSpacing = 12.dp
    val CardPadding = 16.dp
    val ScreenCardElevation = 2.dp
    val ChipBorderSelected = 1.5.dp
    val ChipBorderDefault = 1.dp
}

val OnboardingScreenCardShape = RoundedCornerShape(OnboardingDimens.ScreenCardCorner)
val OnboardingSectionCardShape = RoundedCornerShape(OnboardingDimens.SectionCardCorner)
val OnboardingFieldShape = RoundedCornerShape(OnboardingDimens.FieldCorner)

@Composable
fun OnboardingStepTitle(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun OnboardingSectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = OnboardingSectionCardShape,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(OnboardingDimens.CardPadding),
            verticalArrangement = Arrangement.spacedBy(OnboardingDimens.FieldSpacing)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = colorScheme.onSurface
            )
            content()
        }
    }
}