package com.example.learningpandaai.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val DefaultEmailDomainChips = listOf(
    "@gmail.com",
    "@outlook.com",
    "@yahoo.com",
    "@hotmail.com"
)

@Composable
fun EmailDomainChipRow(
    currentEmail: String,
    domains: List<String> = DefaultEmailDomainChips,
    onEmailUpdated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        domains.forEach { domain ->
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(1.dp, colorScheme.outline, CircleShape)
                    .clickable { onEmailUpdated(currentEmail.substringBefore("@") + domain) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = domain,
                    color = colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
