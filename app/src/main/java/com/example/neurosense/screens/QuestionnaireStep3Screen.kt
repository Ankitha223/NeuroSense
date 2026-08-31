package com.example.neurosense.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.neurosense.components.BackButton
import com.example.neurosense.components.QuestionCard
import com.example.neurosense.data.UserStorage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.tasks.await
@Composable
fun QuestionnaireStep3Screen(
    navController: NavController
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val answers = remember {
        mutableStateMapOf<String, Boolean?>()
    }

    val questions = listOf(
        "Do you feel that your symptoms are getting worse?",
        "Do your symptoms affect your daily activities?",
        "Do you feel difficulty controlling your movements?",
        "Do you feel that your coordination has decreased?"
    )

    var message by remember {
        mutableStateOf("")
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // --------------------------------------------------
        // BACK BUTTON
        // --------------------------------------------------

        item {

            BackButton(
                navController = navController
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        // --------------------------------------------------
        // HEADER
        // --------------------------------------------------

        item {

            Text(
                text = "Health Questionnaire",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Step 3 of 3"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Final Assessment",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // --------------------------------------------------
        // QUESTIONS
        // --------------------------------------------------

        item {

            questions.forEach { question ->

                QuestionCard(
                    question = question,
                    selectedAnswer = answers[question],
                    onAnswerSelected = { answer ->

                        answers[question] = answer

                        message = ""
                    }
                )
            }
        }

        // --------------------------------------------------
        // COMPLETE ASSESSMENT
        // --------------------------------------------------

        item {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            if (message.isNotEmpty()) {

                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }

            Button(

                onClick = {

                    val unanswered =
                        questions.any { question ->
                            answers[question] == null
                        }

                    if (unanswered) {

                        message =
                            "Please answer all questions."

                        return@Button
                    }

                    isSaving = true
                    message = ""

                    scope.launch {

                        try {

                            // --------------------------------------------------
                            // GET USER ID
                            // --------------------------------------------------

                            val userStorage =
                                UserStorage(context)

                            val userId =
                                userStorage.getUserId()

                            if (userId.isBlank()) {

                                message =
                                    "User ID not found."

                                isSaving = false

                                return@launch
                            }

                            // --------------------------------------------------
                            // GET QUESTIONNAIRE DATA
                            // --------------------------------------------------

                            val preferences =
                                context.getSharedPreferences(
                                    "neurosense_questionnaire",
                                    Context.MODE_PRIVATE
                                )

                            var score = 0

                            // --------------------------------------------------
                            // STEP 1
                            // --------------------------------------------------

                            val step1Questions = listOf(

                                "Do you experience hand tremors?",
                                "Do you have difficulty maintaining balance while walking?",
                                "Do you feel muscle stiffness?",
                                "Do you experience numbness or tingling?",
                                "Do you have difficulty speaking clearly?",
                                "Do you notice slower body movements than usual?",
                                "Do you experience frequent dizziness?",
                                "Do you have difficulty gripping objects?"
                            )

                            step1Questions.forEach { question ->

                                if (
                                    preferences.getBoolean(
                                        "step1_$question",
                                        false
                                    )
                                ) {
                                    score++
                                }
                            }

                            // --------------------------------------------------
                            // STEP 2
                            // --------------------------------------------------

                            val step2Questions = listOf(

                                "Do you find it difficult to perform daily activities?",
                                "Do you have difficulty walking for a long distance?",
                                "Do you have difficulty getting up from a chair?",
                                "Do you have difficulty holding small objects?",
                                "Do you feel weakness in your limbs?",
                                "Do you experience difficulty coordinating movements?"
                            )

                            step2Questions.forEach { question ->

                                if (
                                    preferences.getBoolean(
                                        "step2_$question",
                                        false
                                    )
                                ) {
                                    score++
                                }
                            }

                            // --------------------------------------------------
                            // STEP 3
                            // --------------------------------------------------

                            questions.forEach { question ->

                                if (answers[question] == true) {
                                    score++
                                }
                            }

                            // --------------------------------------------------
                            // CALCULATE RESULT
                            // --------------------------------------------------

                            val totalQuestions =
                                step1Questions.size +
                                        step2Questions.size +
                                        questions.size

                            val percentage =
                                (score * 100) / totalQuestions

                            val result = when {

                                percentage <= 25 ->
                                    "Low indication"

                                percentage <= 50 ->
                                    "Moderate indication"

                                else ->
                                    "Higher indication"
                            }

                            Log.d(
                                "FirebaseAssessment",
                                "Score: $score/$totalQuestions"
                            )

                            Log.d(
                                "FirebaseAssessment",
                                "Percentage: $percentage%"
                            )

                            Log.d(
                                "FirebaseAssessment",
                                "Result: $result"
                            )

                            // --------------------------------------------------
                            // FIREBASE DATA
                            // --------------------------------------------------

                            val assessmentData =
                                hashMapOf<String, Any>(

                                    "userId" to userId,

                                    "score" to score,

                                    "totalQuestions" to
                                            totalQuestions,

                                    "percentage" to
                                            percentage,

                                    "result" to result,

                                    "timestamp" to
                                            System.currentTimeMillis()
                                )

                            // --------------------------------------------------
                            // SAVE TO FIREBASE
                            // --------------------------------------------------

                            val firebaseSaved =
                                withTimeoutOrNull(5000L) {

                                    FirebaseFirestore
                                        .getInstance()
                                        .collection("users")
                                        .document(userId)
                                        .collection("assessments")
                                        .document("latest")
                                        .set(assessmentData)
                                        .await()

                                    true

                                } ?: false

                            if (firebaseSaved) {

                                Log.d(
                                    "FirebaseAssessment",
                                    "Assessment saved successfully."
                                )

                            } else {

                                Log.w(
                                    "FirebaseAssessment",
                                    "Firebase save timed out or failed. Continuing to dashboard."
                                )
                            }

                            // --------------------------------------------------
                            // CLEAR QUESTIONNAIRE DATA
                            // --------------------------------------------------

                            preferences
                                .edit()
                                .clear()
                                .apply()

                            isSaving = false

                            // --------------------------------------------------
                            // GO TO DASHBOARD
                            // --------------------------------------------------

                            navController.navigate(
                                "dashboard"
                            ) {

                                popUpTo("questionnaire") {
                                    inclusive = true
                                }
                            }

                        } catch (e: Exception) {

                            Log.e(
                                "FirebaseAssessment",
                                "Assessment processing error",
                                e
                            )

                            isSaving = false

                            navController.navigate(
                                "dashboard"
                            ) {

                                popUpTo("questionnaire") {
                                    inclusive = true
                                }
                            }
                        }
                    }
                },

                enabled = !isSaving,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)

            ) {

                Text(
                    text =
                        if (isSaving)
                            "Saving..."
                        else
                            "Complete Assessment",

                    fontSize = 17.sp
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}