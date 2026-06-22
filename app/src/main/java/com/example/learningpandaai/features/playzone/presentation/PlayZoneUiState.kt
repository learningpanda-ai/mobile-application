package com.example.learningpandaai.features.playzone.presentation

import androidx.compose.ui.graphics.vector.ImageVector

/** User-visible copy for Play Zone — supplied by ViewModel / API layer. */
data class PlayZoneLabels(
    val screenTitle: String,
    val screenSubtitle: String,
    val gameZoneBadge: String,
    val heroTitle: String,
    val heroSubtitle: String,
    val rankLabel: String,
    val xpEarnedLabel: String,
    val gamesPlayedLabel: String,
    val gameStreakLabel: String,
    val chooseModeTitle: String,
    val comingSoonBadge: String,
    val launchingSoonLabel: String,
    val emptyTitle: String,
    val emptySubtitle: String,
    val emptyHint: String,
    val loadingMessage: String,
    val retryLabel: String
)

data class UpcomingGameMode(
    val title: String,
    val description: String,
    val tags: List<String>,
    val icon: ImageVector
)

sealed interface PlayZoneUiState {
    data class Loading(val message: String) : PlayZoneUiState

    data class Error(
        val message: String,
        val retryLabel: String
    ) : PlayZoneUiState

    data class Success(
        val labels: PlayZoneLabels,
        val rankDisplay: String,
        val xpEarnedDisplay: String,
        val gamesPlayedDisplay: String,
        val gameStreakDisplay: String,
        val gameModes: List<UpcomingGameMode>
    ) : PlayZoneUiState
}