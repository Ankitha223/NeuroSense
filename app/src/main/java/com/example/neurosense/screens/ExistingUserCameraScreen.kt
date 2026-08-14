package com.example.neurosense.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.example.neurosense.recognition.FaceImageProcessor
import com.example.neurosense.recognition.FaceNetRecognizer
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ExistingUserCameraScreen(
    navController: NavController
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    var isVerifying by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasCameraPermission = granted

            if (!granted) {
                message = "Camera permission is required."
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

    val faceProcessor = remember {
        FaceImageProcessor()
    }

    val faceNet = remember {
        FaceNetRecognizer(context)
    }

    DisposableEffect(Unit) {

        onDispose {
            faceProcessor.close()
            faceNet.close()
        }
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

        if (message.isNotEmpty()) {

            Text(
                text = message,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        Button(
            onClick = {

                val capture = imageCapture

                if (capture == null) {

                    message = "Camera is not ready."

                    return@Button
                }

                if (isVerifying) {
                    return@Button
                }

                isVerifying = true
                message = "Capturing face..."

                capture.takePicture(
                    ContextCompat.getMainExecutor(context),

                    object :
                        ImageCapture.OnImageCapturedCallback() {

                        override fun onCaptureSuccess(
                            image:
                            androidx.camera.core.ImageProxy
                        ) {

                            scope.launch {

                                try {

                                    message =
                                        "Processing face..."

                                    /*
                                     * Convert CameraX image
                                     * to Bitmap.
                                     */
                                    val bitmap =
                                        imageProxyToBitmap(image)

                                    image.close()

                                    if (bitmap == null) {

                                        message =
                                            "Could not read camera image."

                                        isVerifying = false

                                        return@launch
                                    }

                                    Log.d(
                                        "FaceVerification",
                                        "Camera image received."
                                    )

                                    /*
                                     * Detect exactly ONE face
                                     * and crop it.
                                     */
                                    message =
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

                                        message =
                                            "Face not detected. Show exactly one face."

                                        isVerifying = false

                                        return@launch
                                    }

                                    Log.d(
                                        "FaceVerification",
                                        "Face detected successfully."
                                    )

                                    /*
                                     * Generate FaceNet embedding
                                     * from ONLY the cropped face.
                                     */
                                    message =
                                        "Generating FaceNet embedding..."

                                    val currentEmbedding =
                                        faceNet.getEmbedding(
                                            croppedFace
                                        )

                                    Log.d(
                                        "FaceVerification",
                                        "Current embedding size: ${currentEmbedding.size}"
                                    )

                                    if (currentEmbedding.size != 128) {

                                        message =
                                            "Invalid face embedding."

                                        isVerifying = false

                                        return@launch
                                    }

                                    /*
                                     * Get registered embedding.
                                     */
                                    val userStorage =
                                        UserStorage(context)

                                    val savedEmbedding =
                                        userStorage.getFaceEmbedding()

                                    /*
                                     * IMPORTANT:
                                     * savedEmbedding is nullable.
                                     */
                                    if (
                                        savedEmbedding == null ||
                                        savedEmbedding.size != 128
                                    ) {

                                        Log.e(
                                            "FaceVerification",
                                            "No valid registered embedding found."
                                        )

                                        message =
                                            "No registered face found."

                                        isVerifying = false

                                        return@launch
                                    }

                                    Log.d(
                                        "FaceVerification",
                                        "Saved embedding size: ${savedEmbedding.size}"
                                    )

                                    /*
                                     * Calculate cosine similarity.
                                     */
                                    val similarity =
                                        faceNet.cosineSimilarity(
                                            currentEmbedding,
                                            savedEmbedding
                                        )

                                    Log.d(
                                        "FaceVerification",
                                        "Cosine similarity: $similarity"
                                    )

                                    /*
                                     * FaceNet threshold.
                                     *
                                     * Start with 0.70.
                                     * We will adjust after testing.
                                     */
                                    val threshold = 0.70f

                                    if (similarity >= threshold) {

                                        Log.d(
                                            "FaceVerification",
                                            "FACE MATCH"
                                        )

                                        message =
                                            "Face verified successfully!"

                                        isVerifying = false

                                        /*
                                         * Go to next screen.
                                         *
                                         * Change "questionnaire"
                                         * if your next screen
                                         * has another route.
                                         */
                                        navController.navigate(
                                            "questionnaire"
                                        )

                                    } else {

                                        Log.d(
                                            "FaceVerification",
                                            "FACE NOT MATCHED"
                                        )

                                        message =
                                            "Face verification failed."

                                        isVerifying = false
                                    }

                                } catch (e: Exception) {

                                    try {
                                        image.close()
                                    } catch (_: Exception) {
                                    }

                                    Log.e(
                                        "FaceVerification",
                                        "Verification error",
                                        e
                                    )

                                    message =
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
                                "Camera capture failed",
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

/*
 * Converts CameraX ImageProxy to Bitmap.
 *
 * This version expects an RGBA image.
 */
private fun imageProxyToBitmap(
    image: androidx.camera.core.ImageProxy
): Bitmap? {

    return try {

        val plane = image.planes[0]

        val buffer = plane.buffer

        val bytes = ByteArray(
            buffer.remaining()
        )

        buffer.get(bytes)

        BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size
        )

    } catch (e: Exception) {

        Log.e(
            "FaceVerification",
            "Bitmap conversion failed",
            e
        )

        null
    }
}