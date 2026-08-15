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
import com.example.neurosense.recognition.FaceImageProcessor
import com.example.neurosense.recognition.FaceNetRecognizer
import com.example.neurosense.viewmodel.RegistrationViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

@Composable
fun CameraCaptureScreen(
    navController: NavController,
    viewModel: RegistrationViewModel
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

    var isCapturing by remember {
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

    val faceNetRecognizer = remember {
        FaceNetRecognizer(context)
    }

    DisposableEffect(Unit) {

        onDispose {
            faceProcessor.close()
            faceNetRecognizer.close()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = "Register Face",
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
                    .padding(horizontal = 16.dp)
            )
        }

        Button(
            onClick = {

                val capture = imageCapture

                if (capture == null) {

                    message = "Camera is not ready."

                    return@Button
                }

                isCapturing = true
                message = "Capturing face..."

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

                val file = File(
                    context.filesDir,
                    "face_$userId.jpg"
                )

                val outputOptions =
                    ImageCapture.OutputFileOptions
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

                            scope.launch {

                                try {

                                    Log.d(
                                        "FaceRegistration",
                                        "Image captured."
                                    )

                                    val bitmap =
                                        BitmapFactory.decodeFile(
                                            file.absolutePath
                                        )

                                    if (bitmap == null) {

                                        message =
                                            "Could not read captured image."

                                        isCapturing = false

                                        return@launch
                                    }

                                    message =
                                        "Detecting face..."

                                    /*
                                     * Detect exactly one face
                                     * and crop it.
                                     */
                                    val croppedFace =
                                        faceProcessor.cropFace(
                                            bitmap
                                        )

                                    if (croppedFace == null) {

                                        message =
                                            "Face not detected. Show exactly one face."

                                        Log.d(
                                            "FaceRegistration",
                                            "Face detection failed."
                                        )

                                        isCapturing = false

                                        return@launch
                                    }

                                    Log.d(
                                        "FaceRegistration",
                                        "Face detected successfully."
                                    )

                                    message =
                                        "Generating FaceNet embedding..."

                                    /*
                                     * Generate embedding
                                     * ONLY from cropped face.
                                     */
                                    val embedding =
                                        faceNetRecognizer.getEmbedding(
                                            croppedFace
                                        )

                                    Log.d(
                                        "FaceRegistration",
                                        "Embedding generated."
                                    )

                                    Log.d(
                                        "FaceRegistration",
                                        "Embedding size: ${embedding.size}"
                                    )

                                    if (embedding.size != 128) {

                                        message =
                                            "Invalid FaceNet embedding."

                                        isCapturing = false

                                        return@launch
                                    }

                                    val userStorage =
                                        UserStorage(context)

                                    /*
                                     * Save user information
                                     * locally.
                                     */
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
                                            file.absolutePath
                                    )

                                    /*
                                     * Save 128-dimensional
                                     * FaceNet embedding locally.
                                     */
                                    userStorage.saveFaceEmbedding(
                                        embedding
                                    )

                                    val savedEmbedding =
                                        userStorage
                                            .getFaceEmbedding()

                                    Log.d(
                                        "FaceRegistration",
                                        "Saved embedding size: ${
                                            savedEmbedding?.size ?: 0
                                        }"
                                    )

                                    if (
                                        savedEmbedding == null ||
                                        savedEmbedding.size != 128
                                    ) {

                                        message =
                                            "Failed to save face embedding."

                                        isCapturing = false

                                        return@launch
                                    }

                                    /*
                                     * --------------------------------------------------
                                     * FIREBASE FIRESTORE
                                     * --------------------------------------------------
                                     *
                                     * Save the registered user's basic details
                                     * to Firestore.
                                     */

                                    message =
                                        "Saving user to Firebase..."

                                    val firestore =
                                        FirebaseFirestore
                                            .getInstance()

                                    val userData =
                                        hashMapOf(
                                            "userId" to viewModel.userId,
                                            "name" to viewModel.name,
                                            "age" to viewModel.age,
                                            "gender" to viewModel.gender,
                                            "createdAt" to System.currentTimeMillis()
                                        )

                                    firestore
                                        .collection("users")
                                        .document(viewModel.userId)
                                        .set(userData)
                                        .await()

                                    Log.d(
                                        "FirebaseRegistration",
                                        "User saved successfully."
                                    )

                                    Log.d(
                                        "FirebaseRegistration",
                                        "User ID: ${viewModel.userId}"
                                    )

                                    /*
                                     * Update ViewModel.
                                     */
                                    viewModel.faceImagePath =
                                        file.absolutePath

                                    viewModel.faceCaptured =
                                        true

                                    message =
                                        "Face registered successfully!"

                                    Log.d(
                                        "FaceRegistration",
                                        "SUCCESS: Face registered."
                                    )

                                    isCapturing = false

                                    /*
                                     * Return to registration screen.
                                     */
                                    navController.popBackStack()

                                } catch (e: Exception) {

                                    isCapturing = false

                                    message =
                                        "Registration failed: ${
                                            e.message ?: "Unknown error"
                                        }"

                                    Log.e(
                                        "FaceRegistration",
                                        "Error during registration",
                                        e
                                    )
                                }
                            }
                        }

                        override fun onError(
                            exception: ImageCaptureException
                        ) {

                            isCapturing = false

                            message =
                                "Failed to capture image."

                            Log.e(
                                "FaceRegistration",
                                "Camera capture failed",
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
                        "Capture Face",

                fontSize = 18.sp
            )
        }
    }
}