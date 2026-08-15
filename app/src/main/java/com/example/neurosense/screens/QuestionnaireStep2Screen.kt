package com.example.neurosense.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.neurosense.components.QuestionCard

@Composable
fun QuestionnaireStep2Screen(
    navController: NavController
) {

    val context = LocalContext.current

    val answers = remember {
        mutableStateMapOf<String, Boolean?>()
    }

    val questions = listOf(
        "Do you find it difficult to perform daily activities?",
        "Do you have difficulty walking for a long distance?",
        "Do you have difficulty getting up from a chair?",
        "Do you have difficulty holding small objects?",
        "Do you feel weakness in your limbs?",
        "Do you experience difficulty coordinating movements?"
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

        item {

            Text(
                text = "Health Questionnaire",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { 0.66f },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Step 2 of 3")

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Daily & Motor Activities",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        items(questions) { question ->

            QuestionCard(
                question = question,
                selectedAnswer = answers[question],
                onAnswerSelected = {
                    answers[question] = it
                    message = ""
                }
            )
        }

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

                    if (questions.any { answers[it] == null }) {

                        message = "Please answer all questions."

                        return@Button
                    }

                    /*
                     * Store Step 2 answers.
                     */

                    val preferences =
                        context.getSharedPreferences(
                            "neurosense_questionnaire",
                            Context.MODE_PRIVATE
                        )

                    val editor = preferences.edit()

                    questions.forEach { question ->

                        editor.putBoolean(
                            "step2_$question",
                            answers[question] == true
                        )
                    }

                    editor.apply()

                    Log.d(
                        "Questionnaire",
                        "Step 2 completed"
                    )

                    navController.navigate(
                        "questionnaire_step3"
                    )
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