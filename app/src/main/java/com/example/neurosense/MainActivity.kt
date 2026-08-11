package com.example.neurosense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.neurosense.navigation.AppNavigation
import com.example.neurosense.ui.theme.NeuroSenseTheme
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NeuroSenseTheme {
                AppNavigation()
            }
        }

    }
}