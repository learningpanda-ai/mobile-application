package com.example.learningpandaai.features.askpanda.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.designsystem.components.UserAvatar

@Composable
fun AskPandaTopBar(
    appTitle: String,
    userName: String,
    userInitial: String,
    userAvatarUrl: String?,
    userPlan: String? = null,
    streakLabel: String,
    usageQuotaLabel: String? = null,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(11.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            UserAvatar(
                imageUrl = userAvatarUrl,
                displayName = userName.ifBlank { userInitial },
                size = 36.dp,
                initialTextStyle = MaterialTheme.typography.labelLarge,
                plan = userPlan,
                contentDescription = "Your profile photo"
            )
            Text(
                text = appTitle,
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!usageQuotaLabel.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .widthIn(max = 120.dp)
                        .clip(CircleShape)
                        .background(colorScheme.secondaryContainer.copy(alpha = 0.85f))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = usageQuotaLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colorScheme.primary.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = streakLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}
