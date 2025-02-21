package com.techibot.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.techibot.app.data.ApiConfig
import com.techibot.app.data.SessionManager
import com.techibot.app.data.model.ImageGenerationRequest
import com.techibot.app.data.model.ImageGenerationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ImageGenerationService {
    @POST(ApiConfig.Endpoints.IMAGE_GENERATION)
    suspend fun generateImage(
        @Header("Authorization") token: String,
        @Body request: ImageGenerationRequest
    ): ImageGenerationResponse
}

sealed class ImageGenerationState {
    object Idle : ImageGenerationState()
    object Loading : ImageGenerationState()
    data class Success(val imageBase64: String) : ImageGenerationState()
    data class Error(val message: String) : ImageGenerationState()
}

class ImageGenerationViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    private val _generationState = MutableStateFlow<ImageGenerationState>(ImageGenerationState.Idle)
    val generationState: StateFlow<ImageGenerationState> = _generationState

    private val imageService = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ImageGenerationService::class.java)

    fun generateImage(prompt: String, style: String = "digital-art") {
        viewModelScope.launch {
            try {
                _generationState.value = ImageGenerationState.Loading

                val token = sessionManager.getAuthToken()
                    ?: throw IllegalStateException("توکن احراز هویت یافت نشد")

                val response = imageService.generateImage(
                    "Bearer $token",
                    ImageGenerationRequest(prompt, style)
                )
                _generationState.value = ImageGenerationState.Success(response.image)
            } catch (e: Exception) {
                _generationState.value = ImageGenerationState.Error(
                    e.localizedMessage ?: "خطا در تولید تصویر"
                )
            }
        }
    }
}