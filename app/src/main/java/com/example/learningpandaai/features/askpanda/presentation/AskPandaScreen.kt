package com.example.learningpandaai.features.askpanda.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.example.learningpandaai.R
import com.example.learningpandaai.core.designsystem.components.DashboardErrorFallback
import com.example.learningpandaai.core.designsystem.components.DashboardPullToRefresh
import com.example.learningpandaai.core.designsystem.components.ErrorStateContent
import com.example.learningpandaai.core.designsystem.layout.ResponsiveContentWidth
import com.example.learningpandaai.core.designsystem.layout.ScreenWidthClass
import com.example.learningpandaai.core.designsystem.theme.BrandPrimary
import com.example.learningpandaai.core.designsystem.theme.BrandSecondary
import com.example.learningpandaai.core.designsystem.theme.BrandSecondaryContainer
import com.example.learningpandaai.core.designsystem.theme.PureWhite
import com.example.learningpandaai.core.designsystem.theme.SurfaceBanner
import com.example.learningpandaai.core.designsystem.theme.TextPrimary
import com.example.learningpandaai.core.designsystem.theme.TextSecondary
import com.example.learningpandaai.features.askpanda.presentation.components.AskPandaAiDisclaimer
import com.example.learningpandaai.features.askpanda.presentation.components.AskPandaChatInput
import com.example.learningpandaai.features.askpanda.presentation.components.AskPandaChatMode
import com.example.learningpandaai.features.askpanda.presentation.components.AskPandaHistorySheet
import com.example.learningpandaai.features.askpanda.presentation.components.AskPandaModeToggle
import com.example.learningpandaai.features.askpanda.presentation.components.AskPandaSubjectBar
import com.example.learningpandaai.features.askpanda.presentation.components.AskPandaSubjectSheet
import com.example.learningpandaai.features.askpanda.presentation.components.AskPandaTopBar
import com.example.learningpandaai.features.askpanda.presentation.components.AskPandaVideoMode
import com.example.learningpandaai.features.askpanda.presentation.components.AskPandaVoiceMode

