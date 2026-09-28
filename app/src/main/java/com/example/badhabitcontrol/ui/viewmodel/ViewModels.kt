package com.example.badhabitcontrol.ui.viewmodel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.badhabitcontrol.data.entity.DayRecord
import com.example.badhabitcontrol.data.entity.Habit
import com.example.badhabitcontrol.data.entity.UrgeEvent
import com.example.badhabitcontrol.data.model.DayStatus
import com.example.badhabitcontrol.data.model.HabitStats
import com.example.badhabitcontrol.data.model.UrgeIntensity
import com.example.badhabitcontrol.data.model.UrgeOutcome
import com.example.badhabitcontrol.data.model.UrgeTrigger
import com.example.badhabitcontrol.data.repository.HabitRepository
import com.example.badhabitcontrol.domain.streak.StreakCalculator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class HabitCardState(
    val habit: Habit,
    val currentStreak: Int,
    val sinceDateText: String
)

data class HomeUiState(
    val cigarettes: HabitCardState? = null,
    val masturbation: HabitCardState? = null,
    val isLoading: Boolean = true
)

class HomeViewModel(private val repository: HabitRepository) : ViewModel() {

    private val today: LocalDate get() = LocalDate.now()

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAllHabits(),
        repository.getDayRecords(1L),
        repository.getDayRecords(2L)
    ) { habits, cigRecords, mastRecords ->
        val cigHabit = habits.find { it.id == 1L } ?: Habit(1L, "Cigarettes", System.currentTimeMillis())
        val mastHabit = habits.find { it.id == 2L } ?: Habit(2L, "Masturbation", System.currentTimeMillis())

        val cigCreated = Instant.ofEpochMilli(cigHabit.createdAt).atZone(ZoneId.systemDefault()).toLocalDate()
        val mastCreated = Instant.ofEpochMilli(mastHabit.createdAt).atZone(ZoneId.systemDefault()).toLocalDate()

        val cigMap = cigRecords.associate {
            (StreakCalculator.parseDate(it.localDate) ?: today) to try {
                DayStatus.valueOf(it.status)
            } catch (_: Exception) {
                DayStatus.CLEAN
            }
        }
        val mastMap = mastRecords.associate {
            (StreakCalculator.parseDate(it.localDate) ?: today) to try {
                DayStatus.valueOf(it.status)
            } catch (_: Exception) {
                DayStatus.CLEAN
            }
        }

        val cigStreak = StreakCalculator.calculateCurrentStreak(cigCreated, cigMap, today)
        val mastStreak = StreakCalculator.calculateCurrentStreak(mastCreated, mastMap, today)

        val formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy")
        val cigSince = cigCreated.format(formatter)
        val mastSince = mastCreated.format(formatter)

        HomeUiState(
            cigarettes = HabitCardState(cigHabit, cigStreak, cigSince),
            masturbation = HabitCardState(mastHabit, mastStreak, mastSince),
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
}

data class HabitDetailUiState(
    val habit: Habit? = null,
    val stats: HabitStats = HabitStats(),
    val sinceText: String = "",
    val recordsByDate: Map<LocalDate, DayStatus> = emptyMap(),
    val events: List<UrgeEvent> = emptyList(),
    val isLoading: Boolean = true
)

class HabitViewModel(
    private val habitId: Long,
    private val repository: HabitRepository
) : ViewModel() {

    private val today: LocalDate get() = LocalDate.now()

    val uiState: StateFlow<HabitDetailUiState> = combine(
        repository.getHabitById(habitId),
        repository.getDayRecords(habitId),
        repository.getUrgeEvents(habitId)
    ) { habit, dayRecords, events ->
        val effectiveHabit = habit ?: Habit(
            habitId,
            if (habitId == 1L) "Cigarettes" else "Masturbation",
            System.currentTimeMillis()
        )
        val habitCreated = Instant.ofEpochMilli(effectiveHabit.createdAt).atZone(ZoneId.systemDefault()).toLocalDate()

        val recordMap = dayRecords.associate {
            (StreakCalculator.parseDate(it.localDate) ?: today) to try {
                DayStatus.valueOf(it.status)
            } catch (_: Exception) {
                DayStatus.CLEAN
            }
        }

        val stats = StreakCalculator.computeStats(habitCreated, dayRecords, events, today)
        val sinceText = habitCreated.format(DateTimeFormatter.ofPattern("d MMMM, yyyy"))

        HabitDetailUiState(
            habit = effectiveHabit,
            stats = stats,
            sinceText = sinceText,
            recordsByDate = recordMap,
            events = events,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HabitDetailUiState())

    fun recordRelapse() {
        viewModelScope.launch {
            repository.recordRelapse(habitId, StreakCalculator.formatDate(today))
        }
    }
}

enum class UrgeStep {
    SETUP,
    TIMER,
    OUTCOME,
    DONE
}

data class UrgeUiState(
    val step: UrgeStep = UrgeStep.SETUP,
    val intensity: UrgeIntensity = UrgeIntensity.MEDIUM,
    val timerSeconds: Int = 300,
    val trigger: UrgeTrigger = UrgeTrigger.STRESS,
    val remainingSeconds: Int = 300,
    val isTimerRunning: Boolean = false
)

class UrgeViewModel(
    private val habitId: Long,
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UrgeUiState())
    val uiState: StateFlow<UrgeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var timerEndTimeMs = 0L

    init {
        viewModelScope.launch {
            repository.defaultTimerSeconds.collect { defaultSec ->
                if (_uiState.value.step == UrgeStep.SETUP) {
                    _uiState.value = _uiState.value.copy(
                        timerSeconds = defaultSec,
                        remainingSeconds = defaultSec
                    )
                }
            }
        }
    }

    fun setIntensity(intensity: UrgeIntensity) {
        _uiState.value = _uiState.value.copy(intensity = intensity)
    }

    fun setTimerDuration(seconds: Int) {
        _uiState.value = _uiState.value.copy(
            timerSeconds = seconds,
            remainingSeconds = seconds
        )
    }

    fun setTrigger(trigger: UrgeTrigger) {
        _uiState.value = _uiState.value.copy(trigger = trigger)
    }

    fun startTimer() {
        val totalSec = _uiState.value.timerSeconds
        timerEndTimeMs = SystemClock.elapsedRealtime() + (totalSec * 1000L)
        _uiState.value = _uiState.value.copy(
            step = UrgeStep.TIMER,
            isTimerRunning = true,
            remainingSeconds = totalSec
        )

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val remainingMs = timerEndTimeMs - SystemClock.elapsedRealtime()
                val remainingSec = (remainingMs / 1000L).coerceAtLeast(0L).toInt()
                _uiState.value = _uiState.value.copy(remainingSeconds = remainingSec)

                if (remainingSec <= 0) {
                    _uiState.value = _uiState.value.copy(
                        step = UrgeStep.OUTCOME,
                        isTimerRunning = false
                    )
                    break
                }
                delay(500)
            }
        }
    }

    fun finishTimerEarly() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            step = UrgeStep.OUTCOME,
            isTimerRunning = false,
            remainingSeconds = 0
        )
    }

    fun recordOutcome(outcome: UrgeOutcome) {
        viewModelScope.launch {
            val todayStr = StreakCalculator.formatDate(LocalDate.now())
            val state = _uiState.value
            repository.recordUrgeOutcome(
                habitId = habitId,
                date = todayStr,
                intensity = state.intensity,
                timerSeconds = state.timerSeconds,
                trigger = state.trigger,
                outcome = outcome
            )
            _uiState.value = _uiState.value.copy(step = UrgeStep.DONE)
        }
    }
}

class HistoryViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _selectedHabitId = MutableStateFlow(1L)
    val selectedHabitId: StateFlow<Long> = _selectedHabitId.asStateFlow()

    private val today: LocalDate get() = LocalDate.now()

    fun selectHabit(id: Long) {
        _selectedHabitId.value = id
    }

    val habitDetailState: StateFlow<HabitDetailUiState> = combine(
        _selectedHabitId,
        repository.getAllHabits()
    ) { habitId, habits ->
        val habit = habits.find { it.id == habitId } ?: Habit(
            habitId,
            if (habitId == 1L) "Cigarettes" else "Masturbation",
            System.currentTimeMillis()
        )
        habit
    }.combine(repository.getDayRecords(1L)) { habit, cigRecords ->
        habit to cigRecords
    }.combine(repository.getDayRecords(2L)) { (habit, cigRecords), mastRecords ->
        val records = if (habit.id == 1L) cigRecords else mastRecords
        habit to records
    }.combine(repository.getUrgeEvents(1L)) { (habit, records), cigEvents ->
        Triple(habit, records, cigEvents)
    }.combine(repository.getUrgeEvents(2L)) { (habit, records, cigEvents), mastEvents ->
        val events = if (habit.id == 1L) cigEvents else mastEvents
        val habitCreated = Instant.ofEpochMilli(habit.createdAt).atZone(ZoneId.systemDefault()).toLocalDate()

        val recordMap = records.associate {
            (StreakCalculator.parseDate(it.localDate) ?: today) to try {
                DayStatus.valueOf(it.status)
            } catch (_: Exception) {
                DayStatus.CLEAN
            }
        }

        val stats = StreakCalculator.computeStats(habitCreated, records, events, today)
        val sinceText = habitCreated.format(DateTimeFormatter.ofPattern("d MMMM, yyyy"))

        HabitDetailUiState(
            habit = habit,
            stats = stats,
            sinceText = sinceText,
            recordsByDate = recordMap,
            events = events,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HabitDetailUiState())
}

class SettingsViewModel(private val repository: HabitRepository) : ViewModel() {

    val defaultTimerSeconds: StateFlow<Int> = repository.defaultTimerSeconds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 300)

    val notificationsEnabled: StateFlow<Boolean> = repository.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setDefaultTimer(seconds: Int) {
        viewModelScope.launch {
            repository.setDefaultTimerSeconds(seconds)
        }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.setNotificationsEnabled(enabled)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }
}

class AppViewModelFactory(
    private val repository: HabitRepository,
    private val habitId: Long = 1L
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(repository) as T
            modelClass.isAssignableFrom(HabitViewModel::class.java) ->
                HabitViewModel(habitId, repository) as T
            modelClass.isAssignableFrom(UrgeViewModel::class.java) ->
                UrgeViewModel(habitId, repository) as T
            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(repository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
