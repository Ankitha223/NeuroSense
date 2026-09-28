
package com.example.neurosense.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.neurosense.components.BackButton
import com.example.neurosense.data.AssessmentData

data class ExerciseRecommendation(
    val area: String,
    val title: String,
    val description: String,
    val steps: String
)

@Composable
fun DoctorConsultationScreen(
    navController: NavController
) {

    // --------------------------------------------------
    // GET LATEST ASSESSMENT VALUES
    // --------------------------------------------------

    val tremorValue =
        AssessmentData.tremorValue

    val movementValue =
        AssessmentData.movementValue

    val stabilityValue =
        AssessmentData.stabilityValue

    val forceValue =
        AssessmentData.forceValue

    val pressureValue =
        AssessmentData.pressureValue

    // --------------------------------------------------
    // CHECK ASSESSMENT
    // --------------------------------------------------

    val assessmentCompleted =
        tremorValue != 0.0 ||
                movementValue != 0.0 ||
                stabilityValue != 0.0 ||
                forceValue != 0.0 ||
                pressureValue != 0.0

    // --------------------------------------------------
    // GENERATE WELLNESS RECOMMENDATIONS
    // --------------------------------------------------

    val recommendations =
        remember(
            tremorValue,
            movementValue,
            stabilityValue,
            forceValue,
            pressureValue
        ) {

            val list =
                mutableListOf<ExerciseRecommendation>()

            if (!assessmentCompleted) {

                list.add(
                    ExerciseRecommendation(
                        area = "General Wellness",
                        title = "Gentle Daily Movement",
                        description =
                            "Regular gentle movement can support mobility, flexibility and healthy daily activity.",
                        steps =
                            "1. Walk slowly for 5–10 minutes.\n" +
                                    "2. Move your arms gently.\n" +
                                    "3. Take short breaks when needed.\n" +
                                    "4. Stop if you feel pain, dizziness or unusual discomfort."
                    )
                )

                list.add(
                    ExerciseRecommendation(
                        area = "Flexibility",
                        title = "Gentle Stretching",
                        description =
                            "Simple stretching can help maintain comfortable movement and flexibility.",
                        steps =
                            "1. Sit or stand comfortably.\n" +
                                    "2. Slowly stretch your arms and shoulders.\n" +
                                    "3. Hold each comfortable stretch for 10–15 seconds.\n" +
                                    "4. Relax and repeat gently."
                    )
                )

            } else {

                // --------------------------------------------------
                // TREMOR / HAND CONTROL
                // --------------------------------------------------

                if (tremorValue > 3.0) {

                    list.add(
                        ExerciseRecommendation(
                            area = "Movement Control",
                            title = "Slow Hand & Wrist Movement",
                            description =
                                "Controlled hand and wrist movements can help maintain comfortable hand mobility.",
                            steps =
                                "1. Rest your forearm comfortably on a table.\n" +
                                        "2. Slowly open and close your hand.\n" +
                                        "3. Rotate your wrist gently in both directions.\n" +
                                        "4. Repeat 5–10 times.\n" +
                                        "5. Keep movements slow and comfortable."
                        )
                    )
                }

                // --------------------------------------------------
                // MOVEMENT
                // --------------------------------------------------

                if (movementValue < 6.0) {

                    list.add(
                        ExerciseRecommendation(
                            area = "Mobility",
                            title = "Gentle Walking",
                            description =
                                "Light walking supports regular movement and everyday mobility.",
                            steps =
                                "1. Walk on a safe and level surface.\n" +
                                        "2. Start slowly for about 5 minutes.\n" +
                                        "3. Maintain a comfortable pace.\n" +
                                        "4. Increase duration gradually when comfortable.\n" +
                                        "5. Use appropriate support if required."
                        )
                    )
                }

                // --------------------------------------------------
                // STABILITY
                // --------------------------------------------------

                if (stabilityValue > 3.5) {

                    list.add(
                        ExerciseRecommendation(
                            area = "Balance & Stability",
                            title = "Supported Standing",
                            description =
                                "Simple supported standing practice can help maintain body control during everyday activities.",
                            steps =
                                "1. Stand near a wall or sturdy chair.\n" +
                                        "2. Keep one hand lightly on the support.\n" +
                                        "3. Keep your feet comfortably apart.\n" +
                                        "4. Hold the position for 10–20 seconds.\n" +
                                        "5. Repeat 2–3 times.\n" +
                                        "6. Do not perform this exercise without support if you feel unsteady."
                        )
                    )
                }

                // --------------------------------------------------
                // FORCE
                // --------------------------------------------------

                if (forceValue < 3.0) {

                    list.add(
                        ExerciseRecommendation(
                            area = "Hand Strength",
                            title = "Gentle Grip Exercise",
                            description =
                                "Light grip activity can help maintain hand strength during everyday tasks.",
                            steps =
                                "1. Use a soft stress ball or folded soft towel.\n" +
                                        "2. Gently squeeze it.\n" +
                                        "3. Hold for 3–5 seconds.\n" +
                                        "4. Slowly release.\n" +
                                        "5. Repeat 5–10 times.\n" +
                                        "6. Avoid excessive force."
                        )
                    )
                }

                // --------------------------------------------------
                // PRESSURE
                // --------------------------------------------------

                if (
                    pressureValue < 25.0 ||
                    pressureValue > 45.0
                ) {

                    list.add(
                        ExerciseRecommendation(
                            area = "Hand Comfort",
                            title = "Hand Relaxation",
                            description =
                                "Relaxation movements can reduce unnecessary tension during hand activities.",
                            steps =
                                "1. Place your hand comfortably on a flat surface.\n" +
                                        "2. Relax your fingers.\n" +
                                        "3. Slowly spread your fingers apart.\n" +
                                        "4. Hold for a few seconds.\n" +
                                        "5. Relax the hand completely.\n" +
                                        "6. Repeat 5 times."
                        )
                    )
                }

                // --------------------------------------------------
                // GENERAL WELLNESS
                // --------------------------------------------------

                if (list.isEmpty()) {

                    list.add(
                        ExerciseRecommendation(
                            area = "General Wellness",
                            title = "Maintain Regular Activity",
                            description =
                                "Your current demo assessment does not indicate an area requiring additional wellness activities.",
                            steps =
                                "1. Continue regular light physical activity.\n" +
                                        "2. Take short movement breaks during long periods of sitting.\n" +
                                        "3. Continue comfortable stretching and mobility exercises.\n" +
                                        "4. Repeat NeuroSense assessments regularly."
                        )
                    )
                }
            }

            list
        }

    // --------------------------------------------------
    // SCREEN
    // --------------------------------------------------

    Column(
        modifier =
            Modifier.fillMaxSize()
    ) {

        // --------------------------------------------------
        // HEADER
        // --------------------------------------------------

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
        ) {

            BackButton(
                navController =
                    navController
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "Wellness & Prevention",

                fontSize =
                    28.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Personalized wellness activities based on your latest NeuroSense assessment."
            )
        }

        // --------------------------------------------------
        // CONTENT
        // --------------------------------------------------

        LazyColumn(

            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),

            verticalArrangement =
                Arrangement.spacedBy(14.dp)

        ) {

            // --------------------------------------------------
            // INTRODUCTION
            // --------------------------------------------------

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(18.dp)
                    ) {

                        Text(
                            text =
                                "Your Personalized Wellness Plan",

                            fontSize =
                                20.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                if (assessmentCompleted) {

                                    "NeuroSense reviewed your latest assessment and selected general wellness activities for areas that may benefit from additional attention."

                                } else {

                                    "Complete a sensor assessment to receive personalized wellness suggestions."
                                }
                        )
                    }
                }
            }

            // --------------------------------------------------
            // RECOMMENDATION TITLE
            // --------------------------------------------------

            if (assessmentCompleted) {

                item {

                    Text(
                        text =
                            "Recommended Activities",

                        fontSize =
                            22.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            // --------------------------------------------------
            // RECOMMENDATIONS
            // --------------------------------------------------

            items(recommendations) { recommendation ->

                ExerciseRecommendationCard(
                    recommendation =
                        recommendation
                )
            }

            // --------------------------------------------------
            // HEALTH AI ASSISTANT
            // --------------------------------------------------

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .secondaryContainer
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(18.dp)
                    ) {

                        Text(
                            text =
                                "Need More Guidance?",

                            fontSize =
                                20.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "Ask the NeuroSense Health AI Assistant for simple health and wellness information."
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Button(

                            onClick = {

                                navController.navigate(
                                    "chatbot"
                                )
                            },

                            modifier =
                                Modifier.fillMaxWidth()

                        ) {

                            Text(
                                text =
                                    "Open Health AI Assistant"
                            )
                        }
                    }
                }
            }

            // --------------------------------------------------
            // SAFETY
            // --------------------------------------------------

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier =
                            Modifier.padding(18.dp)
                    ) {

                        Text(
                            text =
                                "Safety First",

                            fontSize =
                                20.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "These are general wellness suggestions and are not a treatment for a specific medical condition."
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "Perform activities gently and stop if you experience pain, dizziness, weakness or unusual discomfort."
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "For persistent or serious changes, seek advice from a qualified healthcare professional."
                        )
                    }
                }
            }

            // --------------------------------------------------
            // NEW ASSESSMENT
            // --------------------------------------------------

            item {

                OutlinedButton(

                    onClick = {

                        navController.navigate(
                            "sensor_assessment"
                        )
                    },

                    modifier =
                        Modifier.fillMaxWidth()

                ) {

                    Text(
                        text =
                            "Take New Assessment"
                    )
                }
            }

            // --------------------------------------------------
            // DISCLAIMER
            // --------------------------------------------------

            item {

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Text(
                    text =
                        "NeuroSense provides monitoring and general wellness support. It does not provide medical diagnosis or replace professional medical advice.",

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
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
// EXERCISE RECOMMENDATION CARD
// --------------------------------------------------

@Composable
private fun ExerciseRecommendationCard(
    recommendation: ExerciseRecommendation
) {

    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Text(
                text =
                    recommendation.area,

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    recommendation.title,

                fontSize =
                    20.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    recommendation.description,

                fontSize =
                    15.sp
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text =
                    "How to do it",

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    recommendation.steps,

                fontSize =
                    15.sp
            )
        }
    }
}

