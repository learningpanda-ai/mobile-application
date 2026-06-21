package com.example.learningpandaai.features.askpanda.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import com.example.learningpandaai.core.designsystem.theme.BrandSecondary
import com.example.learningpandaai.features.askpanda.presentation.ShortcutPrompt

@Composable
fun AskPandaChatEmptyState(
    greetingTemplate: String,
    userName: String,
    subtitle: String,
    shortcuts: List<ShortcutPrompt>,
    onShortcutClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val greeting = if (greetingTemplate.contains("%s")) {
        greetingTemplate.format(userName)
    } else {
        "$greetingTemplate $userName"
    }
    val scrollState = rememberScrollState()
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!imeVisible) {
            PandaLogoCircle(size = 72.dp)
            Spacer(modifier = Modifier.heightIn(min = 12.dp))
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                shortcuts.chunked(2).forEach { rowShortcuts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowShortcuts.forEach { prompt ->
                            ShortcutChip(
                                label = prompt.label,
                                onClick = { onShortcutClick(prompt.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowShortcuts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 52.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = androidx.compose.ui.graphics.Color.Transparent,
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelMedium,
            color = BrandSecondary,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 3
        )
    }
}
