package com.example.learningpandaai.features.askpanda.data.remote

import com.google.gson.annotations.SerializedName

/** A chat message item used by the UI layer (role + content). */
data class ChatMessageItemDto(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String,
    val id: Long? = null,
    val userQuestion: String? = null,
    val userMessageId: Long? = null,
    val feedback: String? = null,
    val isFeedbackSubmitting: Boolean = false
)

// ─── Conversations (API_GUIDE §9) ───────────────────────────────────────────

data class CreateConversationRequest(
    @SerializedName("title") val title: String? = null,
    @SerializedName("subject") val subject: String? = null
)

data class ConversationDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String? = null,
    @SerializedName("subject") val subject: String? = null,
    @SerializedName("class_name") val className: String? = null,
    @SerializedName("last_message_at") val lastMessageAt: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class ConversationMessageDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class ConversationDetailDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String? = null,
    @SerializedName("subject") val subject: String? = null,
    @SerializedName("class_name") val className: String? = null,
    @SerializedName("last_message_at") val lastMessageAt: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("messages") val messages: List<ConversationMessageDto>? = null
)

data class SendMessageRequest(
    @SerializedName("message") val message: String
)

/** Response of POST /chat/conversations/{id}/messages (api.md §9). */
data class SendMessageResponseDto(
    @SerializedName("conversation_id") val conversationId: Long? = null,
    @SerializedName("conversation_title") val conversationTitle: String? = null,
    @SerializedName("message") val message: ConversationMessageDto? = null,
    @SerializedName("reply") val reply: ConversationMessageDto? = null
)

data class ChatUsagePeriodDto(
    @SerializedName("messages_used") val messagesUsed: Int = 0,
    @SerializedName("message_limit") val messageLimit: Int? = null,
    @SerializedName("tokens_used") val tokensUsed: Int = 0,
    @SerializedName("token_limit") val tokenLimit: Int? = null
)

data class ChatUsageDto(
    @SerializedName("plan_code") val planCode: String? = null,
    @SerializedName("plan_name") val planName: String? = null,
    @SerializedName("daily") val daily: ChatUsagePeriodDto? = null,
    @SerializedName("monthly") val monthly: ChatUsagePeriodDto? = null,
    @SerializedName("conversations_used") val conversationsUsed: Int = 0,
    @SerializedName("max_conversations") val maxConversations: Int? = null
)

data class SubmitMessageFeedbackRequest(
    @SerializedName("rating") val rating: String,
    @SerializedName("user_message_id") val userMessageId: Long? = null,
    @SerializedName("user_question") val userQuestion: String,
    @SerializedName("assistant_response") val assistantResponse: String
)

data class MessageFeedbackResponseDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("message_id") val messageId: Long? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)
