
package com.example.neurosense.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.neurosense.screens.SensorAssessmentScreen
import com.example.neurosense.screens.CameraCaptureScreen
import com.example.neurosense.screens.ExistingUserScreen
import com.example.neurosense.screens.FaceRegistrationScreen
import com.example.neurosense.screens.LoginChoiceScreen
import com.example.neurosense.screens.QuestionnaireScreen
import com.example.neurosense.screens.QuestionnaireStep2Screen
import com.example.neurosense.screens.QuestionnaireStep3Screen
import com.example.neurosense.screens.SplashScreen
import com.example.neurosense.viewmodel.RegistrationViewModel
import com.example.neurosense.screens.DashboardScreen
import com.example.neurosense.screens.DailyReportScreen
import com.example.neurosense.screens.SensorGraphsScreen
import com.example.neurosense.screens.PreviousReportsScreen
import com.example.neurosense.screens.DoctorConsultationScreen
import com.example.neurosense.screens.ChatbotScreen
import com.example.neurosense.screens.AssessmentHistoryScreen
import com.example.neurosense.screens.SensorApiTestScreen

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

        composable("questionnaire_step2") {
            QuestionnaireStep2Screen(navController)
        }

        composable("questionnaire_step3") {
            QuestionnaireStep3Screen(navController)
        }

        composable("dashboard") {
            DashboardScreen(navController)
        }

        composable("doctor_consultation") {
            DoctorConsultationScreen(
                navController = navController
            )
        }

        composable("chatbot") {
            ChatbotScreen(
                navController = navController
            )
        }

        composable("sensor_assessment") {
            SensorAssessmentScreen(
                navController = navController
            )
        }

        composable("sensor_graphs") {
            SensorGraphsScreen(navController)
        }

        composable("daily_report") {
            DailyReportScreen(
                navController = navController
            )
        }

        composable("previous_reports") {
            PreviousReportsScreen(
                navController = navController
            )
        }

        composable("assessment_history") {
            AssessmentHistoryScreen(
                navController = navController
            )
        }

        // Temporary test screen for MySQL/PHP connection
        composable("sensor_api_test") {
            SensorApiTestScreen()
        }
    }
}

