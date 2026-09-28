package com.example.neurosense.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.neurosense.SensorApi
import com.example.neurosense.SensorData
import kotlinx.coroutines.launch

@Composable
fun SensorApiTestScreen() {

    var sensorData by remember {
        mutableStateOf<List<SensorData>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(
            onClick = {

                scope.launch {

                    isLoading = true
                    errorMessage = ""

                    try {

                        sensorData = SensorApi.getSensorData()

                    } catch (e: Exception) {

                        errorMessage =
                            e.message ?: "Unknown error"

                    } finally {

                        isLoading = false
                    }
                }
            }
        ) {
            Text("Fetch Sensor Data")
        }

        if (isLoading) {

            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }

        if (errorMessage.isNotEmpty()) {

            Text(
                text = "Error: $errorMessage",
                modifier = Modifier.padding(16.dp)
            )
        }

        if (sensorData.isNotEmpty()) {

            val latest = sensorData.first()

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text("Latest Sensor Data")

                Text("ID: ${latest.id}")

                Text("AX: ${latest.ax}")

                Text("AY: ${latest.ay}")

                Text("AZ: ${latest.az}")

                Text("GX: ${latest.gx}")

                Text("GY: ${latest.gy}")

                Text("GZ: ${latest.gz}")

                Text("FSR: ${latest.fsr}")

                Text("Time: ${latest.created_at}")
            }
        }
    }
}