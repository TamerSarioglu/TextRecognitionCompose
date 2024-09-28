package com.tamersarioglu.textrecognitioncompose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun PickImageFromGallery(
    onImagePicked: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val bitmap = BitmapFactory.decodeStream(
                    context.contentResolver.openInputStream(uri)
                )
                onImagePicked(bitmap)
            }
        }
    )

    Button(onClick = { galleryLauncher.launch("image/*") }) {
        Text("Pick Image from Gallery")
    }
}