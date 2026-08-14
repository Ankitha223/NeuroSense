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

import java.io.File

@Composable
fun ExistingUserScreen(
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

    /*
     * FaceNet recognizer
     */
    val faceNetRecognizer = remember {
        FaceNetRecognizer(context)
    }

    /*
     * User storage
     */
    val userStorage = remember {
        UserStorage(context)
    }

    /*
     * Close FaceNet when screen is destroyed.
     */
    DisposableEffect(Unit) {

        onDispose {
            faceNetRecognizer.close()
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
                    text = "Camera permission is required",
                    fontSize = 18.sp
                )
            }
        }

        /*
         * Verification result
         */
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

        Button(
            onClick = {

                val capture = imageCapture

                if (capture == null) {
                    return@Button
                }

                isVerifying = true
                verificationMessage = "Verifying face..."

                /*
                 * Temporary file for verification image.
                 */
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
                                 * 1. Load captured image
                                 * --------------------------------
                                 */

                                val bitmap =
                                    BitmapFactory.decodeFile(
                                        file.absolutePath
                                    )

                                if (bitmap == null) {

                                    verificationMessage =
                                        "Could not read captured image."

                                    isVerifying = false
                                    return
                                }

                                /*
                                 * --------------------------------
                                 * 2. Generate new FaceNet embedding
                                 * --------------------------------
                                 */

                                val currentEmbedding =
                                    faceNetRecognizer
                                        .getEmbedding(bitmap)

                                Log.d(
                                    "FaceVerification",
                                    "Current embedding size: ${currentEmbedding.size}"
                                )

                                /*
                                 * --------------------------------
                                 * 3. Get saved embedding
                                 * --------------------------------
                                 */

                                val savedEmbedding =
                                    userStorage
                                        .getFaceEmbedding()

                                Log.d(
                                    "FaceVerification",
                                    "Saved embedding size: ${savedEmbedding.size}"
                                )

                                /*
                                 * --------------------------------
                                 * 4. Check saved user
                                 * --------------------------------
                                 */

                                if (savedEmbedding.size != 128) {

                                    verificationMessage =
                                        "No registered face found."

                                    isVerifying = false
                                    return
                                }

                                /*
                                 * --------------------------------
                                 * 5. Calculate similarity
                                 * --------------------------------
                                 */

                                val similarity =
                                    faceNetRecognizer
                                        .cosineSimilarity(
                                            currentEmbedding,
                                            savedEmbedding
                                        )

                                Log.d(
                                    "FaceVerification",
                                    "Cosine similarity: $similarity"
                                )

                                /*
                                 * --------------------------------
                                 * 6. Authentication threshold
                                 * --------------------------------
                                 *
                                 * We are using 0.70 as a
                                 * starting threshold.
                                 *
                                 * We will tune this after testing.
                                 */

                                val threshold = 0.70f

                                if (similarity >= threshold) {

                                    Log.d(
                                        "FaceVerification",
                                        "FACE MATCH"
                                    )

                                    verificationMessage =
                                        "Face verified successfully!"

                                    /*
                                     * Dashboard will be connected
                                     * here in the next step.
                                     *
                                     * For now, we stay on this screen
                                     * so we can test the similarity.
                                     */

                                } else {

                                    Log.d(
                                        "FaceVerification",
                                        "FACE NOT MATCHED"
                                    )

                                    verificationMessage =
                                        "Face not recognized. Please try again."
                                }

                            } catch (e: Exception) {

                                Log.e(
                                    "FaceVerification",
                                    "Face verification failed",
                                    e
                                )

                                verificationMessage =
                                    "Face verification failed."

                            } finally {

                                isVerifying = false
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