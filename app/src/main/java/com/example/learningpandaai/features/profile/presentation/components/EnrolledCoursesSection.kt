package com.example.learningpandaai.features.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.designsystem.theme.BrandSecondary
import com.example.learningpandaai.core.designsystem.theme.TextOnChip
import com.example.learningpandaai.features.profile.presentation.EnrolledCourseGroup

@Composable
fun EnrolledCoursesSection(
    sectionTitle: String,
    courseGroups: List<EnrolledCourseGroup>,
    editContentDescription: String,
    modifier: Modifier = Modifier
) {
    if (courseGroups.isEmpty()) return

    val colorScheme = MaterialTheme.colorScheme

    ProfileCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
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
            IconButton(onClick = { /* edit courses — future */ }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = editContentDescription,
                    tint = BrandSecondary
                )
            }
        }
        courseGroups.forEachIndexed { index, group ->
            Column(
                modifier = Modifier.padding(top = if (index == 0) 16.dp else 14.dp)
            ) {
                Text(
                    text = group.categoryName,
                    style = MaterialTheme.typography.labelLarge,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    group.topics.forEach { topic ->
                        Text(
                            text = topic,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(colorScheme.surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
