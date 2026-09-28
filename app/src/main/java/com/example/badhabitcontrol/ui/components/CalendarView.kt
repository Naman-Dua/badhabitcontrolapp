package com.example.badhabitcontrol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.badhabitcontrol.data.model.DayStatus
import com.example.badhabitcontrol.ui.theme.CalmBorder
import com.example.badhabitcontrol.ui.theme.CalmSurface
import com.example.badhabitcontrol.ui.theme.IndicatorToday
import com.example.badhabitcontrol.ui.theme.StatusClean
import com.example.badhabitcontrol.ui.theme.StatusRelapse
import com.example.badhabitcontrol.ui.theme.StatusUrgeManaged
import com.example.badhabitcontrol.ui.theme.TextMuted
import com.example.badhabitcontrol.ui.theme.TextPrimary
import com.example.badhabitcontrol.ui.theme.TextSecondary
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarView(
    recordsByDate: Map<LocalDate, DayStatus>,
    modifier: Modifier = Modifier,
    initialMonth: YearMonth = YearMonth.now(),
    today: LocalDate = LocalDate.now()
) {
    var currentMonth by remember { mutableStateOf(initialMonth) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CalmSurface)
            .border(1.dp, CalmBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        // Month header with navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { currentMonth = currentMonth.minusMonths(1) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Month",
                    tint = TextSecondary
                )
            }

            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()} ${currentMonth.year}",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                letterSpacing = 1.sp
            )

            IconButton(
                onClick = { currentMonth = currentMonth.plusMonths(1) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Month",
                    tint = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Weekday headers: M T W T F S S
        Row(modifier = Modifier.fillMaxWidth()) {
            val weekDays = listOf(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY
            )
            for (day in weekDays) {
                Text(
                    text = day.getDisplayName(TextStyle.NARROW, Locale.ENGLISH),
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dates grid
        val firstDayOfMonth = currentMonth.atDay(1)
        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDayOffset = firstDayOfMonth.dayOfWeek.value - 1 // Monday = 0, Sunday = 6

        val totalCells = ((firstDayOffset + daysInMonth + 6) / 7) * 7
        val rows = totalCells / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - firstDayOffset + 1

                    if (dayNumber in 1..daysInMonth) {
                        val date = currentMonth.atDay(dayNumber)
                        val isToday = (date == today)
                        val status = recordsByDate[date]

                        DayCell(
                            dayNumber = dayNumber,
                            isToday = isToday,
                            status = status,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Empty slot outside current month
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Legend row (accessible symbols + labels)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center
        ) {
            LegendItem(symbol = "✓", label = "Clean", color = StatusClean)
            Spacer(modifier = Modifier.width(12.dp))
            LegendItem(symbol = "⚡", label = "Urge Managed", color = StatusUrgeManaged)
            Spacer(modifier = Modifier.width(12.dp))
            LegendItem(symbol = "×", label = "Relapse", color = StatusRelapse)
            Spacer(modifier = Modifier.width(12.dp))
            LegendItem(symbol = "●", label = "Today", color = IndicatorToday)
        }
    }
}

@Composable
private fun DayCell(
    dayNumber: Int,
    isToday: Boolean,
    status: DayStatus?,
    modifier: Modifier = Modifier
) {
    val statusSymbol = when (status) {
        DayStatus.CLEAN -> "✓"
        DayStatus.URGE_MANAGED -> "⚡"
        DayStatus.RELAPSE -> "×"
        null -> null
    }

    val statusColor = when (status) {
        DayStatus.CLEAN -> StatusClean
        DayStatus.URGE_MANAGED -> StatusUrgeManaged
        DayStatus.RELAPSE -> StatusRelapse
        null -> Color.Transparent
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            // Today indicator: distinct outer ring outline
            .then(
                if (isToday) {
                    Modifier.border(1.5.dp, IndicatorToday, CircleShape)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayNumber.toString(),
                color = if (isToday) TextPrimary else TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
            if (statusSymbol != null) {
                Text(
                    text = statusSymbol,
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 10.sp
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    symbol: String,
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = symbol,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}
