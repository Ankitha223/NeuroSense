package com.example.neurosense.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.neurosense.screens.CameraCaptureScreen
import com.example.neurosense.screens.ExistingUserScreen
import com.example.neurosense.screens.FaceRegistrationScreen
import com.example.neurosense.screens.LoginChoiceScreen
import com.example.neurosense.screens.QuestionnaireScreen
import com.example.neurosense.screens.SplashScreen
import com.example.neurosense.viewmodel.RegistrationViewModel
import com.example.neurosense.screens.DashboardScreen
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val registrationViewModel: RegistrationViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {

            SplashScreen(navController)
        }

        composable("login_choice") {

            LoginChoiceScreen(navController)
        }

        composable("face_registration") {

            FaceRegistrationScreen(
                navController = navController,
                viewModel = registrationViewModel
            )
        }

        composable("camera_capture") {

            CameraCaptureScreen(
                navController = navController,
                viewModel = registrationViewModel
            )
        }

        composable("existing_user") {

            ExistingUserScreen(
                navController = navController
            )
        }

        composable("questionnaire") {

            QuestionnaireScreen(navController)
        }
        composable("dashboard") {

            DashboardScreen(navController)
        }
    }
}