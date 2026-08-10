package com.example.saathi.data.local

import com.example.saathi.network.QdrantApi
import com.example.saathi.network.QdrantPoint
import com.example.saathi.network.QdrantUpsertRequest
import com.example.saathi.util.Config
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class QdrantMemoryService {

    private val api: QdrantApi by lazy {
        Retrofit.Builder()
            .baseUrl(Config.QDRANT_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient())
            .build()
            .create(QdrantApi::class.java)
    }

    suspend fun saveFact(text: String, metadata: Map<String, Any> = emptyMap()) {
        if (Config.QDRANT_API_KEY.isBlank()) return
        
        try {
            val point = QdrantPoint(
                id = UUID.randomUUID().toString(),
                vector = List(1536) { 0f }, // Dummy vector for now (needs real embeddings)
                payload = metadata + ("text" to text)
            )
            
            api.upsertPoints(
                apiKey = Config.QDRANT_API_KEY,
                collectionName = "saathi_memory",
                request = QdrantUpsertRequest(listOf(point))
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
