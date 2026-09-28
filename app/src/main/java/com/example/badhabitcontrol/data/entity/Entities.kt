package com.example.badhabitcontrol.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey
    val id: Long,
    val name: String,
    val createdAt: Long,
    val active: Boolean = true
)

@Entity(
    tableName = "day_records",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId", "localDate"], unique = true)
    ]
)
data class DayRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val localDate: String, // ISO YYYY-MM-DD
    val status: String,    // CLEAN, URGE_MANAGED, RELAPSE
    val createdAt: Long
)

@Entity(
    tableName = "urge_events",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId"])
    ]
)
data class UrgeEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val createdAt: Long,
    val intensity: String,   // LOW, MEDIUM, HIGH
    val timerSeconds: Int,   // 180, 300, 600, 900
    val trigger: String,     // STRESS, BOREDOM, ALONE, etc.
    val outcome: String      // PASSED, STILL_URGE, RELAPSE
)
