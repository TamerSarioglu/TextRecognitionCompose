package com.tamersarioglu.textrecognitioncompose

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tamersarioglu.textrecognitioncompose.ui.theme.TextRecognitionComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TextRecognitionComposeTheme {
                TextRecognitionScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextRecognitionScreen() {
    var recognizedText by remember { mutableStateOf("") }
    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    var startCamera by remember { mutableStateOf(false) }
    var pickFromGallery by remember { mutableStateOf(false) }

    val cameraPermissionGranted = remember { mutableStateOf(false) }
    val storagePermissionGranted = remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            cameraPermissionGranted.value = isGranted
            if (!isGranted) {
                Toast.makeText(context, "Camera permission denied.", Toast.LENGTH_SHORT).show()
            } else {
                startCamera = true
            }
        }
    )

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            storagePermissionGranted.value = isGranted
            if (!isGranted) {
                Toast.makeText(context, "Storage permission denied.", Toast.LENGTH_SHORT).show()
            } else {
                pickFromGallery = true
            }
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Text Recognition") })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween // This ensures proper spacing
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Display the image and recognized text
                    bitmap?.let {
                        Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxWidth())
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Recognized Text: $recognizedText", modifier = Modifier.fillMaxWidth())
                }

                // Conditionally invoke Composables based on flags
                if (startCamera && cameraPermissionGranted.value) {
                    CaptureImage { capturedBitmap ->
                        bitmap = capturedBitmap
                        recognizeText(capturedBitmap) { text ->
                            recognizedText = text
                        }
                        startCamera = false
                    }
                }

                if (pickFromGallery && storagePermissionGranted.value) {
                    PickImageFromGallery { pickedBitmap ->
                        bitmap = pickedBitmap
                        recognizeText(pickedBitmap) { text ->
                            recognizedText = text
                        }
                        pickFromGallery = false
                    }
                }

                // Buttons for triggering camera and gallery actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Adds space between buttons
                ) {
                    Button(
                        onClick = {
                            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text("Start Camera and Check Permission")
                    }

                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                storagePermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                            } else {
                                storagePermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text("Pick Image from Gallery")
                    }
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun TextRecognitionScreenPreview() {
    TextRecognitionComposeTheme {
        TextRecognitionScreen()
    }
}