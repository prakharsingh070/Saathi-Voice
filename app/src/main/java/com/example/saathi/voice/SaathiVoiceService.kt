package com.example.saathi.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class SaathiVoiceService(
    private val context: Context,
    private val ttsService: TextToSpeechService,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) : VoiceService {

    private val _state = MutableStateFlow(VoiceState.IDLE)
    override val state = _state.asStateFlow()

    private val _recordingDurationMillis = MutableStateFlow(0L)
    override val recordingDurationMillis = _recordingDurationMillis.asStateFlow()

    private val _transcriptionResult = MutableSharedFlow<TranscriptionResult>()
    override val transcriptionResult = _transcriptionResult.asSharedFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var durationJob: Job? = null

    private fun startForegroundService() {
        val serviceIntent = Intent(context, SaathiForegroundService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    private fun stopForegroundService() {
        val serviceIntent = Intent(context, SaathiForegroundService::class.java)
        context.stopService(serviceIntent)
    }

    override fun startListening() {
        if (_state.value != VoiceState.IDLE) return
        ttsService.stop()
        
        startForegroundService()
        _state.value = VoiceState.LISTENING

        scope.launch {
            if (speechRecognizer == null) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }

            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    _state.value = VoiceState.RECORDING
                    startDurationTimer()
                }

                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    _state.value = VoiceState.PROCESSING
                    stopDurationTimer()
                }

                override fun onError(error: Int) {
                    _state.value = VoiceState.ERROR
                    stopDurationTimer()
                    stopForegroundService()
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.get(0) ?: ""
                    
                    scope.launch {
                        _state.value = VoiceState.TRANSCRIBING
                        _transcriptionResult.emit(TranscriptionResult(text, "hi", 1.0f))
                        
                        // Wait for ViewModel to handle it and call setSpeaking
                        delay(2000)
                        if (_state.value == VoiceState.TRANSCRIBING) {
                            _state.value = VoiceState.IDLE
                            stopForegroundService()
                        }
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            speechRecognizer?.startListening(intent)
        }
    }

    private fun startDurationTimer() {
        durationJob?.cancel()
        val startTime = System.currentTimeMillis()
        durationJob = scope.launch {
            while (isActive) {
                _recordingDurationMillis.value = System.currentTimeMillis() - startTime
                delay(100)
            }
        }
    }

    private fun stopDurationTimer() {
        durationJob?.cancel()
        _recordingDurationMillis.value = 0
    }

    override fun stopListening() {
        speechRecognizer?.stopListening()
    }

    override fun cancelListening() {
        speechRecognizer?.cancel()
        stopDurationTimer()
        _state.value = VoiceState.IDLE
        stopForegroundService()
    }

    override fun setSpeaking(isSpeaking: Boolean) {
        if (isSpeaking) {
            _state.value = VoiceState.SPEAKING
        } else {
            _state.value = VoiceState.IDLE
            stopForegroundService()
        }
    }
}
