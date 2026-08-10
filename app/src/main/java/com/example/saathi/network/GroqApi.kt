package com.example.saathi.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class GroqMessage(
    val role: String,
    val content: String
)

data class GroqChatRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<GroqMessage>,
    val temperature: Float = 0.7f,
    val max_tokens: Int = 1024
)

data class GroqChoice(
    val message: GroqMessage
)

data class GroqChatResponse(
    val choices: List<GroqChoice>
)

interface GroqApi {
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") auth: String,
        @Body request: GroqChatRequest
    ): Response<GroqChatResponse>
}
