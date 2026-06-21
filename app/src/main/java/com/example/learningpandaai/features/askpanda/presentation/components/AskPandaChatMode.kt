package com.example.learningpandaai.features.askpanda.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learningpandaai.features.askpanda.data.remote.ChatMessageItemDto
import com.example.learningpandaai.features.askpanda.domain.ChatMessageFeedback
import com.example.learningpandaai.features.askpanda.presentation.AskPandaLabels
import com.example.learningpandaai.features.askpanda.presentation.ShortcutPrompt

private const val CHAT_LIST_BOTTOM_PADDING = 20
private const val ROLE_ASSISTANT = "assistant"

@Composable
fun AskPandaChatMode(
    chatHistory: List<ChatMessageItemDto>,
    labels: AskPandaLabels,
    userName: String,
    shortcutPrompts: List<ShortcutPrompt>,
    isStreaming: Boolean,
    onShortcutClick: (String) -> Unit,
    onFeedbackClick: (Int, ChatMessageFeedback) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(chatHistory.size, isStreaming) {
        val lastIndex = when {
            isStreaming -> chatHistory.size
            chatHistory.isNotEmpty() -> chatHistory.lastIndex
            else -> return@LaunchedEffect
        }
        listState.animateScrollToItem(lastIndex)
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (chatHistory.isEmpty() && !isStreaming) {
            AskPandaChatEmptyState(
                greetingTemplate = labels.emptyChatGreeting,
                userName = userName,
                subtitle = labels.emptyChatSubtitle,
                shortcuts = shortcutPrompts,
                onShortcutClick = onShortcutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 8.dp,
                    bottom = CHAT_LIST_BOTTOM_PADDING.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(
                    items = chatHistory,
                    key = { index, message -> "${message.role}_${index}_${message.content.hashCode()}" }
                ) { index, message ->
                    val isAssistant = message.role.equals(ROLE_ASSISTANT, ignoreCase = true)
                    ChatBubble(
                        message = message,
                        thumbsUpContentDescription = labels.thumbsUpContentDescription,
                        thumbsDownContentDescription = labels.thumbsDownContentDescription,
                        showFeedbackActions = isAssistant && !isStreaming && message.id != null,
                        onFeedbackClick = { feedback -> onFeedbackClick(index, feedback) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                // Once the first token arrives it's appended to a growing assistant bubble
                // above, so the indicator is only needed while we're waiting for that first token.
                if (isStreaming && chatHistory.lastOrNull()?.role != ROLE_ASSISTANT) {
                    item(key = "typing_indicator") {
                        AskPandaTypingIndicator(
                            label = labels.typingIndicatorLabel,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
