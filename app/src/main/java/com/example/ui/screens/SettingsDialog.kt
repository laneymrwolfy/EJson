package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EditorSettings
import com.example.ui.theme.EJsonYellow

@Composable
fun SettingsDialog(
    currentSettings: EditorSettings,
    onDismiss: () -> Unit,
    onSaveSettings: (EditorSettings) -> Unit,
    onResetSettings: () -> Unit
) {
    var themeMode by remember { mutableStateOf(currentSettings.themeMode) }
    var fontSize by remember { mutableFloatStateOf(currentSettings.fontSizeSp.toFloat()) }
    var showLineNumbers by remember { mutableStateOf(currentSettings.showLineNumbers) }
    var wordWrap by remember { mutableStateOf(currentSettings.wordWrap) }
    var syntaxHighlighting by remember { mutableStateOf(currentSettings.syntaxHighlighting) }
    var indentSpaces by remember { mutableStateOf(currentSettings.indentSpaces) }
    var confirmUnsaved by remember { mutableStateOf(currentSettings.confirmUnsavedChanges) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = EJsonYellow)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editor Settings", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Theme Selection
                Column {
                    Text("Theme", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("dark" to "Dark", "light" to "Light", "system" to "System").forEach { (mode, label) ->
                            FilterChip(
                                selected = themeMode == mode,
                                onClick = { themeMode = mode },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EJsonYellow,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }

                // Font Size Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Font Size", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("${fontSize.toInt()} sp", fontSize = 13.sp, color = EJsonYellow, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = fontSize,
                        onValueChange = { fontSize = it },
                        valueRange = 10f..24f,
                        steps = 13,
                        colors = SliderDefaults.colors(
                            thumbColor = EJsonYellow,
                            activeTrackColor = EJsonYellow
                        )
                    )
                }

                // Indentation
                Column {
                    Text("Indentation", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(2 to "2 Spaces", 4 to "4 Spaces").forEach { (spaces, label) ->
                            FilterChip(
                                selected = indentSpaces == spaces,
                                onClick = { indentSpaces = spaces },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EJsonYellow,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }

                // Line Numbers Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Show Line Numbers", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Displays gutter column on left", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = showLineNumbers,
                        onCheckedChange = { showLineNumbers = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = EJsonYellow)
                    )
                }

                // Word Wrap Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Word Wrapping", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Wrap long JSON lines to view width", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = wordWrap,
                        onCheckedChange = { wordWrap = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = EJsonYellow)
                    )
                }

                // Syntax Highlighting Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Syntax Highlighting", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Colors keys, strings, and brackets", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = syntaxHighlighting,
                        onCheckedChange = { syntaxHighlighting = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = EJsonYellow)
                    )
                }

                // Confirm Unsaved Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Confirm Unsaved Changes", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Prompt when exiting modified document", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = confirmUnsaved,
                        onCheckedChange = { confirmUnsaved = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = EJsonYellow)
                    )
                }

                // Reset Settings
                TextButton(
                    onClick = {
                        onResetSettings()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset to Defaults", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveSettings(
                        EditorSettings(
                            themeMode = themeMode,
                            fontSizeSp = fontSize.toInt(),
                            showLineNumbers = showLineNumbers,
                            wordWrap = wordWrap,
                            syntaxHighlighting = syntaxHighlighting,
                            indentSpaces = indentSpaces,
                            confirmUnsavedChanges = confirmUnsaved
                        )
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EJsonYellow, contentColor = Color.Black),
                modifier = Modifier.testTag("save_settings_button")
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
