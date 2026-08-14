package com.example.neurosense.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.util.Log

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
import com.example.neurosense.data.UserStorage
import com.example.neurosense.recognition.FaceNetRecognizer

import java.io.File


@Composable
fun ExistingUserCameraScreen(
    navController: NavController
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

    // ==================================================
    // MESSAGE
    // ==================================================

    var message by remember {
        mutableStateOf("Position your face inside the camera")
    }

    // ==================================================
    // VERIFICATION STATE
    // ==================================================

    var isVerifying by remember {
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
    // UI
    // ==================================================

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // ==================================================
        // TITLE
        // ==================================================

        Text(
            text = "Face Verification",
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // ==================================================
        // CAMERA
        // ==================================================

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
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

        // ==================================================
        // MESSAGE
        // ==================================================

        Text(
            text = message,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(8.dp)
        )

        // ==================================================
        // VERIFY BUTTON
        // ==================================================

        Button(

            onClick = {

                val capture = imageCapture

                if (capture == null) {

                    message =
                        "Camera is not ready."

                    return@Button
                }

                isVerifying = true

                message =
                    "Capturing face..."

                // ==================================================
                // TEMPORARY IMAGE FILE
                // ==================================================

                val file = File(
                    context.cacheDir,
                    "verification_face.jpg"
                )

                val outputOptions =
                    ImageCapture.OutputFileOptions
                        .Builder(file)
                        .build()

                // ==================================================
                // CAPTURE IMAGE
                // ==================================================

                capture.takePicture(

                    outputOptions,

                    ContextCompat.getMainExecutor(context),

                    object :
                        ImageCapture.OnImageSavedCallback {

                        // ==================================================
                        // IMAGE SAVED
                        // ==================================================

                        override fun onImageSaved(
                            outputFileResults:
                            ImageCapture.OutputFileResults
                        ) {

                            try {

                                // ==================================================
                                // LOAD CAPTURED IMAGE
                                // ==================================================

                                message =
                                    "Reading captured face..."

                                val bitmap =
                                    BitmapFactory.decodeFile(
                                        file.absolutePath
                                    )

                                if (bitmap == null) {

                                    message =
                                        "Could not read captured image."

                                    isVerifying = false

                                    return
                                }

                                // ==================================================
                                // LOAD SAVED EMBEDDING
                                // ==================================================

                                message =
                                    "Loading registered face..."

                                val userStorage =
                                    UserStorage(context)

                                val savedEmbedding =
                                    userStorage.getFaceEmbedding()

                                // ==================================================
                                // CHECK SAVED EMBEDDING
                                // ==================================================

                                if (savedEmbedding.size != 128) {

                                    Log.e(
                                        "FaceVerification",
                                        "Invalid saved embedding size: ${savedEmbedding.size}"
                                    )

                                    message =
                                        "No valid registered face found."

                                    isVerifying = false

                                    return
                                }

                                // ==================================================
                                // GENERATE CURRENT EMBEDDING
                                // ==================================================

                                message =
                                    "Generating FaceNet embedding..."

                                val faceNet =
                                    FaceNetRecognizer(context)

                                val currentEmbedding =
                                    faceNet.getEmbedding(bitmap)

                                Log.d(
                                    "FaceVerification",
                                    "Current embedding size: ${currentEmbedding.size}"
                                )

                                Log.d(
                                    "FaceVerification",
                                    "Saved embedding size: ${savedEmbedding.size}"
                                )

                                // ==================================================
                                // CHECK CURRENT EMBEDDING
                                // ==================================================

                                if (currentEmbedding.size != 128) {

                                    faceNet.close()

                                    message =
                                        "Face embedding generation failed."

                                    isVerifying = false

                                    return
                                }

                                // ==================================================
                                // COSINE SIMILARITY
                                // ==================================================

                                message =
                                    "Comparing faces..."

                                val similarity =
                                    faceNet.cosineSimilarity(
                                        currentEmbedding,
                                        savedEmbedding
                                    )

                                faceNet.close()

                                // ==================================================
                                // LOG SIMILARITY
                                // ==================================================

                                Log.d(
                                    "FaceVerification",
                                    "Cosine similarity = $similarity"
                                )

                                // ==================================================
                                // FACE MATCH THRESHOLD
                                // ==================================================

                                val threshold = 0.70f

                                Log.d(
                                    "FaceVerification",
                                    "Threshold = $threshold"
                                )

                                // ==================================================
                                // MATCH
                                // ==================================================

                                if (similarity >= threshold) {

                                    Log.d(
                                        "FaceVerification",
                                        "FACE MATCH"
                                    )

                                    message =
                                        "Face verified successfully!"

                                    // Small delay is not necessary;
                                    // navigate directly.
                                    navController.navigate(
                                        "dashboard"
                                    )

                                } else {

                                    Log.d(
                                        "FaceVerification",
                                        "FACE NOT MATCHED"
                                    )

                                    message =
                                        "Face not recognized."
                                }

                            } catch (e: Exception) {

                                Log.e(
                                    "FaceVerification",
                                    "Verification failed",
                                    e
                                )

                                message =
                                    "Verification failed."

                            } finally {

                                isVerifying = false
                            }
                        }

                        // ==================================================
                        // CAPTURE ERROR
                        // ==================================================

                        override fun onError(
                            exception: ImageCaptureException
                        ) {

                            Log.e(
                                "FaceVerification",
                                "Failed to capture face",
                                exception
                            )

                            message =
                                "Failed to capture face."

                            isVerifying = false
                        }
                    }
                )
            },

            enabled =
                hasCameraPermission &&
                        imageCapture != null &&
                        !isVerifying,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(55.dp)

        ) {

            Text(

                text =
                    if (isVerifying)
                        "Verifying..."
                    else
                        "Verify Face",

                fontSize = 18.sp
            )
        }
    }
}