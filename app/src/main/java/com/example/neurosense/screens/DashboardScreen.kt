package com.example.neurosense.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.neurosense.data.SensorData
import com.example.neurosense.data.SimulatedSensorRepository
import kotlinx.coroutines.delay
import kotlin.math.sqrt

@Composable
fun DashboardScreen(
    navController: NavController
) {

    val repository = remember {
        SimulatedSensorRepository()
    }

    var sensorData by remember {
        mutableStateOf(
            repository.getCurrentSensorData()
        )
    }

    /*
     * Temporary simulated sensor updates.
     *
     * Later this will be replaced with
     * real ESP32/BLE sensor data.
     */
    LaunchedEffect(Unit) {

        while (true) {

            sensorData =
                repository.getCurrentSensorData()

            delay(1000)
        }
    }

    val movementMagnitude =
        calculateMagnitude(
            sensorData.bmi270X,
            sensorData.bmi270Y,
            sensorData.bmi270Z
        )

    val accelerationMagnitude =
        calculateMagnitude(
            sensorData.mpu9250AccelX,
            sensorData.mpu9250AccelY,
            sensorData.mpu9250AccelZ
        )

    val gyroMagnitude =
        calculateMagnitude(
            sensorData.mpu9250GyroX,
            sensorData.mpu9250GyroY,
            sensorData.mpu9250GyroZ
        )

    val tremorLevel =
        calculateTremorLevel(
            sensorData.bmi270X,
            sensorData.bmi270Y
        )

    val stability =
        calculateStability(
            sensorData.bmi270X,
            sensorData.bmi270Y
        )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        item {

            Text(
                text = "NeuroSense",
                style =
                    MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Daily Health Dashboard",
                style =
                    MaterialTheme.typography.titleMedium
            )
        }

        item {

            Card(
                modifier = Modifier.fillMaxWidth(),
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
                            "Current simulated sensor readings are being displayed for testing."
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
                }
            }
        }

        item {

            Text(
                text = "BMI270",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {

            SensorSummaryCard(
                sensorName = "Movement",
                value =
                    String.format(
                        "%.2f",
                        movementMagnitude
                    )
            )
        }

        item {

            SensorSummaryCard(
                sensorName = "Tremor Level",
                value =
                    tremorLevel
            )
        }

        item {

            SensorSummaryCard(
                sensorName = "Stability",
                value =
                    stability
            )
        }

        item {

            Text(
                text = "MPU9250",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {

            SensorSummaryCard(
                sensorName =
                    "Acceleration Magnitude",
                value =
                    String.format(
                        "%.2f",
                        accelerationMagnitude
                    )
            )
        }

        item {

            SensorSummaryCard(
                sensorName =
                    "Gyroscope Magnitude",
                value =
                    String.format(
                        "%.2f",
                        gyroMagnitude
                    )
            )
        }

        item {

            SensorSummaryCard(
                sensorName =
                    "Orientation / Magnetometer",
                value =
                    String.format(
                        "%.2f, %.2f, %.2f",
                        sensorData.mpu9250MagX,
                        sensorData.mpu9250MagY,
                        sensorData.mpu9250MagZ
                    )
            )
        }

        item {

            Text(
                text = "Force & Pressure Sensors",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {

            SensorSummaryCard(
                sensorName = "FSR402 Force",
                value =
                    String.format(
                        "%.2f",
                        sensorData.fsr402Force
                    )
            )
        }

        item {

            SensorSummaryCard(
                sensorName =
                    "FlexiForce A201 Pressure",
                value =
                    String.format(
                        "%.2f",
                        sensorData.flexiForcePressure
                    )
            )
        }

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
                            "Graphs for movement, tremor, stability, acceleration, gyroscope, force and pressure will be added here."
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            // Graph screen will be connected later.
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
                            "Your previous daily sensor reports will be stored here."
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            // Firebase reports later.
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

@Composable
private fun SensorSummaryCard(
    sensorName: String,
    value: String
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

            Text(
                text = sensorName,
                fontSize = 17.sp,
                fontWeight =
                    FontWeight.SemiBold
            )

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}

private fun calculateMagnitude(
    x: Float,
    y: Float,
    z: Float
): Float {

    return sqrt(
        (x * x) +
                (y * y) +
                (z * z)
    )
}

private fun calculateTremorLevel(
    x: Float,
    y: Float
): String {

    val movement =
        sqrt(
            (x * x) +
                    (y * y)
        )

    return when {

        movement < 0.3f ->
            "Low"

        movement < 0.7f ->
            "Moderate"

        else ->
            "Elevated"
    }
}

private fun calculateStability(
    x: Float,
    y: Float
): String {

    val variation =
        kotlin.math.abs(x) +
                kotlin.math.abs(y)

    return when {

        variation < 0.6f ->
            "Stable"

        variation < 1.2f ->
            "Moderate"

        else ->
            "Unstable"
    }
}