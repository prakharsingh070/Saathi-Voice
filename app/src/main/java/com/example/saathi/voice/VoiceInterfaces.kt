package com.example.saathi.voice

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

enum class VoiceState {
    IDLE, LISTENING, RECORDING, PROCESSING, TRANSCRIBING, SPEAKING, ERROR
}

data class TranscriptionResult(
    val text: String,
    val language: String,
    val confidence: Float
)

interface SpeechToTextService {
    suspend fun transcribe(audioData: ByteArray): TranscriptionResult
}

interface VoiceService {
    val state: StateFlow<VoiceState>
    val recordingDurationMillis: StateFlow<Long>
    val transcriptionResult: SharedFlow<TranscriptionResult>
    
    fun startListening()
    fun stopListening()
    fun cancelListening()
    fun setSpeaking(isSpeaking: Boolean)
}
