package com.example.learningpandaai.features.askpanda.domain

enum class ChatMessageFeedback {
    POSITIVE,
    NEGATIVE;

    fun toApiRating(): String = when (this) {
        POSITIVE -> "positive"
        NEGATIVE -> "negative"
    }
}

data class MessageFeedbackRequest(
    val conversationId: String,
    val assistantMessageId: Long,
    val rating: ChatMessageFeedback,
    val userQuestion: String,
    val assistantResponse: String,
    val userMessageId: Long? = null
)
