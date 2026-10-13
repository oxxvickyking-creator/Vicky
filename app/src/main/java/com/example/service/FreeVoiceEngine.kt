package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class FreeVoiceEngine(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentUtteranceId = MutableStateFlow<String?>(null)
    val currentUtteranceId: StateFlow<String?> = _currentUtteranceId.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.0f)
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                        _currentUtteranceId.value = utteranceId
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                    }
                })
            }
        }
    }

    fun setVoicePersona(persona: String, language: String) {
        if (!isInitialized) return
        val ttsInstance = tts ?: return

        // Set Language
        when {
            language.contains("Hindi", ignoreCase = true) -> {
                ttsInstance.language = Locale("hi", "IN")
            }
            language.contains("Urdu", ignoreCase = true) -> {
                ttsInstance.language = Locale("ur", "PK")
            }
            language.contains("Spanish", ignoreCase = true) -> {
                ttsInstance.language = Locale("es", "ES")
            }
            else -> {
                ttsInstance.language = Locale.US
            }
        }

        // Persona voice tuning
        when (persona) {
            "Deep Cinematic Male" -> {
                ttsInstance.setPitch(0.75f)
                ttsInstance.setSpeechRate(0.92f)
            }
            "Energetic Tech" -> {
                ttsInstance.setPitch(1.15f)
                ttsInstance.setSpeechRate(1.1f)
            }
            "Storyteller Female" -> {
                ttsInstance.setPitch(1.2f)
                ttsInstance.setSpeechRate(0.95f)
            }
            "Warm Explainer" -> {
                ttsInstance.setPitch(1.0f)
                ttsInstance.setSpeechRate(1.0f)
            }
            "Epic Movie Trailer" -> {
                ttsInstance.setPitch(0.65f)
                ttsInstance.setSpeechRate(0.88f)
            }
            else -> {
                ttsInstance.setPitch(1.0f)
                ttsInstance.setSpeechRate(1.0f)
            }
        }
    }

    fun speak(text: String, utteranceId: String = "scene_${System.currentTimeMillis()}") {
        if (text.isBlank()) return
        stop()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        _currentUtteranceId.value = null
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
