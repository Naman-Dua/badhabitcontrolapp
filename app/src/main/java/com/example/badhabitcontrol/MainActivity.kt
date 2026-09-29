package com.example.badhabitcontrol

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.example.badhabitcontrol.ui.navigation.AppNavigation
import com.example.badhabitcontrol.ui.theme.BadHabitTheme

class MainActivity : ComponentActivity() {

    private val directUrgeHabitId = mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
        val app = application as BadHabitApp
        setContent {
            BadHabitTheme {
                AppNavigation(
                    repository = app.repository,
                    directUrgeHabitId = directUrgeHabitId.value,
                    onUrgeHandled = { directUrgeHabitId.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val habitId = intent?.getLongExtra(EXTRA_URGE_HABIT_ID, -1L) ?: -1L
        if (habitId > 0) {
            directUrgeHabitId.value = habitId
        }
    }

    companion object {
        const val EXTRA_URGE_HABIT_ID = "extra_urge_habit_id"
    }
}
