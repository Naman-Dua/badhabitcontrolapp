package com.example.badhabitcontrol.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.badhabitcontrol.data.entity.DayRecord
import com.example.badhabitcontrol.data.entity.Habit
import com.example.badhabitcontrol.data.entity.UrgeEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE active = 1 ORDER BY id ASC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    fun getHabitById(id: Long): Flow<Habit?>

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    suspend fun getHabitByIdDirect(id: Long): Habit?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHabits(habits: List<Habit>)
}

@Dao
interface DayRecordDao {
    @Query("SELECT * FROM day_records WHERE habitId = :habitId ORDER BY localDate ASC")
    fun getRecordsForHabit(habitId: Long): Flow<List<DayRecord>>

    @Query("SELECT * FROM day_records WHERE habitId = :habitId ORDER BY localDate ASC")
    suspend fun getRecordsForHabitDirect(habitId: Long): List<DayRecord>

    @Query("SELECT * FROM day_records WHERE habitId = :habitId AND localDate = :date LIMIT 1")
    suspend fun getRecord(habitId: Long, date: String): DayRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: DayRecord): Long

    @Transaction
    suspend fun recordRelapse(habitId: Long, date: String, nowEpoch: Long) {
        val existing = getRecord(habitId, date)
        val updated = existing?.copy(status = "RELAPSE") ?: DayRecord(
            habitId = habitId,
            localDate = date,
            status = "RELAPSE",
            createdAt = nowEpoch
        )
        insertOrUpdate(updated)
    }

    @Transaction
    suspend fun recordUrgeManaged(habitId: Long, date: String, nowEpoch: Long) {
        val existing = getRecord(habitId, date)
        // If already recorded as RELAPSE today, relapse status is strictly preserved
        if (existing?.status == "RELAPSE") return
        val updated = existing?.copy(status = "URGE_MANAGED") ?: DayRecord(
            habitId = habitId,
            localDate = date,
            status = "URGE_MANAGED",
            createdAt = nowEpoch
        )
        insertOrUpdate(updated)
    }

    @Query("DELETE FROM day_records")
    suspend fun clearAll()
}

@Dao
interface UrgeEventDao {
    @Query("SELECT * FROM urge_events WHERE habitId = :habitId ORDER BY createdAt DESC")
    fun getEventsForHabit(habitId: Long): Flow<List<UrgeEvent>>

    @Query("SELECT * FROM urge_events WHERE habitId = :habitId ORDER BY createdAt DESC")
    suspend fun getEventsForHabitDirect(habitId: Long): List<UrgeEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: UrgeEvent): Long

    @Query("DELETE FROM urge_events")
    suspend fun clearAll()
}
