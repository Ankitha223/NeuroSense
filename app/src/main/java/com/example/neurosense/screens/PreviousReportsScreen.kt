package com.example.neurosense.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.neurosense.components.BackButton
import com.example.neurosense.data.UserStorage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun PreviousReportsScreen(
    navController: NavController
) {

    val context = LocalContext.current

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var score by remember {
        mutableStateOf(0)
    }

    var totalQuestions by remember {
        mutableStateOf(0)
    }

    var percentage by remember {
        mutableStateOf(0)
    }

    var result by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {

        try {

            val userStorage =
                UserStorage(context)

            val userId =
                userStorage.getUserId()

            if (userId.isBlank()) {

                errorMessage =
                    "User ID not found."

                isLoading = false

                return@LaunchedEffect
            }

            val document =
                FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("assessments")
                    .document("latest")
                    .get()
                    .await()

            if (document.exists()) {

                score =
                    document
                        .getLong("score")
                        ?.toInt()
                        ?: 0

                totalQuestions =
                    document
                        .getLong("totalQuestions")
                        ?.toInt()
                        ?: 0

                percentage =
                    document
                        .getLong("percentage")
                        ?.toInt()
                        ?: 0

                result =
                    document
                        .getString("result")
                        ?: "Unknown"

            } else {

                errorMessage =
                    "No previous assessment found."
            }

        } catch (e: Exception) {

            errorMessage =
                "Unable to load previous report."
        }

        isLoading = false
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

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

        item {

            Text(
                text =
                    "Previous Reports",

                fontSize =
                    28.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Previous NeuroSense Assessment"
            )
        }

        if (isLoading) {

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier =
                            Modifier.padding(20.dp)
                    ) {

                        CircularProgressIndicator()

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Text(
                            text =
                                "Loading previous report..."
                        )
                    }
                }
            }
        }

        if (
            !isLoading &&
            errorMessage.isNotEmpty()
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
                                    .errorContainer
                        )
                ) {

                    Text(
                        text =
                            errorMessage,

                        modifier =
                            Modifier.padding(18.dp),

                        fontSize =
                            18.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }

        if (
            !isLoading &&
            errorMessage.isEmpty()
        ) {

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                if (
                                    result ==
                                    "Low indication"
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
                                "Assessment Result",

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
                                result,

                            fontSize =
                                24.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }

            item {

                ReportValueCard(
                    title =
                        "Score",

                    value =
                        "$score / $totalQuestions"
                )
            }

            item {

                ReportValueCard(
                    title =
                        "Percentage",

                    value =
                        "$percentage%"
                )
            }

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
                                "This assessment is intended for monitoring support only and does not provide a medical diagnosis."
                        )
                    }
                }
            }

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
        }

        item {

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )
        }
    }
}

@Composable
private fun ReportValueCard(
    title: String,
    value: String
) {

    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(18.dp),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text =
                    title,

                fontSize =
                    17.sp,

                fontWeight =
                    FontWeight.SemiBold
            )

            Text(
                text =
                    value,

                fontSize =
                    17.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}