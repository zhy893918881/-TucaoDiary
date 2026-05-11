package com.ai.tucaodiary.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AiApiService {

    @POST("chat/completions")
    suspend fun generateText(
        @Body request: ChatRequest
    ): ChatResponse
}
