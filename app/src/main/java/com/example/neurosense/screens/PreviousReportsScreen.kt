
package com.example.neurosense.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.neurosense.components.BackButton
import com.example.neurosense.data.AssessmentData

data class PreviousSensorReport(
    val name: String,
    val reading: String,
    val status: String
)

@Composable
fun PreviousReportsScreen(
    navController: NavController
) {

    // --------------------------------------------------
    // GET LATEST LOCAL ASSESSMENT
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
    // CHECK WHETHER ASSESSMENT EXISTS
    // --------------------------------------------------

    val assessmentAvailable =
        tremorValue != 0.0 ||
                movementValue != 0.0 ||
                stabilityValue != 0.0 ||
                forceValue != 0.0 ||
                pressureValue != 0.0

    // --------------------------------------------------
    // STATUS
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

    val reports = listOf(

        PreviousSensorReport(
            name = "Tremor",
            reading = String.format("%.2f", tremorValue),
            status = tremorStatus
        ),

        PreviousSensorReport(
            name = "Movement",
            reading = String.format("%.2f m/s²", movementValue),
            status = movementStatus
        ),

        PreviousSensorReport(
            name = "Stability",
            reading = String.format("%.2f", stabilityValue),
            status = stabilityStatus
        ),

        PreviousSensorReport(
            name = "Force",
            reading = String.format("%.2f N", forceValue),
            status = forceStatus
        ),

        PreviousSensorReport(
            name = "Pressure",
            reading = String.format("%.2f kPa", pressureValue),
            status = pressureStatus
        )
    )

    val attentionCount =
        reports.count {
            it.status == "Needs Attention"
        }

    val overallStatus =
        if (attentionCount > 0)
            "Needs Attention"
        else
            "Normal"

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
        // BACK
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
                text = "Previous Reports",

                fontSize = 28.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Your latest NeuroSense assessment summary."
            )
        }

        // --------------------------------------------------
        // NO REPORT
        // --------------------------------------------------

        if (!assessmentAvailable) {

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
                                "Complete a sensor assessment to generate your first report."
                        )
                    }
                }
            }

            item {

                Button(

                    onClick = {

                        navController.navigate(
                            "sensor_assessment"
                        )
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(55.dp)

                ) {

                    Text(
                        text =
                            "Start Sensor Assessment"
                    )
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
                                "Latest Assessment",

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
                                "$attentionCount of ${reports.size} areas require attention based on the current demo readings."
                        )
                    }
                }
            }

            // --------------------------------------------------
            // SENSOR RESULTS
            // --------------------------------------------------

            item {

                Text(
                    text =
                        "Sensor Summary",

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            items(reports.size) { index ->

                PreviousReportCard(
                    report =
                        reports[index]
                )
            }

            // --------------------------------------------------
            // VIEW GRAPHS
            // --------------------------------------------------

            item {

                Button(

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
            // NEW ASSESSMENT
            // --------------------------------------------------

            item {

                OutlinedButton(

                    onClick = {

                        navController.navigate(
                            "sensor_assessment"
                        )
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(55.dp)

                ) {

                    Text(
                        text =
                            "Take New Assessment"
                    )
                }
            }

            // --------------------------------------------------
            // INFORMATION
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
                                "Information",

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
                                "This report is based on the latest local demo sensor assessment. Backend and permanent report storage will be connected later."
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "NeuroSense provides monitoring support and does not replace professional medical diagnosis."
                        )
                    }
                }
            }
        }

        item {

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )
        }
    }
}

// --------------------------------------------------
// REPORT CARD
// --------------------------------------------------

@Composable
private fun PreviousReportCard(
    report: PreviousSensorReport
) {

    val needsAttention =
        report.status == "Needs Attention"

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    if (needsAttention) {

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
                    report.name,

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
                    "Reading: ${report.reading}"
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Status: ${report.status}",

                fontWeight =
                    FontWeight.SemiBold
            )
        }
    }
}

