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

    val context =
        LocalContext.current

    val userStorage =
        remember {
            UserStorage(context)
        }

    // --------------------------------------------------
    // CURRENT USER DETAILS
    // --------------------------------------------------

    val userName =
        userStorage.getName()

    val userAge =
        userStorage.getAge()

    val userGender =
        userStorage.getGender()

    // --------------------------------------------------
    // DEMO SENSOR DATA
    // --------------------------------------------------

    val sensorData = listOf(

        DemoSensor(
            "BMI270 - Movement",
            "9.77",
            "Normal"
        ),

        DemoSensor(
            "BMI270 - Tremor",
            "Elevated",
            "Needs attention"
        ),

        DemoSensor(
            "BMI270 - Stability",
            "Unstable",
            "Needs attention"
        ),

        DemoSensor(
            "MPU9250 - Acceleration",
            "9.67 m/s²",
            "Normal"
        ),

        DemoSensor(
            "MPU9250 - Gyroscope",
            "2.27",
            "Normal"
        ),

        DemoSensor(
            "MPU9250 - Orientation",
            "-23.41°, -9.87°, 24.60°",
            "Normal"
        ),

        DemoSensor(
            "FSR402 - Force",
            "4.82 N",
            "Normal"
        ),

        DemoSensor(
            "FlexiForce A201 - Pressure",
            "38.6 kPa",
            "Normal"
        )
    )

    // --------------------------------------------------
    // DASHBOARD
    // --------------------------------------------------

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)

    ) {

        // --------------------------------------------------
        // HEADER
        // --------------------------------------------------

        item {

            Text(
                text = "NeuroSense",
                style =
                    MaterialTheme.typography.headlineMedium,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text = "Daily Health Dashboard",
                style =
                    MaterialTheme.typography.titleMedium
            )
        }

        // --------------------------------------------------
        // USER PROFILE
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
                        text = "User Profile",
                        fontSize = 20.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Text(
                        text =
                            "Name: $userName",
                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )

                    Text(
                        text =
                            "Age: $userAge",
                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )

                    Text(
                        text =
                            "Gender: $userGender",
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
                        text = "Today's Report",
                        fontSize = 20.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Your latest sensor readings are being displayed using simulated demo data."
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            "Overall Status: Monitoring",
                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text = "Demo Mode"
                    )
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
                fontWeight =
                    FontWeight.Bold
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
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(18.dp)
                ) {

                    Text(
                        text =
                            "Daily Visualization",
                        fontSize = 20.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "View graphical trends for movement, tremor, stability, acceleration, gyroscope, force and pressure."
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    OutlinedButton(

                        onClick = {

                            navController.navigate(
                                "sensor_graphs"
                            )
                        },

                        modifier =
                            Modifier.fillMaxWidth()

                    ) {

                        Text(
                            text =
                                "View Sensor Graphs"
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
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(18.dp)
                ) {

                    Text(
                        text =
                            "Previous Reports",
                        fontSize = 20.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "View your previous daily sensor readings and reports."
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    OutlinedButton(

                        onClick = {
                            // We will connect this later.
                        },

                        modifier =
                            Modifier.fillMaxWidth()

                    ) {

                        Text(
                            text =
                                "View Previous Reports"
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
                        "questionnaire"
                    )
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(55.dp)

            ) {

                Text(
                    text =
                        "Start New Assessment",
                    fontSize = 17.sp
                )
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
// SENSOR CARD
// --------------------------------------------------

@Composable
private fun SensorSummaryCard(
    sensor: DemoSensor
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
                        sensor.name,
                    fontSize = 17.sp,
                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        sensor.status
                )
            }

            Text(
                text =
                    sensor.value,
                fontSize = 16.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}