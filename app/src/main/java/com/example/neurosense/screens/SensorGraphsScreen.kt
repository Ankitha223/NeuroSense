
package com.example.neurosense.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class GraphData(
    val name: String,
    val unit: String,
    val values: List<Float>
)

@Composable
fun SensorGraphsScreen(
    navController: NavController
) {

    val graphs = listOf(

        GraphData(
            name = "BMI270 Movement",
            unit = "m/s²",
            values = listOf(
                8.9f,
                9.2f,
                9.7f,
                10.1f,
                9.8f,
                9.5f,
                10.3f,
                9.77f
            )
        ),

        GraphData(
            name = "BMI270 Tremor",
            unit = "Tremor Level",
            values = listOf(
                1.2f,
                1.8f,
                2.4f,
                3.1f,
                2.8f,
                3.5f,
                2.9f,
                3.2f
            )
        ),

        GraphData(
            name = "BMI270 Stability",
            unit = "%",
            values = listOf(
                92f,
                89f,
                85f,
                82f,
                87f,
                84f,
                80f,
                83f
            )
        ),

        GraphData(
            name = "MPU9250 Acceleration",
            unit = "m/s²",
            values = listOf(
                9.1f,
                9.4f,
                9.6f,
                9.8f,
                9.7f,
                9.5f,
                9.9f,
                9.67f
            )
        ),

        GraphData(
            name = "MPU9250 Gyroscope",
            unit = "rad/s",
            values = listOf(
                1.2f,
                1.5f,
                1.9f,
                2.2f,
                2.0f,
                2.4f,
                2.1f,
                2.27f
            )
        ),

        GraphData(
            name = "MPU9250 Orientation",
            unit = "Degrees",
            values = listOf(
                -15f,
                -18f,
                -12f,
                -20f,
                -17f,
                -22f,
                -19f,
                -23.41f
            )
        ),

        GraphData(
            name = "FSR402 Force",
            unit = "N",
            values = listOf(
                3.2f,
                3.8f,
                4.1f,
                4.5f,
                4.8f,
                4.6f,
                4.9f,
                4.82f
            )
        ),

        GraphData(
            name = "FlexiForce A201 Pressure",
            unit = "kPa",
            values = listOf(
                31f,
                34f,
                36f,
                39f,
                38f,
                41f,
                39f,
                38.6f
            )
        )
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = "Sensor Graphs",
            fontSize = 28.sp,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(20.dp)
        )

        Text(
            text = "Daily Sensor Visualization",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                bottom = 12.dp
            )
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(20.dp)
        ) {

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.primaryContainer
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Demo Data",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "The graphs currently use simulated sensor readings because the physical sensors are not connected."
                        )
                    }
                }
            }

            items(graphs) { graph ->

                SensorGraphCard(
                    graph = graph
                )
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
        }
    }
}

@Composable
private fun SensorGraphCard(
    graph: GraphData
) {

    /*
     * Get the Compose color BEFORE entering Canvas.
     *
     * MaterialTheme is @Composable and cannot be
     * called directly inside the Canvas drawing block.
     */
    val graphColor =
        MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = graph.name,
                fontSize = 19.sp,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = graph.unit,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {

                val values = graph.values

                if (values.size < 2) {
                    return@Canvas
                }

                val minValue =
                    values.minOrNull() ?: 0f

                val maxValue =
                    values.maxOrNull() ?: 1f

                val range =
                    if (maxValue - minValue == 0f) {
                        1f
                    } else {
                        maxValue - minValue
                    }

                val width = size.width
                val height = size.height

                val stepX =
                    width / (values.size - 1)

                /*
                 * Draw graph lines.
                 */
                for (i in 0 until values.size - 1) {

                    val x1 =
                        i * stepX

                    val x2 =
                        (i + 1) * stepX

                    val y1 =
                        height -
                                ((values[i] - minValue) / range) *
                                height

                    val y2 =
                        height -
                                ((values[i + 1] - minValue) / range) *
                                height

                    drawLine(
                        color = graphColor,
                        start = Offset(
                            x1,
                            y1
                        ),
                        end = Offset(
                            x2,
                            y2
                        ),
                        strokeWidth = 6f
                    )
                }

                /*
                 * Draw data points.
                 */
                values.forEachIndexed { index, value ->

                    val x =
                        index * stepX

                    val y =
                        height -
                                ((value - minValue) / range) *
                                height

                    drawCircle(
                        color = graphColor,
                        radius = 6f,
                        center = Offset(
                            x,
                            y
                        )
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            val latest =
                graph.values.lastOrNull() ?: 0f

            Text(
                text =
                    "Latest: %.2f %s".format(
                        latest,
                        graph.unit
                    ),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

