package com.example.badhabitcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.badhabitcontrol.ui.navigation.AppNavigation
import com.example.badhabitcontrol.ui.theme.BadHabitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as BadHabitApp
        setContent {
            BadHabitTheme {
                AppNavigation(repository = app.repository)
            }
        }
    }
}
