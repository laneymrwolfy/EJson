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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EJsonDarkBorder
import com.example.ui.theme.EJsonDarkCard
import com.example.ui.theme.EJsonTextSecondary
import com.example.ui.theme.EJsonYellow
import com.example.util.JsonEngine

@Composable
fun ManifestGeneratorDialog(
    onDismiss: () -> Unit,
    onGenerate: (fileName: String, content: String) -> Unit
) {
    var packName by remember { mutableStateOf("My Custom Bedrock Pack") }
    var packDesc by remember { mutableStateOf("Created with EJson Bedrock Studio") }
    var packType by remember { mutableStateOf("behavior") } // "behavior", "resource", "skin"
    var headerUuid by remember { mutableStateOf(JsonEngine.generateUuid()) }
    var moduleUuid by remember { mutableStateOf(JsonEngine.generateUuid()) }
    var minEngineMajor by remember { mutableStateOf("1") }
    var minEngineMinor by remember { mutableStateOf("26") }
    var minEnginePatch by remember { mutableStateOf("40") }
    var includeDependencies by remember { mutableStateOf(false) }
    var depUuid by remember { mutableStateOf(JsonEngine.generateUuid()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EJsonYellow)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Manifest.json Generator", fontWeight = FontWeight.Bold, fontSize = 17.sp)
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
                    text = "Generates a clean Bedrock 1.26.40.5 manifest with unique random UUIDs.",
                    fontSize = 12.sp,
                    color = EJsonTextSecondary
                )

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
                        label = { Text("Skin Pack", fontSize = 12.sp) },
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Header UUID", fontSize = 11.sp, color = EJsonTextSecondary)
                            Text(headerUuid, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = EJsonYellow, maxLines = 1)
                        }
                        IconButton(onClick = { headerUuid = JsonEngine.generateUuid() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Regenerate UUID", tint = EJsonYellow)
                        }
                    }
                }

                // Module UUID Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EJsonDarkCard),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Module UUID", fontSize = 11.sp, color = EJsonTextSecondary)
                            Text(moduleUuid, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = EJsonYellow, maxLines = 1)
                        }
                        IconButton(onClick = { moduleUuid = JsonEngine.generateUuid() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Regenerate UUID", tint = EJsonYellow)
                        }
                    }
                }

                // Dependencies Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Include Dependency", fontSize = 13.sp)
                    Switch(
                        checked = includeDependencies,
                        onCheckedChange = { includeDependencies = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = EJsonYellow)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val major = minEngineMajor.toIntOrNull() ?: 1
                    val minor = minEngineMinor.toIntOrNull() ?: 26
                    val patch = minEnginePatch.toIntOrNull() ?: 40

                    val moduleType = when (packType) {
                        "behavior" -> "data"
                        "resource" -> "resources"
                        else -> "skin_pack"
                    }

                    val depBlock = if (includeDependencies) {
                        """,
  "dependencies": [
    {
      "uuid": "$depUuid",
      "version": [1, 0, 0]
    }
  ]"""
                    } else ""

                    val formatVer = if (packType == "skin") 1 else 2
                    val generatedJson = """{
  "format_version": $formatVer,
  "header": {
    "name": "$packName",
    "description": "$packDesc",
    "uuid": "$headerUuid",
    "version": [1, 0, 0],
    "min_engine_version": [$major, $minor, $patch]
  },
  "modules": [
    {
      "description": "$packName module",
      "type": "$moduleType",
      "uuid": "$moduleUuid",
      "version": [1, 0, 0]
    }
  ]$depBlock
}"""
                    onGenerate("manifest.json", generatedJson)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EJsonYellow, contentColor = Color.Black),
                modifier = Modifier.testTag("create_manifest_button")
            ) {
                Text("Generate Manifest", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
