package com.techibot.app.data

object ApiConfig {
    const val BASE_URL = "https://techibot-api.example.com/"
    const val API_TIMEOUT = 30L // seconds
    
    object Endpoints {
        const val LOGIN = "api/auth/token"
        const val CHAT = "api/chat"
        const val IMAGE_ANALYSIS = "api/analyze/image"
        const val IMAGE_GENERATION = "api/generate/image"
    }
}
