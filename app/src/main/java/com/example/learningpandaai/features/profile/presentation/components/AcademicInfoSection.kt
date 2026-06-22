package com.example.learningpandaai.features.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.designsystem.theme.BrandPrimary
import com.example.learningpandaai.core.designsystem.theme.PureWhite
import com.example.learningpandaai.core.designsystem.theme.TextOnChip
import com.example.learningpandaai.features.profile.presentation.SelectableChip

@Composable
fun AcademicInfoSection(
    sectionTitle: String,
    academicClassLabel: String,
    educationalBoardLabel: String,
    gradeOptions: List<SelectableChip>,
    boardOptions: List<SelectableChip>,
    onGradeSelected: (String) -> Unit,
    onBoardSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    ProfileCard(modifier = modifier, elevation = 4.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.School,
                contentDescription = null,
                tint = TextOnChip
            )
            Text(
                text = sectionTitle,
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
        ChipGroup(
            label = academicClassLabel,
            chips = gradeOptions,
            onChipSelected = onGradeSelected,
            modifier = Modifier.padding(top = 16.dp)
        )
        ChipGroup(
            label = educationalBoardLabel,
            chips = boardOptions,
            onChipSelected = onBoardSelected,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun ChipGroup(
    label: String,
    chips: List<SelectableChip>,
    onChipSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chips.forEach { chip ->
                SelectionChip(
                    label = chip.label,
                    isSelected = chip.isSelected,
                    onClick = { onChipSelected(chip.id) }
                )
            }
        }
    }
}

@Composable
private fun SelectionChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) BrandPrimary else colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        color = if (isSelected) PureWhite else colorScheme.onSurface,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
    )
}
