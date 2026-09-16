package com.example.aicropcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aicropcare.data.preferences.SessionManager
import com.example.aicropcare.network.ChatHistoryItem
import com.example.aicropcare.repository.ChatbotRepository
import com.example.aicropcare.theme.*
import com.example.aicropcare.ui.components.ChatBubble
import com.example.aicropcare.utils.Constants
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isBot: Boolean,
    val isError: Boolean = false
)

@Composable
fun ChatbotScreen(
    chatbotRepository: ChatbotRepository? = null
) {
    val context = LocalContext.current
    val repository = remember {
        chatbotRepository ?: ChatbotRepository(SessionManager(context))
    }

    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                text = Constants.MSG_CHATBOT_INITIAL,
                isBot = true
            )
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    fun sendMessage(text: String) {
        if (text.isBlank() || isLoading) return
        val userMsg = text.trim()
        messages.add(ChatMessage(text = userMsg, isBot = false))
        inputText = ""
        isLoading = true

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)

            // Prepare recent conversation context for Gemini AI
            val historyList = messages
                .filter { !it.isError && it.text != Constants.MSG_CHATBOT_INITIAL }
                .takeLast(6)
                .map { msg ->
                    ChatHistoryItem(
                        role = if (msg.isBot) "model" else "user",
                        text = msg.text
                    )
                }

            val result = repository.sendQuery(userMsg, historyList)

            result.onSuccess { botAnswer ->
                messages.add(
                    ChatMessage(
                        text = botAnswer,
                        isBot = true
                    )
                )
            }.onFailure { ex ->
                val errorMsg = ex.localizedMessage ?: "Failed to get response from Krishi AI. Please check your internet connection."
                messages.add(
                    ChatMessage(
                        text = "⚠️ $errorMsg",
                        isBot = true,
                        isError = true
                    )
                )
            }

            isLoading = false
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val suggestions = listOf(
        "🌿 Tomato leaf yellowing",
        "🌾 Rice blast symptoms",
        "🐛 Natural pest control",
        "💧 Irrigation schedule",
        "🥔 Potato early blight",
        "🌱 Organic fertilizer tips"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
    ) {
        // Chatbot Header Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AgriSurface,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AgriPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Krishi AI",
                        tint = AgriPrimaryDark,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Krishi AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriPrimaryDark
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isLoading) AgriSecondary else AgriSuccess)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isLoading) "Krishi AI is thinking..." else "AI Agricultural Assistant Active",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }

                Surface(
                    color = AgriPrimaryContainer,
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AgriPrimaryDark,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Gemini AI",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriPrimaryDark
                        )
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(messages, key = { it.id + (if (it.isBot) 1 else 0) }) { message ->
                ChatBubble(
                    message = message.text,
                    isBot = message.isBot
                )
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = AgriPrimaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = AgriPrimaryDark
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Krishi AI is answering...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AgriPrimaryDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(AgriSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(suggestions) { chipText ->
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .border(1.dp, AgriBorder, RoundedCornerShape(100.dp))
                        .clickable(enabled = !isLoading) { sendMessage(chipText) },
                    color = AgriPrimaryContainer
                ) {
                    Text(
                        text = chipText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriPrimaryDark
                    )
                }
            }
        }

        // Input Form
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AgriSurface,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Ask a question about your crops...",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 50.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AgriTextPrimary,
                        unfocusedTextColor = AgriTextPrimary,
                        cursorColor = AgriPrimaryDark,
                        focusedBorderColor = AgriPrimary,
                        unfocusedBorderColor = AgriBorder,
                        focusedContainerColor = AgriBackground,
                        unfocusedContainerColor = AgriBackground,
                        focusedPlaceholderColor = AgriTextSecondary,
                        unfocusedPlaceholderColor = AgriTextSecondary
                    ),
                    textStyle = androidx.compose.material3.LocalTextStyle.current.copy(color = AgriTextPrimary),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { sendMessage(inputText) },
                    enabled = inputText.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !isLoading) AgriPrimary else AgriDivider)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank() && !isLoading) Color.White else AgriTextSecondary
                    )
                }
            }
        }
    }
}
