
package com.example.neurosense.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.neurosense.alerts.AlertManager
import com.example.neurosense.components.BackButton
import com.example.neurosense.data.AssessmentData
import com.example.neurosense.data.UserStorage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlin.math.max
import kotlin.random.Random
// --------------------------------------------------
// ASSESSMENT TASK
// --------------------------------------------------

data class AssessmentTask(
    val title: String,
    val instruction: String,
    val gifResource: Int
)

// --------------------------------------------------
// SENSOR ASSESSMENT SCREEN
// --------------------------------------------------

@Composable
fun SensorAssessmentScreen(
    navController: NavController
) {

    // --------------------------------------------------
    // CONTEXT
    // --------------------------------------------------

    val context = LocalContext.current

    // --------------------------------------------------
    // ASSESSMENT STATE
    // --------------------------------------------------

    var isTesting by remember {
        mutableStateOf(false)
    }

    var assessmentCompleted by remember {
        mutableStateOf(false)
    }

    var currentTaskIndex by remember {
        mutableStateOf(0)
    }

    var progress by remember {
        mutableStateOf(0f)
    }

    // --------------------------------------------------
    // SENSOR VALUES
    // --------------------------------------------------

    var tremorValue by remember {
        mutableStateOf(0.0)
    }

    var movementValue by remember {
        mutableStateOf(0.0)
    }

    var stabilityValue by remember {
        mutableStateOf(0.0)
    }

    var forceValue by remember {
        mutableStateOf(0.0)
    }

    var pressureValue by remember {
        mutableStateOf(0.0)
    }

    // --------------------------------------------------
    // LIVE GRAPH DATA
    // --------------------------------------------------

    var liveGraphValues by remember {
        mutableStateOf(listOf<Float>())
    }

    // --------------------------------------------------
    // ASSESSMENT TIMING
    // --------------------------------------------------

    var assessmentStartTime by remember {
        mutableStateOf(0L)
    }

    var assessmentEndTime by remember {
        mutableStateOf(0L)
    }

    var assessmentDurationSeconds by remember {
        mutableStateOf(0L)
    }

    // --------------------------------------------------
    // FIREBASE STATUS
    // --------------------------------------------------

    var saveMessage by remember {
        mutableStateOf("")
    }

    // --------------------------------------------------
    // FOUR ASSESSMENT TASKS
    // --------------------------------------------------

    val assessmentTasks = remember {

        listOf(

            AssessmentTask(
                title = "Hand Movement",
                instruction =
                    "Follow the hand movement shown in the animation.",
                gifResource =
                    com.example.neurosense.R.drawable.hand_move
            ),

            AssessmentTask(
                title = "Hand Up and Down",
                instruction =
                    "Move your hand up and down following the animation.",
                gifResource =
                    com.example.neurosense.R.drawable.handmove_up_down
            ),

            AssessmentTask(
                title = "Finger Tapping",
                instruction =
                    "Perform the tapping movement shown in the animation.",
                gifResource =
                    com.example.neurosense.R.drawable.tapping
            ),

            AssessmentTask(
                title = "Wrist Rotation",
                instruction =
                    "Rotate your wrist following the movement shown in the animation.",
                gifResource =
                    com.example.neurosense.R.drawable.wrist_rotation
            )
        )
    }

    // --------------------------------------------------
    // START SENSOR TEST
    // --------------------------------------------------

    fun startSensorTest() {

        // Record the exact time the complete assessment starts.
        assessmentStartTime = System.currentTimeMillis()
        assessmentEndTime = 0L
        assessmentDurationSeconds = 0L

        isTesting = true

        assessmentCompleted = false

        currentTaskIndex = 0

        progress = 0f

        saveMessage = ""

        tremorValue = 0.0

        movementValue = 0.0

        stabilityValue = 0.0

        forceValue = 0.0

        pressureValue = 0.0

        liveGraphValues = emptyList()
    }

    // --------------------------------------------------
    // SIMULATED LIVE SENSOR DATA
    // --------------------------------------------------

    LaunchedEffect(isTesting) {

        if (isTesting) {

            liveGraphValues = emptyList()

            // ------------------------------------------
            // FOUR TASKS
            // ------------------------------------------

            for (taskIndex in assessmentTasks.indices) {

                currentTaskIndex = taskIndex

                liveGraphValues = emptyList()

                // --------------------------------------
                // EACH TASK RUNS FOR 5 SECONDS
                // --------------------------------------

                for (second in 1..50) {

                    delay(100)

                    // ----------------------------------
                    // SIMULATED SENSOR VALUES
                    // ----------------------------------

                    val newTremor =
                        Random.nextDouble(
                            0.5,
                            4.5
                        )

                    val newMovement =
                        Random.nextDouble(
                            5.0,
                            10.0
                        )

                    val newStability =
                        Random.nextDouble(
                            0.5,
                            5.0
                        )

                    val newForce =
                        Random.nextDouble(
                            2.0,
                            8.0
                        )

                    val newPressure =
                        Random.nextDouble(
                            20.0,
                            50.0
                        )

                    // ----------------------------------
                    // UPDATE SENSOR VALUES
                    // ----------------------------------

                    tremorValue = newTremor

                    movementValue = newMovement

                    stabilityValue = newStability

                    forceValue = newForce

                    pressureValue = newPressure

                    // ----------------------------------
                    // GRAPH VALUE
                    // ----------------------------------

                    val graphValue =
                        when (taskIndex) {

                            0 ->
                                newMovement.toFloat()

                            1 ->
                                newStability.toFloat()

                            2 ->
                                newTremor.toFloat()

                            else ->
                                newMovement.toFloat()
                        }

                    liveGraphValues =
                        (
                                liveGraphValues +
                                        graphValue
                                ).takeLast(40)

                    // ----------------------------------
                    // PROGRESS
                    // ----------------------------------

                    val taskProgress =
                        second / 50f

                    progress =
                        (
                                taskIndex +
                                        taskProgress
                                ) /
                                assessmentTasks.size
                }
            }

            // --------------------------------------------------
            // FINAL VALUES
            // --------------------------------------------------

            tremorValue =
                Random.nextDouble(
                    0.5,
                    4.5
                )

            movementValue =
                Random.nextDouble(
                    5.0,
                    10.0
                )

            stabilityValue =
                Random.nextDouble(
                    0.5,
                    5.0
                )

            forceValue =
                Random.nextDouble(
                    2.0,
                    8.0
                )

            pressureValue =
                Random.nextDouble(
                    20.0,
                    50.0
                )

            // --------------------------------------------------
            // SAVE TO ASSESSMENT DATA
            // --------------------------------------------------

            AssessmentData.tremorValue =
                tremorValue

            AssessmentData.movementValue =
                movementValue

            AssessmentData.stabilityValue =
                stabilityValue

            AssessmentData.forceValue =
                forceValue

            AssessmentData.pressureValue =
                pressureValue

            AssessmentData.assessmentTimestamp =
                System.currentTimeMillis()

            // --------------------------------------------------
            // CHECK ALERTS
            // --------------------------------------------------

            AlertManager.checkAllReadings(

                context = context,

                tremorValue =
                    tremorValue,

                movementValue =
                    movementValue,

                stabilityValue =
                    stabilityValue,

                forceValue =
                    forceValue,

                pressureValue =
                    pressureValue
            )

            // --------------------------------------------------
            // ASSESSMENT END TIME + DURATION
            // --------------------------------------------------

            assessmentEndTime = System.currentTimeMillis()

            val durationMillis =
                assessmentEndTime - assessmentStartTime

            assessmentDurationSeconds =
                (durationMillis / 1000L).coerceAtLeast(0L)

            // --------------------------------------------------
            // SAVE TO FIREBASE
            // --------------------------------------------------

            try {

                val userStorage =
                    UserStorage(context)

                val userId =
                    userStorage.getUserId()

                if (userId.isBlank()) {

                    saveMessage =
                        "Assessment completed, but user ID was not found."

                } else {

                    // Use the assessment end time as the report timestamp.
                    val timestamp =
                        assessmentEndTime

                    val assessmentData =
                        hashMapOf(

                            // Existing timestamp used by the app.
                            "timestamp" to timestamp,

                            // New history timing fields.
                            "startTime" to assessmentStartTime,

                            "endTime" to assessmentEndTime,

                            "durationSeconds" to assessmentDurationSeconds,

                            // Sensor results.
                            "tremor" to tremorValue,

                            "movement" to movementValue,

                            "stability" to stabilityValue,

                            "force" to forceValue,

                            "pressure" to pressureValue
                        )

                    FirebaseFirestore
                        .getInstance()
                        .collection("users")
                        .document(userId)
                        .collection("sensorAssessments")
                        .document(timestamp.toString())
                        .set(assessmentData)
                        .await()

                    saveMessage =
                        "Assessment saved successfully."
                }

            } catch (e: Exception) {

                saveMessage =
                    "Assessment completed, but could not be saved to Firebase."
            }

            // --------------------------------------------------
            // COMPLETE ASSESSMENT
            // --------------------------------------------------

            currentTaskIndex =
                assessmentTasks.lastIndex

            progress = 1f

            isTesting = false

            assessmentCompleted = true
        }
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
                    "Sensor Assessment",

                fontSize =
                    28.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "NeuroSense analyzes movement, tremor, stability, force and pressure patterns."
            )
        }

        // --------------------------------------------------
        // ASSESSMENT MODE
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
                                .secondaryContainer
                    )
            ) {

                Column(

                    modifier =
                        Modifier.padding(18.dp)
                ) {

                    Text(

                        text =
                            "Assessment Mode",

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
                            "Demo Mode",

                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(

                        text =
                            "The current version uses simulated sensor readings. Actual hardware integration will be added later."
                    )
                }
            }
        }

        // --------------------------------------------------
        // CURRENT TASK GIF + LIVE GRAPH
        // --------------------------------------------------

        if (isTesting) {

            item {

                val currentTask =
                    assessmentTasks[currentTaskIndex]

                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Column(

                        modifier =
                            Modifier.padding(12.dp)
                    ) {

                        // ----------------------------------
                        // TASK TITLE
                        // ----------------------------------

                        Text(

                            text =
                                "Task ${currentTaskIndex + 1} of ${assessmentTasks.size}",

                            fontSize =
                                20.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(

                            text =
                                currentTask.title,

                            fontSize =
                                18.sp,

                            fontWeight =
                                FontWeight.SemiBold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                currentTask.instruction
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        // ----------------------------------
                        // GIF + GRAPH
                        // ----------------------------------

                        Row(

                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {

                            // ------------------------------
                            // GIF
                            // ------------------------------

                            Card(

                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(230.dp)
                            ) {

                                AsyncImage(

                                    model =
                                        ImageRequest
                                            .Builder(context)
                                            .data(
                                                currentTask.gifResource
                                            )
                                            .build(),

                                    contentDescription =
                                        currentTask.title,

                                    modifier =
                                        Modifier.fillMaxSize()
                                )
                            }

                            // ------------------------------
                            // LIVE GRAPH
                            // ------------------------------

                            Card(

                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(230.dp)
                            ) {

                                Column(

                                    modifier =
                                        Modifier.padding(8.dp)
                                ) {

                                    Text(

                                        text =
                                            "Live Sensor",

                                        fontSize =
                                            15.sp,

                                        fontWeight =
                                            FontWeight.Bold
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(8.dp)
                                    )

                                    LiveSensorGraph(
                                        values =
                                            liveGraphValues
                                    )
                                }
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        // ----------------------------------
                        // PROGRESS
                        // ----------------------------------

                        LinearProgressIndicator(

                            progress = {
                                progress
                            },

                            modifier =
                                Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        Text(

                            text =
                                "${(progress * 100).toInt()}% completed"
                        )
                    }
                }
            }
        }

        // --------------------------------------------------
        // INSTRUCTIONS BEFORE START
        // --------------------------------------------------

        if (!isTesting && !assessmentCompleted) {

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
                                "Assessment Instructions",

                            fontSize =
                                20.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )

                        Text(

                            text =
                                "You will complete four simple movement tasks.\n\n" +
                                        "Follow the animated instruction for each task and perform the movement naturally.\n\n" +
                                        "Sensor readings will be displayed as a live graph while you perform each task."
                        )
                    }
                }
            }
        }

        // --------------------------------------------------
        // START BUTTON
        // --------------------------------------------------

        if (!isTesting && !assessmentCompleted) {

            item {

                Button(

                    onClick = {
                        startSensorTest()
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(55.dp)

                ) {

                    Text(

                        text =
                            "Start Sensor Assessment",

                        fontSize =
                            17.sp
                    )
                }
            }
        }

        // --------------------------------------------------
        // COMPLETION MESSAGE
        // --------------------------------------------------

        if (assessmentCompleted) {

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
                                "Assessment Completed",

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
                                "All four assessment tasks have been completed successfully."
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(

                            text =
                                "Sensor usage duration: ${assessmentDurationSeconds}s",

                            fontWeight =
                                FontWeight.SemiBold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(

                            text =
                                "The displayed readings are simulated demo values."
                        )
                    }
                }
            }
        }

        // --------------------------------------------------
        // FIREBASE MESSAGE
        // --------------------------------------------------

        if (
            assessmentCompleted &&
            saveMessage.isNotEmpty()
        ) {

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

                    Text(

                        text =
                            saveMessage,

                        modifier =
                            Modifier.padding(16.dp),

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }

        // --------------------------------------------------
        // RESULTS
        // --------------------------------------------------

        if (assessmentCompleted) {

            item {

                Text(

                    text =
                        "Sensor Results",

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            item {

                SensorResultCard(

                    sensorName =
                        "BMI270 - Tremor",

                    value =
                        String.format(
                            "%.2f",
                            tremorValue
                        ),

                    unit =
                        "Intensity"
                )
            }

            item {

                SensorResultCard(

                    sensorName =
                        "BMI270 - Movement",

                    value =
                        String.format(
                            "%.2f",
                            movementValue
                        ),

                    unit =
                        "Movement Index"
                )
            }

            item {

                SensorResultCard(

                    sensorName =
                        "BMI270 - Stability",

                    value =
                        String.format(
                            "%.2f",
                            stabilityValue
                        ),

                    unit =
                        "Stability Index"
                )
            }

            item {

                SensorResultCard(

                    sensorName =
                        "MPU9250 - Acceleration",

                    value =
                        String.format(
                            "%.2f",
                            movementValue
                        ),

                    unit =
                        "m/s²"
                )
            }

            item {

                SensorResultCard(

                    sensorName =
                        "MPU9250 - Gyroscope",

                    value =
                        String.format(
                            "%.2f",
                            stabilityValue
                        ),

                    unit =
                        "rad/s"
                )
            }

            item {

                SensorResultCard(

                    sensorName =
                        "FSR402 - Force",

                    value =
                        String.format(
                            "%.2f",
                            forceValue
                        ),

                    unit =
                        "N"
                )
            }

            item {

                SensorResultCard(

                    sensorName =
                        "FlexiForce A201 - Pressure",

                    value =
                        String.format(
                            "%.2f",
                            pressureValue
                        ),

                    unit =
                        "kPa"
                )
            }

            // --------------------------------------------------
            // VIEW REPORT
            // --------------------------------------------------

            item {

                Button(

                    onClick = {

                        navController.navigate(
                            "daily_report"
                        )
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(55.dp)

                ) {

                    Text(

                        text =
                            "View Assessment Report",

                        fontSize =
                            17.sp
                    )
                }
            }
        }

        // --------------------------------------------------
        // DISCLAIMER
        // --------------------------------------------------

        item {

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(

                text =
                    "NeuroSense provides monitoring support and does not replace professional medical diagnosis.",

                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )
        }
    }
}

// --------------------------------------------------
// LIVE SENSOR GRAPH
// --------------------------------------------------

@Composable
private fun LiveSensorGraph(
    values: List<Float>
) {

    val graphColor =
        MaterialTheme.colorScheme.primary

    Canvas(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(175.dp)

    ) {

        if (values.size < 2) {

            return@Canvas
        }

        val minimum =
            values.minOrNull() ?: 0f

        val maximum =
            values.maxOrNull() ?: 1f

        val graphMinimum =
            minimum

        val graphMaximum =
            max(
                maximum,
                minimum + 0.1f
            )

        val range =
            graphMaximum -
                    graphMinimum

        val stepX =
            size.width /
                    (values.size - 1)

        for (i in 0 until values.size - 1) {

            val x1 =
                i * stepX

            val x2 =
                (i + 1) * stepX

            val y1 =
                size.height -
                        (
                                (
                                        values[i] -
                                                graphMinimum
                                        ) /
                                        range
                                ) *
                        size.height

            val y2 =
                size.height -
                        (
                                (
                                        values[i + 1] -
                                                graphMinimum
                                        ) /
                                        range
                                ) *
                        size.height

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
                    5f
            )
        }

        values.forEachIndexed {
                index,
                value ->

            val x =
                index * stepX

            val y =
                size.height -
                        (
                                (
                                        value -
                                                graphMinimum
                                        ) /
                                        range
                                ) *
                        size.height

            drawCircle(

                color =
                    graphColor,

                radius =
                    4f,

                center =
                    Offset(
                        x,
                        y
                    )
            )
        }
    }
}

// --------------------------------------------------
// SENSOR RESULT CARD
// --------------------------------------------------

@Composable
private fun SensorResultCard(

    sensorName: String,

    value: String,

    unit: String
) {

    Card(

        modifier =
            Modifier.fillMaxWidth()
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Column(

                modifier =
                    Modifier.weight(1f)
            ) {

                Text(

                    text =
                        sensorName,

                    fontSize =
                        17.sp,

                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        unit
                )
            }

            Text(

                text =
                    value,

                fontSize =
                    16.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}
