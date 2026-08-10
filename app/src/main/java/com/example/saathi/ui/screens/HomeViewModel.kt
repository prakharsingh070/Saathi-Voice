package com.example.saathi.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saathi.domain.model.Message
import com.example.saathi.domain.repository.HistoryRepository
import com.example.saathi.domain.repository.SchemeRepository
import com.example.saathi.domain.AssistantBrain
import com.example.saathi.ui.components.VoiceState as UiVoiceState
import com.example.saathi.voice.TextToSpeechService
import com.example.saathi.voice.VoiceService
import com.example.saathi.voice.VoiceState as DomainVoiceState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel(
    private val voiceService: VoiceService,
    private val assistantBrain: AssistantBrain,
    private val historyRepository: HistoryRepository,
    private val ttsService: TextToSpeechService
) : ViewModel() {
    
    private val _navigateToResults = MutableSharedFlow<String>(replay = 0)
    val navigateToResults = _navigateToResults.asSharedFlow()

    val voiceState: StateFlow<UiVoiceState> = voiceService.state.map { state ->
        when (state) {
            DomainVoiceState.IDLE -> UiVoiceState.IDLE
            DomainVoiceState.LISTENING, DomainVoiceState.RECORDING -> UiVoiceState.LISTENING
            DomainVoiceState.PROCESSING, DomainVoiceState.TRANSCRIBING -> UiVoiceState.THINKING
            DomainVoiceState.SPEAKING -> UiVoiceState.SPEAKING
            DomainVoiceState.ERROR -> UiVoiceState.ERROR
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, UiVoiceState.IDLE)

    init {
        // Observe transcription result
        viewModelScope.launch {
            voiceService.transcriptionResult.collect { result ->
                handleTranscription(result.text, result.language)
            }
        }
    }

    private fun handleTranscription(text: String, language: String) {
        viewModelScope.launch {
            // Add user message to history
            historyRepository.addMessage(Message(
                id = UUID.randomUUID().toString(),
                text = text,
                isUser = true
            ))

            // Get intelligent response from AssistantBrain
            val response = assistantBrain.getResponse(text)
            
            // Add bot message to history
            historyRepository.addMessage(Message(
                id = UUID.randomUUID().toString(),
                text = response.text,
                isUser = false
            ))

            // Set greeting for UI
            _greeting.value = response.text

            // Trigger TTS and set state to SPEAKING
            voiceService.setSpeaking(true)
            ttsService.speak(response.speakingText, language)
            
            // Wait for TTS to likely finish (in real app we'd use listener)
            delay(6000)
            voiceService.setSpeaking(false)
            _greeting.value = "Bol kar poochiye..."
        }
    }

    val recordingDuration = voiceService.recordingDurationMillis
        .map { "${it / 1000}s" }
        .stateIn(viewModelScope, SharingStarted.Lazily, "0s")

    private val _greeting = MutableStateFlow("Namaste, main SAATHI hoon.")
    val greeting = _greeting.asStateFlow()

    fun onMicClick() {
        if (voiceService.state.value == DomainVoiceState.IDLE) {
            voiceService.startListening()
        } else {
            voiceService.stopListening()
        }
    }
    
    fun setGreeting(text: String) {
        _greeting.value = text
    }
}
