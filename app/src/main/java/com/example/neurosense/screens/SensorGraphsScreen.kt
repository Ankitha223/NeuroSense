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

data class GraphData(
    val name: String,
    val unit: String,
    val values: List<Float>
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
                movement - 0.8f,
                movement - 0.4f,
                movement - 0.2f,
                movement + 0.3f,
                movement - 0.1f,
                movement + 0.2f,
                movement - 0.3f,
                movement
            )
        ),

        GraphData(
            name = "BMI270 Tremor",
            unit = "Tremor Level",
            values = listOf(
                tremor - 0.6f,
                tremor - 0.3f,
                tremor + 0.2f,
                tremor - 0.1f,
                tremor + 0.4f,
                tremor + 0.1f,
                tremor - 0.2f,
                tremor
            )
        ),

        GraphData(
            name = "BMI270 Stability",
            unit = "Stability Index",
            values = listOf(
                stability + 0.8f,
                stability + 0.5f,
                stability - 0.2f,
                stability + 0.3f,
                stability - 0.4f,
                stability + 0.1f,
                stability - 0.1f,
                stability
            )
        ),

        GraphData(
            name = "MPU9250 Acceleration",
            unit = "m/s²",
            values = listOf(
                movement - 0.5f,
                movement - 0.2f,
                movement + 0.1f,
                movement + 0.3f,
                movement - 0.1f,
                movement + 0.2f,
                movement - 0.2f,
                movement
            )
        ),

        GraphData(
            name = "MPU9250 Gyroscope",
            unit = "rad/s",
            values = listOf(
                stability - 0.5f,
                stability - 0.2f,
                stability + 0.1f,
                stability + 0.4f,
                stability + 0.2f,
                stability + 0.5f,
                stability + 0.1f,
                stability
            )
        ),

        GraphData(
            name = "FSR402 Force",
            unit = "N",
            values = listOf(
                force - 0.8f,
                force - 0.5f,
                force - 0.2f,
                force + 0.1f,
                force + 0.4f,
                force + 0.2f,
                force + 0.5f,
                force
            )
        ),

        GraphData(
            name = "FlexiForce A201 Pressure",
            unit = "kPa",
            values = listOf(
                pressure - 5f,
                pressure - 3f,
                pressure - 1f,
                pressure + 2f,
                pressure - 2f,
                pressure + 3f,
                pressure + 1f,
                pressure
            )
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
                text = "Sensor Graphs",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
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
                                "These graphs visualize the latest sensor assessment. The current readings are simulated because the physical sensors have not yet been connected."
                        )
                    }
                }
            }

            // --------------------------------------------------
            // GRAPHS
            // --------------------------------------------------

            items(graphs) { graph ->

                SensorGraphCard(
                    graph = graph
                )
            }

            // --------------------------------------------------
            // BACK BUTTON
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
                            "Back"
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

                val minValue =
                    values.minOrNull()
                        ?: 0f

                val maxValue =
                    values.maxOrNull()
                        ?: 1f

                val range =
                    if (
                        maxValue - minValue == 0f
                    ) {

                        1f

                    } else {

                        maxValue - minValue
                    }

                val width =
                    size.width

                val height =
                    size.height

                val stepX =
                    width /
                            (values.size - 1)

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
                                                minValue) /
                                                range
                                        ) *
                                height

                    val y2 =
                        height -
                                (
                                        (values[i + 1] -
                                                minValue) /
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
                                                minValue) /
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
        }
    }
}