package com.example.neurosense.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceRegistrationScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("Select Gender") }

    val genders = listOf(
        "Male",
        "Female",
        "Other"
    )

    var faceCaptured by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf("") }

    val ageFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "New User Registration",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Please enter your personal details",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Name

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Full Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        ageFocusRequester.requestFocus()
                    }
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Age

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(ageFocusRequester),
                label = { Text("Age") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Gender

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {

                OutlinedTextField(
                    value = selectedGender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {

                    genders.forEach { gender ->

                        DropdownMenuItem(
                            text = {
                                Text(gender)
                            },
                            onClick = {
                                selectedGender = gender
                                expanded = false
                            }
                        )

                    }

                }

            }

            Spacer(modifier = Modifier.height(30.dp))

            // Capture Face Button

            Button(
                onClick = {

                    // Temporary Simulation

                    faceCaptured = true

                    userId =
                        "NS${System.currentTimeMillis().toString().takeLast(6)}"

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = if (faceCaptured)
                        "Face Captured ✓"
                    else
                        "Capture Face",
                    fontSize = 18.sp
                )

            }

            // Show User ID only after capture

            if (faceCaptured) {

                Spacer(modifier = Modifier.height(25.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Generated User ID",
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = userId,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1976D2)
                        )

                    }

                }

            }

            Spacer(modifier = Modifier.height(25.dp))

            // Continue Button

            Button(
                onClick = {
                    navController.navigate("questionnaire")
                },
                enabled = faceCaptured,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = "Continue",
                    fontSize = 18.sp
                )

            }

            Spacer(modifier = Modifier.height(30.dp))

        }

    }

}