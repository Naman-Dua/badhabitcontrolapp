package com.example.badhabitcontrol.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.badhabitcontrol.data.entity.DayRecord
import com.example.badhabitcontrol.data.entity.Habit
import com.example.badhabitcontrol.data.entity.UrgeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Habit::class, DayRecord::class, UrgeEvent::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun dayRecordDao(): DayRecordDao
    abstract fun urgeEventDao(): UrgeEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bad_habit_control.db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed default habits
                            CoroutineScope(Dispatchers.IO).launch {
                                val now = System.currentTimeMillis()
                                getInstance(context).habitDao().insertHabits(
                                    listOf(
                                        Habit(id = 1L, name = "Cigarettes", createdAt = now),
                                        Habit(id = 2L, name = "Masturbation", createdAt = now)
                                    )
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
