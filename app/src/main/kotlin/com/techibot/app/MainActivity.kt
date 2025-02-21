package com.techibot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TechiBotTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainNavigation()
                }
            }
        }
    }
}
