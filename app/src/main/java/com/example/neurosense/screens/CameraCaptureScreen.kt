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
import com.example.neurosense.components.BackButton
import com.example.neurosense.data.UserStorage
import com.example.neurosense.data.UserData
import com.example.neurosense.recognition.FaceImageProcessor
import com.example.neurosense.recognition.FaceNetRecognizer
import com.example.neurosense.viewmodel.RegistrationViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
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

    /*
     * Existing user found during face matching.
     */
    var existingUser by remember {
        mutableStateOf<UserData?>(null)
    }

    /*
     * Show duplicate profile dialog.
     */
    var showReplaceDialog by remember {
        mutableStateOf(false)
    }

    /*
     * Store the newly generated embedding temporarily.
     */
    var pendingEmbedding by remember {
        mutableStateOf<FloatArray?>(null)
    }

    /*
     * Store the newly captured image temporarily.
     */
    var pendingImagePath by remember {
        mutableStateOf<String?>(null)
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasCameraPermission = granted

            if (!granted) {

                message =
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

    val faceProcessor = remember {
        FaceImageProcessor()
    }

    val faceNetRecognizer = remember {
        FaceNetRecognizer(context)
    }

    val userStorage = remember {
        UserStorage(context)
    }

    DisposableEffect(Unit) {

        onDispose {

            faceProcessor.close()
            faceNetRecognizer.close()
        }
    }

    /*
     * --------------------------------------------------
     * REPLACE EXISTING USER
     * --------------------------------------------------
     */

    fun replaceExistingUser() {

        val matchedUser = existingUser
        val embedding = pendingEmbedding
        val imagePath = pendingImagePath

        if (
            matchedUser == null ||
            embedding == null ||
            imagePath == null
        ) {

            message =
                "Unable to replace existing profile."

            return
        }

        scope.launch {

            try {

                isCapturing = true
                message = "Replacing profile..."

                /*
                 * Keep the SAME User ID.
                 */
                val userId =
                    matchedUser.userId

                /*
                 * Update ViewModel.
                 */
                viewModel.userId =
                    userId

                viewModel.faceImagePath =
                    imagePath

                viewModel.faceCaptured =
                    true

                /*
                 * Replace old information.
                 */
                userStorage.updateUser(

                    userId =
                        userId,

                    name =
                        viewModel.name,

                    age =
                        viewModel.age,

                    gender =
                        viewModel.gender,

                    faceImagePath =
                        imagePath,

                    embedding =
                        embedding
                )

                /*
                 * Firebase update.
                 */
                try {

                    val firestore =
                        FirebaseFirestore
                            .getInstance()

                    val userData =
                        hashMapOf<String, Any>(

                            "userId" to
                                    userId,

                            "name" to
                                    viewModel.name,

                            "age" to
                                    viewModel.age,

                            "gender" to
                                    viewModel.gender,

                            "faceImagePath" to
                                    imagePath,

                            "timestamp" to
                                    System.currentTimeMillis()
                        )

                    val firebaseSaved =
                        withTimeoutOrNull(5000L) {

                            firestore
                                .collection("users")
                                .document(userId)
                                .set(userData)
                                .await()

                            true

                        } ?: false

                    if (firebaseSaved) {

                        Log.d(
                            "FirebaseRegistration",
                            "Existing user replaced successfully."
                        )

                    } else {

                        Log.w(
                            "FirebaseRegistration",
                            "Firebase update timed out."
                        )
                    }

                } catch (e: Exception) {

                    Log.e(
                        "FirebaseRegistration",
                        "Firebase update failed.",
                        e
                    )
                }

                message =
                    "Profile replaced successfully!"

                isCapturing = false

                /*
                 * Clear temporary values.
                 */
                pendingEmbedding = null
                pendingImagePath = null
                existingUser = null

                navController.popBackStack()

            } catch (e: Exception) {

                Log.e(
                    "FaceRegistration",
                    "Profile replacement failed.",
                    e
                )

                message =
                    "Failed to replace profile."

                isCapturing = false
            }
        }
    }

    /*
     * --------------------------------------------------
     * UI
     * --------------------------------------------------
     */

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        /*
         * --------------------------------------------------
         * BACK BUTTON + TITLE
         * --------------------------------------------------
         */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            BackButton(
                navController = navController
            )

            Text(
                text = "Register Face",
                fontSize = 24.sp
            )
        }

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

                val capture =
                    imageCapture

                if (capture == null) {

                    message =
                        "Camera is not ready."

                    return@Button
                }

                isCapturing = true

                message =
                    "Capturing face..."

                /*
                 * Do NOT generate a User ID yet.
                 *
                 * We first check whether
                 * this face already exists.
                 */

                val temporaryId =
                    "temp_${System.currentTimeMillis()}"

                val file =
                    File(
                        context.filesDir,
                        "face_$temporaryId.jpg"
                    )

                val outputOptions =
                    ImageCapture
                        .OutputFileOptions
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

                            scope.launch {

                                try {

                                    Log.d(
                                        "FaceRegistration",
                                        "Image captured."
                                    )

                                    message =
                                        "Detecting face..."

                                    val bitmap =
                                        BitmapFactory
                                            .decodeFile(
                                                file.absolutePath
                                            )

                                    if (bitmap == null) {

                                        message =
                                            "Could not read captured image."

                                        isCapturing = false

                                        return@launch
                                    }

                                    /*
                                     * --------------------------------------------------
                                     * FACE DETECTION
                                     * --------------------------------------------------
                                     */

                                    val croppedFace =
                                        faceProcessor
                                            .cropFace(bitmap)

                                    if (croppedFace == null) {

                                        message =
                                            "Face not detected. Show exactly one face."

                                        isCapturing = false

                                        return@launch
                                    }

                                    Log.d(
                                        "FaceRegistration",
                                        "Face detected successfully."
                                    )

                                    message =
                                        "Generating face embedding..."

                                    /*
                                     * --------------------------------------------------
                                     * FACE EMBEDDING
                                     * --------------------------------------------------
                                     */

                                    val embedding =
                                        faceNetRecognizer
                                            .getEmbedding(
                                                croppedFace
                                            )

                                    if (embedding.size != 128) {

                                        message =
                                            "Invalid FaceNet embedding."

                                        isCapturing = false

                                        return@launch
                                    }

                                    Log.d(
                                        "FaceRegistration",
                                        "Embedding generated: ${embedding.size}"
                                    )

                                    /*
                                     * --------------------------------------------------
                                     * CHECK EXISTING FACES
                                     * --------------------------------------------------
                                     */

                                    message =
                                        "Checking existing profiles..."

                                    val allUsers =
                                        userStorage
                                            .getAllUsers()

                                    var matchedUser:
                                            UserData? = null

                                    var highestSimilarity =
                                        0f

                                    for (user in allUsers) {

                                        val storedEmbedding =
                                            user.faceEmbedding

                                        if (
                                            storedEmbedding != null &&
                                            storedEmbedding.size == 128
                                        ) {

                                            val similarity =
                                                faceNetRecognizer
                                                    .cosineSimilarity(
                                                        embedding,
                                                        storedEmbedding
                                                    )

                                            Log.d(
                                                "FaceMatching",
                                                "User ${user.userId} similarity = $similarity"
                                            )

                                            if (
                                                similarity >
                                                highestSimilarity
                                            ) {

                                                highestSimilarity =
                                                    similarity

                                                matchedUser =
                                                    user
                                            }
                                        }
                                    }

                                    /*
                                     * --------------------------------------------------
                                     * FACE MATCH THRESHOLD
                                     * --------------------------------------------------
                                     */

                                    val FACE_MATCH_THRESHOLD =
                                        0.70f

                                    if (
                                        matchedUser != null &&
                                        highestSimilarity >=
                                        FACE_MATCH_THRESHOLD
                                    ) {

                                        /*
                                         * Existing profile found.
                                         *
                                         * DON'T SAVE YET.
                                         */

                                        Log.d(
                                            "FaceMatching",
                                            "Existing face found: ${matchedUser!!.userId}"
                                        )

                                        Log.d(
                                            "FaceMatching",
                                            "Similarity: $highestSimilarity"
                                        )

                                        pendingEmbedding =
                                            embedding

                                        pendingImagePath =
                                            file.absolutePath

                                        existingUser =
                                            matchedUser

                                        isCapturing = false

                                        message =
                                            ""

                                        showReplaceDialog =
                                            true

                                    } else {

                                        /*
                                         * --------------------------------------------------
                                         * NEW USER
                                         * --------------------------------------------------
                                         */

                                        Log.d(
                                            "FaceMatching",
                                            "No matching face found."
                                        )

                                        val newUserId =
                                            if (
                                                viewModel.userId.isNotEmpty()
                                            ) {

                                                viewModel.userId

                                            } else {

                                                "NS${
                                                    System.currentTimeMillis()
                                                        .toString()
                                                        .takeLast(6)
                                                }"
                                            }

                                        viewModel.userId =
                                            newUserId

                                        userStorage.saveUser(

                                            userId =
                                                newUserId,

                                            name =
                                                viewModel.name,

                                            age =
                                                viewModel.age,

                                            gender =
                                                viewModel.gender,

                                            faceImagePath =
                                                file.absolutePath
                                        )

                                        userStorage.saveFaceEmbedding(
                                            embedding
                                        )

                                        viewModel.faceImagePath =
                                            file.absolutePath

                                        viewModel.faceCaptured =
                                            true

                                        message =
                                            "Face registered successfully!"

                                        /*
                                         * --------------------------------------------------
                                         * FIREBASE SAVE
                                         * --------------------------------------------------
                                         */

                                        scope.launch {

                                            try {

                                                val firestore =
                                                    FirebaseFirestore
                                                        .getInstance()

                                                val userData =
                                                    hashMapOf<String, Any>(

                                                        "userId" to
                                                                newUserId,

                                                        "name" to
                                                                viewModel.name,

                                                        "age" to
                                                                viewModel.age,

                                                        "gender" to
                                                                viewModel.gender,

                                                        "faceImagePath" to
                                                                file.absolutePath,

                                                        "timestamp" to
                                                                System.currentTimeMillis()
                                                    )

                                                val firebaseSaved =
                                                    withTimeoutOrNull(
                                                        5000L
                                                    ) {

                                                        firestore
                                                            .collection(
                                                                "users"
                                                            )
                                                            .document(
                                                                newUserId
                                                            )
                                                            .set(
                                                                userData
                                                            )
                                                            .await()

                                                        true

                                                    } ?: false

                                                if (firebaseSaved) {

                                                    Log.d(
                                                        "FirebaseRegistration",
                                                        "User saved successfully."
                                                    )

                                                } else {

                                                    Log.w(
                                                        "FirebaseRegistration",
                                                        "Firebase save timed out."
                                                    )
                                                }

                                            } catch (e: Exception) {

                                                Log.e(
                                                    "FirebaseRegistration",
                                                    "Firebase save failed.",
                                                    e
                                                )
                                            }
                                        }

                                        isCapturing = false

                                        navController
                                            .popBackStack()
                                    }

                                } catch (e: Exception) {

                                    Log.e(
                                        "FaceRegistration",
                                        "Face registration failed.",
                                        e
                                    )

                                    message =
                                        "Face registration failed."

                                    isCapturing = false
                                }
                            }
                        }

                        override fun onError(
                            exception:
                            ImageCaptureException
                        ) {

                            isCapturing = false

                            message =
                                "Failed to capture image."

                            Log.e(
                                "FaceRegistration",
                                "Camera capture failed.",
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

    /*
     * --------------------------------------------------
     * EXISTING PROFILE DIALOG
     * --------------------------------------------------
     */

    if (showReplaceDialog) {

        AlertDialog(

            onDismissRequest = {

                /*
                 * Same as Cancel.
                 */
                showReplaceDialog =
                    false

                pendingEmbedding = null
                pendingImagePath = null
                existingUser = null

                message =
                    "Profile was not changed."
            },

            title = {

                Text(
                    text =
                        "Existing profile found"
                )
            },

            text = {

                Text(
                    text =
                        "A profile with this face already exists.\n\n" +
                                "Do you want to replace the old information " +
                                "with the new details?"
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        showReplaceDialog =
                            false

                        replaceExistingUser()
                    }

                ) {

                    Text(
                        text = "Replace"
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showReplaceDialog =
                            false

                        pendingEmbedding = null
                        pendingImagePath = null
                        existingUser = null

                        message =
                            "Profile was not changed."
                    }

                ) {

                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }
}