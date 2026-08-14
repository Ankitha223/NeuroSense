package com.example.neurosense.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DashboardScreen(
    navController: NavController
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "NeuroSense Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Welcome back!",
            style = MaterialTheme.typography.titleMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Neurological Assessment",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Start a new assessment to record your latest readings."
                )
            }
        }

        Button(
            onClick = {
                navController.navigate("questionnaire")
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Start New Assessment"
            )
        }

        Button(
            onClick = {
                // Firebase reports will be connected here later.
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "View Previous Reports"
            )
        }
    }
}