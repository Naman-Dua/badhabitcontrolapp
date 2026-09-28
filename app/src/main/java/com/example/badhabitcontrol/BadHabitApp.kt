package com.example.badhabitcontrol

import android.app.Application
import com.example.badhabitcontrol.data.repository.HabitRepository

class BadHabitApp : Application() {
    val repository: HabitRepository by lazy {
        HabitRepository(this)
    }
}
