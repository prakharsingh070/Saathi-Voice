package com.example.saathi.network

import com.example.saathi.util.Config
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GroqService {

    private val api: GroqApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.groq.com/openai/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(GroqApi::class.java)
    }

    suspend fun getHumanResponse(userInput: String, context: String): String {
        if (Config.GROQ_API_KEY.isBlank()) return ""

        val systemPrompt = """
            You are SAATHI, an expert Indian Government Welfare Assistant. 
            You are helpful, polite, and authoritative. Your goal is to guide citizens to real government schemes and hospital assistance.
            
            STRICT RULES:
            1. Language: Speak in natural Hinglish (Hindi words written in English letters + English).
            2. Professionalism: Do NOT guess or give rubbish answers. If you don't know a specific scheme, ask the user for their problem area (Health, Education, Farming).
            3. Follow-up: To find a scheme, you MUST ask exactly one follow-up question per turn about: State, Age, Occupation, or Income.
            4. Schemes: Prioritize real schemes like PM-KISAN, Ayushman Bharat (for hospitals/health), PMEGP (for business), or Post Matric Scholarship.
            5. Scheme Format: When recommending a scheme, ALWAYS end your response with this EXACT tag:
               [SCHEME_FOUND: Scheme Name | Official URL | Key Benefit]
            
            Conversation History:
            $context
        """.trimIndent()

        val messages = listOf(
            GroqMessage("system", systemPrompt),
            GroqMessage("user", userInput)
        )

        return try {
            val response = api.getChatCompletion(
                auth = "Bearer ${Config.GROQ_API_KEY}",
                request = GroqChatRequest(messages = messages)
            )
            if (response.isSuccessful) {
                response.body()?.choices?.firstOrNull()?.message?.content ?: ""
            } else {
                "Ji, maaf kijiye. Server mein kuch dikkat hai. Kya aap phir se bol sakte hain?"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Ji, network connection check kijiye. Main aapki madad nahi kar paa raha hoon."
        }
    }
}
