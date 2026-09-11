package com.example.aicropcare.network

import com.example.aicropcare.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var currentBaseUrl: String = Constants.DEFAULT_BASE_URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .retryOnConnectionFailure(true)
        .build()

    private var retrofitInstance: Retrofit? = null
    private var apiServiceInstance: ApiService? = null

    fun getApiService(baseUrl: String = currentBaseUrl): ApiService {
        val normalizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        if (retrofitInstance == null || currentBaseUrl != normalizedUrl) {
            currentBaseUrl = normalizedUrl
            retrofitInstance = Retrofit.Builder()
                .baseUrl(currentBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            apiServiceInstance = retrofitInstance!!.create(ApiService::class.java)
        }
        return apiServiceInstance!!
    }

    fun getBaseUrl(): String = currentBaseUrl

    fun setBaseUrl(newUrl: String) {
        currentBaseUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        retrofitInstance = null
        apiServiceInstance = null
    }
}
