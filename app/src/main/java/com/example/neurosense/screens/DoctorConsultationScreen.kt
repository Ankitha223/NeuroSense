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
import com.example.neurosense.components.BackButton

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@Composable
fun DoctorConsultationScreen(
    navController: NavController
) {

    // --------------------------------------------------
    // CHAT MESSAGES
    // --------------------------------------------------

    val messages = remember {

        mutableStateListOf(

            ChatMessage(
                text =
                    "Hello! I am the NeuroSense AI Assistant. " +
                            "I can explain your assessment, sensor readings, " +
                            "and general health-monitoring information.",
                isUser = false
            )
        )
    }

    // --------------------------------------------------
    // OTHER QUESTION
    // --------------------------------------------------

    var showOtherInput by remember {
        mutableStateOf(false)
    }

    var typedQuestion by remember {
        mutableStateOf("")
    }

    // --------------------------------------------------
    // PREDEFINED QUESTIONS
    // --------------------------------------------------

    val predefinedQuestions = listOf(

        "What does my assessment result mean?",

        "What does my tremor reading mean?",

        "What does my stability reading mean?",

        "What does my movement reading mean?",

        "Why are sensor readings monitored?",

        "How can I monitor changes over time?",

        "What should I do if my readings change?",

        "Other"
    )

    // --------------------------------------------------
    // AI RESPONSE
    // --------------------------------------------------

    fun getAIResponse(question: String): String {

        return when {

            question ==
                    "What does my assessment result mean?" ->

                "Your assessment result summarizes the responses " +
                        "and measurements collected during your assessment. " +
                        "It is intended for monitoring changes over time " +
                        "and does not provide a medical diagnosis."

            question ==
                    "What does my tremor reading mean?" ->

                "The tremor reading represents the amount of " +
                        "shaking or movement variation detected during " +
                        "the assessment. Changes in this value can be " +
                        "monitored over time."

            question ==
                    "What does my stability reading mean?" ->

                "The stability reading represents how consistent " +
                        "your movement and balance were during the " +
                        "measurement. Lower stability can indicate " +
                        "greater movement variation."

            question ==
                    "What does my movement reading mean?" ->

                "The movement reading represents motion detected " +
                        "by the sensor during the assessment. Comparing " +
                        "readings over multiple assessments can help " +
                        "identify changes in movement patterns."

            question ==
                    "Why are sensor readings monitored?" ->

                "Sensor readings allow NeuroSense to observe movement " +
                        "patterns and changes over time. This information " +
                        "can support personal monitoring and reporting."

            question ==
                    "How can I monitor changes over time?" ->

                "Regular assessments can help you compare your readings " +
                        "over time. Looking at trends rather than a single " +
                        "reading provides more useful monitoring information."

            question ==
                    "What should I do if my readings change?" ->

                "A change in a reading does not automatically indicate " +
                        "a medical problem. Continue monitoring your results " +
                        "and consider discussing persistent or concerning " +
                        "changes with a qualified healthcare professional."

            else ->

                "I can provide general information about your symptoms, " +
                        "assessment results, sensor readings, and health " +
                        "monitoring. I cannot provide a medical diagnosis. " +
                        "For a specific medical concern, please consult " +
                        "a qualified healthcare professional."
        }
    }

    // --------------------------------------------------
    // SCREEN
    // --------------------------------------------------

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // --------------------------------------------------
        // HEADER
        // --------------------------------------------------

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            BackButton(
                navController = navController
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "AI Health Assistant",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    "NeuroSense AI-powered health monitoring assistant"
            )
        }

        // --------------------------------------------------
        // CHAT AREA
        // --------------------------------------------------

        LazyColumn(

            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),

            verticalArrangement =
                Arrangement.spacedBy(10.dp)

        ) {

            items(messages) { message ->

                ChatMessageCard(
                    message = message
                )
            }

            item {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        "What would you like to know?",

                    fontSize = 18.sp,

                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )
            }

            // --------------------------------------------------
            // PREDEFINED QUESTIONS
            // --------------------------------------------------

            if (!showOtherInput) {

                items(predefinedQuestions) { question ->

                    OutlinedButton(

                        onClick = {

                            if (question == "Other") {

                                showOtherInput = true

                            } else {

                                messages.add(
                                    ChatMessage(
                                        text = question,
                                        isUser = true
                                    )
                                )

                                messages.add(
                                    ChatMessage(
                                        text =
                                            getAIResponse(
                                                question
                                            ),
                                        isUser = false
                                    )
                                )
                            }
                        },

                        modifier =
                            Modifier.fillMaxWidth()

                    ) {

                        Text(
                            text = question
                        )
                    }
                }
            }

            // --------------------------------------------------
            // OTHER INPUT
            // --------------------------------------------------

            if (showOtherInput) {

                item {

                    OutlinedTextField(

                        value =
                            typedQuestion,

                        onValueChange = {
                            typedQuestion = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text(
                                "Type your question"
                            )
                        },

                        placeholder = {
                            Text(
                                "Ask the AI Assistant..."
                            )
                        },

                        minLines = 3
                    )

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Button(

                        onClick = {

                            if (
                                typedQuestion
                                    .isNotBlank()
                            ) {

                                messages.add(
                                    ChatMessage(
                                        text =
                                            typedQuestion
                                                .trim(),
                                        isUser = true
                                    )
                                )

                                messages.add(
                                    ChatMessage(
                                        text =
                                            getAIResponse(
                                                typedQuestion
                                            ),
                                        isUser = false
                                    )
                                )

                                typedQuestion = ""

                                showOtherInput = false
                            }
                        },

                        enabled =
                            typedQuestion
                                .isNotBlank(),

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(55.dp)

                    ) {

                        Text(
                            text = "Ask AI",
                            fontSize = 17.sp
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    OutlinedButton(

                        onClick = {

                            typedQuestion = ""

                            showOtherInput = false
                        },

                        modifier =
                            Modifier.fillMaxWidth()

                    ) {

                        Text(
                            text = "Back to Questions"
                        )
                    }
                }
            }

            // --------------------------------------------------
            // DISCLAIMER
            // --------------------------------------------------

            item {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "NeuroSense AI provides general health-monitoring " +
                                "information and does not provide medical diagnosis.",

                    style =
                        MaterialTheme.typography.bodySmall
                )

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )
            }
        }
    }
}

// --------------------------------------------------
// CHAT MESSAGE CARD
// --------------------------------------------------

@Composable
private fun ChatMessageCard(
    message: ChatMessage
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    if (message.isUser) {

                        MaterialTheme
                            .colorScheme
                            .secondaryContainer

                    } else {

                        MaterialTheme
                            .colorScheme
                            .surfaceVariant
                    }
            )
    ) {

        Column(
            modifier =
                Modifier.padding(14.dp)
        ) {

            Text(
                text =
                    if (message.isUser)
                        "You"
                    else
                        "NeuroSense AI",

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    message.text,

                fontSize =
                    15.sp
            )
        }
    }
}