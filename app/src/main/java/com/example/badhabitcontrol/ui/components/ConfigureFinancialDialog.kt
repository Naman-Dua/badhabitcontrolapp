package com.example.badhabitcontrol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.badhabitcontrol.data.model.FinancialConfig
import com.example.badhabitcontrol.ui.theme.CalmBackground
import com.example.badhabitcontrol.ui.theme.CalmBorder
import com.example.badhabitcontrol.ui.theme.CalmSurface
import com.example.badhabitcontrol.ui.theme.TextMuted
import com.example.badhabitcontrol.ui.theme.TextPrimary
import com.example.badhabitcontrol.ui.theme.TextSecondary

@Composable
fun ConfigureFinancialDialog(
    initialConfig: FinancialConfig,
    habitName: String,
    onSave: (FinancialConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var enabled by remember { mutableStateOf(initialConfig.enabled) }
    var dailyCostText by remember { mutableStateOf(if (initialConfig.dailyCost > 0) initialConfig.dailyCost.toString() else "10.0") }
    var currency by remember { mutableStateOf(initialConfig.currencySymbol) }
    var unitsPerDayText by remember { mutableStateOf(if (initialConfig.unitsPerDay > 0) initialConfig.unitsPerDay.toString() else "15.0") }
    var unitName by remember { mutableStateOf(initialConfig.unitName) }

    val currencies = listOf("$", "€", "£", "₹", "¥")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Financial Settings • $habitName",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Enabled Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Track Money Saved",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                    Switch(
                        checked = enabled,
                        onCheckedChange = { enabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CalmBackground,
                            checkedTrackColor = TextPrimary,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = CalmSurface
                        )
                    )
                }

                if (enabled) {
                    Spacer(modifier = Modifier.height(14.dp))

                    // Currency selector
                    Text(text = "CURRENCY", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currencies.forEach { sym ->
                            val isSelected = (sym == currency)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) TextPrimary else CalmBackground)
                                    .border(1.dp, if (isSelected) TextPrimary else CalmBorder, RoundedCornerShape(6.dp))
                                    .clickable { currency = sym }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sym,
                                    color = if (isSelected) CalmBackground else TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Daily cost
                    Text(text = "ESTIMATED DAILY COST", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = dailyCostText,
                        onValueChange = { dailyCostText = it },
                        modifier = Modifier.fillMaxWidth(),
                        prefix = { Text(currency, color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextPrimary,
                            unfocusedBorderColor = CalmBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Units avoided daily
                    Text(text = "UNITS CONSUMED DAILY", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = unitsPerDayText,
                            onValueChange = { unitsPerDayText = it },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TextPrimary,
                                unfocusedBorderColor = CalmBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = unitName,
                            onValueChange = { unitName = it },
                            modifier = Modifier.weight(1.2f),
                            placeholder = { Text("unit name", color = TextMuted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TextPrimary,
                                unfocusedBorderColor = CalmBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cost = dailyCostText.toDoubleOrNull() ?: 0.0
                    val units = unitsPerDayText.toDoubleOrNull() ?: 0.0
                    onSave(
                        FinancialConfig(
                            enabled = enabled,
                            dailyCost = cost,
                            currencySymbol = currency,
                            unitName = unitName.ifBlank { "items" },
                            unitsPerDay = units
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TextPrimary,
                    contentColor = CalmBackground
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = CalmSurface,
        shape = RoundedCornerShape(16.dp)
    )
}
