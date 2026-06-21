package com.example.learningpandaai.features.askpanda.data

import com.example.learningpandaai.features.askpanda.domain.ChatUsageSummary
import com.example.learningpandaai.features.askpanda.domain.AgentRepository
import com.example.learningpandaai.features.askpanda.domain.ChatMessage
import com.example.learningpandaai.features.askpanda.domain.ChatSession
import com.example.learningpandaai.features.askpanda.domain.ChatSessionDetail
import com.example.learningpandaai.features.askpanda.domain.MessageFeedbackRequest
import com.example.learningpandaai.features.askpanda.domain.SendMessageResult
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAgentRepositoryImpl @Inject constructor() : AgentRepository {

    private val sessions = linkedMapOf<String, ChatSession>()
    private val messagesBySession = mutableMapOf<String, MutableList<ChatMessage>>()
    private var nextSessionId = 1L
    private var nextMessageId = 1L

    override suspend fun getSessions(): Result<List<ChatSession>> {
        delay(400)
        return Result.success(
            sessions.values
                .filter { it.hasConversationHistory() }
                .sortedByDescending { it.updatedAt }
        )
    }

    override suspend fun getSession(sessionId: String): Result<ChatSessionDetail> {
        delay(300)
        val session = sessions[sessionId]
            ?: return Result.failure(NoSuchElementException("Session not found"))
        val messages = messagesBySession[sessionId].orEmpty().toList()
        return Result.success(ChatSessionDetail(session = session, messages = messages))
    }

    override suspend fun createSession(
        subject: String,
        className: String,
        title: String?
    ): Result<ChatSession> {
        delay(400)
        val now = nowIso()
        val session = ChatSession(
            id = (nextSessionId++).toString(),
            subject = subject,
            className = className,
            title = title.orEmpty(),
            createdAt = now,
            updatedAt = now,
            lastMessageAt = null
        )
        sessions[session.id] = session
        messagesBySession[session.id] = mutableListOf()
        return Result.success(session)
    }

    override suspend fun deleteSession(sessionId: String): Result<Unit> {
        delay(250)
        sessions.remove(sessionId)
        messagesBySession.remove(sessionId)
        return Result.success(Unit)
    }

    override suspend fun getUsage(): Result<ChatUsageSummary> {
        delay(200)
        return Result.success(
            ChatUsageSummary(
                planName = "Free",
                dailyUsed = 3,
                dailyLimit = 20,
                monthlyUsed = 12,
                monthlyLimit = 400
            )
        )
    }

    override suspend fun sendMessage(conversationId: String, message: String): Result<SendMessageResult> {
        delay(700)
        val session = sessions[conversationId]
            ?: return Result.failure(NoSuchElementException("Session not found"))
        val stored = messagesBySession.getOrPut(conversationId) { mutableListOf() }
        val userMessage = ChatMessage(id = nextMessageId++, role = ROLE_USER, content = message)
        stored.add(userMessage)
        val reply = ChatMessage(
            id = nextMessageId++,
            role = ROLE_ASSISTANT,
            content = buildMockAssistantReply(message)
        )
        stored.add(reply)

        val updatedTitle = session.title.ifBlank { message.toSessionTitle() }
        val now = nowIso()
        sessions[conversationId] = session.copy(
            title = updatedTitle,
            updatedAt = now,
            lastMessageAt = now
        )
        return Result.success(
            SendMessageResult(
                userMessage = userMessage,
                reply = reply,
                conversationTitle = updatedTitle
            )
        )
    }

    override suspend fun submitMessageFeedback(request: MessageFeedbackRequest): Result<Unit> {
        delay(200)
        return Result.success(Unit)
    }

    private fun buildMockAssistantReply(query: String): String =
        "**Quick take:** \"$query\" is a solid place to start.\n\n" +
            "• Focus on the core idea first, then the details.\n" +
            "• Want me to quiz you or go deeper on any part?"

    private companion object {
        const val ROLE_USER = "user"
        const val ROLE_ASSISTANT = "assistant"
    }
}

private fun String.toSessionTitle(): String =
    trim().let { if (it.length <= 42) it else it.take(42) + "…" }

private fun nowIso(): String =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date())
