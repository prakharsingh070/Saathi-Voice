package com.example.saathi.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class RimeTtsRequest(
    val text: String,
    val speaker_id: String = "vishwa", // Sophisticated Indian male/female voice
    val model_id: String = "mist-v1",
    val sampling_rate: Int = 24000
)

interface RimeApi {
    @POST("v1/tts")
    suspend fun generateSpeech(
        @Header("Authorization") auth: String,
        @Body request: RimeTtsRequest
    ): Response<ResponseBody>
}
