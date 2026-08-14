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
import com.example.neurosense.recognition.FaceImageProcessor
import com.example.neurosense.recognition.FaceNetRecognizer
import java.io.File
import kotlinx.coroutines.launch

@Composable
fun ExistingUserScreen(
    navController: NavController
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --------------------------------------------------
    // CAMERA PERMISSION
    // --------------------------------------------------

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // --------------------------------------------------
    // CAMERA
    // --------------------------------------------------

    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }

    var isVerifying by remember {
        mutableStateOf(false)
    }

    var verificationMessage by remember {
        mutableStateOf("")
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasCameraPermission = granted

            if (!granted) {
                verificationMessage =
                    "Camera permission is required."
            }
        }

    LaunchedEffect(Unit) {

        if (!hasCameraPermission) {

            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    val previewView = remember {
        PreviewView(context)
    }

    // --------------------------------------------------
    // FACENET
    // --------------------------------------------------

    val faceNetRecognizer = remember {
        FaceNetRecognizer(context)
    }

    // --------------------------------------------------
    // FACE DETECTOR / CROPPER
    // --------------------------------------------------

    val faceProcessor = remember {
        FaceImageProcessor()
    }

    // --------------------------------------------------
    // USER STORAGE
    // --------------------------------------------------

    val userStorage = remember {
        UserStorage(context)
    }

    // --------------------------------------------------
    // RELEASE RESOURCES
    // --------------------------------------------------

    DisposableEffect(Unit) {

        onDispose {

            faceNetRecognizer.close()
            faceProcessor.close()
        }
    }

    // --------------------------------------------------
    // UI
    // --------------------------------------------------

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

        // --------------------------------------------------
        // CAMERA PREVIEW
        // --------------------------------------------------

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
                    text = "Camera permission is required",
                    fontSize = 18.sp
                )
            }
        }

        // --------------------------------------------------
        // MESSAGE
        // --------------------------------------------------

        if (verificationMessage.isNotEmpty()) {

            Text(
                text = verificationMessage,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
            )
        }

        // --------------------------------------------------
        // VERIFY BUTTON
        // --------------------------------------------------

        Button(

            onClick = {

                val capture = imageCapture

                if (capture == null) {

                    verificationMessage =
                        "Camera is not ready."

                    return@Button
                }

                if (isVerifying) {
                    return@Button
                }

                isVerifying = true

                verificationMessage =
                    "Capturing face..."

                // --------------------------------------------------
                // TEMPORARY IMAGE FILE
                // --------------------------------------------------

                val file = File(
                    context.cacheDir,
                    "verification_face.jpg"
                )

                val outputOptions =
                    OutputFileOptions
                        .Builder(file)
                        .build()

                capture.takePicture(

                    outputOptions,

                    ContextCompat.getMainExecutor(
                        context
                    ),

                    object :
                        ImageCapture.OnImageSavedCallback {

                        override fun onImageSaved(
                            outputFileResults:
                            ImageCapture.OutputFileResults
                        ) {

                            // Move processing to coroutine
                            scope.launch {

                                try {

                                    // --------------------------------------------------
                                    // 1. LOAD IMAGE
                                    // --------------------------------------------------

                                    verificationMessage =
                                        "Reading captured image..."

                                    val bitmap =
                                        BitmapFactory.decodeFile(
                                            file.absolutePath
                                        )

                                    if (bitmap == null) {

                                        verificationMessage =
                                            "Could not read captured image."

                                        isVerifying = false

                                        return@launch
                                    }

                                    Log.d(
                                        "FaceVerification",
                                        "Captured image loaded."
                                    )

                                    // --------------------------------------------------
                                    // 2. DETECT + CROP EXACTLY ONE FACE
                                    // --------------------------------------------------

                                    verificationMessage =
                                        "Detecting face..."

                                    val croppedFace =
                                        faceProcessor.cropFace(
                                            bitmap
                                        )

                                    if (croppedFace == null) {

                                        Log.d(
                                            "FaceVerification",
                                            "No face or multiple faces detected."
                                        )

                                        verificationMessage =
                                            "Face not detected. Show exactly one face."

                                        isVerifying = false

                                        return@launch
                                    }

                                    Log.d(
                                        "FaceVerification",
                                        "Exactly one face detected."
                                    )

                                    // --------------------------------------------------
                                    // 3. GENERATE CURRENT FACENET EMBEDDING
                                    // --------------------------------------------------

                                    verificationMessage =
                                        "Generating face embedding..."

                                    val currentEmbedding =
                                        faceNetRecognizer.getEmbedding(
                                            croppedFace
                                        )

                                    Log.d(
                                        "FaceVerification",
                                        "Current embedding size: ${currentEmbedding.size}"
                                    )

                                    if (currentEmbedding.size != 128) {

                                        verificationMessage =
                                            "Invalid face embedding."

                                        isVerifying = false

                                        return@launch
                                    }

                                    // --------------------------------------------------
                                    // 4. GET REGISTERED EMBEDDING
                                    // --------------------------------------------------

                                    verificationMessage =
                                        "Checking registered face..."

                                    val savedEmbedding =
                                        userStorage.getFaceEmbedding()

                                    // IMPORTANT:
                                    // FloatArray? -> check null first
                                    if (savedEmbedding == null) {

                                        Log.e(
                                            "FaceVerification",
                                            "No registered embedding found."
                                        )

                                        verificationMessage =
                                            "No registered face found."

                                        isVerifying = false

                                        return@launch
                                    }

                                    if (savedEmbedding.size != 128) {

                                        Log.e(
                                            "FaceVerification",
                                            "Invalid saved embedding size: ${savedEmbedding.size}"
                                        )

                                        verificationMessage =
                                            "Invalid registered face data."

                                        isVerifying = false

                                        return@launch
                                    }

                                    Log.d(
                                        "FaceVerification",
                                        "Saved embedding size: ${savedEmbedding.size}"
                                    )

                                    // --------------------------------------------------
                                    // 5. COSINE SIMILARITY
                                    // --------------------------------------------------

                                    val similarity =
                                        faceNetRecognizer.cosineSimilarity(
                                            currentEmbedding,
                                            savedEmbedding
                                        )

                                    Log.d(
                                        "FaceVerification",
                                        "Cosine similarity: $similarity"
                                    )

                                    // --------------------------------------------------
                                    // 6. THRESHOLD
                                    // --------------------------------------------------

                                    val threshold = 0.70f

                                    Log.d(
                                        "FaceVerification",
                                        "Threshold: $threshold"
                                    )

                                    // --------------------------------------------------
                                    // 7. VERIFY
                                    // --------------------------------------------------

                                    if (similarity >= threshold) {

                                        Log.d(
                                            "FaceVerification",
                                            "FACE MATCH"
                                        )

                                        verificationMessage =
                                            "Face verified successfully!"

                                        isVerifying = false

                                        // --------------------------------------------------
                                        // 8. GO TO NEXT SCREEN
                                        // --------------------------------------------------

                                        navController.navigate(
                                            "questionnaire"
                                        )

                                    } else {

                                        Log.d(
                                            "FaceVerification",
                                            "FACE NOT MATCHED"
                                        )

                                        verificationMessage =
                                            "Face not recognized. Please try again."

                                        isVerifying = false
                                    }

                                } catch (e: Exception) {

                                    Log.e(
                                        "FaceVerification",
                                        "Face verification failed",
                                        e
                                    )

                                    verificationMessage =
                                        "Face verification failed."

                                    isVerifying = false
                                }
                            }
                        }

                        override fun onError(
                            exception: ImageCaptureException
                        ) {

                            Log.e(
                                "FaceVerification",
                                "Image capture failed",
                                exception
                            )

                            verificationMessage =
                                "Unable to capture face."

                            isVerifying = false
                        }
                    }
                )
            },

            enabled =
                hasCameraPermission &&
                        imageCapture != null &&
                        !isVerifying,

            modifier = Modifier
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