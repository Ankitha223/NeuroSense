package com.example.neurosense.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.util.Log

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageCapture.OutputFileOptions
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
import com.example.neurosense.data.UserStorage
import com.example.neurosense.recognition.FaceNetRecognizer
import com.example.neurosense.viewmodel.RegistrationViewModel

import java.io.File


@Composable
fun CameraCaptureScreen(
    navController: NavController,
    viewModel: RegistrationViewModel
) {

    val context = LocalContext.current

    // ==================================================
    // CAMERA PERMISSION
    // ==================================================

    var hasCameraPermission by remember {

        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // ==================================================
    // IMAGE CAPTURE
    // ==================================================

    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }

    var isCapturing by remember {
        mutableStateOf(false)
    }

    // ==================================================
    // CAMERA PERMISSION LAUNCHER
    // ==================================================

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasCameraPermission = granted
        }

    LaunchedEffect(Unit) {

        if (!hasCameraPermission) {

            launcher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    // ==================================================
    // CAMERA PREVIEW
    // ==================================================

    val previewView = remember {
        PreviewView(context)
    }

    // ==================================================
    // FACENET RECOGNIZER
    // ==================================================

    val faceNetRecognizer = remember {
        FaceNetRecognizer(context)
    }

    // Close FaceNet when this screen is destroyed
    DisposableEffect(Unit) {

        onDispose {
            faceNetRecognizer.close()
        }
    }

    // ==================================================
    // UI
    // ==================================================

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // ==================================================
        // CAMERA
        // ==================================================

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

        // ==================================================
        // CAPTURE BUTTON
        // ==================================================

        Button(

            onClick = {

                val capture = imageCapture

                if (capture == null) {
                    return@Button
                }

                isCapturing = true

                // ==================================================
                // GENERATE USER ID
                // ==================================================

                val userId =

                    if (viewModel.userId.isNotEmpty()) {

                        viewModel.userId

                    } else {

                        "NS${
                            System.currentTimeMillis()
                                .toString()
                                .takeLast(6)
                        }"
                    }

                viewModel.userId = userId

                // ==================================================
                // FACE IMAGE FILE
                // ==================================================

                val file = File(
                    context.filesDir,
                    "face_$userId.jpg"
                )

                val outputOptions =
                    OutputFileOptions
                        .Builder(file)
                        .build()

                // ==================================================
                // TAKE PHOTO
                // ==================================================

                capture.takePicture(

                    outputOptions,

                    ContextCompat.getMainExecutor(context),

                    object :
                        ImageCapture.OnImageSavedCallback {

                        // ==================================================
                        // PHOTO SAVED SUCCESSFULLY
                        // ==================================================

                        override fun onImageSaved(
                            outputFileResults:
                            ImageCapture.OutputFileResults
                        ) {

                            try {

                                // ==================================================
                                // 1. SAVE IMAGE PATH
                                // ==================================================

                                viewModel.faceImagePath =
                                    file.absolutePath

                                // ==================================================
                                // 2. LOAD IMAGE
                                // ==================================================

                                val bitmap =
                                    BitmapFactory.decodeFile(
                                        file.absolutePath
                                    )

                                if (bitmap == null) {

                                    Log.e(
                                        "FaceNetTest",
                                        "Could not load captured image"
                                    )

                                    isCapturing = false
                                    return
                                }

                                // ==================================================
                                // 3. GENERATE FACENET EMBEDDING
                                // ==================================================

                                Log.d(
                                    "FaceNetTest",
                                    "Generating FaceNet embedding..."
                                )

                                val embedding =
                                    faceNetRecognizer
                                        .getEmbedding(bitmap)

                                // ==================================================
                                // 4. CHECK EMBEDDING
                                // ==================================================

                                Log.d(
                                    "FaceNetTest",
                                    "FaceNet embedding generated!"
                                )

                                Log.d(
                                    "FaceNetTest",
                                    "Embedding size: ${embedding.size}"
                                )

                                if (embedding.size != 128) {

                                    Log.e(
                                        "FaceNetTest",
                                        "Invalid embedding size: ${embedding.size}"
                                    )

                                    isCapturing = false
                                    return
                                }

                                // ==================================================
                                // 5. SAVE USER + EMBEDDING
                                // ==================================================

                                val userStorage =
                                    UserStorage(context)

                                userStorage.saveUser(

                                    userId =
                                        viewModel.userId,

                                    name =
                                        viewModel.name,

                                    age =
                                        viewModel.age,

                                    gender =
                                        viewModel.gender,

                                    faceImagePath =
                                        viewModel.faceImagePath,

                                    faceEmbedding =
                                        embedding
                                )

                                // ==================================================
                                // 6. READ SAVED EMBEDDING
                                // ==================================================

                                val savedEmbedding = userStorage.getFaceEmbedding()

                                if (savedEmbedding == null) {

                                    Log.e(
                                        "FaceNetTest",
                                        "ERROR: Face embedding was not saved."
                                    )

                                    isCapturing = false
                                    return

                                }

                                Log.d(
                                    "FaceNetTest",
                                    "Saved embedding size: ${savedEmbedding.size}"
                                )

                                if (savedEmbedding.size != 128) {

                                    Log.e(
                                        "FaceNetTest",
                                        "ERROR: Invalid saved embedding size."
                                    )

                                    isCapturing = false
                                    return
                                }

                                Log.d(
                                    "FaceNetTest",
                                    "SUCCESS: Face embedding saved!"
                                )

                                // ==================================================
                                // 8. MARK FACE AS CAPTURED
                                // ==================================================

                                viewModel.faceCaptured = true

                                isCapturing = false

                                // ==================================================
                                // 9. RETURN TO REGISTRATION
                                // ==================================================

                                navController.popBackStack()

                            } catch (e: Exception) {

                                isCapturing = false

                                Log.e(
                                    "FaceNetTest",
                                    "FaceNet processing failed",
                                    e
                                )
                            }
                        }

                        // ==================================================
                        // PHOTO CAPTURE ERROR
                        // ==================================================

                        override fun onError(
                            exception: ImageCaptureException
                        ) {

                            isCapturing = false

                            Log.e(
                                "FaceNetTest",
                                "Image capture failed",
                                exception
                            )
                        }
                    }
                )
            },

            // ==================================================
            // BUTTON ENABLE CONDITION
            // ==================================================

            enabled =
                hasCameraPermission &&
                        imageCapture != null &&
                        !isCapturing,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(55.dp)

        ) {

            Text(
                text =
                    if (isCapturing)
                        "Processing..."
                    else
                        "Capture",

                fontSize = 18.sp
            )
        }
    }
}
