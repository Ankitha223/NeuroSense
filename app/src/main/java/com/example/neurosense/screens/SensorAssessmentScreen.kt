package com.example.neurosense.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.neurosense.components.BackButton
import com.example.neurosense.data.AssessmentData
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SensorAssessmentScreen(
    navController: NavController
) {

    // --------------------------------------------------
    // ASSESSMENT STATE
    // --------------------------------------------------

    var isTesting by remember {
        mutableStateOf(false)
    }

    var assessmentCompleted by remember {
        mutableStateOf(false)
    }

    var progress by remember {
        mutableStateOf(0f)
    }

    // --------------------------------------------------
    // SENSOR VALUES
    // --------------------------------------------------

    var tremorValue by remember {
        mutableStateOf(0.0)
    }

    var movementValue by remember {
        mutableStateOf(0.0)
    }

    var stabilityValue by remember {
        mutableStateOf(0.0)
    }

    var forceValue by remember {
        mutableStateOf(0.0)
    }

    var pressureValue by remember {
        mutableStateOf(0.0)
    }

    // --------------------------------------------------
    // START SENSOR TEST
    // --------------------------------------------------

    fun startSensorTest() {

        isTesting = true
        assessmentCompleted = false
        progress = 0f

        tremorValue = 0.0
        movementValue = 0.0
        stabilityValue = 0.0
        forceValue = 0.0
        pressureValue = 0.0
    }

    // --------------------------------------------------
    // SIMULATED SENSOR MEASUREMENT
    // --------------------------------------------------

    LaunchedEffect(isTesting) {

        if (isTesting) {

            for (i in 1..5) {

                delay(1000)

                progress = i / 5f
            }

            // --------------------------------------------------
            // GENERATE SIMULATED VALUES
            // --------------------------------------------------

            tremorValue =
                Random.nextDouble(
                    0.5,
                    4.5
                )

            movementValue =
                Random.nextDouble(
                    5.0,
                    10.0
                )

            stabilityValue =
                Random.nextDouble(
                    0.5,
                    5.0
                )

            forceValue =
                Random.nextDouble(
                    2.0,
                    8.0
                )

            pressureValue =
                Random.nextDouble(
                    20.0,
                    50.0
                )

            // --------------------------------------------------
            // SAVE VALUES
            // --------------------------------------------------

            AssessmentData.tremorValue =
                tremorValue

            AssessmentData.movementValue =
                movementValue

            AssessmentData.stabilityValue =
                stabilityValue

            AssessmentData.forceValue =
                forceValue

            AssessmentData.pressureValue =
                pressureValue

            // --------------------------------------------------
            // COMPLETE
            // --------------------------------------------------

            isTesting = false

            assessmentCompleted = true

            progress = 1f
        }
    }

    // --------------------------------------------------
    // SCREEN
    // --------------------------------------------------

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)

    ) {

        // --------------------------------------------------
        // BACK BUTTON
        // --------------------------------------------------

        item {

            BackButton(
                navController = navController
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )
        }

        // --------------------------------------------------
        // HEADER
        // --------------------------------------------------

        item {

            Text(
                text =
                    "Sensor Assessment",

                fontSize =
                    28.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "NeuroSense analyzes movement, tremor, stability, force and pressure patterns."
            )
        }

        // --------------------------------------------------
        // ASSESSMENT MODE
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
                            "Assessment Mode",

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
                            "Demo Mode",

                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "The current version uses simulated sensor readings. Actual hardware integration will be added later."
                    )
                }
            }
        }

        // --------------------------------------------------
        // INSTRUCTIONS
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
                            "Instructions",

                        fontSize =
                            20.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Text(
                        text =
                            "1. Keep your hand relaxed.\n\n" +
                                    "2. Follow the assessment instructions.\n\n" +
                                    "3. Remain as still as possible during the measurement.\n\n" +
                                    "4. Press the button below when you are ready."
                    )
                }
            }
        }

        // --------------------------------------------------
        // START BUTTON
        // --------------------------------------------------

        item {

            Button(

                onClick = {
                    startSensorTest()
                },

                enabled =
                    !isTesting,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(55.dp)

            ) {

                Text(
                    text =
                        if (isTesting)
                            "Measuring..."
                        else
                            "Start Sensor Assessment",

                    fontSize =
                        17.sp
                )
            }
        }

        // --------------------------------------------------
        // PROGRESS
        // --------------------------------------------------

        if (isTesting) {

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
                                "Collecting Sensor Data",

                            fontSize =
                                20.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        LinearProgressIndicator(
                            progress = {
                                progress
                            },

                            modifier =
                                Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "${(progress * 100).toInt()}% completed"
                        )
                    }
                }
            }
        }

        // --------------------------------------------------
        // RESULTS
        // --------------------------------------------------

        if (assessmentCompleted) {

            item {

                Text(
                    text =
                        "Sensor Results",

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            item {

                SensorResultCard(
                    sensorName =
                        "BMI270 - Tremor",

                    value =
                        String.format(
                            "%.2f",
                            tremorValue
                        ),

                    unit =
                        "Intensity"
                )
            }

            item {

                SensorResultCard(
                    sensorName =
                        "BMI270 - Movement",

                    value =
                        String.format(
                            "%.2f",
                            movementValue
                        ),

                    unit =
                        "Movement Index"
                )
            }

            item {

                SensorResultCard(
                    sensorName =
                        "BMI270 - Stability",

                    value =
                        String.format(
                            "%.2f",
                            stabilityValue
                        ),

                    unit =
                        "Stability Index"
                )
            }

            item {

                SensorResultCard(
                    sensorName =
                        "MPU9250 - Acceleration",

                    value =
                        String.format(
                            "%.2f",
                            movementValue
                        ),

                    unit =
                        "m/s²"
                )
            }

            item {

                SensorResultCard(
                    sensorName =
                        "MPU9250 - Gyroscope",

                    value =
                        String.format(
                            "%.2f",
                            stabilityValue
                        ),

                    unit =
                        "rad/s"
                )
            }

            item {

                SensorResultCard(
                    sensorName =
                        "FSR402 - Force",

                    value =
                        String.format(
                            "%.2f",
                            forceValue
                        ),

                    unit =
                        "N"
                )
            }

            item {

                SensorResultCard(
                    sensorName =
                        "FlexiForce A201 - Pressure",

                    value =
                        String.format(
                            "%.2f",
                            pressureValue
                        ),

                    unit =
                        "kPa"
                )
            }

            // --------------------------------------------------
            // COMPLETION
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
                                "Assessment Completed",

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
                                "Sensor data collection has been completed successfully."
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "The displayed readings are simulated demo values."
                        )
                    }
                }
            }

            // --------------------------------------------------
            // VIEW REPORT
            // --------------------------------------------------

            item {

                Button(

                    onClick = {

                        navController.navigate(
                            "daily_report"
                        )
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(55.dp)

                ) {

                    Text(
                        text =
                            "View Assessment Report",

                        fontSize =
                            17.sp
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
                    Modifier.height(10.dp)
            )

            Text(
                text =
                    "NeuroSense provides monitoring support and does not replace professional medical diagnosis.",

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

// --------------------------------------------------
// SENSOR RESULT CARD
// --------------------------------------------------

@Composable
private fun SensorResultCard(
    sensorName: String,
    value: String,
    unit: String
) {

    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

            horizontalArrangement =
                Arrangement.SpaceBetween

        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        sensorName,

                    fontSize =
                        17.sp,

                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        unit
                )
            }

            Text(
                text =
                    value,

                fontSize =
                    16.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}