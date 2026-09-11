package com.example.aicropcare.network

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("app")
    val app: String? = null
)
