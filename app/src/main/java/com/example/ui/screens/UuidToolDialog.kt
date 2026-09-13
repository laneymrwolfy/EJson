package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EJsonDarkCard
import com.example.ui.theme.EJsonTextSecondary
import com.example.ui.theme.EJsonYellow
import com.example.util.JsonEngine

@Composable
fun UuidToolDialog(
    onDismiss: () -> Unit,
    onCopyUuid: (String) -> Unit
) {
    val uuidList = remember {
        mutableStateListOf(
            JsonEngine.generateUuid(),
            JsonEngine.generateUuid(),
            JsonEngine.generateUuid()
        )
    }

    fun refreshAll() {
        uuidList.clear()
        repeat(3) { uuidList.add(JsonEngine.generateUuid()) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFF26C6DA))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bedrock UUID Generator", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Offline random v4 UUIDs for Minecraft manifests and modules.",
                    fontSize = 12.sp,
                    color = EJsonTextSecondary
                )

                uuidList.forEachIndexed { index, uuid ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = EJsonDarkCard)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (index == 0) "Header UUID" else "Module UUID #$index",
                                    fontSize = 11.sp,
                                    color = EJsonTextSecondary
                                )
                                Text(
                                    text = uuid,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = EJsonYellow,
                                    maxLines = 1
                                )
                            }
                            IconButton(onClick = { onCopyUuid(uuid) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { refreshAll() },
                    modifier = Modifier.testTag("regenerate_uuids_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Regenerate")
                }
                TextButton(onClick = onDismiss) {
                    Text("Done")
                }
            }
        }
    )
}
