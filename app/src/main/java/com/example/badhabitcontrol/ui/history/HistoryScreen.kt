package com.example.badhabitcontrol.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.badhabitcontrol.ui.components.CalendarView
import com.example.badhabitcontrol.ui.components.HeatmapView
import com.example.badhabitcontrol.ui.theme.CalmBackground
import com.example.badhabitcontrol.ui.theme.CalmBorder
import com.example.badhabitcontrol.ui.theme.CalmSurface
import com.example.badhabitcontrol.ui.theme.StatusClean
import com.example.badhabitcontrol.ui.theme.StatusRelapse
import com.example.badhabitcontrol.ui.theme.StatusUrgeManaged
import com.example.badhabitcontrol.ui.theme.TextMuted
import com.example.badhabitcontrol.ui.theme.TextPrimary
import com.example.badhabitcontrol.ui.theme.TextSecondary
import com.example.badhabitcontrol.ui.viewmodel.HistoryViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val selectedHabitId by viewModel.selectedHabitId.collectAsState()
    val state by viewModel.habitDetailState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalmBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "HISTORY & ANALYTICS",
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Habit Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CalmSurface)
                .border(1.dp, CalmBorder, RoundedCornerShape(10.dp))
                .padding(4.dp)
        ) {
            HabitTabButton(
                label = "CIGARETTES",
                isSelected = (selectedHabitId == 1L),
                onClick = { viewModel.selectHabit(1L) },
                modifier = Modifier.weight(1f)
            )
            HabitTabButton(
                label = "MASTURBATION",
                isSelected = (selectedHabitId == 2L),
                onClick = { viewModel.selectHabit(2L) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Metrics Grid (Current, Longest, Clean Days, Urge Management Rate)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CalmSurface)
                .border(1.dp, CalmBorder, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MetricBox(label = "Current", value = "${state.stats.currentStreak}d")
            MetricBox(label = "Longest", value = "${state.stats.longestStreak}d")
            MetricBox(label = "Clean Days", value = "${state.stats.totalCleanDays}d")
            MetricBox(
                label = "Urge Mgt",
                value = String.format(Locale.getDefault(), "%.0f%%", state.stats.urgeManagementRate)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Calendar
        CalendarView(recordsByDate = state.recordsByDate)

        Spacer(modifier = Modifier.height(24.dp))

        // Heatmap
        HeatmapView(data = state.heatmapData)

        Spacer(modifier = Modifier.height(24.dp))

        // Trigger Frequency Breakdown
        if (state.stats.triggerCounts.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CalmSurface)
                    .border(1.dp, CalmBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "TRIGGER FREQUENCY",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                state.stats.triggerCounts.entries
                    .sortedByDescending { it.value }
                    .forEach { (trigger, count) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = trigger.label, color = TextPrimary, fontSize = 13.sp)
                            Text(text = "$count times", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Recent Events
        if (state.events.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CalmSurface)
                    .border(1.dp, CalmBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "RECENT EVENTS",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                val formatter = DateTimeFormatter.ofPattern("d MMM, h:mm a")
                state.events.take(15).forEach { event ->
                    val timeStr = Instant.ofEpochMilli(event.createdAt)
                        .atZone(ZoneId.systemDefault())
                        .format(formatter)

                    val (color, label) = when (event.outcome) {
                        "PASSED" -> StatusClean to "Urge Managed"
                        "STILL_URGE" -> StatusUrgeManaged to "Still an Urge"
                        "RELAPSE" -> StatusRelapse to "Relapse Recorded"
                        else -> TextSecondary to event.outcome
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = label, color = color, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Trigger: ${event.trigger}", color = TextMuted, fontSize = 11.sp)
                        }
                        Text(text = timeStr, color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun HabitTabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) TextPrimary else CalmSurface)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) CalmBackground else TextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}
