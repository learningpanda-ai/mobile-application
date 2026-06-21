package com.example.learningpandaai.features.askpanda.data.local

import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.features.askpanda.domain.AgentRepository
import com.example.learningpandaai.features.askpanda.domain.ChatMessageFeedback
import com.example.learningpandaai.features.askpanda.domain.MessageFeedbackRequest
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Flushes locally queued chat feedback to the backend when the API is available.
 * Safe to call often — skips work when the queue is empty.
 */
@Singleton
class ChatFeedbackSyncManager @Inject constructor(
    private val localStore: ChatFeedbackLocalStore,
    private val agentRepository: AgentRepository
) {

    suspend fun syncPending(): Int {
        val pending = localStore.getPending()
        if (pending.isEmpty()) return 0

        var syncedCount = 0
        for (item in pending) {
            agentRepository.submitMessageFeedback(item.toRequest())
                .onSuccess {
                    localStore.markSynced(item.localId)
                    syncedCount++
                }
                .onFailure { error ->
                    if (error is HttpException && error.code() == HTTP_NOT_FOUND) {
                        // Conversation or message gone — drop stale feedback.
                        localStore.remove(item.localId)
                        Logger.d("ChatFeedbackSync: dropped stale feedback localId=${item.localId}")
                    } else {
                        Logger.d(
                            "ChatFeedbackSync: will retry later — localId=${item.localId}, " +
                                "reason=${error.message}"
                        )
                    }
                }
        }
        return syncedCount
    }

    private fun StoredChatFeedback.toRequest() = MessageFeedbackRequest(
        conversationId = conversationId,
        assistantMessageId = assistantMessageId,
        rating = when (rating) {
            ChatMessageFeedback.NEGATIVE.toApiRating() -> ChatMessageFeedback.NEGATIVE
            else -> ChatMessageFeedback.POSITIVE
        },
        userQuestion = userQuestion,
        assistantResponse = assistantResponse,
        userMessageId = userMessageId
    )

    private companion object {
        const val HTTP_NOT_FOUND = 404
    }
}
