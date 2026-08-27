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

@Composable
fun QuestionnaireScreen(
    navController: NavController
) {

    val context = LocalContext.current

    val answers = remember {
        mutableStateMapOf<String, Boolean?>()
    }

    val questions = listOf(
        "Do you experience hand tremors?",
        "Do you have difficulty maintaining balance while walking?",
        "Do you feel muscle stiffness?",
        "Do you experience numbness or tingling?",
        "Do you have difficulty speaking clearly?",
        "Do you notice slower body movements than usual?",
        "Do you experience frequent dizziness?",
        "Do you have difficulty gripping objects?"
    )

    var message by remember {
        mutableStateOf("")
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
                progress = { 0.33f },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Step 1 of 3"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Neurological Symptoms",
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

                        Log.d(
                            "Questionnaire",
                            "$question = $answer"
                        )
                    }
                )
            }
        }

        // --------------------------------------------------
        // NEXT BUTTON
        // --------------------------------------------------

        item {

            Spacer(modifier = Modifier.height(12.dp))

            if (message.isNotEmpty()) {

                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {

                    Log.d(
                        "Questionnaire",
                        "Next button clicked"
                    )

                    Log.d(
                        "Questionnaire",
                        "Answered: ${answers.size}/${questions.size}"
                    )

                    val unansweredQuestions =
                        questions.filter { question ->
                            answers[question] == null
                        }

                    if (unansweredQuestions.isNotEmpty()) {

                        message =
                            "Please answer all questions. " +
                                    "${unansweredQuestions.size} question(s) remaining."

                        Log.d(
                            "Questionnaire",
                            "Unanswered: $unansweredQuestions"
                        )

                        return@Button
                    }

                    /*
                     * Save Step 1 answers.
                     */
                    val preferences =
                        context.getSharedPreferences(
                            "neurosense_questionnaire",
                            Context.MODE_PRIVATE
                        )

                    val editor = preferences.edit()

                    questions.forEach { question ->

                        val answer =
                            answers[question] ?: false

                        editor.putBoolean(
                            "step1_$question",
                            answer
                        )
                    }

                    editor.apply()

                    Log.d(
                        "Questionnaire",
                        "Step 1 completed successfully."
                    )

                    /*
                     * Move to Step 2.
                     */
                    navController.navigate(
                        "questionnaire_step2"
                    ) {

                        launchSingleTop = true
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = "Next",
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}