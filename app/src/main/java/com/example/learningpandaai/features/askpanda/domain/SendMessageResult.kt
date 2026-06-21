package com.example.learningpandaai.features.askpanda.domain

data class SendMessageResult(
    val userMessage: ChatMessage,
    val reply: ChatMessage,
    val conversationTitle: String? = null
)
