package com.example.learningpandaai.features.playzone.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learningpandaai.core.designsystem.components.ErrorStateContent
import com.example.learningpandaai.core.designsystem.components.shimmerPlaceholder
import com.example.learningpandaai.core.designsystem.theme.BrandPrimary
import com.example.learningpandaai.core.designsystem.theme.BrandPrimaryGradientEnd
import com.example.learningpandaai.core.designsystem.theme.LearningPandaAITheme
import com.example.learningpandaai.core.designsystem.theme.PureWhite

private val PlayZoneCardShape = RoundedCornerShape(16.dp)
private val PlayZoneContentPadding = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

@Composable
fun PlayZoneScreen(viewModel: PlayZoneViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is PlayZoneUiState.Loading -> PlayZoneLoadingSkeleton()
        is PlayZoneUiState.Error -> ErrorStateContent(
            message = state.message,
            retryLabel = state.retryLabel,
            onRetry = viewModel::retry,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
        is PlayZoneUiState.Success -> PlayZoneSuccessContent(state = state)
    }
}

@Composable
private fun PlayZoneSuccessContent(state: PlayZoneUiState.Success) {
    val labels = state.labels
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .then(PlayZoneContentPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PlayZoneHeader(title = labels.screenTitle, subtitle = labels.screenSubtitle)

        GameZoneHeroBanner(
            badge = labels.gameZoneBadge,
            title = labels.heroTitle,
            subtitle = labels.heroSubtitle
        )

        StatsGrid(
            rankLabel = labels.rankLabel,
            rankValue = state.rankDisplay,
            xpLabel = labels.xpEarnedLabel,
            xpValue = state.xpEarnedDisplay,
            gamesLabel = labels.gamesPlayedLabel,
            gamesValue = state.gamesPlayedDisplay,
            streakLabel = labels.gameStreakLabel,
            streakValue = state.gameStreakDisplay
        )

        Text(
            text = labels.chooseModeTitle,
            style = MaterialTheme.typography.titleSmall,
            color = colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        state.gameModes.forEach { mode ->
            GameModeCard(
                mode = mode,
                badgeLabel = labels.comingSoonBadge,
                launchingSoonLabel = labels.launchingSoonLabel
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun PlayZoneHeader(title: String, subtitle: String) {
    val colorScheme = MaterialTheme.colorScheme

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GameZoneHeroBanner(badge: String, title: String, subtitle: String) {
    val gradient = Brush.linearGradient(colors = listOf(BrandPrimary, BrandPrimaryGradientEnd))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PlayZoneCardShape)
            .background(gradient)
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = PureWhite.copy(alpha = 0.92f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(PureWhite.copy(alpha = 0.16f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = PureWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = PureWhite.copy(alpha = 0.92f),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun StatsGrid(
    rankLabel: String,
    rankValue: String,
    xpLabel: String,
    xpValue: String,
    gamesLabel: String,
    gamesValue: String,
    streakLabel: String,
    streakValue: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                icon = Icons.Filled.EmojiEvents,
                label = rankLabel,
                value = rankValue,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Filled.Bolt,
                label = xpLabel,
                value = xpValue,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                icon = Icons.Filled.CalendarMonth,
                label = gamesLabel,
                value = gamesValue,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Filled.Timer,
                label = streakLabel,
                value = streakValue,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    PlayZoneCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorScheme.secondary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GameModeCard(
    mode: UpcomingGameMode,
    badgeLabel: String,
    launchingSoonLabel: String
) {
    val colorScheme = MaterialTheme.colorScheme

    PlayZoneCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = mode.icon,
                        contentDescription = null,
                        tint = colorScheme.secondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = mode.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                ComingSoonBadge(label = badgeLabel)
            }

            Text(
                text = mode.description,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mode.tags.forEach { tag ->
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(colorScheme.surface)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = launchingSoonLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ComingSoonBadge(label: String) {
    val colorScheme = MaterialTheme.colorScheme

    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = colorScheme.onPrimary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colorScheme.primary)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun PlayZoneCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = PlayZoneCardShape,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

@Composable
private fun PlayZoneLoadingSkeleton() {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .then(PlayZoneContentPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .height(24.dp)
                .shimmerPlaceholder(RoundedCornerShape(6.dp))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .shimmerPlaceholder(PlayZoneCardShape)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .shimmerPlaceholder(PlayZoneCardShape)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .shimmerPlaceholder(PlayZoneCardShape)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayZoneSuccessPreview() {
    LearningPandaAITheme {
        PlayZoneSuccessContent(
            state = PlayZoneUiState.Success(
                labels = PlayZoneLabels(
                    screenTitle = "Gamify",
                    screenSubtitle = "Earn XP, beat your classmates, and make studying feel like a game.",
                    gameZoneBadge = "GAME ZONE",
                    heroTitle = "Level Up Your Learning",
                    heroSubtitle = "Pick a mode and start playing — new game types are launching soon.",
                    rankLabel = "Rank",
                    xpEarnedLabel = "XP Earned",
                    gamesPlayedLabel = "Games Played",
                    gameStreakLabel = "Game Streak",
                    chooseModeTitle = "Choose Your Game Mode",
                    comingSoonBadge = "COMING SOON",
                    launchingSoonLabel = "Launching soon",
                    emptyTitle = "",
                    emptySubtitle = "",
                    emptyHint = "",
                    loadingMessage = "",
                    retryLabel = "Retry"
                ),
                rankDisplay = "—",
                xpEarnedDisplay = "0 XP",
                gamesPlayedDisplay = "0",
                gameStreakDisplay = "—",
                gameModes = emptyList()
            )
        )
    }
}