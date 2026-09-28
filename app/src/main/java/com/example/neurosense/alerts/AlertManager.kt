package com.example.neurosense.alerts

import android.content.Context
import com.example.neurosense.notifications.NotificationHelper

object AlertManager {


// --------------------------------------------------
// SENSOR CONTACT
// --------------------------------------------------

    fun checkSensorContact(
        context: Context,
        sensorConnected: Boolean
    ) {

        if (!sensorConnected) {

            NotificationHelper
                .showSensorRemovedNotification(
                    context
                )
        }
    }

// --------------------------------------------------
// TREMOR
// --------------------------------------------------

    fun checkTremor(
        context: Context,
        tremorValue: Double
    ) {

        if (tremorValue > 4.0) {

            NotificationHelper.showCriticalAlert(
                context,
                "Your current movement reading is higher than the normal monitoring range. Please pause the activity and check your sensor placement."
            )
        }
    }

// --------------------------------------------------
// MOVEMENT
// --------------------------------------------------

    fun checkMovement(
        context: Context,
        movementValue: Double
    ) {

        if (movementValue < 5.5) {

            NotificationHelper.showCriticalAlert(
                context,
                "Your current movement reading is outside the expected monitoring range. Please rest and check the sensor position."
            )
        }
    }

// --------------------------------------------------
// STABILITY
// --------------------------------------------------

    fun checkStability(
        context: Context,
        stabilityValue: Double
    ) {

        if (stabilityValue > 4.5) {

            NotificationHelper.showCriticalAlert(
                context,
                "Your current stability reading requires attention. Please remain seated or supported and check your surroundings."
            )
        }
    }

// --------------------------------------------------
// FORCE
// --------------------------------------------------

    fun checkForce(
        context: Context,
        forceValue: Double
    ) {

        if (forceValue < 2.5) {

            NotificationHelper.showCriticalAlert(
                context,
                "Your current force reading is lower than the monitoring range. Please check the sensor placement and try again."
            )
        }
    }

// --------------------------------------------------
// PRESSURE
// --------------------------------------------------

    fun checkPressure(
        context: Context,
        pressureValue: Double
    ) {

        if (
            pressureValue < 22.0 ||
            pressureValue > 48.0
        ) {

            NotificationHelper.showCriticalAlert(
                context,
                "Your current pressure reading is outside the monitoring range. Please check the sensor placement."
            )
        }
    }

// --------------------------------------------------
// CHECK ALL READINGS
// --------------------------------------------------

    fun checkAllReadings(
        context: Context,
        tremorValue: Double,
        movementValue: Double,
        stabilityValue: Double,
        forceValue: Double,
        pressureValue: Double
    ) {

        checkTremor(
            context,
            tremorValue
        )

        checkMovement(
            context,
            movementValue
        )

        checkStability(
            context,
            stabilityValue
        )

        checkForce(
            context,
            forceValue
        )

        checkPressure(
            context,
            pressureValue
        )
    }


}
