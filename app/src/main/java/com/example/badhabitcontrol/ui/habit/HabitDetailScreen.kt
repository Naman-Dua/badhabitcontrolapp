package com.example.badhabitcontrol.ui.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.badhabitcontrol.ui.components.CalendarView
import com.example.badhabitcontrol.ui.components.ConfigureFinancialDialog
import com.example.badhabitcontrol.ui.components.FinancialImpactCard
import com.example.badhabitcontrol.ui.theme.CalmBackground
import com.example.badhabitcontrol.ui.theme.CalmBorder
import com.example.badhabitcontrol.ui.theme.CalmSurface
import com.example.badhabitcontrol.ui.theme.StatusClean
import com.example.badhabitcontrol.ui.theme.StatusRelapse
import com.example.badhabitcontrol.ui.theme.TextMuted
import com.example.badhabitcontrol.ui.theme.TextPrimary
import com.example.badhabitcontrol.ui.theme.TextSecondary
import com.example.badhabitcontrol.ui.viewmodel.HabitViewModel

@Composable
fun HabitDetailScreen(
    viewModel: HabitViewModel,
    onBack: () -> Unit,
    onStartUrge: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showRelapseDialog by remember { mutableStateOf(false) }
    var showFinancialConfigDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalmBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = (state.habit?.name ?: "").uppercase(),
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TextSecondary)
            }
        } else {
            // Main streak display
            Text(
                text = state.stats.currentStreak.toString(),
                color = TextPrimary,
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 72.sp
            )

            Text(
                text = "DAYS CLEAN",
                color = StatusClean,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Since ${state.sinceText}",
                color = TextMuted,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Stats row: Current, Longest, Total Clean
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CalmSurface)
                    .border(1.dp, CalmBorder, RoundedCornerShape(12.dp))
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Current", value = state.stats.currentStreak.toString())
                StatItem(label = "Longest", value = state.stats.longestStreak.toString())
                StatItem(label = "Total Clean", value = state.stats.totalCleanDays.toString())
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Calendar
            CalendarView(recordsByDate = state.recordsByDate)

            Spacer(modifier = Modifier.height(24.dp))

            // Financial Impact
            FinancialImpactCard(
                config = state.financialConfig,
                stats = state.financialStats,
                onConfigureClick = { showFinancialConfigDialog = true }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Actions: I'm Having an Urge & Record Relapse
            Button(
                onClick = { state.habit?.let { onStartUrge(it.id) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CalmSurface,
                    contentColor = TextPrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, CalmBorder)
            ) {
                Text(
                    text = "I'M HAVING AN URGE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showRelapseDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = StatusRelapse
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusRelapse.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "RECORD RELAPSE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showRelapseDialog) {
        AlertDialog(
            onDismissRequest = { showRelapseDialog = false },
            title = {
                Text(
                    text = "Record Relapse",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = "This will reset your current ${state.habit?.name ?: "habit"} streak to 0. All past clean days and longest streak records remain permanently saved.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.recordRelapse()
                        showRelapseDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusRelapse,
                        contentColor = TextPrimary
                    )
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRelapseDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CalmSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showFinancialConfigDialog) {
        ConfigureFinancialDialog(
            initialConfig = state.financialConfig,
            habitName = state.habit?.name ?: "Habit",
            onSave = { newConfig ->
                viewModel.updateFinancialConfig(newConfig)
                showFinancialConfigDialog = false
            },
            onDismiss = { showFinancialConfigDialog = false }
        )
    }
}

@Composable
private fun StatItem(
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
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}
