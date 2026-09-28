package com.example.neurosense.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.neurosense.components.BackButton
import com.example.neurosense.data.AssessmentData

data class ReportItem(
    val name: String,
    val reading: String,
    val status: String,
    val explanation: String
)

@Composable
fun DailyReportScreen(
    navController: NavController
) {

    // --------------------------------------------------
    // GET ASSESSMENT VALUES
    // --------------------------------------------------

    val tremorValue =
        AssessmentData.tremorValue

    val movementValue =
        AssessmentData.movementValue

    val stabilityValue =
        AssessmentData.stabilityValue

    val forceValue =
        AssessmentData.forceValue

    val pressureValue =
        AssessmentData.pressureValue

    // --------------------------------------------------
    // CHECK WHETHER ASSESSMENT WAS COMPLETED
    // --------------------------------------------------

    val assessmentCompleted =
        tremorValue != 0.0 ||
                movementValue != 0.0 ||
                stabilityValue != 0.0 ||
                forceValue != 0.0 ||
                pressureValue != 0.0

    // --------------------------------------------------
    // DETERMINE STATUS
    // --------------------------------------------------

    val tremorStatus =
        if (tremorValue > 3.0)
            "Needs Attention"
        else
            "Normal"

    val movementStatus =
        if (movementValue < 6.0)
            "Needs Attention"
        else
            "Normal"

    val stabilityStatus =
        if (stabilityValue > 3.5)
            "Needs Attention"
        else
            "Normal"

    val forceStatus =
        if (forceValue < 3.0)
            "Needs Attention"
        else
            "Normal"

    val pressureStatus =
        if (
            pressureValue < 25.0 ||
            pressureValue > 45.0
        )
            "Needs Attention"
        else
            "Normal"

    // --------------------------------------------------
    // REPORT ITEMS
    // --------------------------------------------------

    val reportItems = listOf(

        ReportItem(
            name =
                "Tremor",

            reading =
                String.format(
                    "%.2f",
                    tremorValue
                ),

            status =
                tremorStatus,

            explanation =
                if (tremorStatus == "Normal") {

                    "The simulated tremor reading is within the current demo reference range."

                } else {

                    "The simulated tremor reading is above the current demo reference range."
                }
        ),

        ReportItem(
            name =
                "Movement",

            reading =
                String.format(
                    "%.2f m/s²",
                    movementValue
                ),

            status =
                movementStatus,

            explanation =
                if (movementStatus == "Normal") {

                    "Movement activity is within the current simulated reference range."

                } else {

                    "Movement activity is below the current simulated reference range."
                }
        ),

        ReportItem(
            name =
                "Stability",

            reading =
                String.format(
                    "%.2f",
                    stabilityValue
                ),

            status =
                stabilityStatus,

            explanation =
                if (stabilityStatus == "Normal") {

                    "The simulated stability value is within the current demo reference range."

                } else {

                    "The simulated stability value is above the current demo reference threshold."
                }
        ),

        ReportItem(
            name =
                "Force",

            reading =
                String.format(
                    "%.2f N",
                    forceValue
                ),

            status =
                forceStatus,

            explanation =
                if (forceStatus == "Normal") {

                    "The simulated force measurement is within the current demo reference range."

                } else {

                    "The simulated force measurement is below the current demo reference range."
                }
        ),

        ReportItem(
            name =
                "Pressure",

            reading =
                String.format(
                    "%.2f kPa",
                    pressureValue
                ),

            status =
                pressureStatus,

            explanation =
                if (pressureStatus == "Normal") {

                    "The simulated pressure reading is within the current demo reference range."

                } else {

                    "The simulated pressure reading is outside the current demo reference range."
                }
        )
    )

    // --------------------------------------------------
    // OVERALL STATUS
    // --------------------------------------------------

    val attentionCount =
        reportItems.count {
            it.status == "Needs Attention"
        }

    val overallStatus =
        if (!assessmentCompleted) {

            "No Assessment"

        } else if (attentionCount > 0) {

            "Needs Attention"

        } else {

            "Normal"
        }

    // --------------------------------------------------
    // ATTENTION AREAS
    // --------------------------------------------------

    val attentionAreas =
        reportItems
            .filter {
                it.status == "Needs Attention"
            }
            .joinToString(", ") {
                it.name
            }

    // --------------------------------------------------
    // SCREEN
    // --------------------------------------------------

    LazyColumn(

        modifier =
            Modifier
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
                navController =
                    navController
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
                    "Daily Report",

                fontSize =
                    28.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    "NeuroSense Daily Assessment Summary",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )
        }

        // --------------------------------------------------
        // NO ASSESSMENT MESSAGE
        // --------------------------------------------------

        if (!assessmentCompleted) {

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
                                "No Assessment Available",

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
                                "Complete a sensor assessment first to generate your daily report."
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Button(
                            onClick = {

                                navController.navigate(
                                    "sensor_assessment"
                                )
                            },

                            modifier =
                                Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text =
                                    "Start Sensor Assessment"
                            )
                        }
                    }
                }
            }

        } else {

            // --------------------------------------------------
            // OVERALL STATUS
            // --------------------------------------------------

            item {

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    colors =
                        CardDefaults.cardColors(

                            containerColor =
                                if (
                                    overallStatus ==
                                    "Normal"
                                ) {

                                    MaterialTheme
                                        .colorScheme
                                        .primaryContainer

                                } else {

                                    MaterialTheme
                                        .colorScheme
                                        .errorContainer
                                }
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(18.dp)
                    ) {

                        Text(
                            text =
                                "Overall Status",

                            fontSize =
                                18.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                overallStatus,

                            fontSize =
                                24.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                if (attentionCount == 0) {

                                    "All monitored areas are within the current simulated reference ranges."

                                } else {

                                    "$attentionCount area(s) require attention based on the simulated readings."
                                }
                        )
                    }
                }
            }

            // --------------------------------------------------
            // ATTENTION AREAS
            // --------------------------------------------------

            if (attentionCount > 0) {

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
                                    "Areas to Focus On",

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
                                    attentionAreas,

                                fontSize =
                                    17.sp,

                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Text(
                                text =
                                    "These areas are highlighted only according to the current demo reference thresholds."
                            )
                        }
                    }
                }
            }

            // --------------------------------------------------
            // SUMMARY
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
                                "Today's Summary",

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
                                if (attentionCount == 0) {

                                    "Your latest simulated sensor readings are currently within the demo reference ranges."

                                } else {

                                    "Your latest simulated sensor readings have been reviewed and the areas requiring attention are highlighted below."
                                }
                        )
                    }
                }
            }

            // --------------------------------------------------
            // REPORT ITEMS
            // --------------------------------------------------

            items(reportItems) { item ->

                ReportItemCard(
                    item =
                        item
                )
            }

            // --------------------------------------------------
            // WELLNESS & PREVENTION
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
                                "Wellness & Prevention",

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
                                "View general wellness activities selected according to your latest assessment."
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        OutlinedButton(

                            onClick = {

                                navController.navigate(
                                    "doctor_consultation"
                                )
                            },

                            modifier =
                                Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text =
                                    "View Wellness Suggestions"
                            )
                        }
                    }
                }
            }

            // --------------------------------------------------
            // IMPORTANT
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
                                "Important",

                            fontSize =
                                18.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "This report is based on simulated demo sensor data. NeuroSense provides monitoring and wellness support and does not provide medical diagnosis."
                        )
                    }
                }
            }

            // --------------------------------------------------
            // SENSOR GRAPHS
            // --------------------------------------------------

            item {

                OutlinedButton(

                    onClick = {

                        navController.navigate(
                            "sensor_graphs"
                        )
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(55.dp)

                ) {

                    Text(
                        text =
                            "View Sensor Graphs"
                    )
                }
            }

            // --------------------------------------------------
            // DASHBOARD
            // --------------------------------------------------

            item {

                Button(

                    onClick = {

                        navController.navigate(
                            "dashboard"
                        ) {

                            popUpTo(
                                "dashboard"
                            ) {

                                inclusive =
                                    false
                            }

                            launchSingleTop =
                                true
                        }
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(55.dp)

                ) {

                    Text(
                        text =
                            "Back to Dashboard"
                    )
                }
            }
        }

        // --------------------------------------------------
        // BOTTOM SPACING
        // --------------------------------------------------

        item {

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )
        }
    }
}

// --------------------------------------------------
// REPORT ITEM CARD
// --------------------------------------------------

@Composable
private fun ReportItemCard(
    item: ReportItem
) {

    val isAttention =
        item.status ==
                "Needs Attention"

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    if (isAttention) {

                        MaterialTheme
                            .colorScheme
                            .errorContainer

                    } else {

                        MaterialTheme
                            .colorScheme
                            .surface
                    }
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Text(
                text =
                    item.name,

                fontSize =
                    18.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Reading: ${item.reading}"
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Status: ${item.status}",

                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    item.explanation
            )
        }
    }
}