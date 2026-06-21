package com.example.learningpandaai.features.askpanda.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.designsystem.theme.SurfaceChip
import com.example.learningpandaai.core.designsystem.theme.TextOnChip
import com.example.learningpandaai.features.askpanda.presentation.SubjectOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskPandaSubjectSheet(
    visible: Boolean,
    title: String,
    closeContentDescription: String,
    subjects: List<SubjectOption>,
    onDismiss: () -> Unit,
    onSubjectSelected: (String) -> Unit
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colorScheme = MaterialTheme.colorScheme

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = closeContentDescription,
                    tint = colorScheme.onSurface
                )
            }
        }
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            subjects.forEachIndexed { index, subject ->
                SubjectSheetRow(
                    subject = subject,
                    onClick = { onSubjectSelected(subject.id) }
                )
                if (index < subjects.lastIndex) {
                    HorizontalDivider(color = colorScheme.outlineVariant, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
private fun SubjectSheetRow(
    subject: SubjectOption,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (subject.isSelected) {
                    Modifier.background(SurfaceChip.copy(alpha = 0.35f))
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = subject.name,
            style = MaterialTheme.typography.bodyLarge,
            color = if (subject.isSelected) TextOnChip else colorScheme.onSurface,
            fontWeight = if (subject.isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
        if (subject.isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = TextOnChip,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(SurfaceChip)
                    .padding(4.dp)
            )
        }
    }
}
