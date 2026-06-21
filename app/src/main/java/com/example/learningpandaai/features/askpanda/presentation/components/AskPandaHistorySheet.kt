package com.example.learningpandaai.features.askpanda.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.designsystem.theme.BrandPrimary
import com.example.learningpandaai.core.designsystem.theme.PureWhite
import com.example.learningpandaai.core.designsystem.theme.SurfaceHighlight
import com.example.learningpandaai.features.askpanda.presentation.ChatSessionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskPandaHistorySheet(
    visible: Boolean,
    title: String,
    closeContentDescription: String,
    emptyTitle: String,
    emptySubtitle: String,
    newSessionLabel: String,
    sessions: List<ChatSessionItem>,
    currentSessionId: String?,
    onDismiss: () -> Unit,
    onNewSession: () -> Unit,
    onSessionSelected: (String) -> Unit
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

        if (sessions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = emptyTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = emptySubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
            ) {
                items(sessions, key = { it.id }) { session ->
                    val isActive = session.id == currentSessionId
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSessionSelected(session.id) }
                            .background(
                                if (isActive) SurfaceHighlight else colorScheme.surface
                            )
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = session.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${session.subject} • Class ${session.className}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                    HorizontalDivider(color = colorScheme.outlineVariant)
                }
            }
        }

        Button(
            onClick = onNewSession,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPrimary,
                contentColor = PureWhite
            )
        ) {
            Text(
                text = "+ $newSessionLabel",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
