package com.example.saathi.domain.repository

import com.example.saathi.domain.model.ConversationSession
import com.example.saathi.domain.model.Message
import com.example.saathi.domain.model.Scheme
import com.example.saathi.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface SchemeRepository {
    suspend fun searchSchemes(query: String): List<Scheme>
    suspend fun getSchemeById(id: String): Scheme?
}

interface UserRepository {
    fun getUserProfile(): Flow<UserProfile>
    suspend fun updateUserProfile(profile: UserProfile)
    suspend fun removeFact(fact: String)
    suspend fun clearRememberedData()
}

interface HistoryRepository {
    fun getConversationHistory(): Flow<List<Message>>
    fun getSessions(): Flow<List<ConversationSession>>
    suspend fun addMessage(message: Message)
    suspend fun clearHistory()
}
