package com.example.learningpandaai.features.askpanda.data

import com.example.learningpandaai.core.network.ApiErrorMapper
import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.features.askpanda.data.remote.AgentApiService
import com.example.learningpandaai.features.askpanda.data.remote.ChatUsageDto
import com.example.learningpandaai.features.askpanda.data.remote.ConversationDetailDto
import com.example.learningpandaai.features.askpanda.data.remote.ConversationDto
import com.example.learningpandaai.features.askpanda.data.remote.CreateConversationRequest
import com.example.learningpandaai.features.askpanda.data.remote.SendMessageRequest
import com.example.learningpandaai.features.askpanda.data.remote.SubmitMessageFeedbackRequest
import com.example.learningpandaai.features.askpanda.domain.AccountNotActivatedException
import com.example.learningpandaai.features.askpanda.domain.ChatUsageSummary
import com.example.learningpandaai.features.askpanda.domain.AgentRepository
import com.example.learningpandaai.features.askpanda.domain.ChatMessage
import com.example.learningpandaai.features.askpanda.domain.ChatSession
import com.example.learningpandaai.features.askpanda.domain.ChatSessionDetail
import com.example.learningpandaai.features.askpanda.domain.MessageFeedbackRequest
import com.example.learningpandaai.features.askpanda.domain.SendMessageResult
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AgentRepositoryImpl @Inject constructor(
    private val agentApiService: AgentApiService
) : AgentRepository {

    override suspend fun getSessions(): Result<List<ChatSession>> {
        return try {
            val conversations = agentApiService.getConversations()
            Logger.d("getSessions: success — count=${conversations.size}")
            Result.success(conversations.map { it.toDomain() }.filter { it.hasConversationHistory() })
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("getSessions: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            Logger.e("getSessions: HTTP ${e.code()} — ${e.message()}", e)
            if (e.code() == HTTP_FORBIDDEN) {
                Result.failure(AccountNotActivatedException())
            } else {
                Result.failure(ApiErrorMapper.mapHttpException(e))
            }
        } catch (e: Exception) {
            Logger.e("getSessions: unexpected error — ${e.message}", e)
            Result.failure(ApiErrorMapper.mapThrowable(e, "Failed to load chat sessions. Please retry."))
        }
    }

    override suspend fun getSession(sessionId: String): Result<ChatSessionDetail> {
        return try {
            val detail = agentApiService.getConversation(sessionId)
            Logger.d("getSession: success — id=${detail.id}, messages=${detail.messages?.size ?: 0}")
            Result.success(
                ChatSessionDetail(
                    session = detail.toDomain(),
                    messages = detail.messages.orEmpty().mapNotNull { msg ->
                        val role = msg.role ?: return@mapNotNull null
                        val content = msg.content ?: return@mapNotNull null
                        ChatMessage(id = msg.id, role = role, content = content)
                    }
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("getSession: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            Logger.e("getSession: HTTP ${e.code()} — ${e.message()}", e)
            Result.failure(ApiErrorMapper.mapHttpException(e))
        } catch (e: Exception) {
            Logger.e("getSession: unexpected error — ${e.message}", e)
            Result.failure(ApiErrorMapper.mapThrowable(e, "Failed to load chat session. Please retry."))
        }
    }

    override suspend fun createSession(
        subject: String,
        className: String,
        title: String?
    ): Result<ChatSession> {
        return try {
            val dto = agentApiService.createConversation(
                CreateConversationRequest(title = title, subject = subject)
            )
            Logger.d("createSession: success — id=${dto.id}, subject=${dto.subject}")
            Result.success(dto.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("createSession: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            Logger.e("createSession: HTTP ${e.code()} — ${e.message()}", e)
            if (e.code() == HTTP_FORBIDDEN) {
                Result.failure(AccountNotActivatedException())
            } else {
                Result.failure(ApiErrorMapper.mapHttpException(e))
            }
        } catch (e: Exception) {
            Logger.e("createSession: unexpected error — ${e.message}", e)
            Result.failure(ApiErrorMapper.mapThrowable(e, "Failed to create chat session. Please retry."))
        }
    }

    override suspend fun deleteSession(sessionId: String): Result<Unit> {
        return try {
            agentApiService.deleteConversation(sessionId)
            Logger.d("deleteSession: success — sessionId=$sessionId")
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("deleteSession: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            Logger.e("deleteSession: HTTP ${e.code()} — ${e.message()}", e)
            Result.failure(ApiErrorMapper.mapHttpException(e))
        } catch (e: Exception) {
            Logger.e("deleteSession: unexpected error — ${e.message}", e)
            Result.failure(ApiErrorMapper.mapThrowable(e, "Failed to delete session. Please retry."))
        }
    }

    override suspend fun getUsage(): Result<ChatUsageSummary> {
        return try {
            val usage = agentApiService.getUsage()
            Logger.d("getUsage: daily=${usage.daily?.messagesUsed}/${usage.daily?.messageLimit}")
            Result.success(usage.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("getUsage: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            Logger.e("getUsage: HTTP ${e.code()} — ${e.message()}", e)
            if (e.code() == HTTP_FORBIDDEN) {
                Result.failure(AccountNotActivatedException())
            } else {
                Result.failure(ApiErrorMapper.mapHttpException(e))
            }
        } catch (e: Exception) {
            Logger.e("getUsage: unexpected error — ${e.message}", e)
            Result.failure(ApiErrorMapper.mapThrowable(e, "Failed to load chat usage. Please retry."))
        }
    }

    override suspend fun sendMessage(conversationId: String, message: String): Result<SendMessageResult> {
        return try {
            val response = agentApiService.sendMessage(conversationId, SendMessageRequest(message = message))
            val userMessage = response.message
            val reply = response.reply
            Logger.d("sendMessage: reply received for conversation=$conversationId")
            Result.success(
                SendMessageResult(
                    userMessage = ChatMessage(
                        id = userMessage?.id,
                        role = userMessage?.role ?: ROLE_USER,
                        content = userMessage?.content.orEmpty()
                    ),
                    reply = ChatMessage(
                        id = reply?.id,
                        role = reply?.role ?: ROLE_ASSISTANT,
                        content = reply?.content.orEmpty()
                    ),
                    conversationTitle = response.conversationTitle?.trim()?.takeIf { it.isNotEmpty() }
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("sendMessage: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            Logger.e("sendMessage: HTTP ${e.code()} — ${e.message()}", e)
            if (e.code() == HTTP_FORBIDDEN) {
                Result.failure(AccountNotActivatedException())
            } else {
                Result.failure(ApiErrorMapper.mapHttpException(e))
            }
        } catch (e: Exception) {
            Logger.e("sendMessage: unexpected error — ${e.message}", e)
            Result.failure(ApiErrorMapper.mapThrowable(e, "Failed to send message. Please retry."))
        }
    }

    override suspend fun submitMessageFeedback(request: MessageFeedbackRequest): Result<Unit> {
        return try {
            agentApiService.submitMessageFeedback(
                conversationId = request.conversationId,
                messageId = request.assistantMessageId,
                request = SubmitMessageFeedbackRequest(
                    rating = request.rating.toApiRating(),
                    userMessageId = request.userMessageId,
                    userQuestion = request.userQuestion,
                    assistantResponse = request.assistantResponse
                )
            )
            Logger.d(
                "submitMessageFeedback: success — conversation=${request.conversationId}, " +
                        "message=${request.assistantMessageId}, rating=${request.rating.toApiRating()}"
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("submitMessageFeedback: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            Logger.e("submitMessageFeedback: HTTP ${e.code()} — ${e.message()}", e)
            if (e.code() == HTTP_FORBIDDEN) {
                Result.failure(AccountNotActivatedException())
            } else {
                Result.failure(ApiErrorMapper.mapHttpException(e))
            }
        } catch (e: Exception) {
            Logger.e("submitMessageFeedback: unexpected error — ${e.message}", e)
            Result.failure(ApiErrorMapper.mapThrowable(e, "Failed to save your feedback. Please retry."))
        }
    }

    private fun ConversationDto.toDomain() = ChatSession(
        id = id.toString(),
        subject = subject.orEmpty(),
        className = className.orEmpty(),
        title = title.orEmpty(),
        createdAt = createdAt.orEmpty(),
        updatedAt = (lastMessageAt ?: updatedAt ?: createdAt).orEmpty(),
        lastMessageAt = lastMessageAt
    )

    private fun ConversationDetailDto.toDomain() = ChatSession(
        id = id.toString(),
        subject = subject.orEmpty(),
        className = className.orEmpty(),
        title = title.orEmpty(),
        createdAt = createdAt.orEmpty(),
        updatedAt = (lastMessageAt ?: updatedAt ?: createdAt).orEmpty(),
        lastMessageAt = lastMessageAt
    )

    private fun ChatUsageDto.toDomain() = ChatUsageSummary(
        planName = planName.orEmpty().ifBlank { planCode.orEmpty().ifBlank { "Free" } },
        dailyUsed = daily?.messagesUsed ?: 0,
        dailyLimit = daily?.messageLimit,
        monthlyUsed = monthly?.messagesUsed ?: 0,
        monthlyLimit = monthly?.messageLimit
    )

    private companion object {
        const val HTTP_FORBIDDEN = 403
        const val ROLE_USER = "user"
        const val ROLE_ASSISTANT = "assistant"
    }
}
