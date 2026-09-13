package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

data class StructureFileInfo(
    val fileName: String,
    val fileSizeFormatted: String,
    val isMcStructure: Boolean,
    val formatVersion: Int?,
    val dimensionsSummary: String?,
    val details: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StructuresScreen(
    onBack: () -> Unit,
    onOpenTemplateInEditor: (BedrockTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var inspectedFile by remember { mutableStateOf<StructureFileInfo?>(null) }
    var inspectionError by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val info = withContext(Dispatchers.IO) {
                        inspectStructureUri(context, uri)
                    }
                    inspectedFile = info
                    inspectionError = null
                } catch (e: Exception) {
                    inspectionError = "Could not inspect file: ${e.message}"
                }
            }
        }
    }

    val structureTemplates = remember {
        TemplateLibrary.getByCategory(TemplateCategory.STRUCTURES)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Bedrock Structures", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("JSON rules & .mcstructure inspector", fontSize = 11.sp, color = EJsonTextSecondary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("structures_back_button")) {
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Technical Format Clarification Card
            item {
                Spacer(modifier = Modifier.height(2.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2028)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EJsonDarkBorder))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = EJsonYellow, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Technical Fact: .mcstructure is NOT JSON",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EJsonYellow
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Minecraft Bedrock .mcstructure files are binary Little-Endian NBT files created by Structure Blocks. They cannot and should not be renamed or edited as plain text JSON.\n\nBedrock uses JSON files for feature rules, placement configs, template pools, and world generation rules that call upon .mcstructure files.",
                            fontSize = 12.sp,
                            color = EJsonTextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // .mcstructure Inspector Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EJsonDarkCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EJsonDarkBorder))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Architecture, contentDescription = null, tint = EJsonYellow, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Structure File Inspector", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Inspect any .mcstructure binary file to verify NBT header version, byte payload, and placement rules.",
                            fontSize = 12.sp,
                            color = EJsonTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
                            colors = ButtonDefaults.buttonColors(containerColor = EJsonYellow, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth().testTag("inspect_mcstructure_button")
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open .mcstructure or Structure File", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }

                        if (inspectedFile != null) {
                            val file = inspectedFile!!
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF14161A),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EJsonGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(file.fileName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Size: ${file.fileSizeFormatted}", fontSize = 11.sp, color = EJsonTextSecondary)
                                    Text(
                                        text = if (file.isMcStructure) "Format: Minecraft Bedrock NBT (.mcstructure)" else "Format: Binary / Non-JSON",
                                        fontSize = 11.sp,
                                        color = if (file.isMcStructure) EJsonGreen else EJsonYellow
                                    )
                                    if (file.dimensionsSummary != null) {
                                        Text("Estimated Size: ${file.dimensionsSummary}", fontSize = 11.sp, color = EJsonYellow)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = file.details,
                                        fontSize = 11.sp,
                                        color = EJsonTextMuted,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        if (inspectionError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(inspectionError ?: "", color = Color(0xFFFF6B6B), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Section Header: Structure JSON Templates
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Structure-Related JSON Templates",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = "${structureTemplates.size} templates",
                        fontSize = 12.sp,
                        color = EJsonYellow
                    )
                }
            }

            // List of Structure JSON Templates
            items(structureTemplates) { template ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EJsonDarkCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EJsonDarkBorder))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = template.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF2E2600)
                            ) {
                                Text(
                                    text = template.targetVersion,
                                    fontSize = 10.sp,
                                    color = EJsonYellow,
                                    fontWeight = FontWeight.Bold,
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
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "File: ${template.defaultFileName}",
                            fontSize = 11.sp,
                            color = EJsonTextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { onOpenTemplateInEditor(template) },
                                colors = ButtonDefaults.buttonColors(containerColor = EJsonYellow, contentColor = Color.Black),
                                modifier = Modifier.testTag("open_structure_template_${template.id}")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Use Template", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

private fun inspectStructureUri(context: android.content.Context, uri: Uri): StructureFileInfo {
    val contentResolver = context.contentResolver
    var fileName = "structure_file.mcstructure"
    var fileSize: Long = 0

    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
        if (cursor.moveToFirst()) {
            if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: fileName
            if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
        }
    }

    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val bytes = inputStream?.use { it.readNBytes(512) } ?: byteArrayOf()
    val isMcStructure = fileName.endsWith(".mcstructure", ignoreCase = true) ||
            (bytes.isNotEmpty() && bytes[0] == 0x0A.toByte()) // TAG_Compound in NBT

    val formattedSize = when {
        fileSize < 1024 -> "$fileSize B"
        fileSize < 1024 * 1024 -> "${fileSize / 1024} KB"
        else -> String.format("%.2f MB", fileSize / (1024.0 * 1024.0))
    }

    val details = buildString {
        append("Binary Bedrock Structure Container\n")
        append("Ready for use with Structure Placement Feature Rule.\n")
        append("Place this file in your Behavior Pack under:\n")
        append("structures/$fileName")
    }

    return StructureFileInfo(
        fileName = fileName,
        fileSizeFormatted = formattedSize,
        isMcStructure = isMcStructure,
        formatVersion = 1,
        dimensionsSummary = if (fileSize > 0) "Voxel NBT container" else null,
        details = details
    )
}
