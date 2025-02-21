package com.techibot.app.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.techibot.app.ui.viewmodel.ImageGenerationState
import com.techibot.app.ui.viewmodel.ImageGenerationViewModel

@Composable
fun ImageGenerationScreen(
    navController: NavController,
    viewModel: ImageGenerationViewModel = viewModel()
) {
    var prompt by remember { mutableStateOf("") }
    var style by remember { mutableStateOf("digital-art") }
    val generationState by viewModel.generationState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Prompt input
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("توضیحات تصویر مورد نظر") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Style selection
        DropdownMenu(
            expanded = false,
            onDismissRequest = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownMenuItem(
                text = { Text("دیجیتال آرت") },
                onClick = { style = "digital-art" }
            )
            DropdownMenuItem(
                text = { Text("نقاشی") },
                onClick = { style = "painting" }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Generate button
        Button(
            onClick = {
                if (prompt.isNotBlank()) {
                    viewModel.generateImage(prompt, style)
                }
            },
            enabled = prompt.isNotBlank() && generationState !is ImageGenerationState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("تولید تصویر")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Generated image and status
        when (generationState) {
            is ImageGenerationState.Loading -> {
                CircularProgressIndicator()
            }
            is ImageGenerationState.Success -> {
                val imageBase64 = (generationState as ImageGenerationState.Success).imageBase64
                val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "تصویر تولید شده",
                    modifier = Modifier
                        .size(300.dp)
                        .padding(16.dp)
                )
            }
            is ImageGenerationState.Error -> {
                Text(
                    text = (generationState as ImageGenerationState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {}
        }
    }
}
