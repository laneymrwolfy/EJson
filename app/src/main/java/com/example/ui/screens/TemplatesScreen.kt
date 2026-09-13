package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.ui.theme.EJsonTextSecondary
import com.example.ui.theme.EJsonYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    onBack: () -> Unit,
    onSelectTemplate: (BedrockTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<TemplateCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var previewTemplate by remember { mutableStateOf<BedrockTemplate?>(null) }

    val filteredTemplates = remember(selectedCategory, searchQuery) {
        TemplateLibrary.templates.filter { item ->
            val matchesCategory = selectedCategory == null || item.category == selectedCategory
            val matchesSearch = searchQuery.isEmpty() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    item.defaultFileName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Bedrock Templates", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("templates_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EJsonDarkBg)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Bedrock templates...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EJsonYellow) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("templates_search_input"),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EJsonYellow,
                    unfocusedBorderColor = EJsonDarkBorder
                )
            )

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All (${TemplateLibrary.templates.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EJsonYellow,
                        selectedLabelColor = Color.Black
                    )
                )

                TemplateCategory.entries.forEach { cat ->
                    val count = TemplateLibrary.templates.count { it.category == cat }
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text("${cat.displayName} ($count)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EJsonYellow,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Templates List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("templates_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTemplates, key = { it.id }) { template ->
                    TemplateItemCard(
                        template = template,
                        onUse = { onSelectTemplate(template) },
                        onPreview = { previewTemplate = template }
                    )
                }
            }
        }
    }

    // Template Preview Dialog
    previewTemplate?.let { t ->
        AlertDialog(
            onDismissRequest = { previewTemplate = null },
            title = {
                Column {
                    Text(text = t.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = t.defaultFileName,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = EJsonYellow
                    )
                }
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color(0xFF14161A), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = t.content,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val selected = previewTemplate
                        previewTemplate = null
                        if (selected != null) onSelectTemplate(selected)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EJsonYellow,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Use Template")
                }
            },
            dismissButton = {
                TextButton(onClick = { previewTemplate = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun TemplateItemCard(
    template: BedrockTemplate,
    onUse: () -> Unit,
    onPreview: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("template_card_${template.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EJsonDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, EJsonDarkBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = template.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (template.targetVersion.contains("1.26")) Color(0xFF1B381D) else Color(0xFF232832)
                ) {
                    Text(
                        text = template.targetVersion,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (template.targetVersion.contains("1.26")) EJsonGreen else Color(0xFF90CAF9),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = template.description,
                fontSize = 12.sp,
                color = EJsonTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = template.defaultFileName,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = EJsonYellow
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = onPreview,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Preview", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onUse,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EJsonYellow,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Use", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
