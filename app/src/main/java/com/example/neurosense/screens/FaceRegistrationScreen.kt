package com.example.neurosense.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun FaceRegistrationScreen(navController: NavController) {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Face Registration",
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "This screen will capture the user's face."
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { }
            ) {
                Text("Capture Face")
            }
        }
    }
}