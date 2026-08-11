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

    var isCapturing by remember {
        mutableStateOf(false)
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

    /*
     * FaceNet recognizer
     */
    val faceNetRecognizer = remember {
        FaceNetRecognizer(context)
    }

    /*
     * Close FaceNet when this screen leaves memory.
     */
    DisposableEffect(Unit) {

        onDispose {
            faceNetRecognizer.close()
        }
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

                val capture = imageCapture

                if (capture == null) {
                    return@Button
                }

                isCapturing = true

                /*
                 * Generate User ID if one doesn't already exist.
                 */
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

                /*
                 * File where the actual camera photo is saved.
                 */
                val file = File(
                    context.filesDir,
                    "face_$userId.jpg"
                )

                val outputOptions =
                    OutputFileOptions
                        .Builder(file)
                        .build()

                capture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),

                    object :
                        ImageCapture.OnImageSavedCallback {

                        override fun onImageSaved(
                            outputFileResults:
                            ImageCapture.OutputFileResults
                        ) {

                            try {

                                /*
                                 * --------------------------------
                                 * 1. Save image path
                                 * --------------------------------
                                 */

                                viewModel.faceImagePath =
                                    file.absolutePath

                                /*
                                 * --------------------------------
                                 * 2. Load captured image
                                 * --------------------------------
                                 */

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

                                /*
                                 * --------------------------------
                                 * 3. Generate FaceNet embedding
                                 * --------------------------------
                                 */

                                val embedding =
                                    faceNetRecognizer
                                        .getEmbedding(bitmap)

                                /*
                                 * --------------------------------
                                 * 4. Verify embedding
                                 * --------------------------------
                                 */

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

                                /*
                                 * --------------------------------
                                 * 5. Save user + embedding
                                 * --------------------------------
                                 */

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

                                /*
                                 * --------------------------------
                                 * 6. Verify that it was saved
                                 * --------------------------------
                                 */

                                val savedEmbedding =
                                    userStorage
                                        .getFaceEmbedding()

                                Log.d(
                                    "FaceNetTest",
                                    "Saved embedding size: ${savedEmbedding.size}"
                                )

                                if (savedEmbedding.size == 128) {

                                    Log.d(
                                        "FaceNetTest",
                                        "SUCCESS: Face embedding saved!"
                                    )

                                } else {

                                    Log.e(
                                        "FaceNetTest",
                                        "ERROR: Face embedding was not saved correctly."
                                    )
                                }

                                /*
                                 * --------------------------------
                                 * 7. Mark face as captured
                                 * --------------------------------
                                 */

                                viewModel.faceCaptured = true

                                isCapturing = false

                                /*
                                 * --------------------------------
                                 * 8. Return to registration
                                 * --------------------------------
                                 */

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

            enabled =
                hasCameraPermission &&
                        imageCapture != null &&
                        !isCapturing,

            modifier = Modifier
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