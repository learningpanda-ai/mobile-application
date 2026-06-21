package com.example.learningpandaai.features.askpanda.data.local

import com.google.gson.annotations.SerializedName

/**
 * Persisted chat feedback — saved on device immediately, synced to the backend when available.
 */
data class StoredChatFeedback(
    @SerializedName("local_id") val localId: String,
    @SerializedName("conversation_id") val conversationId: String,
    @SerializedName("assistant_message_id") val assistantMessageId: Long,
    @SerializedName("user_message_id") val userMessageId: Long? = null,
    @SerializedName("rating") val rating: String,
    @SerializedName("user_question") val userQuestion: String,
    @SerializedName("assistant_response") val assistantResponse: String,
    @SerializedName("created_at_epoch_ms") val createdAtEpochMs: Long,
    @SerializedName("synced_at_epoch_ms") val syncedAtEpochMs: Long? = null
)
