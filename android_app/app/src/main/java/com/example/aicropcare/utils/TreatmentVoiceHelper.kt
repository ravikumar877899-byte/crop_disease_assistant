package com.example.aicropcare.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TreatmentVoiceHelper(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    
    // We keep error state only for complete initialization failure
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context, this)
        } catch (e: Exception) {
            Log.e("TreatmentVoiceHelper", "Exception during TTS init", e)
            _error.value = "voice_initialization_failed"
        }
    }

    private var pendingSpeechText: Pair<String, String>? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            _isInitialized.value = true
            _error.value = null
            pendingSpeechText?.let {
                speak(it.first, it.second)
                pendingSpeechText = null
            }
        } else {
            Log.e("TreatmentVoiceHelper", "Initialization Failed! Status: $status")
            _error.value = "voice_initialization_failed"
        }
    }

    private fun getRegionalLocale(langCode: String): Locale {
        return when (langCode.lowercase()) {
            "ta" -> Locale("ta", "IN")
            "hi" -> Locale("hi", "IN")
            "te" -> Locale("te", "IN")
            "kn" -> Locale("kn", "IN")
            "ml" -> Locale("ml", "IN")
            "mr" -> Locale("mr", "IN")
            "bn" -> Locale("bn", "IN")
            "gu" -> Locale("gu", "IN")
            "pa" -> Locale("pa", "IN")
            "en" -> Locale.US
            else -> Locale.US
        }
    }

    private fun getLanguageOnlyLocale(langCode: String): Locale {
        return Locale(langCode.lowercase())
    }

    fun speak(text: String, langCode: String) {
        val ttsInstance = tts
        if (ttsInstance == null) {
            _error.value = "voice_initialization_failed"
            return
        }

        if (!_isInitialized.value) {
            pendingSpeechText = Pair(text, langCode)
            return
        }

        // 1. Try regional locale
        var locale = getRegionalLocale(langCode)
        var isAvailable = ttsInstance.isLanguageAvailable(locale)
        
        if (isAvailable == TextToSpeech.LANG_MISSING_DATA || isAvailable == TextToSpeech.LANG_NOT_SUPPORTED) {
            // 2. Try language-only locale for the 8 languages (and ta/en if needed)
            val langOnlyLocale = getLanguageOnlyLocale(langCode)
            val isLangOnlyAvailable = ttsInstance.isLanguageAvailable(langOnlyLocale)
            
            if (isLangOnlyAvailable != TextToSpeech.LANG_MISSING_DATA && isLangOnlyAvailable != TextToSpeech.LANG_NOT_SUPPORTED) {
                locale = langOnlyLocale
            } else {
                // 3. Silently fallback to English if both are unavailable
                Log.w("TreatmentVoiceHelper", "Language $langCode not supported. Silently falling back to English.")
                locale = Locale.US
            }
        }

        // Clear any previous error since we now silently fallback instead of showing red error
        _error.value = null
        
        // Attempt to set language (even if fallback is US, we just try to speak)
        ttsInstance.setLanguage(locale)
        
        // Stop current speech if any
        if (_isSpeaking.value) {
            stop()
        }

        // Setup utterance progress listener
        ttsInstance.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
            }

            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
            }
        })

        val params = android.os.Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "treatment_speech")
        
        ttsInstance.speak(text, TextToSpeech.QUEUE_FLUSH, params, "treatment_speech")
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
