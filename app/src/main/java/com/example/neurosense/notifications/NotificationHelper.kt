package com.example.neurosense.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.neurosense.R

object NotificationHelper {


    private const val CHANNEL_ID = "neurosense_alerts"
    private const val CHANNEL_NAME = "NeuroSense Alerts"
    private const val CHANNEL_DESCRIPTION =
        "Notifications for sensor status and important readings"

    private const val SENSOR_REMOVED_ID = 1001
    private const val CRITICAL_ALERT_ID = 1002
    private const val SENSOR_RECONNECTED_ID = 1003

// --------------------------------------------------
// CREATE NOTIFICATION CHANNEL
// --------------------------------------------------

    fun createNotificationChannel(
        context: Context
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description =
                        CHANNEL_DESCRIPTION
                }

            val notificationManager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            notificationManager.createNotificationChannel(
                channel
            )
        }
    }

// --------------------------------------------------
// CHECK NOTIFICATION PERMISSION
// --------------------------------------------------

    private fun canPostNotifications(
        context: Context
    ): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }

        return true
    }

// --------------------------------------------------
// SENSOR REMOVED
// --------------------------------------------------

    fun showSensorRemovedNotification(
        context: Context
    ) {

        if (!canPostNotifications(context)) {
            return
        }

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle(
                    "Sensor Placement Alert"
                )
                .setContentText(
                    "Please check that your NeuroSense sensor is properly attached."
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "The sensor may have lost contact. Please check that it is properly attached and positioned."
                        )
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .build()

        postNotification(
            context,
            SENSOR_REMOVED_ID,
            notification
        )
    }

// --------------------------------------------------
// CRITICAL READING
// --------------------------------------------------

    fun showCriticalAlert(
        context: Context,
        message: String
    ) {

        if (!canPostNotifications(context)) {
            return
        }

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle(
                    "NeuroSense Alert"
                )
                .setContentText(
                    message
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(message)
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .build()

        postNotification(
            context,
            CRITICAL_ALERT_ID,
            notification
        )
    }

// --------------------------------------------------
// SENSOR RECONNECTED
// --------------------------------------------------

    fun showSensorReconnectedNotification(
        context: Context
    ) {

        if (!canPostNotifications(context)) {
            return
        }

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle(
                    "Sensor Reconnected"
                )
                .setContentText(
                    "Your NeuroSense sensor is connected again."
                )
                .setPriority(
                    NotificationCompat.PRIORITY_DEFAULT
                )
                .setAutoCancel(true)
                .build()

        postNotification(
            context,
            SENSOR_RECONNECTED_ID,
            notification
        )
    }

// --------------------------------------------------
// SAFE NOTIFICATION POSTING
// --------------------------------------------------

    private fun postNotification(
        context: Context,
        notificationId: Int,
        notification: android.app.Notification
    ) {

        if (!canPostNotifications(context)) {
            return
        }

        try {

            NotificationManagerCompat
                .from(context)
                .notify(
                    notificationId,
                    notification
                )

        } catch (e: SecurityException) {

            e.printStackTrace()
        }
    }

}
