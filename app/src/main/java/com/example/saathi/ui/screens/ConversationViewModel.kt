package com.example.saathi.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saathi.domain.AssistantBrain
import com.example.saathi.domain.model.ConversationSession
import com.example.saathi.domain.model.Message
import com.example.saathi.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ConversationViewModel(
    private val historyRepository: HistoryRepository,
    private val assistantBrain: AssistantBrain
) : ViewModel() {
    val messages: StateFlow<List<Message>> = historyRepository.getConversationHistory()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val sessions: StateFlow<List<ConversationSession>> = historyRepository.getSessions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val userMessage = Message(
                id = UUID.randomUUID().toString(),
                text = text,
                isUser = true
            )
            historyRepository.addMessage(userMessage)
            
            // Get intelligent response from AssistantBrain
            val response = assistantBrain.getResponse(text)

            val botMessage = Message(
                id = UUID.randomUUID().toString(),
                text = response.text,
                isUser = false
            )
            historyRepository.addMessage(botMessage)
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clearHistory()
        }
    }
}
