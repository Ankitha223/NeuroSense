package com.example.neurosense.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.neurosense.data.AssessmentData
import com.example.neurosense.data.UserStorage

data class DemoSensor(
    val name: String,
    val value: String,
    val status: String
)

@Composable
fun DashboardScreen(
    navController: NavController
) {

    // --------------------------------------------------
    // USER STORAGE
    // --------------------------------------------------

    val context = LocalContext.current

    val userStorage = remember {
        UserStorage(context)
    }

    // --------------------------------------------------
    // CURRENT USER DETAILS
    // --------------------------------------------------

    val userName = userStorage.getName()
    val userAge = userStorage.getAge()
    val userGender = userStorage.getGender()

    // --------------------------------------------------
    // ASSESSMENT VALUES
    // --------------------------------------------------

    val tremorValue = AssessmentData.tremorValue
    val movementValue = AssessmentData.movementValue
    val stabilityValue = AssessmentData.stabilityValue
    val forceValue = AssessmentData.forceValue
    val pressureValue = AssessmentData.pressureValue

    // --------------------------------------------------
    // CHECK WHETHER ASSESSMENT EXISTS
    // --------------------------------------------------

    val assessmentCompleted =
        tremorValue != 0.0 ||
                movementValue != 0.0 ||
                stabilityValue != 0.0 ||
                forceValue != 0.0 ||
                pressureValue != 0.0

    // --------------------------------------------------
    // DETERMINE SENSOR STATUS
    // --------------------------------------------------

    val tremorStatus =
        if (tremorValue > 3.0) "Needs attention" else "Normal"

    val movementStatus =
        if (movementValue < 6.0) "Needs attention" else "Normal"

    val stabilityStatus =
        if (stabilityValue > 3.5) "Needs attention" else "Normal"

    val forceStatus =
        if (forceValue < 3.0) "Needs attention" else "Normal"

    val pressureStatus =
        if (pressureValue < 25.0 || pressureValue > 45.0)
            "Needs attention"
        else
            "Normal"

    // --------------------------------------------------
    // SENSOR DATA
    // --------------------------------------------------

    val sensorData =
        if (assessmentCompleted) {

            listOf(

                DemoSensor(
                    name = "BMI270 - Movement",
                    value = String.format("%.2f m/s²", movementValue),
                    status = movementStatus
                ),

                DemoSensor(
                    name = "BMI270 - Tremor",
                    value = String.format("%.2f", tremorValue),
                    status = tremorStatus
                ),

                DemoSensor(
                    name = "BMI270 - Stability",
                    value = String.format("%.2f", stabilityValue),
                    status = stabilityStatus
                ),

                DemoSensor(
                    name = "MPU9250 - Acceleration",
                    value = String.format("%.2f m/s²", movementValue),
                    status = movementStatus
                ),

                DemoSensor(
                    name = "MPU9250 - Gyroscope",
                    value = String.format("%.2f rad/s", stabilityValue),
                    status = stabilityStatus
                ),

                DemoSensor(
                    name = "MPU9250 - Orientation",
                    value = "Demo data",
                    status = "Normal"
                ),

                DemoSensor(
                    name = "FSR402 - Force",
                    value = String.format("%.2f N", forceValue),
                    status = forceStatus
                ),

                DemoSensor(
                    name = "FlexiForce A201 - Pressure",
                    value = String.format("%.2f kPa", pressureValue),
                    status = pressureStatus
                )
            )

        } else {

            listOf(

                DemoSensor(
                    name = "BMI270 - Movement",
                    value = "Not measured",
                    status = "Pending"
                ),

                DemoSensor(
                    name = "BMI270 - Tremor",
                    value = "Not measured",
                    status = "Pending"
                ),

                DemoSensor(
                    name = "BMI270 - Stability",
                    value = "Not measured",
                    status = "Pending"
                ),

                DemoSensor(
                    name = "MPU9250 - Acceleration",
                    value = "Not measured",
                    status = "Pending"
                ),

                DemoSensor(
                    name = "MPU9250 - Gyroscope",
                    value = "Not measured",
                    status = "Pending"
                ),

                DemoSensor(
                    name = "MPU9250 - Orientation",
                    value = "Not measured",
                    status = "Pending"
                ),

                DemoSensor(
                    name = "FSR402 - Force",
                    value = "Not measured",
                    status = "Pending"
                ),

                DemoSensor(
                    name = "FlexiForce A201 - Pressure",
                    value = "Not measured",
                    status = "Pending"
                )
            )
        }

    // --------------------------------------------------
    // OVERALL STATUS
    // --------------------------------------------------

    val attentionCount =
        if (assessmentCompleted) {

            listOf(
                tremorStatus,
                movementStatus,
                stabilityStatus,
                forceStatus,
                pressureStatus
            ).count {
                it == "Needs attention"
            }

        } else {
            0
        }

    val overallStatus =
        when {
            !assessmentCompleted -> "No assessment"
            attentionCount > 0 -> "Needs Attention"
            else -> "Normal"
        }

    // --------------------------------------------------
    // DASHBOARD
    // --------------------------------------------------

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // --------------------------------------------------
        // HEADER
        // --------------------------------------------------

        item {

            Text(
                text = "NeuroSense",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Daily Health Dashboard",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // --------------------------------------------------
        // USER PROFILE
        // --------------------------------------------------

        item {

            Card(
                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.secondaryContainer
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "User Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Name: $userName",
                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "Age: $userAge",
                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "Gender: $userGender",
                        fontSize = 16.sp
                    )
                }
            }
        }

        // --------------------------------------------------
        // TODAY'S REPORT
        // --------------------------------------------------

        item {

            Card(
                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor =
                        if (overallStatus == "Normal") {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.secondaryContainer
                        }
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Today's Report",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    if (assessmentCompleted) {

                        Text(
                            text =
                                "Your latest simulated sensor assessment has been completed."
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text =
                                "Overall Status: $overallStatus",
                            fontWeight = FontWeight.SemiBold
                        )

                        if (attentionCount > 0) {

                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )

                            Text(
                                text =
                                    "$attentionCount sensor area(s) require attention."
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = "Demo Mode"
                        )

                    } else {

                        Text(
                            text =
                                "No sensor assessment has been completed yet."
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Start a new assessment to generate sensor readings."
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedButton(
                        onClick = {

                            if (assessmentCompleted) {

                                navController.navigate(
                                    "daily_report"
                                )

                            } else {

                                navController.navigate(
                                    "sensor_assessment"
                                )
                            }
                        },

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text =
                                if (assessmentCompleted)
                                    "View Today's Report"
                                else
                                    "Start Assessment"
                        )
                    }
                }
            }
        }

        // --------------------------------------------------
        // SENSOR SUMMARY
        // --------------------------------------------------

        item {

            Text(
                text = "Sensor Summary",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(sensorData) { sensor ->

            SensorSummaryCard(
                sensor = sensor
            )
        }

        // --------------------------------------------------
        // SENSOR GRAPHS
        // --------------------------------------------------

        item {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Daily Visualization",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "View graphical trends for movement, tremor, stability, acceleration, gyroscope, force and pressure."
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedButton(
                        onClick = {

                            navController.navigate(
                                "sensor_graphs"
                            )
                        },

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "View Sensor Graphs"
                        )
                    }
                }
            }
        }

        // --------------------------------------------------
        // ASSESSMENT HISTORY
        // --------------------------------------------------

        item {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Assessment History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "View all your previous sensor assessment sessions, dates, times and usage duration."
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedButton(
                        onClick = {

                            navController.navigate(
                                "assessment_history"
                            )
                        },

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "View Assessment History"
                        )
                    }
                }
            }
        }

        // --------------------------------------------------
        // PREVIOUS REPORTS
        // --------------------------------------------------

        item {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Previous Reports",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "View your previous NeuroSense assessment report."
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedButton(
                        onClick = {

                            navController.navigate(
                                "previous_reports"
                            )
                        },

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "View Previous Reports"
                        )
                    }
                }
            }
        }

        // --------------------------------------------------
        // AI ASSISTANT
        // --------------------------------------------------

        item {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "AI Assistant",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Ask questions about your NeuroSense assessment and sensor readings."
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedButton(
                        onClick = {

                            navController.navigate(
                                "chatbot"
                            )
                        },

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "Open AI Assistant"
                        )
                    }
                }
            }
        }

        // --------------------------------------------------
        // WELLNESS & PREVENTION
        // --------------------------------------------------

        item {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Wellness & Prevention",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Get personalized exercise and wellness suggestions based on your latest assessment."
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedButton(
                        onClick = {

                            navController.navigate(
                                "doctor_consultation"
                            )
                        },

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "View Recommendations"
                        )
                    }
                }
            }
        }

        // --------------------------------------------------
        // NEW ASSESSMENT
        // --------------------------------------------------

        item {

            Button(
                onClick = {

                    navController.navigate(
                        "sensor_assessment"
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = "Start New Assessment",
                    fontSize = 17.sp
                )
            }
        }

        // --------------------------------------------------
        // DISCLAIMER
        // --------------------------------------------------

        item {

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text =
                    "NeuroSense provides monitoring support and does not replace professional medical diagnosis.",

                style = MaterialTheme.typography.bodySmall
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}

// --------------------------------------------------
// SENSOR CARD
// --------------------------------------------------

@Composable
private fun SensorSummaryCard(
    sensor: DemoSensor
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = sensor.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = sensor.status
                )
            }

            Text(
                text = sensor.value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}