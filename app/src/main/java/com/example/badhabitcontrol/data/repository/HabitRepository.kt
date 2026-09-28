package com.example.badhabitcontrol.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.badhabitcontrol.data.entity.DayRecord
import com.example.badhabitcontrol.data.entity.Habit
import com.example.badhabitcontrol.data.entity.UrgeEvent
import com.example.badhabitcontrol.data.local.AppDatabase
import com.example.badhabitcontrol.data.model.UrgeIntensity
import com.example.badhabitcontrol.data.model.UrgeOutcome
import com.example.badhabitcontrol.data.model.UrgeTrigger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class HabitRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val habitDao = db.habitDao()
    private val dayRecordDao = db.dayRecordDao()
    private val urgeEventDao = db.urgeEventDao()

    private val KEY_DEFAULT_TIMER = intPreferencesKey("default_timer_seconds")
    private val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")

    fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()

    fun getHabitById(id: Long): Flow<Habit?> = habitDao.getHabitById(id)

    fun getDayRecords(habitId: Long): Flow<List<DayRecord>> = dayRecordDao.getRecordsForHabit(habitId)

    fun getUrgeEvents(habitId: Long): Flow<List<UrgeEvent>> = urgeEventDao.getEventsForHabit(habitId)

    suspend fun recordRelapse(habitId: Long, date: String) {
        val now = System.currentTimeMillis()
        dayRecordDao.recordRelapse(habitId, date, now)
        // Also record an urge event indicating a relapse if none was active
        urgeEventDao.insertEvent(
            UrgeEvent(
                habitId = habitId,
                createdAt = now,
                intensity = UrgeIntensity.MEDIUM.name,
                timerSeconds = 0,
                trigger = UrgeTrigger.RANDOM_OTHER.name,
                outcome = UrgeOutcome.RELAPSE.name
            )
        )
    }

    suspend fun recordUrgeOutcome(
        habitId: Long,
        date: String,
        intensity: UrgeIntensity,
        timerSeconds: Int,
        trigger: UrgeTrigger,
        outcome: UrgeOutcome
    ) {
        val now = System.currentTimeMillis()
        urgeEventDao.insertEvent(
            UrgeEvent(
                habitId = habitId,
                createdAt = now,
                intensity = intensity.name,
                timerSeconds = timerSeconds,
                trigger = trigger.name,
                outcome = outcome.name
            )
        )

        when (outcome) {
            UrgeOutcome.PASSED -> {
                dayRecordDao.recordUrgeManaged(habitId, date, now)
            }
            UrgeOutcome.RELAPSE -> {
                dayRecordDao.recordRelapse(habitId, date, now)
            }
            UrgeOutcome.STILL_URGE -> {
                // STILL_URGE does NOT break streak; does not force relapse
            }
        }
    }

    val defaultTimerSeconds: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_DEFAULT_TIMER] ?: 300 // default 5 minutes (300 sec)
    }

    suspend fun setDefaultTimerSeconds(seconds: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_TIMER] = seconds
        }
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_NOTIFICATIONS] ?: false
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATIONS] = enabled
        }
    }

    suspend fun clearAllData() {
        dayRecordDao.clearAll()
        urgeEventDao.clearAll()
    }
}
