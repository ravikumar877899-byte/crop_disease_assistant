package com.example.aicropcare.data.api

import android.content.Context
import com.example.aicropcare.data.preferences.SessionManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient private constructor(private val context: Context) {

    private val sessionManager = SessionManager(context)
    private var currentBaseUrl = sessionManager.serverUrl
    private var retrofit: Retrofit? = null
    private var apiService: ApiService? = null

    // In-memory cookie store backed by SessionManager
    private val cookieStore = HashMap<String, MutableList<Cookie>>()

    private val cookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            val host = url.host
            val list = cookieStore.getOrPut(host) { mutableListOf() }
            list.removeAll { existing -> cookies.any { it.name == existing.name } }
            list.addAll(cookies)

            // Persist cookie strings
            val cookieStrings = cookies.map { "${it.name}=${it.value}; domain=${it.domain}" }.toSet()
            sessionManager.sessionCookies = cookieStrings
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            val host = url.host
            return cookieStore[host] ?: emptyList()
        }
    }

    private fun createOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(logging)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    fun getService(): ApiService {
        val configuredUrl = sessionManager.serverUrl
        if (apiService == null || currentBaseUrl != configuredUrl) {
            currentBaseUrl = configuredUrl
            retrofit = Retrofit.Builder()
                .baseUrl(currentBaseUrl)
                .client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            apiService = retrofit!!.create(ApiService::class.java)
        }
        return apiService!!
    }

    companion object {
        @Volatile
        private var instance: ApiClient? = null

        fun getInstance(context: Context): ApiClient {
            return instance ?: synchronized(this) {
                instance ?: ApiClient(context.applicationContext).also { instance = it }
            }
        }
    }
}
