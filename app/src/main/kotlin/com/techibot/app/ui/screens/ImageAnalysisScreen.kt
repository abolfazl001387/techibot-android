package com.techibot.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.techibot.app.ui.viewmodel.ImageAnalysisState
import com.techibot.app.ui.viewmodel.ImageAnalysisViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun ImageAnalysisScreen(
    navController: NavController,
    viewModel: ImageAnalysisViewModel = viewModel()
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val analysisState by viewModel.analysisState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let { imageUri ->
            // Convert Uri to File
            val imageFile = File(context.cacheDir, "selected_image.jpg")
            context.contentResolver.openInputStream(imageUri)?.use { input ->
                FileOutputStream(imageFile).use { output ->
                    input.copyTo(output)
                }
            }
            viewModel.analyzeImage(imageFile)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image selection button
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("انتخاب تصویر")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected image preview
        selectedImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "تصویر انتخاب شده",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Analysis result
        when (analysisState) {
            is ImageAnalysisState.Loading -> {
                CircularProgressIndicator()
            }
            is ImageAnalysisState.Success -> {
                Text(
                    text = (analysisState as ImageAnalysisState.Success).analysis,
                    modifier = Modifier.padding(16.dp)
                )
            }
            is ImageAnalysisState.Error -> {
                Text(
                    text = (analysisState as ImageAnalysisState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {}
        }
    }
}
