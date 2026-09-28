package com.example.neurosense

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.neurosense.navigation.AppNavigation
import com.example.neurosense.notifications.NotificationHelper
import com.example.neurosense.ui.theme.NeuroSenseTheme

class MainActivity : ComponentActivity() {
// --------------------------------------------------
// NOTIFICATION PERMISSION
// --------------------------------------------------

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            // Permission result can be handled here if needed.
            // NeuroSense can still work if the user denies it.
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        // --------------------------------------------------
        // CREATE NOTIFICATION CHANNEL
        // --------------------------------------------------

        NotificationHelper.createNotificationChannel(
            this
        )

        // --------------------------------------------------
        // REQUEST NOTIFICATION PERMISSION
        // --------------------------------------------------

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        // --------------------------------------------------
        // APP UI
        // --------------------------------------------------

        setContent {

            NeuroSenseTheme {

                AppNavigation()
            }
        }
    }
}
