package com.example.neurosense.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.example.neurosense.screens.FaceRegistrationScreen
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.neurosense.screens.LoginChoiceScreen
import com.example.neurosense.screens.SplashScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

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
            FaceRegistrationScreen(navController)
        }
    }
}