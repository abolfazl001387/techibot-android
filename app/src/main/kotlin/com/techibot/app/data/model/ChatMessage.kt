package com.techibot.app.data.model

import java.util.Date

data class ChatMessage(
    val id: String = "",
    val content: String,
    val timestamp: Date = Date(),
    val isFromUser: Boolean = true
)

data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val response: String
)
