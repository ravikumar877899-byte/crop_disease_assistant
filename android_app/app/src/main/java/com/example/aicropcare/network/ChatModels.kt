package com.example.aicropcare.network

import com.google.gson.annotations.SerializedName

data class ChatHistoryItem(
    @SerializedName("role")
    val role: String,

    @SerializedName("text")
    val text: String
)

data class ChatRequest(
    @SerializedName("message")
    val message: String,

    @SerializedName("history")
    val history: List<ChatHistoryItem>? = null
)

data class ChatResponse(
    @SerializedName("status")
    val status: String? = "success",

    @SerializedName("response")
    val response: String? = null,

    @SerializedName("engine")
    val engine: String? = null,

    @SerializedName("message")
    val message: String? = null
)
