package com.example.neurosense.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.neurosense.components.QuestionCard
import com.example.neurosense.components.SectionTitle

@Composable
fun QuestionnaireScreen(
    navController: NavController
) {

    val answers = remember {
        mutableStateMapOf<String, Boolean?>()
    }

    val neurologicalQuestions = listOf(
        "Do you experience hand tremors?",
        "Do you have difficulty maintaining balance while walking?",
        "Do you feel muscle stiffness?",
        "Do you experience numbness or tingling?",
        "Do you have difficulty speaking clearly?",
        "Do you notice slower body movements than usual?",
        "Do you experience frequent dizziness?",
        "Do you have difficulty gripping objects?"
    )

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
        }

        item {
            LinearProgressIndicator(
                progress = { 0.33f },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text("Step 1 of 3")
        }

        item {
            SectionTitle("Neurological Symptoms")
        }

        items(neurologicalQuestions) { question ->

            QuestionCard(
                question = question,
                selectedAnswer = answers[question],
                onAnswerSelected = {
                    answers[question] = it
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // Step 2 later
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Next")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}