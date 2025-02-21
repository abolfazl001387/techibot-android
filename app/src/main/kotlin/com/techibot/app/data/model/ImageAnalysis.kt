package com.techibot.app.data.model

data class ImageAnalysisResponse(
    val analysis: String
)

data class ImageGenerationRequest(
    val prompt: String,
    val style: String = "digital-art"
)

data class ImageGenerationResponse(
    val image: String  // Base64 encoded image
)
