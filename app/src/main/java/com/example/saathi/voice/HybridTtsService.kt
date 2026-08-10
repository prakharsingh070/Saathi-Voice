package com.example.saathi.voice

import com.example.saathi.util.Config

class HybridTtsService(
    private val rimeService: RimeTtsService,
    private val androidService: AndroidTtsService
) : TextToSpeechService {

    override fun speak(text: String, language: String) {
        rimeService.speakWithFallback(text) {
            // Fallback to Android TTS if Rime fails or no key
            androidService.speak(text, language)
        }
    }

    override fun stop() {
        rimeService.stop()
        androidService.stop()
    }

    override fun pause() {
        rimeService.pause()
        androidService.pause()
    }

    override fun resume() {
        rimeService.resume()
        androidService.resume()
    }

    override fun shutdown() {
        rimeService.shutdown()
        androidService.shutdown()
    }
}
