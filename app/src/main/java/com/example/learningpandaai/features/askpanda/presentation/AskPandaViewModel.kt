package com.example.learningpandaai.features.askpanda.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningpandaai.core.network.ApiErrorMapper
import com.example.learningpandaai.core.network.SessionExpiredException
import com.example.learningpandaai.core.util.ChatInputLimits
import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.features.askpanda.data.remote.ChatMessageItemDto
import com.example.learningpandaai.features.askpanda.data.local.ChatFeedbackLocalStore
import com.example.learningpandaai.features.askpanda.data.local.ChatFeedbackSyncManager
import com.example.learningpandaai.features.askpanda.domain.AccountNotActivatedException
import com.example.learningpandaai.features.askpanda.domain.AgentRepository
import com.example.learningpandaai.features.askpanda.domain.ChatMessageFeedback
import com.example.learningpandaai.features.askpanda.domain.ChatSession
import com.example.learningpandaai.features.profile.domain.ProfileRepository
import retrofit2.HttpException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AskPandaViewModel @Inject constructor(
    private val agentRepository: AgentRepository,
    private val profileRepository: ProfileRepository,
    private val chatFeedbackLocalStore: ChatFeedbackLocalStore,
    private val chatFeedbackSyncManager: ChatFeedbackSyncManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AskPandaUiState>(
        AskPandaUiState.Loading("Loading Ask Panda…")
    )
    val uiState: StateFlow<AskPandaUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _events = Channel<AskPandaUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadSessions()
        syncProfileImageIfMissing()
        viewModelScope.launch { chatFeedbackSyncManager.syncPending() }
        viewModelScope.launch {
            profileRepository.observeProfileCacheUpdates().collect {
                syncProfileContextFromCache()
            }
        }
    }

    fun retry() = loadSessions(showFullLoading = true)

    /** Re-reads cached profile (grade, name, plan) and updates Ask Panda without a full pull-to-refresh. */
    fun syncProfileContextFromCache() {
        viewModelScope.launch {
            val className = resolveClassName()
            when (val current = _uiState.value) {
                is AskPandaUiState.ClassNotSupported -> {
                    if (className == ALLOWED_CHAT_CLASS) {
                        loadSessions(showFullLoading = false)
                    }
                }
                is AskPandaUiState.Ready -> {
                    if (className != ALLOWED_CHAT_CLASS) {
                        _uiState.value = classNotSupportedState()
                        return@launch
                    }
                    val previousSubject = current.subjects.firstOrNull { it.isSelected }?.name
                    val subjects = loadSubjects()
                    val syncedSubjects = subjects.map { option ->
                        option.copy(isSelected = option.name == previousSubject)
                    }.let { synced ->
                        when {
                            previousSubject != null && synced.any { it.isSelected } -> synced
                            synced.isNotEmpty() -> synced.mapIndexed { index, option ->
                                option.copy(isSelected = index == 0)
                            }
                            else -> synced
                        }
                    }
                    val selectedSubject = syncedSubjects.firstOrNull { it.isSelected }?.name
                    val cached = profileRepository.getCachedSnapshot()
                    val userName = cached.firstName.substringBefore(" ").ifBlank { "Student" }
                    val updated = current.copy(
                        userName = userName,
                        userInitial = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        userAvatarUrl = cached.avatarUrl,
                        userPlan = cached.plan,
                        subjects = syncedSubjects,
                        shortcutPrompts = buildShortcutPrompts(selectedSubject),
                        subjectDisplay = "${selectedSubject ?: "General"} • Class $className"
                    )
                    _uiState.value = updated
                }
                else -> Unit
            }
        }
    }

    fun refresh() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            val streakDays = profileRepository.getCurrentProfile()
                .onSuccess { profile ->
                    updateReady {
                        it.copy(labels = it.labels.copy(streakLabel = profile.currentStreak.toString()))
                    }
                }
                .onFailure { Logger.e("refresh: profile refresh failed — ${it.message}", it) }
                .getOrNull()?.currentStreak ?: 0
            if (resolveClassName() != ALLOWED_CHAT_CLASS) {
                _uiState.value = classNotSupportedState()
            } else {
                reloadSessionsAndStartFresh(showFullLoading = false, streakDays = streakDays)
                refreshUsageQuota()
            }
            _isRefreshing.value = false
            viewModelScope.launch { chatFeedbackSyncManager.syncPending() }
        }
    }

    fun selectTab(tab: AskPandaTab) {
        if (tab != AskPandaTab.CHAT) return
        updateReady { it.copy(selectedTab = AskPandaTab.CHAT) }
    }

    fun selectMode(mode: AskPandaMode) = selectTab(AskPandaTab.fromMode(mode))

    fun setSubjectSheetVisible(visible: Boolean) = updateReady { it.copy(showSubjectSheet = visible) }

    fun setHistorySheetVisible(visible: Boolean) = updateReady { it.copy(showHistorySheet = visible) }

    fun selectSubject(subjectId: String) {
        val ready = _uiState.value as? AskPandaUiState.Ready ?: return
        val selected = ready.subjects.find { it.id == subjectId } ?: return
        if (selected.isSelected) {
            updateReady { it.copy(showSubjectSheet = false) }
            return
        }
        val updated = ready.copy(
            subjects = ready.subjects.map { it.copy(isSelected = it.id == subjectId) },
            showSubjectSheet = false,
            shortcutPrompts = buildShortcutPrompts(selected.name)
        )
        _uiState.value = updated.copy(subjectDisplay = selectedSubjectDisplay(updated))
        viewModelScope.launch { switchToSubjectSession(selected.name) }
    }

    private suspend fun switchToSubjectSession(subjectName: String) {
        startLocalDraftSession()
    }

    fun onInputChanged(text: String) = updateReady {
        val capped = text.take(ChatInputLimits.MESSAGE_MAX_LENGTH)
        it.copy(
            inputText = capped,
            isSendEnabled = capped.isNotBlank() && !it.isStreaming
        )
    }

    fun onShortcutSelected(promptId: String) {
        val ready = _uiState.value as? AskPandaUiState.Ready ?: return
        val prompt = ready.shortcutPrompts.find { it.id == promptId } ?: return
        onInputChanged(prompt.label)
        sendMessage()
    }

    fun sendMessage() {
        val ready = _uiState.value as? AskPandaUiState.Ready ?: return
        val text = ready.inputText.trim()
        if (text.isBlank() || ready.isStreaming) return

        val className = resolveClassName()
        if (className != ALLOWED_CHAT_CLASS) {
            _uiState.value = classNotSupportedState()
            return
        }

        val userMessage = ChatMessageItemDto(role = ROLE_USER, content = text)
        val subject = ready.subjects.firstOrNull { it.isSelected }?.name ?: "General"

        _uiState.value = ready.copy(
            chatHistory = ready.chatHistory + userMessage,
            inputText = "",
            isStreaming = true,
            isSendEnabled = false
        )

        viewModelScope.launch {
            val conversationId = ensureConversationId(subject, className)
            if (conversationId == null) {
                finishSending()
                _events.trySend(AskPandaUiEvent.ChatError("Couldn't start the conversation. Please retry."))
                return@launch
            }
            agentRepository.sendMessage(conversationId, text)
                .onSuccess { result ->
                    val current = _uiState.value as? AskPandaUiState.Ready ?: return@onSuccess
                    val historyWithUserId = if (current.chatHistory.isNotEmpty()) {
                        current.chatHistory.dropLast(1) + ChatMessageItemDto(
                            role = result.userMessage.role,
                            content = result.userMessage.content,
                            id = result.userMessage.id
                        )
                    } else {
                        listOf(
                            ChatMessageItemDto(
                                role = result.userMessage.role,
                                content = result.userMessage.content,
                                id = result.userMessage.id
                            )
                        )
                    }
                    val updatedSessions = result.conversationTitle?.let { title ->
                        current.sessions.map { item ->
                            if (item.id == conversationId) item.copy(title = title) else item
                        }
                    } ?: current.sessions
                    _uiState.value = current.copy(
                        chatHistory = (historyWithUserId + ChatMessageItemDto(
                            role = result.reply.role,
                            content = result.reply.content,
                            id = result.reply.id,
                            userQuestion = result.userMessage.content,
                            userMessageId = result.userMessage.id
                        )).withLocalFeedback(conversationId),
                        isStreaming = false,
                        isSendEnabled = current.inputText.isNotBlank(),
                        sessions = updatedSessions
                    )
                    refreshConversationsList()
                    refreshUsageQuota()
                }
                .onFailure { throwable ->
                    Logger.e("sendMessage: failed — ${throwable.message}", throwable)
                    finishSending()
                    _events.trySend(AskPandaUiEvent.ChatError(mapChatStreamError(throwable)))
                }
        }
    }

    private fun finishSending() {
        val current = _uiState.value as? AskPandaUiState.Ready ?: return
        _uiState.value = current.copy(
            isStreaming = false,
            isSendEnabled = current.inputText.isNotBlank()
        )
    }

    /** Returns the active conversation id, creating one for the subject/class if needed. */
    private suspend fun ensureConversationId(subject: String, className: String): String? {
        val existing = (_uiState.value as? AskPandaUiState.Ready)?.currentSessionId
        if (!existing.isNullOrBlank()) return existing
        var createdId: String? = null
        agentRepository.createSession(subject, className, title = null)
            .onSuccess { session ->
                createdId = session.id
                updateReady { it.copy(currentSessionId = session.id) }
            }
            .onFailure { Logger.e("ensureConversationId: create failed — ${it.message}", it) }
        return createdId
    }

    private suspend fun refreshConversationsList() {
        agentRepository.getSessions().onSuccess { sessions ->
            val sorted = sessions.toHistoryItems()
            updateReady { it.copy(sessions = sorted) }
        }
    }

    private fun mapChatStreamError(throwable: Throwable): String = when {
        throwable is HttpException && throwable.code() == HTTP_TOO_MANY_REQUESTS ->
            "You've reached the hourly limit for Ask Panda. Please try again later."
        throwable is HttpException && throwable.code() == HTTP_UNPROCESSABLE_ENTITY ->
            "Something went wrong with that request. Please try again."
        throwable is HttpException ->
            ApiErrorMapper.mapHttpException(throwable).message ?: "Something went wrong. Please try again."
        throwable is IOException -> ApiErrorMapper.NETWORK_ERROR_MESSAGE
        else -> throwable.message ?: "Something went wrong. Please try again."
    }

    fun toggleVoiceListening() {
        val ready = _uiState.value as? AskPandaUiState.Ready ?: return
        if (ready.voice.isProcessing) return
        if (ready.voice.isListening) {
            stopVoiceListening(ready)
        } else {
            startVoiceListening(ready)
        }
    }

    fun toggleVideoListening() {
        val ready = _uiState.value as? AskPandaUiState.Ready ?: return
        if (ready.video.isProcessing) return
        if (ready.video.isListening) {
            stopVideoListening(ready)
        } else {
            startVideoListening(ready)
        }
    }

    fun onVoicePartialResult(text: String) = updateReady { state ->
        state.copy(
            voice = state.voice.copy(
                liveTranscript = text,
                statusMessage = state.labels.voiceStatusListening
            )
        )
    }

    fun onVoiceFinalResult(text: String) {
        val ready = _uiState.value as? AskPandaUiState.Ready ?: return
        val trimmed = text.trim()
        if (trimmed.isBlank()) {
            stopVoiceListening(ready)
            return
        }
        submitVoiceUtterance(ready, trimmed)
    }

    fun onVideoPartialResult(text: String) = updateReady { state ->
        state.copy(
            video = state.video.copy(
                liveTranscript = text,
                statusLabel = state.labels.videoListeningLabel
            )
        )
    }

    fun onVideoFinalResult(text: String) {
        val ready = _uiState.value as? AskPandaUiState.Ready ?: return
        val trimmed = text.trim()
        if (trimmed.isBlank()) {
            stopVideoListening(ready)
            return
        }
        submitVideoUtterance(ready, trimmed)
    }

    fun onVoiceRecognitionError(message: String) = updateReady { state ->
        state.copy(
            voice = state.voice.copy(
                isListening = false,
                liveTranscript = "",
                statusMessage = message
            )
        )
    }

    fun onVideoRecognitionError(message: String) = updateReady { state ->
        state.copy(
            video = state.video.copy(
                isListening = false,
                liveTranscript = "",
                statusLabel = message
            )
        )
    }

    fun onVoicePermissionDenied() = updateReady { state ->
        state.copy(
            voice = state.voice.copy(
                isListening = false,
                showPermissionHint = true,
                statusMessage = state.labels.voicePermissionHint
            )
        )
    }

    fun onVideoPermissionDenied() = updateReady { state ->
        state.copy(
            video = state.video.copy(
                isListening = false,
                showPermissionHint = true,
                statusLabel = state.labels.voicePermissionHint
            )
        )
    }

    fun endVoiceSession() = updateReady { state ->
        state.copy(
            voice = VoiceUiState(
                statusMessage = state.labels.voiceStatusIdle,
                isListening = false
            )
        )
    }

    fun endVideoSession() = updateReady { state ->
        state.copy(
            video = state.video.copy(
                isListening = false,
                isProcessing = false,
                liveTranscript = "",
                dialogueText = state.labels.videoIdleHint,
                lastAssistantMessage = null,
                statusLabel = state.labels.videoTapToSpeakHint,
                showPermissionHint = false
            )
        )
    }

    fun createNewSession() {
        viewModelScope.launch { startLocalDraftSession() }
    }

    fun selectSession(sessionId: String) {
        viewModelScope.launch { loadSessionIntoState(sessionId) }
    }

    fun submitMessageFeedback(messageIndex: Int, feedback: ChatMessageFeedback) {
        val ready = _uiState.value as? AskPandaUiState.Ready ?: return
        val message = ready.chatHistory.getOrNull(messageIndex) ?: return
        if (!message.role.equals(ROLE_ASSISTANT, ignoreCase = true)) return
        val assistantMessageId = message.id ?: return
        val conversationId = ready.currentSessionId ?: return
        val rating = feedback.toApiRating()
        if (message.feedback != null) return

        val userQuestion = message.userQuestion
            ?: ready.chatHistory.take(messageIndex).lastOrNull {
                it.role.equals(ROLE_USER, ignoreCase = true)
            }?.content
            ?: return

        chatFeedbackLocalStore.saveFeedback(
            conversationId = conversationId,
            assistantMessageId = assistantMessageId,
            userMessageId = message.userMessageId,
            rating = rating,
            userQuestion = userQuestion,
            assistantResponse = message.content
        )

        updateReady { state ->
            state.copy(
                chatHistory = state.chatHistory.mapIndexed { index, item ->
                    if (index == messageIndex) item.copy(feedback = rating) else item
                }
            )
        }

        viewModelScope.launch {
            chatFeedbackSyncManager.syncPending()
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            agentRepository.deleteSession(sessionId)
                .onSuccess {
                    Logger.d("deleteSession: removed sessionId=$sessionId")
                    val ready = _uiState.value as? AskPandaUiState.Ready ?: return@onSuccess
                    val remaining = ready.sessions.filter { it.id != sessionId }
                    if (remaining.isEmpty()) {
                        _uiState.value = ready.copy(
                            sessions = emptyList(),
                            currentSessionId = null,
                            chatHistory = emptyList(),
                            showHistorySheet = false
                        )
                        startLocalDraftSession()
                    } else if (ready.currentSessionId == sessionId) {
                        _uiState.value = ready.copy(sessions = remaining)
                        loadSessionIntoState(remaining.first().id)
                    } else {
                        _uiState.value = ready.copy(sessions = remaining)
                    }
                    _events.trySend(AskPandaUiEvent.SessionDeleted(sessionId))
                }
                .onFailure { throwable ->
                    Logger.e("deleteSession: failed — ${throwable.message}", throwable)
                    _events.trySend(
                        AskPandaUiEvent.ChatError(
                            throwable.message ?: "Couldn't delete that chat. Please retry."
                        )
                    )
                }
        }
    }

    private fun startVoiceListening(ready: AskPandaUiState.Ready) {
        _uiState.value = ready.copy(
            voice = ready.voice.copy(
                isListening = true,
                liveTranscript = "",
                showPermissionHint = false,
                statusMessage = ready.labels.voiceStatusListening
            )
        )
    }

    private fun stopVoiceListening(ready: AskPandaUiState.Ready) {
        _uiState.value = ready.copy(
            voice = ready.voice.copy(
                isListening = false,
                liveTranscript = "",
                statusMessage = ready.labels.voiceStatusIdle
            )
        )
    }

    private fun startVideoListening(ready: AskPandaUiState.Ready) {
        _uiState.value = ready.copy(
            video = ready.video.copy(
                isListening = true,
                liveTranscript = "",
                showPermissionHint = false,
                statusLabel = ready.labels.videoListeningLabel
            )
        )
    }

    private fun stopVideoListening(ready: AskPandaUiState.Ready) {
        _uiState.value = ready.copy(
            video = ready.video.copy(
                isListening = false,
                liveTranscript = "",
                statusLabel = ready.labels.videoTapToSpeakHint
            )
        )
    }

    private fun submitVoiceUtterance(ready: AskPandaUiState.Ready, text: String) {
        _uiState.value = ready.copy(
            voice = ready.voice.copy(
                isListening = false,
                liveTranscript = "",
                lastUserMessage = text,
                lastAssistantMessage = null,
                isProcessing = true,
                statusMessage = ready.labels.voiceStatusProcessing
            )
        )
        deliverVoiceReply(text)
    }

    private fun submitVideoUtterance(ready: AskPandaUiState.Ready, text: String) {
        _uiState.value = ready.copy(
            video = ready.video.copy(
                isListening = false,
                liveTranscript = "",
                dialogueText = text,
                lastAssistantMessage = null,
                isProcessing = true,
                statusLabel = ready.labels.voiceStatusProcessing
            )
        )
        deliverVideoReply(text)
    }

    private fun deliverVoiceReply(userText: String) {
        viewModelScope.launch {
            delay(850)
            val current = _uiState.value as? AskPandaUiState.Ready ?: return@launch
            _uiState.value = current.copy(
                voice = current.voice.copy(
                    isProcessing = false,
                    lastAssistantMessage = buildMockAssistantReply(userText),
                    statusMessage = current.labels.voiceStatusIdle
                )
            )
        }
    }

    private fun deliverVideoReply(userText: String) {
        viewModelScope.launch {
            delay(850)
            val current = _uiState.value as? AskPandaUiState.Ready ?: return@launch
            val reply = buildMockAssistantReply(userText)
            _uiState.value = current.copy(
                video = current.video.copy(
                    isProcessing = false,
                    lastAssistantMessage = reply,
                    dialogueText = reply,
                    statusLabel = current.labels.videoTapToSpeakHint
                )
            )
        }
    }

    private fun syncProfileImageIfMissing() {
        val cached = profileRepository.getCachedSnapshot()
        if (cached.avatarUrl.isNullOrBlank()) {
            viewModelScope.launch {
                profileRepository.getCurrentProfile()
                    .onSuccess { applyCachedProfileToReadyState() }
            }
            return
        }
        applyCachedProfileToReadyState()
    }

    private fun applyCachedProfileToReadyState() {
        val ready = _uiState.value as? AskPandaUiState.Ready ?: return
        val cached = profileRepository.getCachedSnapshot()
        if (ready.userAvatarUrl == cached.avatarUrl && ready.userPlan == cached.plan) return
        _uiState.value = ready.copy(userAvatarUrl = cached.avatarUrl, userPlan = cached.plan)
    }

    private suspend fun refreshUsageQuota() {
        agentRepository.getUsage()
            .onSuccess { usage ->
                updateReady { it.copy(usageQuotaLabel = usage.quotaLabel()) }
            }
            .onFailure { Logger.e("refreshUsageQuota: failed — ${it.message}", it) }
    }

    private fun loadSessions(showFullLoading: Boolean = true) {
        viewModelScope.launch {
            if (showFullLoading) {
                _uiState.value = AskPandaUiState.Loading(buildLabels().loadingMessage)
            }
            val streakDays = profileRepository.getCurrentProfile()
                .onFailure { Logger.e("loadSessions: profile refresh failed — ${it.message}", it) }
                .getOrNull()?.currentStreak ?: 0
            if (resolveClassName() != ALLOWED_CHAT_CLASS) {
                _uiState.value = classNotSupportedState()
                return@launch
            }
            reloadSessionsAndStartFresh(showFullLoading = showFullLoading, streakDays = streakDays)
            refreshUsageQuota()
        }
    }

    private suspend fun loadSubjects(): List<SubjectOption> {
        val apiSubjects = profileRepository.getAvailableSubjects()
            .getOrNull()
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.distinct()

        val names = apiSubjects?.takeIf { it.isNotEmpty() } ?: fallbackSubjectNames()
        return names.mapIndexed { index, name ->
            SubjectOption(id = name.toSubjectId(), name = name, isSelected = index == 0)
        }
    }

    private fun fallbackSubjectNames(): List<String> {
        val courses = profileRepository.getCachedSnapshot().selectedSubjects
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
        return courses.takeIf { it.isNotEmpty() } ?: listOf("General")
    }

    private suspend fun reloadSessionsAndStartFresh(showFullLoading: Boolean, streakDays: Int) {
        val labels = buildLabels(streakDays)
        val subjects = loadSubjects()
        agentRepository.getSessions()
            .onSuccess { sessions ->
                Logger.d("loadSessions: success — count=${sessions.size}")
                val sorted = sessions.toHistoryItems()
                _uiState.value = buildReadyState(labels = labels, sessions = sorted, subjects = subjects)
                applyCachedProfileToReadyState()
                startLocalDraftSession()
            }
            .onFailure { throwable ->
                Logger.e("loadSessions: failed — ${throwable.message}", throwable)
                if (showFullLoading || _uiState.value !is AskPandaUiState.Ready) {
                    _uiState.value = if (throwable.isAccountNotActivated()) {
                        AskPandaUiState.AccountNotActivated(
                            title = ACCOUNT_NOT_ACTIVE_TITLE,
                            message = ACCOUNT_NOT_ACTIVE_MESSAGE,
                            contactButtonLabel = CONTACT_ACTIVATE_LABEL
                        )
                    } else {
                        val sessionExpired = throwable is SessionExpiredException
                        AskPandaUiState.Error(
                            title = if (sessionExpired) ApiErrorMapper.SESSION_EXPIRED_TITLE else null,
                            message = throwable.message ?: "Failed to load Ask Panda.",
                            retryLabel = if (sessionExpired) "Sign in" else labels.retryLabel
                        )
                    }
                }
            }
    }

    private suspend fun startLocalDraftSession() {
        val ready = _uiState.value as? AskPandaUiState.Ready ?: return
        if (resolveClassName() != ALLOWED_CHAT_CLASS) {
            _uiState.value = classNotSupportedState()
            return
        }
        _uiState.value = ready.copy(
            currentSessionId = null,
            chatHistory = emptyList(),
            showHistorySheet = false,
            inputText = "",
            isStreaming = false,
            isSendEnabled = false,
            voice = VoiceUiState(
                statusMessage = ready.labels.voiceStatusIdle,
                isListening = false
            ),
            video = VideoUiState(
                statusLabel = ready.labels.videoTapToSpeakHint,
                dialogueText = ready.labels.videoIdleHint,
                isListening = false
            )
        )
    }

    private suspend fun loadSessionIntoState(sessionId: String) {
        agentRepository.getSession(sessionId)
            .onSuccess { detail ->
                val ready = _uiState.value as? AskPandaUiState.Ready ?: return@onSuccess
                val sessionItem = detail.session.toUiItem()
                val orderedSessions = listOf(sessionItem) +
                    ready.sessions.filter { it.id != sessionId }
                val syncedSubjects = ready.subjects.map { it.copy(isSelected = it.name == detail.session.subject) }
                    .let { synced -> if (synced.any { it.isSelected }) synced else ready.subjects }
                val updated = ready.copy(
                    subjects = syncedSubjects,
                    currentSessionId = sessionId,
                    sessions = orderedSessions,
                    chatHistory = detail.messages.toChatHistoryItems().withLocalFeedback(sessionId),
                    shortcutPrompts = buildShortcutPrompts(syncedSubjects.firstOrNull { it.isSelected }?.name),
                    showHistorySheet = false,
                    inputText = "",
                    isStreaming = false,
                    isSendEnabled = false,
                    voice = VoiceUiState(
                        statusMessage = ready.labels.voiceStatusIdle,
                        isListening = false
                    ),
                    video = VideoUiState(
                        statusLabel = ready.labels.videoTapToSpeakHint,
                        dialogueText = ready.labels.videoIdleHint,
                        isListening = false
                    )
                )
                _uiState.value = updated.copy(subjectDisplay = selectedSubjectDisplay(updated))
            }
            .onFailure { throwable ->
                Logger.e("loadSessionIntoState failed — ${throwable.message}", throwable)
            }
    }

    private fun resolveClassName(): String =
        normalizeClassName(profileRepository.getCachedSnapshot().gradeLevel)

    private fun normalizeClassName(grade: String): String {
        val digitsOnly = grade.filter { it.isDigit() }
        return digitsOnly.ifBlank { grade.trim() }
    }

    private fun classNotSupportedState() = AskPandaUiState.ClassNotSupported(
        title = CLASS_NOT_SUPPORTED_TITLE,
        message = CLASS_NOT_SUPPORTED_MESSAGE
    )

    private fun buildShortcutPrompts(subjectName: String?): List<ShortcutPrompt> {
        val subject = subjectName?.takeIf { it.isNotBlank() } ?: "this subject"
        return listOf(
            ShortcutPrompt("explain", "Explain a key concept in $subject"),
            ShortcutPrompt("summary", "Summarize today's $subject topic"),
            ShortcutPrompt("practice", "Give me a $subject practice question"),
            ShortcutPrompt("quiz", "Quiz me on $subject")
        )
    }

    private fun selectedSubjectDisplay(ready: AskPandaUiState.Ready): String {
        val subject = ready.subjects.firstOrNull { it.isSelected }?.name ?: "General"
        val className = resolveClassName()
        return "$subject • Class $className"
    }

    private fun buildReadyState(
        labels: AskPandaLabels,
        sessions: List<ChatSessionItem>,
        subjects: List<SubjectOption>
    ): AskPandaUiState.Ready {
        val cached = profileRepository.getCachedSnapshot()
        val userName = cached.firstName.substringBefore(" ").ifBlank { "Student" }
        return AskPandaUiState.Ready(
            labels = labels,
            userName = userName,
            userInitial = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
            userAvatarUrl = cached.avatarUrl,
            userPlan = cached.plan,
            selectedTab = AskPandaTab.CHAT,
            subjectDisplay = run {
                val className = resolveClassName()
                val subject = subjects.firstOrNull { it.isSelected }?.name ?: "General"
                "$subject • Class $className"
            },
            subjects = subjects,
            showSubjectSheet = false,
            showHistorySheet = false,
            sessions = sessions,
            currentSessionId = null,
            chatHistory = emptyList(),
            shortcutPrompts = buildShortcutPrompts(subjects.firstOrNull { it.isSelected }?.name),
            inputText = "",
            isStreaming = false,
            isSendEnabled = false,
            voice = VoiceUiState(
                statusMessage = labels.voiceStatusIdle,
                isListening = false
            ),
            video = VideoUiState(
                statusLabel = labels.videoTapToSpeakHint,
                dialogueText = labels.videoIdleHint,
                isListening = false
            )
        )
    }

    private fun buildLabels(streakDays: Int = 0) = AskPandaLabels(
        appTitle = "Learning Panda AI",
        streakLabel = streakDays.toString(),
        chatTabLabel = "Text",
        voiceTabLabel = "Voice",
        videoTabLabel = "Video",
        historyContentDescription = "Chat history",
        subjectDropdownContentDescription = "Switch subject",
        switchSubjectTitle = "Switch Subject",
        closeContentDescription = "Close",
        chatHistoryTitle = "Chat History",
        newSessionLabel = "New Session",
        emptyHistoryTitle = "No past chats yet",
        emptyHistorySubtitle = "Start a new session with Ask Panda to see your history here.",
        emptyChatGreeting = "Hey %s! Panda's here to help.",
        emptyChatSubtitle = "Ask anything about your subject — or tap a quick prompt below.",
        inputPlaceholder = "Message Panda…",
        sendContentDescription = "Send message",
        aiDisclaimer = "Panda can make mistakes — always verify important answers.",
        attachContentDescription = "Add attachment",
        voiceStatusListening = "Listening… speak naturally",
        voiceStatusIdle = "Tap the mic, then talk like you're chatting with a friend",
        voiceStatusProcessing = "Panda is thinking…",
        voicePermissionHint = "Allow microphone access in Settings to talk with Panda.",
        endSessionLabel = "Clear conversation",
        micContentDescription = "Start or stop voice input",
        videoIdleHint = "Hi! I'm Panda. Tap the mic below and ask me anything about your lessons.",
        videoListeningLabel = "Listening…",
        videoTapToSpeakHint = "Tap to speak",
        pandaCamLabel = "Panda",
        typingIndicatorLabel = "Panda is typing",
        thumbsUpContentDescription = "Helpful response",
        thumbsDownContentDescription = "Not helpful response",
        retryLabel = "Retry",
        loadingMessage = "Loading Ask Panda…"
    )

    private fun buildMockAssistantReply(query: String): String =
        "Great question! Here's a friendly breakdown:\n\n" +
            "• \"$query\" connects to what you're studying in class.\n" +
            "• Focus on the core idea first, then the details.\n" +
            "• Want me to quiz you or go deeper on any part?"

    private fun updateReady(block: (AskPandaUiState.Ready) -> AskPandaUiState.Ready) {
        val current = _uiState.value
        if (current is AskPandaUiState.Ready) {
            _uiState.value = block(current)
        }
    }

    private fun List<ChatMessageItemDto>.withLocalFeedback(conversationId: String?): List<ChatMessageItemDto> {
        if (conversationId.isNullOrBlank()) return this
        return map { item ->
            val messageId = item.id ?: return@map item
            if (!item.role.equals(ROLE_ASSISTANT, ignoreCase = true)) return@map item
            val rating = chatFeedbackLocalStore.getRating(conversationId, messageId) ?: return@map item
            item.copy(feedback = rating)
        }
    }

    private fun Throwable.isAccountNotActivated(): Boolean =
        this is AccountNotActivatedException ||
            (cause as? HttpException)?.code() == HTTP_FORBIDDEN

    companion object {
        private const val ROLE_USER = "user"
        private const val ROLE_ASSISTANT = "assistant"
        private const val HTTP_FORBIDDEN = 403
        private const val HTTP_TOO_MANY_REQUESTS = 429
        private const val HTTP_UNPROCESSABLE_ENTITY = 422

        private const val ACCOUNT_NOT_ACTIVE_TITLE = "Account Not Active"
        private const val ACCOUNT_NOT_ACTIVE_MESSAGE =
            "Your Learning Panda account has not been activated yet. " +
                "Please contact our team and we will enable Ask Panda for you."
        private const val CONTACT_ACTIVATE_LABEL = "Contact Us to Activate"

        /** Only this class is currently eligible to use Ask Panda chat. */
        private const val ALLOWED_CHAT_CLASS = "8"
        private const val CLASS_NOT_SUPPORTED_TITLE = "Ask Panda Isn't Available Yet"
        private const val CLASS_NOT_SUPPORTED_MESSAGE =
            "Ask Panda chat is currently available for Class 8 students only. " +
                "We're working on bringing it to your class soon!"
    }

    private fun List<ChatSession>.toHistoryItems(): List<ChatSessionItem> =
        filter { it.hasConversationHistory() }
            .sortedByDescending { it.updatedAt }
            .map { it.toUiItem() }

    private fun ChatSession.toUiItem() = ChatSessionItem(
        id = id,
        subject = subject,
        className = className,
        title = title.toSessionDisplayTitle(subject),
        createdAt = createdAt
    )

    private fun String.toSessionDisplayTitle(subject: String): String {
        val trimmed = trim()
        if (trimmed.isNotBlank() &&
            !trimmed.equals("New doubt session", ignoreCase = true) &&
            !trimmed.equals("New session", ignoreCase = true)
        ) {
            return trimmed
        }
        return subject.ifBlank { "Chat" }
    }
}

private fun String.toSubjectId(): String =
    trim().lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_').ifBlank { "general" }

private fun List<com.example.learningpandaai.features.askpanda.domain.ChatMessage>.toChatHistoryItems():
    List<ChatMessageItemDto> {
    var lastUserMessage: com.example.learningpandaai.features.askpanda.domain.ChatMessage? = null
    return map { message ->
        val isAssistant = message.role.equals("assistant", ignoreCase = true)
        val item = ChatMessageItemDto(
            role = message.role,
            content = message.content,
            id = message.id,
            userQuestion = if (isAssistant) lastUserMessage?.content else null,
            userMessageId = if (isAssistant) lastUserMessage?.id else null
        )
        if (message.role.equals("user", ignoreCase = true)) {
            lastUserMessage = message
        }
        item
    }
}
