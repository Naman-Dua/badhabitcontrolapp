package com.example.badhabitcontrol.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.badhabitcontrol.MainActivity
import com.example.badhabitcontrol.data.entity.Habit
import com.example.badhabitcontrol.data.local.AppDatabase
import com.example.badhabitcontrol.data.model.DayStatus
import com.example.badhabitcontrol.domain.streak.StreakCalculator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class HabitGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = AppDatabase.getInstance(context)
        val today = LocalDate.now()

        // Load Cigarettes (Habit 1)
        val habit1 = db.habitDao().getHabitByIdDirect(1L) ?: Habit(1L, "Cigarettes", System.currentTimeMillis())
        val records1 = db.dayRecordDao().getRecordsForHabitDirect(1L)
        val map1 = records1.associate {
            (StreakCalculator.parseDate(it.localDate) ?: today) to try {
                DayStatus.valueOf(it.status)
            } catch (_: Exception) {
                DayStatus.CLEAN
            }
        }
        val habit1Created = Instant.ofEpochMilli(habit1.createdAt).atZone(ZoneId.systemDefault()).toLocalDate()
        val streak1 = StreakCalculator.calculateCurrentStreak(habit1Created, map1, today)

        // Load Masturbation (Habit 2)
        val habit2 = db.habitDao().getHabitByIdDirect(2L) ?: Habit(2L, "Masturbation", System.currentTimeMillis())
        val records2 = db.dayRecordDao().getRecordsForHabitDirect(2L)
        val map2 = records2.associate {
            (StreakCalculator.parseDate(it.localDate) ?: today) to try {
                DayStatus.valueOf(it.status)
            } catch (_: Exception) {
                DayStatus.CLEAN
            }
        }
        val habit2Created = Instant.ofEpochMilli(habit2.createdAt).atZone(ZoneId.systemDefault()).toLocalDate()
        val streak2 = StreakCalculator.calculateCurrentStreak(habit2Created, map2, today)

        provideContent {
            WidgetContent(
                context = context,
                habit1Name = habit1.name,
                streak1 = streak1,
                habit2Name = habit2.name,
                streak2 = streak2
            )
        }
    }

    @androidx.compose.runtime.Composable
    private fun WidgetContent(
        context: Context,
        habit1Name: String,
        streak1: Int,
        habit2Name: String,
        streak2: Int
    ) {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val urgeIntent1 = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_URGE_HABIT_ID, 1L)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val urgeIntent2 = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_URGE_HABIT_ID, 2L)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // Dark calm theme colors
        val surfaceColor = ColorProvider(Color(0xFF1A1D24))
        val textPrimary = ColorProvider(Color(0xFFE6E9EE))
        val textSecondary = ColorProvider(Color(0xFF8D95A5))
        val statusClean = ColorProvider(Color(0xFF3EB489))
        val buttonBg = ColorProvider(Color(0xFF242833))

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(surfaceColor)
                .cornerRadius(16.dp)
                .padding(12.dp)
                .clickable(actionStartActivity(openAppIntent)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BAD HABIT CONTROL",
                    style = TextStyle(
                        color = textSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Habit 1 Section
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = habit1Name.uppercase(),
                        style = TextStyle(color = textSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$streak1",
                            style = TextStyle(color = textPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = GlanceModifier.width(6.dp))
                        Text(
                            text = "DAYS CLEAN",
                            style = TextStyle(color = statusClean, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Button(
                    text = "⚡ URGE",
                    onClick = actionStartActivity(urgeIntent1),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = buttonBg,
                        contentColor = textPrimary
                    ),
                    modifier = GlanceModifier.height(36.dp)
                )
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Habit 2 Section
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = habit2Name.uppercase(),
                        style = TextStyle(color = textSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$streak2",
                            style = TextStyle(color = textPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = GlanceModifier.width(6.dp))
                        Text(
                            text = "DAYS CLEAN",
                            style = TextStyle(color = statusClean, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Button(
                    text = "⚡ URGE",
                    onClick = actionStartActivity(urgeIntent2),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = buttonBg,
                        contentColor = textPrimary
                    ),
                    modifier = GlanceModifier.height(36.dp)
                )
            }
        }
    }

    companion object {
        suspend fun notifyUpdate(context: Context) {
            try {
                HabitGlanceWidget().updateAll(context)
            } catch (_: Exception) {
                // Ignore if widget is not placed on home screen
            }
        }
    }
}
