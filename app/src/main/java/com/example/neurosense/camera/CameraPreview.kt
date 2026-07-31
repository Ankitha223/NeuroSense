package com.example.neurosense.camera

import android.annotation.SuppressLint
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraPreview(
    previewView: PreviewView,
    modifier: Modifier = Modifier
) {

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )

}