package com.techibot.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techibot.app.data.model.ChatMessage
import com.techibot.app.data.model.ChatRequest
import com.techibot.app.data.model.ChatResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.UUID

interface ChatService {
    @POST("api/chat")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Body request: ChatRequest
    ): ChatResponse
}

sealed class ChatState {
    object Idle : ChatState()
    object Loading : ChatState()
    data class Error(val message: String) : ChatState()
}

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _chatState = MutableStateFlow<ChatState>(ChatState.Idle)
    val chatState: StateFlow<ChatState> = _chatState

    private val chatService = Retrofit.Builder()
        .baseUrl("https://techibot-api.example.com/") // Updated base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ChatService::class.java)

    fun sendMessage(content: String, token: String) {
        viewModelScope.launch {
            try {
                _chatState.value = ChatState.Loading

                // Add user message to the list
                val userMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = content,
                    isFromUser = true
                )
                _messages.value = _messages.value + userMessage

                // Send message to API
                val response = chatService.sendMessage(
                    "Bearer $token",
                    ChatRequest(content)
                )

                // Add bot response to the list
                val botMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = response.response,
                    isFromUser = false
                )
                _messages.value = _messages.value + botMessage

                _chatState.value = ChatState.Idle
            } catch (e: Exception) {
                _chatState.value = ChatState.Error(e.localizedMessage ?: "خطا در ارسال پیام")
            }
        }
    }
}