@Composable
fun AskPandaScreen(viewModel: AskPandaViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is AskPandaUiEvent.ChatError) {
                snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.syncProfileContextFromCache()
        onPauseOrDispose { }
    }

    Box(modifier = Modifier.fillMaxSize()) {
    when (val state = uiState) {
        is AskPandaUiState.Loading -> AskPandaLoadingState(message = state.message)
        is AskPandaUiState.AccountNotActivated -> AskPandaAccountNotActivatedContent(
            title = state.title,
            message = state.message,
            contactButtonLabel = state.contactButtonLabel
        )
        is AskPandaUiState.ClassNotSupported -> DashboardErrorFallback(
            title = state.title,
            message = state.message,
            retryLabel = "Check again",
            onRetry = viewModel::retry,
            modifier = Modifier.fillMaxSize()
        )
        is AskPandaUiState.Error -> {
            if (state.title != null) {
                DashboardErrorFallback(
                    title = state.title,
                    message = state.message,
                    retryLabel = state.retryLabel,
                    onRetry = viewModel::retry,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                ErrorStateContent(
                    message = state.message,
                    retryLabel = state.retryLabel,
                    onRetry = viewModel::retry
                )
            }
        }
        is AskPandaUiState.Ready -> DashboardPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh
        ) {
            AskPandaReadyContent(
                state = state,
                onTabSelected = viewModel::selectTab,
            onSubjectClick = { viewModel.setSubjectSheetVisible(true) },
            onHistoryClick = { viewModel.setHistorySheetVisible(true) },
            onSubjectSheetDismiss = { viewModel.setSubjectSheetVisible(false) },
            onSubjectSelected = viewModel::selectSubject,
            onHistoryDismiss = { viewModel.setHistorySheetVisible(false) },
            onNewSession = viewModel::createNewSession,
            onSessionSelected = viewModel::selectSession,
            onInputChanged = viewModel::onInputChanged,
            onSend = viewModel::sendMessage,
            onShortcutClick = viewModel::onShortcutSelected,
            onFeedbackClick = viewModel::submitMessageFeedback,
            onMicClick = viewModel::toggleVoiceListening,
            onVideoMicClick = viewModel::toggleVideoListening,
            onEndVoiceSession = viewModel::endVoiceSession,
            onVoicePartialResult = viewModel::onVoicePartialResult,
            onVoiceFinalResult = viewModel::onVoiceFinalResult,
            onVoiceRecognitionError = viewModel::onVoiceRecognitionError,
            onVoicePermissionDenied = viewModel::onVoicePermissionDenied,
            onVideoPartialResult = viewModel::onVideoPartialResult,
            onVideoFinalResult = viewModel::onVideoFinalResult,
            onVideoRecognitionError = viewModel::onVideoRecognitionError,
            onVideoPermissionDenied = viewModel::onVideoPermissionDenied
            )
        }
    }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

private const val SUPPORT_EMAIL = "contact@learningpanda.ai"

@Composable
private fun AskPandaAccountNotActivatedContent(
    title: String,
    message: String,
    contactButtonLabel: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Image(
                    painter = painterResource(id = R.drawable.ic_panda_logo),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )
                Surface(
                    shape = CircleShape,
                    color = BrandSecondaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = BrandSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceBanner,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$SUPPORT_EMAIL")
                    }
                    context.startActivity(
                        Intent.createChooser(emailIntent, contactButtonLabel)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPrimary,
                    contentColor = PureWhite
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = contactButtonLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = PureWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun AskPandaLoadingState(message: String) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = BrandPrimary)
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun AskPandaReadyContent(
    state: AskPandaUiState.Ready,
    onTabSelected: (AskPandaTab) -> Unit,
    onSubjectClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSubjectSheetDismiss: () -> Unit,
    onSubjectSelected: (String) -> Unit,
    onHistoryDismiss: () -> Unit,
    onNewSession: () -> Unit,
    onSessionSelected: (String) -> Unit,
    onInputChanged: (String) -> Unit,
    onSend: () -> Unit,
    onShortcutClick: (String) -> Unit,
    onFeedbackClick: (Int, com.example.learningpandaai.features.askpanda.domain.ChatMessageFeedback) -> Unit,
    onMicClick: () -> Unit,
    onVideoMicClick: () -> Unit,
    onEndVoiceSession: () -> Unit,
    onVoicePartialResult: (String) -> Unit,
    onVoiceFinalResult: (String) -> Unit,
    onVoiceRecognitionError: (String) -> Unit,
    onVoicePermissionDenied: () -> Unit,
    onVideoPartialResult: (String) -> Unit,
    onVideoFinalResult: (String) -> Unit,
    onVideoRecognitionError: (String) -> Unit,
    onVideoPermissionDenied: () -> Unit
) {
    val labels = state.labels
    val colorScheme = MaterialTheme.colorScheme

    ResponsiveContentWidth(modifier = Modifier.background(colorScheme.background)) { widthClass ->
        val horizontalPadding = when (widthClass) {
            ScreenWidthClass.Compact -> 16.dp
            ScreenWidthClass.Expanded -> 20.dp
        }

        Column(modifier = Modifier.fillMaxSize()) {
            AskPandaTopBar(
                appTitle = labels.appTitle,
                userName = state.userName,
                userInitial = state.userInitial,
                userAvatarUrl = state.userAvatarUrl,
                userPlan = state.userPlan,
                streakLabel = labels.streakLabel,
                usageQuotaLabel = state.usageQuotaLabel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
                    .padding(top = 2.dp, bottom = 6.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
            ) {
                AskPandaModeToggle(
                    selectedTab = state.selectedTab,
                    chatLabel = labels.chatTabLabel,
                    voiceLabel = labels.voiceTabLabel,
                    pandaChatLabel = labels.videoTabLabel,
                    onTabSelected = onTabSelected,
                    showVideoTab = true,
                    comingSoonTabs = setOf(AskPandaTab.VOICE, AskPandaTab.PANDA_CHAT),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                AskPandaSubjectBar(
                    subjectDisplay = state.subjectDisplay,
                    subjectDropdownContentDescription = labels.subjectDropdownContentDescription,
                    historyContentDescription = labels.historyContentDescription,
                    onSubjectClick = onSubjectClick,
                    onHistoryClick = onHistoryClick,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (state.selectedMode) {
                        AskPandaMode.CHAT -> AskPandaChatMode(
                            chatHistory = state.chatHistory,
                            labels = labels,
                            userName = state.userName,
                            shortcutPrompts = state.shortcutPrompts,
                            isStreaming = state.isStreaming,
                            onShortcutClick = onShortcutClick,
                            onFeedbackClick = onFeedbackClick,
                            modifier = Modifier.fillMaxSize()
                        )
                        AskPandaMode.VOICE -> AskPandaVoiceMode(
                            voice = state.voice,
                            micContentDescription = labels.micContentDescription,
                            permissionHint = labels.voicePermissionHint,
                            endSessionLabel = labels.endSessionLabel,
                            onMicClick = onMicClick,
                            onEndSession = onEndVoiceSession,
                            onPartialResult = onVoicePartialResult,
                            onFinalResult = onVoiceFinalResult,
                            onRecognitionError = onVoiceRecognitionError,
                            onPermissionDenied = onVoicePermissionDenied,
                            modifier = Modifier.fillMaxSize()
                        )
                        AskPandaMode.VIDEO -> AskPandaVideoMode(
                            video = state.video,
                            pandaCamLabel = labels.pandaCamLabel,
                            permissionHint = labels.voicePermissionHint,
                            tapToSpeakHint = labels.videoTapToSpeakHint,
                            onMicClick = onVideoMicClick,
                            onPartialResult = onVideoPartialResult,
                            onFinalResult = onVideoFinalResult,
                            onRecognitionError = onVideoRecognitionError,
                            onPermissionDenied = onVideoPermissionDenied,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            if (state.selectedMode == AskPandaMode.CHAT) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .navigationBarsPadding()
                        .padding(horizontal = horizontalPadding)
                        .padding(top = 6.dp, bottom = 8.dp)
                ) {
                    AskPandaChatInput(
                        value = state.inputText,
                        placeholder = labels.inputPlaceholder,
                        sendContentDescription = labels.sendContentDescription,
                        isSendEnabled = state.isSendEnabled,
                        onValueChange = onInputChanged,
                        onSend = onSend,
                        modifier = Modifier.fillMaxWidth()
                    )
                    AskPandaAiDisclaimer(text = labels.aiDisclaimer)
                }
            }
        }

        AskPandaSubjectSheet(
            visible = state.showSubjectSheet,
            title = labels.switchSubjectTitle,
            closeContentDescription = labels.closeContentDescription,
            subjects = state.subjects,
            onDismiss = onSubjectSheetDismiss,
            onSubjectSelected = onSubjectSelected
        )
        AskPandaHistorySheet(
            visible = state.showHistorySheet,
            title = labels.chatHistoryTitle,
            closeContentDescription = labels.closeContentDescription,
            emptyTitle = labels.emptyHistoryTitle,
            emptySubtitle = labels.emptyHistorySubtitle,
            newSessionLabel = labels.newSessionLabel,
            sessions = state.sessions,
            currentSessionId = state.currentSessionId,
            onDismiss = onHistoryDismiss,
            onNewSession = onNewSession,
            onSessionSelected = onSessionSelected
        )
    }
}
