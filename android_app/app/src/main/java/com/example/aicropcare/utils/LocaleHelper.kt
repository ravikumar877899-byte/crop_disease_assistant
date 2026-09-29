package com.example.aicropcare.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

data class LanguageOption(
    val code: String,
    val nativeName: String,
    val englishName: String
)

object LocaleHelper {
    val SUPPORTED_LANGUAGES = listOf(
        LanguageOption(code = "en", nativeName = "English", englishName = "English"),
        LanguageOption(code = "ta", nativeName = "தமிழ்", englishName = "Tamil"),
        LanguageOption(code = "hi", nativeName = "हिन्दी", englishName = "Hindi"),
        LanguageOption(code = "te", nativeName = "తెలుగు", englishName = "Telugu"),
        LanguageOption(code = "kn", nativeName = "ಕನ್ನಡ", englishName = "Kannada"),
        LanguageOption(code = "ml", nativeName = "മലയാളം", englishName = "Malayalam"),
        LanguageOption(code = "mr", nativeName = "मराठी", englishName = "Marathi"),
        LanguageOption(code = "bn", nativeName = "বাংলা", englishName = "Bengali"),
        LanguageOption(code = "gu", nativeName = "ગુજરાતી", englishName = "Gujarati"),
        LanguageOption(code = "pa", nativeName = "ਪੰਜਾਬੀ", englishName = "Punjabi")
    )

    fun getLanguageOption(code: String): LanguageOption {
        return SUPPORTED_LANGUAGES.firstOrNull { it.code.equals(code, ignoreCase = true) }
            ?: SUPPORTED_LANGUAGES.first()
    }

    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        }

        return context.createConfigurationContext(config)
    }
}
