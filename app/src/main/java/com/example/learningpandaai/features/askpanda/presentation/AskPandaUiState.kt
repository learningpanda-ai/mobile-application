package com.example.learningpandaai.features.askpanda.presentation

enum class AskPandaMode {
    CHAT,
    VOICE,
    VIDEO
}

enum class MessageRole {
    USER,
    ASSISTANT
}

/** All user-visible strings for Ask Panda — supplied by ViewModel / API layer. */
data class AskPandaLabels(
    val appTitle: String,
    val streakLabel: String,
    val chatTabLabel: String,
    val voiceTabLabel: String,
    val videoTabLabel: String,
    val historyContentDescription: String,
    val subjectDropdownContentDescription: String,
    val switchSubjectTitle: String,
    val closeContentDescription: String,
    val chatHistoryTitle: String,
    val newSessionLabel: String,
    val emptyHistoryTitle: String,
    val emptyHistorySubtitle: String,
    val emptyChatGreeting: String,
    val emptyChatSubtitle: String,
    val inputPlaceholder: String,
    val sendContentDescription: String,
    val aiDisclaimer: String,
    val attachContentDescription: String,
    val voiceStatusListening: String,
    val voiceStatusIdle: String,
    val voiceStatusProcessing: String,
    val voicePermissionHint: String,
    val endSessionLabel: String,
    val micContentDescription: String,
    val videoIdleHint: String,
    val videoListeningLabel: String,
    val videoTapToSpeakHint: String,
    val pandaCamLabel: String,
    val typingIndicatorLabel: String,
    val thumbsUpContentDescription: String,
    val thumbsDownContentDescription: String,
    val retryLabel: String,
    val loadingMessage: String
)

data class SubjectOption(
    val id: String,
    val name: String,
    val isSelected: Boolean
)

data class ShortcutPrompt(
    val id: String,
    val label: String
)

data class ConversationMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val sourceCitation: String? = null
)

data class ChatSessionItem(
    val id: String,
    val subject: String,
    val className: String,
    val title: String,
    val createdAt: String
)

data class VoiceUiState(
    val statusMessage: String,
    val isListening: Boolean,
    val isProcessing: Boolean = false,
    val liveTranscript: String = "",
    val lastUserMessage: String? = null,
    val lastAssistantMessage: String? = null,
    val showPermissionHint: Boolean = false
)

data class VideoUiState(
    val statusLabel: String,
    val isListening: Boolean = false,
    val isProcessing: Boolean = false,
    val liveTranscript: String = "",
    val dialogueText: String,
    val lastAssistantMessage: String? = null,
    val showPermissionHint: Boolean = false
)

sealed interface AskPandaUiState {
    data class Loading(val message: String) : AskPandaUiState

    /** Shown when the API returns 403 — the student account exists but is not activated yet. */
    data class AccountNotActivated(
        val title: String,
        val message: String,
        val contactButtonLabel: String
    ) : AskPandaUiState

    /** Shown when the student's current class is not eligible to use Ask Panda chat. */
    data class ClassNotSupported(
        val title: String,
        val message: String
    ) : AskPandaUiState

    data class Error(
        val title: String? = null,
        val message: String,
        val retryLabel: String
    ) : AskPandaUiState

    data class Ready(
        val labels: AskPandaLabels,
        val userName: String,
        val userInitial: String,
        val userAvatarUrl: String? = null,
        val userPlan: String? = null,
        val selectedTab: AskPandaTab,
        val subjectDisplay: String,
        val subjects: List<SubjectOption>,
        val showSubjectSheet: Boolean,
        val showHistorySheet: Boolean,
        val sessions: List<ChatSessionItem>,
        val currentSessionId: String? = null,
        val chatHistory: List<com.example.learningpandaai.features.askpanda.data.remote.ChatMessageItemDto>,
        val shortcutPrompts: List<ShortcutPrompt>,
        val inputText: String,
        val isStreaming: Boolean,
        val isSendEnabled: Boolean,
        val usageQuotaLabel: String? = null,
        val voice: VoiceUiState,
        val video: VideoUiState
    ) : AskPandaUiState {
        val selectedMode: AskPandaMode get() = selectedTab.toMode()
    }
}

sealed interface AskPandaUiEvent {
    data class SessionCreated(val sessionId: String) : AskPandaUiEvent
    data class SessionDeleted(val sessionId: String) : AskPandaUiEvent
    data class ChatError(val message: String) : AskPandaUiEvent
}
