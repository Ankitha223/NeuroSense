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
import androidx.compose.ui.text.input.KeyboardCapitalization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceRegistrationScreen(
    navController: NavController
) {

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var ageError by remember { mutableStateOf("") }
    var genderError by remember { mutableStateOf("") }

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
    val genderFocusRequester = remember { FocusRequester() }
    val captureButtonFocusRequester = remember { FocusRequester() }

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

            OutlinedTextField(
                value = name,
                onValueChange = { input ->

                    val filtered = input.filter {
                        it.isLetter() || it.isWhitespace()
                    }

                    val cleaned = filtered.replace(Regex("\\s+"), " ")

                    name = cleaned.replaceFirstChar {
                        if (it.isLowerCase())
                            it.titlecase()
                        else
                            it.toString()
                    }

                    nameError =
                        when {
                            name.isBlank() ->
                                "Name cannot be empty."

                            name.length < 2 ->
                                "Name should contain at least 2 letters."

                            else -> ""
                        }

                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text("Full Name")
                },

                singleLine = true,

                isError = nameError.isNotEmpty(),

                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),

                keyboardActions = KeyboardActions(
                    onNext = {
                        ageFocusRequester.requestFocus()
                    }
                )

            )

            if (nameError.isNotEmpty()) {

                Text(
                    text = nameError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )

            }

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(

                value = age,

                onValueChange = { input ->

                    val filtered = input.filter {
                        it.isDigit()
                    }

                    if (filtered.length <= 2) {
                        age = filtered
                    }

                    ageError =
                        when {

                            age.isBlank() ->
                                "Age cannot be empty."

                            age.toIntOrNull() == null ->
                                "Please enter a valid age (1–99)."

                            age.toInt() !in 1..99 ->
                                "Please enter a valid age (1–99)."

                            else -> ""

                        }

                },

                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(ageFocusRequester),

                label = {
                    Text("Age")
                },

                singleLine = true,

                isError = ageError.isNotEmpty(),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),

                keyboardActions = KeyboardActions(
                    onNext = {

                        genderFocusRequester.requestFocus()
                        expanded = true

                    }
                )

            )

            if (ageError.isNotEmpty()) {

                Text(
                    text = ageError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )

            }

            Spacer(modifier = Modifier.height(18.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {

                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),

                    keyboardActions = KeyboardActions(
                        onNext = {
                            captureButtonFocusRequester.requestFocus()
                        }
                    ),
                    value = selectedGender,
                    onValueChange = {},
                    readOnly = true,

                    modifier = Modifier
                        .menuAnchor()
                        .focusRequester(genderFocusRequester)
                        .fillMaxWidth(),

                    label = {
                        Text("Gender")
                    },

                    isError = genderError.isNotEmpty(),

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
                                genderError = ""

                                expanded = false

                                captureButtonFocusRequester.requestFocus()

                            }
                        )

                    }

                }

            }

            if (genderError.isNotEmpty()) {

                Text(
                    text = genderError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )

            }

            Spacer(modifier = Modifier.height(30.dp))

            // Validation before enabling Capture Face
            val formValid =
                nameError.isEmpty() &&
                        ageError.isEmpty() &&
                        name.isNotBlank() &&
                        age.isNotBlank() &&
                        selectedGender != "Select Gender"
            // Capture Face Button
            Button(
                onClick = {
                    navController.navigate("camera_capture")
                },

                enabled = formValid,

                modifier = Modifier
                    .focusRequester(captureButtonFocusRequester)
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

            Button(

                onClick = {

                    if (selectedGender == "Select Gender") {

                        genderError = "Please select your gender."
                        return@Button

                    }

                    if (!faceCaptured) {

                        return@Button

                    }

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