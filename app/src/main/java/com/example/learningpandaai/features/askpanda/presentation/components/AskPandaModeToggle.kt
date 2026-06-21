package com.example.learningpandaai.features.askpanda.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningpandaai.core.designsystem.layout.NarrowTabBreakpoint
import com.example.learningpandaai.features.askpanda.presentation.AskPandaTab

@Composable
fun AskPandaModeToggle(
    selectedTab: AskPandaTab,
    chatLabel: String,
    voiceLabel: String,
    pandaChatLabel: String,
    onTabSelected: (AskPandaTab) -> Unit,
    modifier: Modifier = Modifier,
    showVideoTab: Boolean = true,
    comingSoonTabs: Set<AskPandaTab> = emptySet()
) {
    val colorScheme = MaterialTheme.colorScheme
    val tabs = buildList {
        add(TabSpec(AskPandaTab.CHAT, chatLabel, Icons.AutoMirrored.Outlined.Chat))
        add(TabSpec(AskPandaTab.VOICE, voiceLabel, Icons.Outlined.Mic))
        if (showVideoTab) {
            add(TabSpec(AskPandaTab.PANDA_CHAT, pandaChatLabel, Icons.Outlined.Videocam))
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        val iconOnly = maxWidth < NarrowTabBreakpoint
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEach { tabSpec ->
                ModeToggleTab(
                    tabSpec = tabSpec,
                    selectedTab = selectedTab,
                    comingSoonTabs = comingSoonTabs,
                    iconOnly = iconOnly,
                    onTabSelected = onTabSelected,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ModeToggleTab(
    tabSpec: TabSpec,
    selectedTab: AskPandaTab,
    comingSoonTabs: Set<AskPandaTab>,
    iconOnly: Boolean,
    onTabSelected: (AskPandaTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val tab = tabSpec.tab
    val isComingSoon = tab in comingSoonTabs
    val selected = tab == selectedTab && !isComingSoon
    val containerColor by animateColorAsState(
        targetValue = if (selected) colorScheme.surface else Color.Transparent,
        animationSpec = tween(durationMillis = 180),
        label = "tab_container"
    )
    val contentColor by animateColorAsState(
        targetValue = when {
            isComingSoon -> colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
            selected -> colorScheme.primary
            else -> colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 180),
        label = "tab_content"
    )
    val borderModifier = if (selected) {
        Modifier.border(1.dp, colorScheme.outlineVariant, CircleShape)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .then(borderModifier)
            .then(
                if (isComingSoon) {
                    Modifier
                } else {
                    Modifier.clickable { onTabSelected(tab) }
                }
            )
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isComingSoon) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Icon(
                    imageVector = tabSpec.icon,
                    contentDescription = tabSpec.label,
                    tint = contentColor
                )
                Text(
                    text = "SOON",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }
        } else if (iconOnly) {
            Icon(
                imageVector = tabSpec.icon,
                contentDescription = tabSpec.label,
                tint = contentColor
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = tabSpec.icon,
                    contentDescription = null,
                    tint = contentColor
                )
                Text(
                    text = tabSpec.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
            }
        }
    }
}

private data class TabSpec(
    val tab: AskPandaTab,
    val label: String,
    val icon: ImageVector
)
