package com.example.saathi.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SaathiApi {
    @GET("api/v1/health")
    suspend fun checkHealth(): Response<HealthResponse>

    @POST("api/v1/schemes/search")
    suspend fun searchSchemes(@Body request: SchemeSearchRequest): Response<SearchResponse>

    @POST("api/v1/schemes/ingest")
    suspend fun ingestScheme(@Body scheme: SchemeResponse): Response<Unit>
}
