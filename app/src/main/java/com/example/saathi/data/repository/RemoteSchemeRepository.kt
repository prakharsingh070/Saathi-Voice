package com.example.saathi.data.repository

import com.example.saathi.domain.model.Scheme
import com.example.saathi.domain.repository.SchemeRepository
import com.example.saathi.network.SaathiApi
import com.example.saathi.network.SchemeSearchRequest
import com.example.saathi.network.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteSchemeRepository(private val api: SaathiApi) : SchemeRepository {
    
    private var cachedSchemes = listOf<Scheme>()

    override suspend fun searchSchemes(query: String): List<Scheme> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchSchemes(SchemeSearchRequest(query = query, userId = "anonymous"))
            if (response.isSuccessful) {
                val schemes = response.body()?.schemes?.map { it.toDomain() } ?: emptyList()
                cachedSchemes = schemes
                schemes
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getSchemeById(id: String): Scheme? = withContext(Dispatchers.IO) {
        cachedSchemes.find { it.id == id }
    }
}
