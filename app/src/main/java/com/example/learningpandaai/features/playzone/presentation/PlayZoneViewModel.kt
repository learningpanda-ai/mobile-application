package com.example.learningpandaai.features.playzone.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningpandaai.core.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayZoneViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<PlayZoneUiState>(
        PlayZoneUiState.Loading(buildLabels().loadingMessage)
    )
    val uiState: StateFlow<PlayZoneUiState> = _uiState.asStateFlow()

    init {
        loadPlayZone()
    }

    fun retry() = loadPlayZone()

    private fun loadPlayZone() {
        val labels = buildLabels()
        _uiState.value = PlayZoneUiState.Loading(labels.loadingMessage)
        viewModelScope.launch {
            delay(350)
            runCatching { buildLoadedState(labels) }
                .onSuccess { _uiState.value = it }
                .onFailure { throwable ->
                    Logger.e("loadPlayZone: failed — ${throwable.message}", throwable)
                    _uiState.value = PlayZoneUiState.Error(
                        message = throwable.message ?: "Failed to load Play Zone.",
                        retryLabel = labels.retryLabel
                    )
                }
        }
    }

    private fun buildLabels() = PlayZoneLabels(
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
        emptyTitle = "Game modes launching soon",
        emptySubtitle = "Quizzes, battles, and daily challenges are on the way.",
        emptyHint = "Keep learning with Ask Panda while we build the Game Zone.",
        loadingMessage = "Loading Game Zone…",
        retryLabel = "Retry"
    )

    private fun buildGameModes(): List<UpcomingGameMode> = listOf(
        UpcomingGameMode(
            title = "Quizzes",
            description = "Test your knowledge with AI-generated multiple-choice questions tailored to your enrolled subjects and grade.",
            tags = listOf("Multiple Choice", "Timed", "Subject-based"),
            icon = Icons.Filled.Psychology
        ),
        UpcomingGameMode(
            title = "Compete",
            description = "Go head-to-head with other students in real-time academic battles. Climb the leaderboard and earn XP!",
            tags = listOf("Multiplayer", "Real-time", "Leaderboard"),
            icon = Icons.Filled.Groups
        ),
        UpcomingGameMode(
            title = "Daily Challenge",
            description = "A fresh handpicked question every day. Answer correctly to extend your streak and earn bonus XP.",
            tags = listOf("Daily", "Streak Bonus", "Handpicked"),
            icon = Icons.Filled.Calculate
        ),
        UpcomingGameMode(
            title = "Speed Round",
            description = "60 seconds. As many correct answers as possible. Pure adrenaline-fueled learning at its best.",
            tags = listOf("60 Seconds", "High Score", "Fast-paced"),
            icon = Icons.Filled.Timer
        )
    )

    private fun buildLoadedState(labels: PlayZoneLabels): PlayZoneUiState.Success {
        return PlayZoneUiState.Success(
            labels = labels,
            rankDisplay = "—",
            xpEarnedDisplay = "0 XP",
            gamesPlayedDisplay = "0",
            gameStreakDisplay = "—",
            gameModes = buildGameModes()
        )
    }
}