package com.example.learningpandaai.features.askpanda.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

@Composable
fun AskPandaAiDisclaimer(
    text: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp),
        style = MaterialTheme.typography.labelSmall,
        color = colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}
