package com.example.neurosense.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.neurosense.components.BackButton
import com.example.neurosense.data.AssessmentData
import kotlin.math.max

data class GraphData(
    val name: String,
    val unit: String,
    val values: List<Float>,
    val referenceValue: Float? = null
)

@Composable
fun SensorGraphsScreen(
    navController: NavController
) {

    // --------------------------------------------------
    // GET LATEST ASSESSMENT VALUES
    // --------------------------------------------------

    val tremor =
        AssessmentData.tremorValue.toFloat()

    val movement =
        AssessmentData.movementValue.toFloat()

    val stability =
        AssessmentData.stabilityValue.toFloat()

    val force =
        AssessmentData.forceValue.toFloat()

    val pressure =
        AssessmentData.pressureValue.toFloat()

    // --------------------------------------------------
    // GRAPH DATA
    // --------------------------------------------------

    val graphs = listOf(

        GraphData(
            name = "BMI270 Movement",
            unit = "m/s²",
            values = listOf(
                max(0f, movement - 0.8f),
                max(0f, movement - 0.4f),
                max(0f, movement - 0.2f),
                movement + 0.3f,
                max(0f, movement - 0.1f),
                movement + 0.2f,
                max(0f, movement - 0.3f),
                movement
            ),
            referenceValue = 6.0f
        ),

        GraphData(
            name = "BMI270 Tremor",
            unit = "Tremor Level",
            values = listOf(
                max(0f, tremor - 0.6f),
                max(0f, tremor - 0.3f),
                tremor + 0.2f,
                max(0f, tremor - 0.1f),
                tremor + 0.4f,
                tremor + 0.1f,
                max(0f, tremor - 0.2f),
                tremor
            ),
            referenceValue = 3.0f
        ),

        GraphData(
            name = "BMI270 Stability",
            unit = "Stability Index",
            values = listOf(
                stability + 0.8f,
                stability + 0.5f,
                max(0f, stability - 0.2f),
                stability + 0.3f,
                max(0f, stability - 0.4f),
                stability + 0.1f,
                max(0f, stability - 0.1f),
                stability
            ),
            referenceValue = 3.5f
        ),

        GraphData(
            name = "MPU9250 Acceleration",
            unit = "m/s²",
            values = listOf(
                max(0f, movement - 0.5f),
                max(0f, movement - 0.2f),
                movement + 0.1f,
                movement + 0.3f,
                max(0f, movement - 0.1f),
                movement + 0.2f,
                max(0f, movement - 0.2f),
                movement
            ),
            referenceValue = 6.0f
        ),

        GraphData(
            name = "MPU9250 Gyroscope",
            unit = "rad/s",
            values = listOf(
                max(0f, stability - 0.5f),
                max(0f, stability - 0.2f),
                stability + 0.1f,
                stability + 0.4f,
                stability + 0.2f,
                stability + 0.5f,
                stability + 0.1f,
                stability
            ),
            referenceValue = 3.5f
        ),

        GraphData(
            name = "FSR402 Force",
            unit = "N",
            values = listOf(
                max(0f, force - 0.8f),
                max(0f, force - 0.5f),
                max(0f, force - 0.2f),
                force + 0.1f,
                force + 0.4f,
                force + 0.2f,
                force + 0.5f,
                force
            ),
            referenceValue = 3.0f
        ),

        GraphData(
            name = "FlexiForce A201 Pressure",
            unit = "kPa",
            values = listOf(
                max(0f, pressure - 5f),
                max(0f, pressure - 3f),
                max(0f, pressure - 1f),
                pressure + 2f,
                max(0f, pressure - 2f),
                pressure + 3f,
                pressure + 1f,
                pressure
            ),
            referenceValue = null
        )
    )

    // --------------------------------------------------
    // SCREEN
    // --------------------------------------------------

    Column(
        modifier =
            Modifier.fillMaxSize()
    ) {

        // --------------------------------------------------
        // HEADER
        // --------------------------------------------------

        Column(
            modifier =
                Modifier.padding(20.dp)
        ) {

            BackButton(
                navController =
                    navController
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text =
                    "Sensor Graphs",

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
                    "Daily Sensor Visualization",

                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }

        // --------------------------------------------------
        // GRAPH LIST
        // --------------------------------------------------

        LazyColumn(

            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),

            verticalArrangement =
                Arrangement.spacedBy(16.dp),

            contentPadding =
                PaddingValues(20.dp)

        ) {

            // --------------------------------------------------
            // INFORMATION CARD
            // --------------------------------------------------

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
                            Modifier.padding(16.dp)
                    ) {

                        Text(
                            text =
                                "Assessment Visualization",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "These graphs visualize the latest sensor assessment. The current version uses simulated demo readings because the physical sensors have not yet been connected."
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "Reference markers are based on the current demo thresholds used by NeuroSense."
                        )
                    }
                }
            }

            // --------------------------------------------------
            // GRAPHS
            // --------------------------------------------------

            items(graphs) { graph ->

                SensorGraphCard(
                    graph =
                        graph
                )
            }

            // --------------------------------------------------
            // WELLNESS
            // --------------------------------------------------

            item {

                OutlinedButton(

                    onClick = {

                        navController.navigate(
                            "doctor_consultation"
                        )
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(55.dp)

                ) {

                    Text(
                        text =
                            "View Wellness Suggestions"
                    )
                }
            }

            // --------------------------------------------------
            // BACK TO REPORT
            // --------------------------------------------------

            item {

                Button(

                    onClick = {

                        navController.popBackStack()
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(55.dp)

                ) {

                    Text(
                        text =
                            "Back to Report"
                    )
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
}

// --------------------------------------------------
// SENSOR GRAPH CARD
// --------------------------------------------------

@Composable
private fun SensorGraphCard(
    graph: GraphData
) {

    val graphColor =
        MaterialTheme
            .colorScheme
            .primary

    val referenceColor =
        MaterialTheme
            .colorScheme
            .error

    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Text(
                text =
                    graph.name,

                fontSize =
                    19.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    graph.unit,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // --------------------------------------------------
            // GRAPH
            // --------------------------------------------------

            Canvas(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp)

            ) {

                val values =
                    graph.values

                if (values.size < 2) {
                    return@Canvas
                }

                val reference =
                    graph.referenceValue

                val minimumValue =
                    values.minOrNull()
                        ?: 0f

                val maximumValue =
                    values.maxOrNull()
                        ?: 1f

                val graphMinimum =
                    if (reference != null) {

                        minOf(
                            minimumValue,
                            reference
                        )

                    } else {

                        minimumValue
                    }

                val graphMaximum =
                    if (reference != null) {

                        maxOf(
                            maximumValue,
                            reference
                        )

                    } else {

                        maximumValue
                    }

                val range =
                    if (
                        graphMaximum - graphMinimum == 0f
                    ) {

                        1f

                    } else {

                        graphMaximum - graphMinimum
                    }

                val width =
                    size.width

                val height =
                    size.height

                val stepX =
                    width /
                            (values.size - 1)

                // --------------------------------------------------
                // REFERENCE LINE
                // --------------------------------------------------

                if (reference != null) {

                    val referenceY =
                        height -
                                (
                                        (reference -
                                                graphMinimum) /
                                                range
                                        ) *
                                height

                    drawLine(

                        color =
                            referenceColor,

                        start =
                            Offset(
                                0f,
                                referenceY
                            ),

                        end =
                            Offset(
                                width,
                                referenceY
                            ),

                        strokeWidth =
                            3f
                    )
                }

                // --------------------------------------------------
                // GRAPH LINE
                // --------------------------------------------------

                for (
                i in 0 until values.size - 1
                ) {

                    val x1 =
                        i * stepX

                    val x2 =
                        (i + 1) * stepX

                    val y1 =
                        height -
                                (
                                        (values[i] -
                                                graphMinimum) /
                                                range
                                        ) *
                                height

                    val y2 =
                        height -
                                (
                                        (values[i + 1] -
                                                graphMinimum) /
                                                range
                                        ) *
                                height

                    drawLine(

                        color =
                            graphColor,

                        start =
                            Offset(
                                x1,
                                y1
                            ),

                        end =
                            Offset(
                                x2,
                                y2
                            ),

                        strokeWidth =
                            6f
                    )
                }

                // --------------------------------------------------
                // DATA POINTS
                // --------------------------------------------------

                values.forEachIndexed {
                        index,
                        value ->

                    val x =
                        index * stepX

                    val y =
                        height -
                                (
                                        (value -
                                                graphMinimum) /
                                                range
                                        ) *
                                height

                    drawCircle(

                        color =
                            graphColor,

                        radius =
                            6f,

                        center =
                            Offset(
                                x,
                                y
                            )
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            // --------------------------------------------------
            // LATEST VALUE
            // --------------------------------------------------

            val latest =
                graph.values.lastOrNull()
                    ?: 0f

            Text(
                text =
                    "Latest: %.2f %s".format(
                        latest,
                        graph.unit
                    ),

                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,

                fontWeight =
                    FontWeight.SemiBold
            )

            // --------------------------------------------------
            // REFERENCE VALUE
            // --------------------------------------------------

            if (graph.referenceValue != null) {

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Demo reference: %.2f".format(
                            graph.referenceValue
                        ),

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}