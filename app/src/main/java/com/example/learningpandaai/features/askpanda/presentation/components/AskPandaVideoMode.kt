package com.example.learningpandaai.features.askpanda.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.core.designsystem.theme.BrandPrimary
import com.example.learningpandaai.core.designsystem.theme.PureWhite
import com.example.learningpandaai.features.askpanda.presentation.VideoUiState

@Composable
fun AskPandaVideoMode(
    video: VideoUiState,
    pandaCamLabel: String,
    permissionHint: String,
    tapToSpeakHint: String,
    onMicClick: () -> Unit,
    onPartialResult: (String) -> Unit,
    onFinalResult: (String) -> Unit,
    onRecognitionError: (String) -> Unit,
    onPermissionDenied: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "video_mic_pulse")
    val barScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (video.isListening) 1.02f else 1f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar_scale"
    )

    AskPandaVoiceRecognizerEffect(
        isListening = video.isListening,
        onPartialResult = onPartialResult,
        onFinalResult = onFinalResult,
        onError = onRecognitionError,
        onPermissionDenied = onPermissionDenied
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colorScheme.background, colorScheme.surfaceVariant.copy(alpha = 0.35f))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PandaLogoCircle(
                size = 64.dp,
                contentDescription = pandaCamLabel
            )
            Text(
                text = pandaCamLabel,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val displayText = when {
                video.isListening && video.liveTranscript.isNotBlank() -> video.liveTranscript
                video.lastAssistantMessage != null -> video.lastAssistantMessage
                else -> video.dialogueText
            }
            val isUserSpeaking = video.isListening && video.liveTranscript.isNotBlank()

            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isUserSpeaking) colorScheme.primary else colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )

            if (video.isProcessing) {
                AskPandaTypingIndicator(
                    label = video.statusLabel,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (video.showPermissionHint) {
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

        val interactionSource = remember { MutableInteractionSource() }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp)
                .scale(barScale)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (video.isListening) BrandPrimary.copy(alpha = 0.92f) else BrandPrimary
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !video.isProcessing,
                    onClick = onMicClick
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (video.isListening) Icons.Filled.MicOff else Icons.Filled.Mic,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.statusLabel,
                    style = MaterialTheme.typography.titleSmall,
                    color = PureWhite,
                    fontWeight = FontWeight.SemiBold
                )
                if (!video.isListening && !video.isProcessing) {
                    Text(
                        text = tapToSpeakHint,
                        style = MaterialTheme.typography.bodySmall,
                        color = PureWhite.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}
