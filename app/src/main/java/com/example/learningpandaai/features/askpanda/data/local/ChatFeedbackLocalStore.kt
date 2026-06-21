package com.example.learningpandaai.features.askpanda.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatFeedbackLocalStore @Inject constructor(
    @ApplicationContext context: Context,
    private val gson: Gson
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveFeedback(
        conversationId: String,
        assistantMessageId: Long,
        userMessageId: Long?,
        rating: String,
        userQuestion: String,
        assistantResponse: String
    ): StoredChatFeedback {
        val existing = findEntry(conversationId, assistantMessageId)
        if (existing != null) return existing

        val now = System.currentTimeMillis()
        val entry = StoredChatFeedback(
            localId = UUID.randomUUID().toString(),
            conversationId = conversationId,
            assistantMessageId = assistantMessageId,
            userMessageId = userMessageId,
            rating = rating,
            userQuestion = userQuestion,
            assistantResponse = assistantResponse,
            createdAtEpochMs = now,
            syncedAtEpochMs = null
        )
        val updated = loadAll()
            .filterNot {
                it.conversationId == conversationId && it.assistantMessageId == assistantMessageId
            } + entry
        persist(updated)
        return entry
    }

    fun getRating(conversationId: String, assistantMessageId: Long): String? =
        findEntry(conversationId, assistantMessageId)?.rating

    fun getPending(): List<StoredChatFeedback> =
        loadAll().filter { it.syncedAtEpochMs == null }

    fun markSynced(localId: String) {
        val updated = loadAll().map { item ->
            if (item.localId == localId) {
                item.copy(syncedAtEpochMs = System.currentTimeMillis())
            } else {
                item
            }
        }
        persist(updated)
        pruneOldSynced()
    }

    fun remove(localId: String) {
        persist(loadAll().filterNot { it.localId == localId })
    }

    private fun findEntry(conversationId: String, assistantMessageId: Long): StoredChatFeedback? =
        loadAll().lastOrNull {
            it.conversationId == conversationId && it.assistantMessageId == assistantMessageId
        }

    private fun loadAll(): List<StoredChatFeedback> {
        val json = prefs.getString(KEY_ENTRIES, null) ?: return emptyList()
        return runCatching {
            gson.fromJson<List<StoredChatFeedback>>(
                json,
                object : TypeToken<List<StoredChatFeedback>>() {}.type
            )
        }.getOrNull().orEmpty()
    }

    private fun persist(entries: List<StoredChatFeedback>) {
        prefs.edit()
            .putString(KEY_ENTRIES, gson.toJson(entries))
            .apply()
    }

    /** Drop synced rows older than 30 days to keep storage small. */
    private fun pruneOldSynced() {
        val cutoff = System.currentTimeMillis() - SYNCED_RETENTION_MS
        persist(loadAll().filter { it.syncedAtEpochMs == null || it.syncedAtEpochMs >= cutoff })
    }

    private companion object {
        const val PREFS_NAME = "chat_feedback_queue"
        const val KEY_ENTRIES = "entries"
        const val SYNCED_RETENTION_MS = 30L * 24 * 60 * 60 * 1000
    }
}
