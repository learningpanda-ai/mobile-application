package com.example.learningpandaai.features.askpanda.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.designsystem.theme.BrandSecondary
import com.example.learningpandaai.core.designsystem.theme.PureWhite
import com.example.learningpandaai.core.designsystem.theme.StatusError
import com.example.learningpandaai.features.askpanda.data.remote.ChatMessageItemDto
import com.example.learningpandaai.features.askpanda.presentation.VoiceUiState

@Composable
fun AskPandaVoiceMode(
    voice: VoiceUiState,
    micContentDescription: String,
    permissionHint: String,
    endSessionLabel: String,
    onMicClick: () -> Unit,
    onEndSession: () -> Unit,
    onPartialResult: (String) -> Unit,
    onFinalResult: (String) -> Unit,
    onRecognitionError: (String) -> Unit,
    onPermissionDenied: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "voice_pulse")
    val pulseScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (voice.isListening) 1.18f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )
    val ringAlpha by transition.animateFloat(
        initialValue = 0.15f,
        targetValue = if (voice.isListening) 0.45f else 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring_alpha"
    )

    AskPandaVoiceRecognizerEffect(
        isListening = voice.isListening,
        onPartialResult = onPartialResult,
        onFinalResult = onFinalResult,
        onError = onRecognitionError,
        onPermissionDenied = onPermissionDenied
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (voice.lastUserMessage == null && voice.lastAssistantMessage == null && !voice.isListening) {
                PandaLogoCircle(
                    size = 88.dp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            voice.lastUserMessage?.let { text ->
                VoiceUserBubble(text = text)
            }
            voice.lastAssistantMessage?.let { text ->
                ChatBubble(
                    message = ChatMessageItemDto(role = "assistant", content = text),
                    thumbsUpContentDescription = "",
                    thumbsDownContentDescription = "",
                    showFeedbackActions = false,
                    onFeedbackClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (voice.isListening && voice.liveTranscript.isNotBlank()) {
                VoiceUserBubble(
                    text = voice.liveTranscript,
                    isLive = true
                )
            }

            if (voice.isProcessing) {
                AskPandaTypingIndicator(
                    label = voice.statusMessage,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (voice.showPermissionHint) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = permissionHint,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Text(
            text = voice.statusMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (voice.isListening) {
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(BrandSecondary.copy(alpha = ringAlpha))
                )
            }
            IconButton(
                onClick = onMicClick,
                enabled = !voice.isProcessing,
                modifier = Modifier.size(76.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = when {
                        voice.isListening -> BrandSecondary
                        voice.isProcessing -> colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        else -> colorScheme.primary
                    },
                    contentColor = PureWhite,
                    disabledContainerColor = colorScheme.surfaceVariant
                )
            ) {
                Icon(
                    imageVector = if (voice.isListening) Icons.Filled.MicOff else Icons.Filled.Mic,
                    contentDescription = micContentDescription,
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        TextButton(
            onClick = onEndSession,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        ) {
            Text(
                text = endSessionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = StatusError,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun VoiceUserBubble(
    text: String,
    isLive: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 22.dp, bottomEnd = 6.dp),
            color = if (isLive) colorScheme.primary.copy(alpha = 0.85f) else colorScheme.primary
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp)
            )
        }
    }
}
