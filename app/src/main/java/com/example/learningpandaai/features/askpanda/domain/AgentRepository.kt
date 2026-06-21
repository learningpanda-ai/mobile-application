package com.example.learningpandaai.features.askpanda.domain

interface AgentRepository {
    suspend fun getSessions(): Result<List<ChatSession>>
    suspend fun getSession(sessionId: String): Result<ChatSessionDetail>
    suspend fun createSession(subject: String, className: String, title: String? = null): Result<ChatSession>
    suspend fun deleteSession(sessionId: String): Result<Unit>
    suspend fun getUsage(): Result<ChatUsageSummary>

    /**
     * Sends a student message into a conversation and returns the AI tutor's reply.
     * The backend persists both the user message and the assistant reply automatically.
     */
    suspend fun sendMessage(conversationId: String, message: String): Result<SendMessageResult>

    /** Records whether the student was satisfied with a tutor reply. */
    suspend fun submitMessageFeedback(request: MessageFeedbackRequest): Result<Unit>
}
