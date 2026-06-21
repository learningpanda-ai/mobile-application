package com.example.learningpandaai.features.askpanda.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Chat (conversations) endpoints under /api/v1/chat (API_GUIDE §9).
 * Messages are a normal request/response — no SSE streaming. The server persists
 * both the user message and the assistant reply when a message is posted.
 */
interface AgentApiService {

    @POST("api/v1/chat/conversations")
    suspend fun createConversation(@Body request: CreateConversationRequest): ConversationDto

    @GET("api/v1/chat/conversations")
    suspend fun getConversations(): List<ConversationDto>

    @GET("api/v1/chat/conversations/{id}")
    suspend fun getConversation(@Path("id") id: String): ConversationDetailDto

    @DELETE("api/v1/chat/conversations/{id}")
    suspend fun deleteConversation(@Path("id") id: String)

    @POST("api/v1/chat/conversations/{id}/messages")
    suspend fun sendMessage(
        @Path("id") id: String,
        @Body request: SendMessageRequest
    ): SendMessageResponseDto

    @GET("api/v1/chat/usage")
    suspend fun getUsage(): ChatUsageDto

    @POST("api/v1/chat/conversations/{conversationId}/messages/{messageId}/feedback")
    suspend fun submitMessageFeedback(
        @Path("conversationId") conversationId: String,
        @Path("messageId") messageId: Long,
        @Body request: SubmitMessageFeedbackRequest
    ): MessageFeedbackResponseDto
}
