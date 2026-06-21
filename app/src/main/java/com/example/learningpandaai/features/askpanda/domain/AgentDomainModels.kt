package com.example.learningpandaai.features.askpanda.domain

data class ChatMessage(
    val id: Long? = null,
    val role: String,
    val content: String
)

data class ChatSession(
    val id: String,
    val subject: String,
    val className: String,
    val title: String,
    val createdAt: String,
    val updatedAt: String,
    val lastMessageAt: String? = null
) {
    fun hasConversationHistory(): Boolean = !lastMessageAt.isNullOrBlank()
}

data class ChatSessionDetail(
    val session: ChatSession,
    val messages: List<ChatMessage>
)
