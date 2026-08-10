package com.example.saathi.voice

interface TextToSpeechService {
    fun speak(text: String, language: String = "en")
    fun stop()
    fun pause()
    fun resume()
    fun shutdown()
}
