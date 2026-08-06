package com.example.neurosense.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.neurosense.viewmodel.RegistrationViewModel
import androidx.camera.core.ImageCapture
import android.os.Environment
import java.io.File
import androidx.camera.core.ImageCaptureException
@Composable
fun CameraCaptureScreen(
    navController: NavController,
    viewModel: RegistrationViewModel
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
    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

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
                    text = "Waiting for camera permission...",
                    fontSize = 18.sp
                )

            }

        }

        Button(

            onClick = {

                val imageCapture = imageCapture ?: return@Button

                val photoFile = File(

                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),

                    "Face_${System.currentTimeMillis()}.jpg"

                )

                val outputOptions =
                    ImageCapture.OutputFileOptions.Builder(photoFile).build()

                imageCapture.takePicture(

                    outputOptions,

                    ContextCompat.getMainExecutor(context),

                    object : ImageCapture.OnImageSavedCallback {

                        override fun onImageSaved(

                            outputFileResults: ImageCapture.OutputFileResults

                        ) {

                            viewModel.faceCaptured = true

                            viewModel.userId =
                                "NS${System.currentTimeMillis().toString().takeLast(6)}"
                            viewModel.faceImagePath = photoFile.absolutePath

                            navController.popBackStack()

                        }

                        override fun onError(

                            exception: ImageCaptureException

                        ) {

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
                text = "Capture",
                fontSize = 18.sp
            )

        }

    }
}