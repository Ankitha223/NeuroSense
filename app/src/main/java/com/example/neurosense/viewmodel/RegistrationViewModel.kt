package com.example.neurosense.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegistrationViewModel : ViewModel() {

    var name by mutableStateOf("")
    var age by mutableStateOf("")
    var gender by mutableStateOf("Select Gender")
    var faceImagePath by mutableStateOf("")

    var faceCaptured by mutableStateOf(false)

    var userId by mutableStateOf("")

}