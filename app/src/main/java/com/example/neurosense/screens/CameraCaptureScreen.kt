package com.example.neurosense.screens

import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.neurosense.camera.CameraPreview

@Composable
fun CameraCaptureScreen(
    navController: NavController
) {

    val context = LocalContext.current

    val previewView = remember {
        PreviewView(context)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Box(
            modifier = Modifier.weight(1f)
        ) {

            CameraPreview(
                previewView = previewView,
                modifier = Modifier.fillMaxSize()
            )

        }

        Button(
            onClick = {

                println("Capture clicked")
                navController.navigate("camera_capture")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(55.dp)
        ) {

            Text(
                "Capture",
                fontSize = 18.sp
            )

        }

    }

}