package com.techibot.app.data.model

data class AuthRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val token: String
)
