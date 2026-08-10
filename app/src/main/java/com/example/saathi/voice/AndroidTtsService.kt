package com.example.saathi.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class AndroidTtsService(context: Context) : TextToSpeechService {
    
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                tts?.language = Locale("hi", "IN")
            }
        }
    }

    override fun speak(text: String, language: String) {
        if (!isInitialized) return
        
        val locale = when (language.lowercase()) {
            "hi" -> Locale("hi", "IN")
            "en" -> Locale.US
            else -> Locale("hi", "IN")
        }
        
        tts?.language = locale
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun stop() {
        tts?.stop()
    }

    override fun pause() {
        // Android TTS doesn't have a native pause/resume for simple speak calls
        // We just stop for now
        tts?.stop()
    }

    override fun resume() {
        // Not directly supported
    }

    override fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
