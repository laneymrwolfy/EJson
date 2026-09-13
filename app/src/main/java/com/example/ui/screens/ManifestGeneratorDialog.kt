package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EJsonDarkBorder
import com.example.ui.theme.EJsonDarkCard
import com.example.ui.theme.EJsonGreen
import com.example.ui.theme.EJsonTextSecondary
import com.example.ui.theme.EJsonYellow
import java.util.UUID

@Composable
fun ManifestGeneratorDialog(
    onDismiss: () -> Unit,
    onGenerate: (fileName: String, content: String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedMessage by remember { mutableStateOf<String?>(null) }

    // Header State
    var packName by remember { mutableStateOf("My Custom Bedrock Pack") }
    var packDesc by remember { mutableStateOf("Created with EJson Bedrock Studio") }
    var packType by remember { mutableStateOf("behavior") } // "behavior", "resource", "skin", "script"
    var headerUuid by remember { mutableStateOf(UUID.randomUUID().toString()) }
    var headerVerMajor by remember { mutableStateOf("1") }
    var headerVerMinor by remember { mutableStateOf("0") }
    var headerVerPatch by remember { mutableStateOf("0") }

    // Min Engine Version (Default 1.26.40 for primary Bedrock 1.26.40.5 target)
    var minEngineMajor by remember { mutableStateOf("1") }
    var minEngineMinor by remember { mutableStateOf("26") }
    var minEnginePatch by remember { mutableStateOf("40") }

    // Module State
    var moduleUuid by remember { mutableStateOf(UUID.randomUUID().toString()) }
    var moduleVerMajor by remember { mutableStateOf("1") }
    var moduleVerMinor by remember { mutableStateOf("0") }
    var moduleVerPatch by remember { mutableStateOf("0") }

    // Dependencies State
    var includeDependencies by remember { mutableStateOf(false) }
    var depUuid by remember { mutableStateOf(UUID.randomUUID().toString()) }
    var depVerMajor by remember { mutableStateOf("1") }
    var depVerMinor by remember { mutableStateOf("0") }
    var depVerPatch by remember { mutableStateOf("0") }

    fun copyToClipboard(label: String, value: String) {
        clipboardManager.setText(AnnotatedString(value))
        copiedMessage = "Copied $label!"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EJsonYellow)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bedrock Manifest Builder", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Builds Bedrock manifest.json with offline UUID generation and version tracking.",
                    fontSize = 12.sp,
                    color = EJsonTextSecondary
                )

                if (copiedMessage != null) {
                    Text(
                        text = copiedMessage ?: "",
                        fontSize = 12.sp,
                        color = EJsonGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Pack Type Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = packType == "behavior",
                        onClick = { packType = "behavior" },
                        label = { Text("Behavior", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EJsonYellow, selectedLabelColor = Color.Black)
                    )
                    FilterChip(
                        selected = packType == "resource",
                        onClick = { packType = "resource" },
                        label = { Text("Resource", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EJsonYellow, selectedLabelColor = Color.Black)
                    )
                    FilterChip(
                        selected = packType == "skin",
                        onClick = { packType = "skin" },
                        label = { Text("Skin", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EJsonYellow, selectedLabelColor = Color.Black)
                    )
                    FilterChip(
                        selected = packType == "script",
                        onClick = { packType = "script" },
                        label = { Text("Script", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EJsonYellow, selectedLabelColor = Color.Black)
                    )
                }

                // Pack Name
                OutlinedTextField(
                    value = packName,
                    onValueChange = { packName = it },
                    label = { Text("Pack Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EJsonYellow,
                        unfocusedBorderColor = EJsonDarkBorder
                    )
                )

                // Pack Description
                OutlinedTextField(
                    value = packDesc,
                    onValueChange = { packDesc = it },
                    label = { Text("Pack Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EJsonYellow,
                        unfocusedBorderColor = EJsonDarkBorder
                    )
                )

                // Version [major, minor, patch]
                Column {
                    Text(text = "Pack Version [X, Y, Z]:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = headerVerMajor,
                            onValueChange = { headerVerMajor = it.filter { c -> c.isDigit() } },
                            label = { Text("Major") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = headerVerMinor,
                            onValueChange = { headerVerMinor = it.filter { c -> c.isDigit() } },
                            label = { Text("Minor") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = headerVerPatch,
                            onValueChange = { headerVerPatch = it.filter { c -> c.isDigit() } },
                            label = { Text("Patch") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Min Engine Version
                Column {
                    Text(text = "Minimum Engine Version (1.26.40.5 target):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = minEngineMajor,
                            onValueChange = { minEngineMajor = it.filter { c -> c.isDigit() } },
                            label = { Text("Major") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minEngineMinor,
                            onValueChange = { minEngineMinor = it.filter { c -> c.isDigit() } },
                            label = { Text("Minor") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minEnginePatch,
                            onValueChange = { minEnginePatch = it.filter { c -> c.isDigit() } },
                            label = { Text("Patch") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Header UUID Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EJsonDarkCard),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Header UUID", fontSize = 11.sp, color = EJsonTextSecondary)
                        Text(headerUuid, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = EJsonYellow, maxLines = 1)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { headerUuid = UUID.randomUUID().toString() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                Text("Generate", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { copyToClipboard("Header UUID", headerUuid) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                Text("Copy", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Module UUID Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EJsonDarkCard),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Module UUID (${if (packType == "behavior") "data" else if (packType == "resource") "resources" else packType})", fontSize = 11.sp, color = EJsonTextSecondary)
                        Text(moduleUuid, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = EJsonYellow, maxLines = 1)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { moduleUuid = UUID.randomUUID().toString() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                Text("Generate", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { copyToClipboard("Module UUID", moduleUuid) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                Text("Copy", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Dependencies Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Include Pack Dependency", fontSize = 13.sp)
                    Switch(
                        checked = includeDependencies,
                        onCheckedChange = { includeDependencies = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = EJsonYellow)
                    )
                }

                if (includeDependencies) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EJsonDarkCard),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Dependency UUID", fontSize = 11.sp, color = EJsonTextSecondary)
                            Text(depUuid, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = EJsonYellow, maxLines = 1)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { depUuid = UUID.randomUUID().toString() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                    Text("Generate", fontSize = 11.sp)
                                }
                                OutlinedButton(
                                    onClick = { copyToClipboard("Dependency UUID", depUuid) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                    Text("Copy", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val major = minEngineMajor.toIntOrNull() ?: 1
                    val minor = minEngineMinor.toIntOrNull() ?: 26
                    val patch = minEnginePatch.toIntOrNull() ?: 40

                    val hMaj = headerVerMajor.toIntOrNull() ?: 1
                    val hMin = headerVerMinor.toIntOrNull() ?: 0
                    val hPatch = headerVerPatch.toIntOrNull() ?: 0

                    val mMaj = moduleVerMajor.toIntOrNull() ?: 1
                    val mMin = moduleVerMinor.toIntOrNull() ?: 0
                    val mPatch = moduleVerPatch.toIntOrNull() ?: 0

                    val dMaj = depVerMajor.toIntOrNull() ?: 1
                    val dMin = depVerMinor.toIntOrNull() ?: 0
                    val dPatch = depVerPatch.toIntOrNull() ?: 0

                    val moduleType = when (packType) {
                        "behavior" -> "data"
                        "resource" -> "resources"
                        "skin" -> "skin_pack"
                        "script" -> "script"
                        else -> "data"
                    }

                    val scriptFields = if (packType == "script") {
                        """,
      "language": "javascript",
      "entry": "scripts/main.js""""
                    } else ""

                    val depBlock = if (includeDependencies) {
                        """,
  "dependencies": [
    {
      "uuid": "$depUuid",
      "version": [$dMaj, $dMin, $dPatch]
    }
  ]"""
                    } else if (packType == "script") {
                        """,
  "dependencies": [
    {
      "module_name": "@minecraft/server",
      "version": "1.13.0"
    }
  ]"""
                    } else ""

                    val json = """{
  "format_version": 2,
  "header": {
    "name": "$packName",
    "description": "$packDesc",
    "uuid": "$headerUuid",
    "version": [$hMaj, $hMin, $hPatch],
    "min_engine_version": [$major, $minor, $patch]
  },
  "modules": [
    {
      "description": "$packDesc module",
      "type": "$moduleType"$scriptFields,
      "uuid": "$moduleUuid",
      "version": [$mMaj, $mMin, $mPatch]
    }
  ]$depBlock
}"""
                    onGenerate("manifest.json", json)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EJsonYellow, contentColor = Color.Black),
                modifier = Modifier.testTag("dialog_generate_manifest_button")
            ) {
                Text("Open in Editor", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
