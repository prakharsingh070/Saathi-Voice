package com.example.saathi.network

import retrofit2.Response
import retrofit2.http.*

data class QdrantPoint(
    val id: String,
    val vector: List<Float>,
    val payload: Map<String, Any>
)

data class QdrantUpsertRequest(
    val points: List<QdrantPoint>
)

data class QdrantSearchRequest(
    val vector: List<Float>,
    val limit: Int = 5,
    val with_payload: Boolean = true
)

interface QdrantApi {
    @PUT("collections/{collection_name}/points")
    suspend fun upsertPoints(
        @Header("api-key") apiKey: String,
        @Path("collection_name") collectionName: String,
        @Body request: QdrantUpsertRequest
    ): Response<Unit>

    @POST("collections/{collection_name}/points/search")
    suspend fun searchPoints(
        @Header("api-key") apiKey: String,
        @Path("collection_name") collectionName: String,
        @Body request: QdrantSearchRequest
    ): Response<Unit> // Response mapping simplified for now
}
