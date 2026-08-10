package com.example.saathi.data.repository

import com.example.saathi.domain.model.Scheme
import com.example.saathi.domain.repository.SchemeRepository

class HybridSchemeRepository(
    private val remoteRepository: SchemeRepository,
    private val mockRepository: SchemeRepository
) : SchemeRepository {

    override suspend fun searchSchemes(query: String): List<Scheme> {
        return try {
            val remoteResults = remoteRepository.searchSchemes(query)
            if (remoteResults.isNotEmpty()) {
                remoteResults
            } else {
                // Fallback to mock if remote returns nothing
                mockRepository.searchSchemes(query)
            }
        } catch (e: Exception) {
            // Fallback to mock on network error
            mockRepository.searchSchemes(query)
        }
    }

    override suspend fun getSchemeById(id: String): Scheme? {
        return remoteRepository.getSchemeById(id) ?: mockRepository.getSchemeById(id)
    }
}
