package com.tamersarioglu.textrecognitioncompose

import android.app.Application
import android.graphics.Bitmap
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

fun recognizeText(bitmap: Bitmap, onResult: (String) -> Unit) {

    val inputImage = InputImage.fromBitmap(bitmap, 0)
    val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )

    recognizer.process(inputImage)
        .addOnSuccessListener { visionText ->
            onResult(visionText.text)
        }
        .addOnFailureListener { exception ->
            // Handle error
            Toast.makeText(
                Application(),
                "Error: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
}