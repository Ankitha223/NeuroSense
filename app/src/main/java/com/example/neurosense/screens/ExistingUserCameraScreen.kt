package com.example.neurosense.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.neurosense.camera.CameraPreview

@Composable
fun ExistingUserCameraScreen(
    navController: NavController
) {

    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }

    var message by remember {
        mutableStateOf("")
    }

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasCameraPermission = granted
        }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    val previewView = remember {
        PreviewView(context)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = "Face Verification",
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {

            if (hasCameraPermission) {

                CameraPreview(
                    previewView = previewView,
                    modifier = Modifier.fillMaxSize(),
                    onImageCaptureReady = {
                        imageCapture = it
                    }
                )

            } else {

                Text(
                    text = "Camera permission required",
                    fontSize = 18.sp
                )
            }
        }

        if (message.isNotEmpty()) {

            Text(
                text = message,
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp)
            )
        }

        Button(
            onClick = {

                val capture = imageCapture

                if (capture == null) {
                    message = "Camera is not ready."
                    return@Button
                }

                message = "Capturing face..."

                capture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {

                        override fun onCaptureSuccess(
                            image: androidx.camera.core.ImageProxy
                        ) {

                            image.close()

                            message = "Face captured. Verification will be added next."

                        }

                        override fun onError(
                            exception: ImageCaptureException
                        ) {

                            message = "Failed to capture face."

                            exception.printStackTrace()
                        }
                    }
                )
            },

            enabled = hasCameraPermission,

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(55.dp)
        ) {

            Text(
                text = "Capture Face",
                fontSize = 18.sp
            )
        }
    }
}