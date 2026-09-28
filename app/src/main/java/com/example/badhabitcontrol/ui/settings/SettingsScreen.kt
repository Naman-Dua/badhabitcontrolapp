package com.example.badhabitcontrol.ui.settings

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.badhabitcontrol.ui.theme.CalmBackground
import com.example.badhabitcontrol.ui.theme.CalmBorder
import com.example.badhabitcontrol.ui.theme.CalmSurface
import com.example.badhabitcontrol.ui.theme.StatusRelapse
import com.example.badhabitcontrol.ui.theme.TextMuted
import com.example.badhabitcontrol.ui.theme.TextPrimary
import com.example.badhabitcontrol.ui.theme.TextSecondary
import com.example.badhabitcontrol.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val defaultTimer by viewModel.defaultTimerSeconds.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    var showClearDataDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalmBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SETTINGS",
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Default Urge Delay Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CalmSurface)
                .border(1.dp, CalmBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "DEFAULT URGE TIMER",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val durations = listOf(180 to "3 MIN", 300 to "5 MIN", 600 to "10 MIN", 900 to "15 MIN")
                durations.forEach { (sec, label) ->
                    val isSelected = (sec == defaultTimer)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) TextPrimary else CalmBackground)
                            .border(1.dp, if (isSelected) TextPrimary else CalmBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.setDefaultTimer(sec) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) CalmBackground else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notifications Toggle Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CalmSurface)
                .border(1.dp, CalmBorder, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Timer Alerts",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Notify when urge delay finishes",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { viewModel.setNotifications(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CalmBackground,
                    checkedTrackColor = TextPrimary,
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = CalmSurface
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data Reset Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CalmSurface)
                .border(1.dp, CalmBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "DATA MANAGEMENT",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { showClearDataDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StatusRelapse.copy(alpha = 0.15f),
                    contentColor = StatusRelapse
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusRelapse.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "CLEAR ALL LOCAL DATA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // About Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CalmSurface)
                .border(1.dp, CalmBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "ABOUT",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bad Habit Control",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Calm, private, on-device habit management. No accounts, no cloud sync, no gamification.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = {
                Text("Clear All Data", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            },
            text = {
                Text(
                    "Are you sure you want to delete all recorded days and urge events? This cannot be undone.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusRelapse,
                        contentColor = TextPrimary
                    )
                ) {
                    Text("Delete Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CalmSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
