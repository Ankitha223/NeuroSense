
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

    val reportItems = listOf(

        ReportItem(
            name = "Tremor",
            reading = "Elevated",
            status = "Needs Attention",
            explanation =
                "The simulated tremor reading is higher than the normal demo range."
        ),

        ReportItem(
            name = "Stability",
            reading = "83%",
            status = "Needs Attention",
            explanation =
                "The simulated stability value indicates some variation during movement."
        ),

        ReportItem(
            name = "Movement",
            reading = "9.77 m/s²",
            status = "Normal",
            explanation =
                "Movement activity is within the expected simulated range."
        ),

        ReportItem(
            name = "Acceleration",
            reading = "9.67 m/s²",
            status = "Normal",
            explanation =
                "The simulated acceleration reading is within the expected range."
        ),

        ReportItem(
            name = "Gyroscope",
            reading = "2.27 rad/s",
            status = "Normal",
            explanation =
                "The simulated rotational movement is within the demo range."
        ),

        ReportItem(
            name = "Force",
            reading = "4.82 N",
            status = "Normal",
            explanation =
                "The simulated force measurement is within the expected demo range."
        ),

        ReportItem(
            name = "Pressure",
            reading = "38.6 kPa",
            status = "Normal",
            explanation =
                "The simulated pressure reading is within the expected demo range."
        )
    )

    val attentionCount =
        reportItems.count {
            it.status == "Needs Attention"
        }

    val overallStatus =
        if (attentionCount > 0) {
            "Needs Attention"
        } else {
            "Normal"
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        item {

            Text(
                text = "Daily Report",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "NeuroSense Daily Health Summary",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        if (overallStatus == "Normal") {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Overall Status",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = overallStatus,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "$attentionCount area(s) require attention based on the demo readings."
                    )
                }
            }
        }

        item {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Today's Summary",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Your sensor readings have been reviewed and converted into simple status messages for easier understanding."
                    )
                }
            }
        }

        items(reportItems.size) { index ->

            val item = reportItems[index]

            ReportItemCard(
                item = item
            )
        }

        item {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Important",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "This report is based on simulated demo sensor data. NeuroSense provides monitoring support and does not replace professional medical diagnosis."
                    )
                }
            }
        }

        item {

            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = "Back to Dashboard"
                )
            }
        }

        item {

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}

@Composable
private fun ReportItemCard(
    item: ReportItem
) {

    val isAttention =
        item.status == "Needs Attention"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                if (isAttention) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = item.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Reading: ${item.reading}"
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Status: ${item.status}",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = item.explanation
            )
        }
    }
}

