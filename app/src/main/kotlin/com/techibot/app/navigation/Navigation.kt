package com.techibot.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techibot.app.ui.screens.LoginScreen
import com.techibot.app.ui.screens.ChatScreen
import com.techibot.app.ui.screens.ImageAnalysisScreen
import com.techibot.app.ui.screens.ImageGenerationScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("chat") { ChatScreen(navController) }
        composable("image_analysis") { ImageAnalysisScreen(navController) }
        composable("image_generation") { ImageGenerationScreen(navController) }
    }
}
