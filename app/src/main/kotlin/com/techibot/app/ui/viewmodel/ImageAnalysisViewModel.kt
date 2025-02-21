package com.techibot.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.techibot.app.data.ApiConfig
import com.techibot.app.data.SessionManager
import com.techibot.app.data.model.ImageAnalysisResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File

interface ImageService {
    @Multipart
    @POST(ApiConfig.Endpoints.IMAGE_ANALYSIS)
    suspend fun analyzeImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): ImageAnalysisResponse
}

sealed class ImageAnalysisState {
    object Idle : ImageAnalysisState()
    object Loading : ImageAnalysisState()
    data class Success(val analysis: String) : ImageAnalysisState()
    data class Error(val message: String) : ImageAnalysisState()
}

class ImageAnalysisViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    private val _analysisState = MutableStateFlow<ImageAnalysisState>(ImageAnalysisState.Idle)
    val analysisState: StateFlow<ImageAnalysisState> = _analysisState

    private val imageService = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ImageService::class.java)

    fun analyzeImage(imageFile: File) {
        viewModelScope.launch {
            try {
                _analysisState.value = ImageAnalysisState.Loading

                val token = sessionManager.getAuthToken()
                    ?: throw IllegalStateException("توکن احراز هویت یافت نشد")

                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

                val response = imageService.analyzeImage("Bearer $token", body)
                _analysisState.value = ImageAnalysisState.Success(response.analysis)
            } catch (e: Exception) {
                _analysisState.value = ImageAnalysisState.Error(
                    e.localizedMessage ?: "خطا در آنالیز تصویر"
                )
            }
        }
    }
}