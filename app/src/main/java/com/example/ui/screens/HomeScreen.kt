package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.RecentFile
import com.example.ui.theme.EJsonDarkBorder
import com.example.ui.theme.EJsonDarkCard
import com.example.ui.theme.EJsonTextMuted
import com.example.ui.theme.EJsonTextSecondary
import com.example.ui.theme.EJsonYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    recentFiles: List<RecentFile>,
    onOpenJsonClick: () -> Unit,
    onNewJsonClick: () -> Unit,
    onTemplatesClick: () -> Unit,
    onManifestGeneratorClick: () -> Unit,
    onUuidToolClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onRecentFileClick: (RecentFile) -> Unit,
    onRemoveRecentClick: (String) -> Unit,
    onClearAllRecentsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ejson_logo),
                        contentDescription = "EJson Logo",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "EJson",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = EJsonYellow,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "v1.2.7",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Minecraft Bedrock JSON Studio",
                            style = MaterialTheme.typography.bodySmall,
                            color = EJsonTextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Primary Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onOpenJsonClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("open_json_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EJsonYellow,
                        contentColor = Color(0xFF14161B)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open JSON",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Button(
                    onClick = onNewJsonClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("new_json_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "New JSON",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Bedrock Templates Card
        item {
            Card(
                onClick = onTemplatesClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("templates_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EJsonDarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, EJsonDarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2E2600)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LibraryBooks,
                                contentDescription = null,
                                tint = EJsonYellow,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Bedrock Templates",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF1B381D)
                                ) {
                                    Text(
                                        text = "1.26.40.5",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF81C784),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Behavior, Resource & World Gen schemas",
                                style = MaterialTheme.typography.bodySmall,
                                color = EJsonTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Offline Bedrock Tools Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    onClick = onManifestGeneratorClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("manifest_gen_card"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = EJsonDarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EJsonDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = EJsonYellow,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Manifest Wizard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Build pack manifest",
                            fontSize = 12.sp,
                            color = EJsonTextSecondary
                        )
                    }
                }

                Card(
                    onClick = onUuidToolClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("uuid_tool_card"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = EJsonDarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EJsonDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = Color(0xFF26C6DA),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bedrock UUIDs",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Generate offline UUIDs",
                            fontSize = 12.sp,
                            color = EJsonTextSecondary
                        )
                    }
                }
            }
        }

        // Recent Files Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = EJsonYellow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Recent Files",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (recentFiles.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${recentFiles.size})",
                            fontSize = 13.sp,
                            color = EJsonTextMuted
                        )
                    }
                }

                if (recentFiles.isNotEmpty()) {
                    TextButton(onClick = onClearAllRecentsClick) {
                        Text(
                            text = "Clear",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Recent Files List or Empty State
        if (recentFiles.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = EJsonTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No recent files",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Open a .json file or choose a Bedrock template to start editing.",
                            fontSize = 12.sp,
                            color = EJsonTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(recentFiles, key = { it.uriString }) { recent ->
                RecentFileCard(
                    recentFile = recent,
                    onClick = { onRecentFileClick(recent) },
                    onDelete = { onRemoveRecentClick(recent.uriString) },
                    dateText = dateFormat.format(Date(recent.lastOpened))
                )
            }
        }
    }
}

@Composable
fun RecentFileCard(
    recentFile: RecentFile,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    dateText: String
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("recent_file_card"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EJsonDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, EJsonDarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF232832)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = EJsonYellow,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recentFile.fileName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = Color(0xFF262C38)
                        ) {
                            Text(
                                text = recentFile.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF90CAF9),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Text(
                            text = dateText,
                            fontSize = 11.sp,
                            color = EJsonTextMuted
                        )
                    }
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Remove from recents",
                    tint = EJsonTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
