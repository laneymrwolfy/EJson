package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BedrockTemplate
import com.example.model.TemplateCategory
import com.example.model.TemplateLibrary
import com.example.ui.theme.EJsonDarkBg
import com.example.ui.theme.EJsonDarkBorder
import com.example.ui.theme.EJsonDarkCard
import com.example.ui.theme.EJsonGreen
import com.example.ui.theme.EJsonTextMuted
import com.example.ui.theme.EJsonTextSecondary
import com.example.ui.theme.EJsonYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewJsonScreen(
    onBack: () -> Unit,
    onOpenEditor: (fileName: String, content: String) -> Unit,
    onOpenFullTemplates: () -> Unit,
    onOpenStructures: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showManifestDialog by remember { mutableStateOf(false) }

    if (showManifestDialog) {
        ManifestGeneratorDialog(
            onDismiss = { showManifestDialog = false },
            onGenerate = { fileName, content ->
                showManifestDialog = false
                onOpenEditor(fileName, content)
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("New JSON", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Choose starter, category, or Minecraft Bedrock template", fontSize = 11.sp, color = EJsonTextSecondary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("new_json_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EJsonDarkBg)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(EJsonDarkBg)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Option 1: Blank JSON (Strictly starts with valid {})
            item {
                Spacer(modifier = Modifier.height(2.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_blank_json_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B221B)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2E4D2E)))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DataObject, contentDescription = null, tint = EJsonGreen, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Blank JSON", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                Text("Starts immediately with a clean, valid {} object", fontSize = 12.sp, color = EJsonTextSecondary)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF121418),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "{\n  \n}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = EJsonGreen,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onOpenEditor("untitled.json", "{\n  \n}") },
                            colors = ButtonDefaults.buttonColors(containerColor = EJsonGreen, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth().testTag("button_create_blank_json")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create Blank JSON ({})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Option 2: Manifest JSON Builder
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_manifest_card"),
                    colors = CardDefaults.cardColors(containerColor = EJsonDarkCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EJsonDarkBorder))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EJsonYellow, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Manifest JSON Builder", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                Text("Offline UUID generator & 1.26.40.5 engine version", fontSize = 12.sp, color = EJsonTextSecondary)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Configure pack name, description, modules (behavior, resource, script), and generate fresh UUIDs with one tap.",
                            fontSize = 12.sp,
                            color = EJsonTextMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showManifestDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EJsonYellow, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth().testTag("button_open_manifest_builder")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Manifest Builder", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Option 3: All Minecraft Bedrock JSON Templates
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("all_templates_card"),
                    colors = CardDefaults.cardColors(containerColor = EJsonDarkCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EJsonDarkBorder))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenFullTemplates() }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Layers, contentDescription = null, tint = EJsonYellow, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Minecraft Bedrock Templates", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                Text("Browse all ${TemplateLibrary.templates.size} categorized Bedrock JSON files", fontSize = 12.sp, color = EJsonTextSecondary)
                            }
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = EJsonYellow)
                    }
                }
            }

            // Section Header: Categories
            item {
                Text(
                    text = "Pack Categories",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Option 4: Behavior Pack JSON
            item {
                CategoryChoiceCard(
                    title = "Behavior Pack JSON",
                    subtitle = "Entities, items, blocks, recipes, loot tables, spawn rules, controllers",
                    icon = Icons.Default.Handyman,
                    accentColor = Color(0xFF64B5F6),
                    itemCount = "${TemplateLibrary.getByPackType("Behavior Pack").size} templates",
                    onClick = {
                        val first = TemplateLibrary.getByPackType("Behavior Pack").firstOrNull()
                            ?: TemplateLibrary.getBlankTemplate()
                        onOpenEditor(first.defaultFileName, first.content)
                    }
                )
            }

            // Option 5: Resource Pack JSON
            item {
                CategoryChoiceCard(
                    title = "Resource Pack JSON",
                    subtitle = "Client entities, render controllers, animations, attachables, particles, sounds, UI",
                    icon = Icons.Default.Palette,
                    accentColor = Color(0xFFFFB74D),
                    itemCount = "${TemplateLibrary.getByPackType("Resource Pack").size} templates",
                    onClick = {
                        val first = TemplateLibrary.getByPackType("Resource Pack").firstOrNull()
                            ?: TemplateLibrary.getBlankTemplate()
                        onOpenEditor(first.defaultFileName, first.content)
                    }
                )
            }

            // Option 6: World Generation JSON
            item {
                CategoryChoiceCard(
                    title = "World Generation JSON",
                    subtitle = "Ore features, single blocks, scatter features, feature rules, biomes",
                    icon = Icons.Default.Public,
                    accentColor = Color(0xFF81C784),
                    itemCount = "${TemplateLibrary.getByPackType("World Gen").size} templates",
                    onClick = {
                        val first = TemplateLibrary.getByPackType("World Gen").firstOrNull()
                            ?: TemplateLibrary.getBlankTemplate()
                        onOpenEditor(first.defaultFileName, first.content)
                    }
                )
            }

            // Option 7: Structure-related JSON
            item {
                CategoryChoiceCard(
                    title = "Structure-related JSON",
                    subtitle = "Structure feature rules, placement configs, template pools & .mcstructure info",
                    icon = Icons.Default.Architecture,
                    accentColor = EJsonYellow,
                    itemCount = "${TemplateLibrary.getByCategory(TemplateCategory.STRUCTURES).size} templates",
                    onClick = { onOpenStructures() }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun CategoryChoiceCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    itemCount: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = EJsonDarkCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EJsonDarkBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(itemCount, fontSize = 10.sp, color = accentColor)
                    }
                    Text(subtitle, fontSize = 11.sp, color = EJsonTextSecondary, maxLines = 2)
                }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = EJsonTextMuted, modifier = Modifier.size(18.dp))
        }
    }
}
