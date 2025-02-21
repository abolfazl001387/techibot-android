package com.techibot.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.techibot.app.data.ApiConfig
import com.techibot.app.data.SessionManager
import com.techibot.app.data.model.AuthRequest
import com.techibot.app.data.model.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

interface AuthService {
    @POST(ApiConfig.Endpoints.LOGIN)
    suspend fun login(@Body request: AuthRequest): AuthResponse
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val authService = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthService::class.java)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = authService.login(AuthRequest(username, password))
                sessionManager.saveAuthToken(response.token)
                _loginState.value = LoginState.Success(response.token)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("خطا در ورود: ${e.localizedMessage}")
            }
        }
    }

    fun checkAuthState(): Boolean {
        return sessionManager.getAuthToken() != null
    }
}