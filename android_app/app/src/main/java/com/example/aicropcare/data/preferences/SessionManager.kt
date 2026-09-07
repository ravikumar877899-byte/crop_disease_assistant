package com.example.aicropcare.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.aicropcare.data.models.UserProfile
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "ai_crop_care_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_PROFILE = "user_profile"
        private const val KEY_LANGUAGE = "selected_language"
        private const val KEY_SERVER_URL = "server_base_url"
        private const val KEY_COOKIES = "session_cookies"

        // Production Public HTTPS Backend URL (Works globally on mobile data & any Wi-Fi)
        const val PRODUCTION_SERVER_URL = "https://aicropcare-backend.onrender.com/"
        const val EMULATOR_DEV_URL = "http://10.0.2.2:5000/"
        const val DEFAULT_SERVER_URL = PRODUCTION_SERVER_URL
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var userProfile: UserProfile?
        get() {
            val json = prefs.getString(KEY_USER_PROFILE, null) ?: return null
            return try {
                gson.fromJson(json, UserProfile::class.java)
            } catch (e: Exception) {
                null
            }
        }
        set(value) {
            if (value == null) {
                prefs.edit().remove(KEY_USER_PROFILE).apply()
            } else {
                prefs.edit().putString(KEY_USER_PROFILE, gson.toJson(value)).apply()
            }
        }

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    val isTamil: Boolean
        get() = language == "ta"

    var serverUrl: String
        get() = prefs.getString(KEY_SERVER_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL
        set(value) {
            val normalized = if (value.endsWith("/")) value else "$value/"
            prefs.edit().putString(KEY_SERVER_URL, normalized).apply()
        }

    var sessionCookies: Set<String>
        get() = prefs.getStringSet(KEY_COOKIES, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_COOKIES, value).apply()

    fun clearSession() {
        prefs.edit()
            .remove(KEY_IS_LOGGED_IN)
            .remove(KEY_USER_PROFILE)
            .remove(KEY_COOKIES)
            .apply()
    }
}
