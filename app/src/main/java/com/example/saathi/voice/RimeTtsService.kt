package com.example.saathi.voice

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.saathi.network.RimeApi
import com.example.saathi.network.RimeTtsRequest
import com.example.saathi.util.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class RimeTtsService(private val context: Context) : TextToSpeechService {

    private val api: RimeApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.rime.ai/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(RimeApi::class.java)
    }

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    fun speakWithFallback(text: String, onFallback: () -> Unit) {
        if (Config.RIME_API_KEY.isBlank()) {
            onFallback()
            return
        }

        scope.launch {
            try {
                val response = api.generateSpeech(
                    auth = "Bearer ${Config.RIME_API_KEY}",
                    request = RimeTtsRequest(text = text)
                )

                if (response.isSuccessful) {
                    val body = response.body() ?: throw Exception("Empty body")
                    val tempFile = File(context.cacheDir, "rime_tts.mp3")
                    
                    withContext(Dispatchers.IO) {
                        FileOutputStream(tempFile).use { output ->
                            output.write(body.bytes())
                        }
                    }

                    withContext(Dispatchers.Main) {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(tempFile.absolutePath)
                            prepare()
                            start()
                        }
                    }
                } else {
                    Log.e("RimeTts", "API failed: ${response.code()}")
                    withContext(Dispatchers.Main) { onFallback() }
                }
            } catch (e: Exception) {
                Log.e("RimeTts", "Exception in Rime", e)
                withContext(Dispatchers.Main) { onFallback() }
            }
        }
    }

    override fun speak(text: String, language: String) {
        // Required by interface but we prefer speakWithFallback
    }

    override fun stop() {
        mediaPlayer?.stop()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun resume() {
        mediaPlayer?.start()
    }

    override fun shutdown() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
