package com.example.learningpandaai.features.askpanda.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.R
import com.example.learningpandaai.core.designsystem.theme.StatusSuccess
import com.example.learningpandaai.core.designsystem.theme.TextSecondary
import com.example.learningpandaai.features.askpanda.data.remote.ChatMessageItemDto
import com.example.learningpandaai.features.askpanda.domain.ChatMessageFeedback

private const val ROLE_USER = "user"
private const val FEEDBACK_POSITIVE = "positive"
private const val FEEDBACK_NEGATIVE = "negative"

@Composable
fun ChatBubble(
    message: ChatMessageItemDto,
    thumbsUpContentDescription: String,
    thumbsDownContentDescription: String,
    showFeedbackActions: Boolean,
    onFeedbackClick: (ChatMessageFeedback) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.role.equals(ROLE_USER, ignoreCase = true)
    val colorScheme = MaterialTheme.colorScheme

    val bubbleShape = if (isUser) {
        RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 22.dp, bottomEnd = 6.dp)
    } else {
        RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 6.dp, bottomEnd = 22.dp)
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val maxBubbleWidth = maxWidth * 0.82f

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            if (!isUser) {
                PandaAvatar(modifier = Modifier.padding(end = 8.dp))
            }

            Column(
                modifier = Modifier.widthIn(max = maxBubbleWidth),
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
            ) {
                Surface(
                    shape = bubbleShape,
                    color = if (isUser) colorScheme.primary else colorScheme.surfaceVariant,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isUser) {
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp)
                        )
                    } else {
                        ChatMarkdownText(
                            text = message.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp)
                        )
                    }
                }

                if (!isUser && showFeedbackActions) {
                    if (message.feedback != null) {
                        LockedFeedbackIcon(
                            feedback = message.feedback,
                            thumbsUpContentDescription = thumbsUpContentDescription,
                            thumbsDownContentDescription = thumbsDownContentDescription,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    } else {
                        ChatMessageFeedbackRow(
                            thumbsUpContentDescription = thumbsUpContentDescription,
                            thumbsDownContentDescription = thumbsDownContentDescription,
                            onFeedbackClick = onFeedbackClick,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageFeedbackRow(
    thumbsUpContentDescription: String,
    thumbsDownContentDescription: String,
    onFeedbackClick: (ChatMessageFeedback) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FeedbackIconButton(
            imageVector = Icons.Outlined.ThumbUp,
            contentDescription = thumbsUpContentDescription,
            isSelected = false,
            enabled = true,
            onClick = { onFeedbackClick(ChatMessageFeedback.POSITIVE) }
        )
        FeedbackIconButton(
            imageVector = Icons.Outlined.ThumbDown,
            contentDescription = thumbsDownContentDescription,
            isSelected = false,
            enabled = true,
            onClick = { onFeedbackClick(ChatMessageFeedback.NEGATIVE) }
        )
    }
}

@Composable
private fun LockedFeedbackIcon(
    feedback: String,
    thumbsUpContentDescription: String,
    thumbsDownContentDescription: String,
    modifier: Modifier = Modifier
) {
    val isPositive = feedback == FEEDBACK_POSITIVE
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isPositive) Icons.Outlined.ThumbUp else Icons.Outlined.ThumbDown,
            contentDescription = if (isPositive) {
                thumbsUpContentDescription
            } else {
                thumbsDownContentDescription
            },
            tint = if (isPositive) StatusSuccess else MaterialTheme.colorScheme.error,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .size(18.dp)
        )
    }
}

@Composable
private fun FeedbackIconButton(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val tint = when {
        isSelected && imageVector == Icons.Outlined.ThumbUp -> StatusSuccess
        isSelected -> MaterialTheme.colorScheme.error
        else -> TextSecondary
    }

    IconButton(onClick = onClick, enabled = enabled) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
internal fun PandaAvatar(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_panda_logo),
            contentDescription = "Panda assistant",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
