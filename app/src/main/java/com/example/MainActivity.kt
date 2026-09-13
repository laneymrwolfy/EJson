package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.model.BedrockTemplate
import com.example.model.EditorSettings
import com.example.model.RecentFile
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ManifestGeneratorDialog
import com.example.ui.screens.SettingsDialog
import com.example.ui.screens.TemplatesScreen
import com.example.ui.screens.UuidToolDialog
import com.example.ui.theme.EJsonTheme
import com.example.util.JsonEngine
import com.example.util.StorageManager

enum class AppScreen {
    HOME,
    EDITOR,
    TEMPLATES
}

class MainActivity : ComponentActivity() {

    private lateinit var storageManager: StorageManager
    private var pendingIntentUri = mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        storageManager = StorageManager(applicationContext)

        // Handle initial intent if app opened with a file
        intent?.data?.let { uri ->
            pendingIntentUri.value = uri
        }

        setContent {
            EJsonApp(
                storageManager = storageManager,
                incomingUri = pendingIntentUri.value,
                onUriHandled = { pendingIntentUri.value = null }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.data?.let { uri ->
            pendingIntentUri.value = uri
        }
    }
}

@Composable
fun EJsonApp(
    storageManager: StorageManager,
    incomingUri: Uri?,
    onUriHandled: () -> Unit
) {
    var settings by remember { mutableStateOf(storageManager.getSettings()) }
    var recentFiles by remember { mutableStateOf(storageManager.getRecentFiles()) }

    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var currentUri by remember { mutableStateOf<Uri?>(null) }
    var currentFileName by remember { mutableStateOf("untitled.json") }
    var currentContent by remember { mutableStateOf("") }

    // Dialog flags
    var showManifestGenerator by remember { mutableStateOf(false) }
    var showUuidTool by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Pending save content for Save As contract
    var pendingSaveAsContent by remember { mutableStateOf<String?>(null) }

    // SAF Launchers
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val content = storageManager.readTextFromUri(uri)
                val (name, size) = storageManager.queryFileInfo(uri)
                val analysis = JsonEngine.analyzeBedrock(content)
                val recent = RecentFile(
                    uriString = uri.toString(),
                    fileName = name,
                    category = if (analysis.isBedrock) analysis.category else "Generic JSON",
                    lastOpened = System.currentTimeMillis(),
                    sizeBytes = size
                )
                storageManager.addRecentFile(recent)
                recentFiles = storageManager.getRecentFiles()

                currentUri = uri
                currentFileName = name
                currentContent = content
                currentScreen = AppScreen.EDITOR
            } catch (e: Exception) {
                // Toast or feedback
            }
        }
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val contentToWrite = pendingSaveAsContent ?: currentContent
                storageManager.writeTextToUri(uri, contentToWrite)
                val (name, size) = storageManager.queryFileInfo(uri)
                val analysis = JsonEngine.analyzeBedrock(contentToWrite)
                val recent = RecentFile(
                    uriString = uri.toString(),
                    fileName = name,
                    category = if (analysis.isBedrock) analysis.category else "Generic JSON",
                    lastOpened = System.currentTimeMillis(),
                    sizeBytes = size
                )
                storageManager.addRecentFile(recent)
                recentFiles = storageManager.getRecentFiles()

                currentUri = uri
                currentFileName = name
                currentContent = contentToWrite
                pendingSaveAsContent = null
                currentScreen = AppScreen.EDITOR
            } catch (e: Exception) {
                // Failed write
            }
        }
    }

    // Handle incoming file Uri from Intent
    LaunchedEffect(incomingUri) {
        if (incomingUri != null) {
            try {
                val content = storageManager.readTextFromUri(incomingUri)
                val (name, size) = storageManager.queryFileInfo(incomingUri)
                val analysis = JsonEngine.analyzeBedrock(content)
                val recent = RecentFile(
                    uriString = incomingUri.toString(),
                    fileName = name,
                    category = if (analysis.isBedrock) analysis.category else "Generic JSON",
                    lastOpened = System.currentTimeMillis(),
                    sizeBytes = size
                )
                storageManager.addRecentFile(recent)
                recentFiles = storageManager.getRecentFiles()

                currentUri = incomingUri
                currentFileName = name
                currentContent = content
                currentScreen = AppScreen.EDITOR
                onUriHandled()
            } catch (ignored: Exception) {
                onUriHandled()
            }
        }
    }

    EJsonTheme(themeMode = settings.themeMode) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        recentFiles = recentFiles,
                        onOpenJsonClick = {
                            openDocumentLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                        },
                        onNewJsonClick = {
                            currentUri = null
                            currentFileName = "untitled.json"
                            currentContent = "{\n  \n}"
                            currentScreen = AppScreen.EDITOR
                        },
                        onTemplatesClick = {
                            currentScreen = AppScreen.TEMPLATES
                        },
                        onManifestGeneratorClick = {
                            showManifestGenerator = true
                        },
                        onUuidToolClick = {
                            showUuidTool = true
                        },
                        onSettingsClick = {
                            showSettingsDialog = true
                        },
                        onRecentFileClick = { recent ->
                            try {
                                val uri = Uri.parse(recent.uriString)
                                val content = storageManager.readTextFromUri(uri)
                                currentUri = uri
                                currentFileName = recent.fileName
                                currentContent = content
                                currentScreen = AppScreen.EDITOR
                            } catch (e: Exception) {
                                // If file no longer accessible, remove
                                storageManager.removeRecentFile(recent.uriString)
                                recentFiles = storageManager.getRecentFiles()
                            }
                        },
                        onRemoveRecentClick = { uriStr ->
                            storageManager.removeRecentFile(uriStr)
                            recentFiles = storageManager.getRecentFiles()
                        },
                        onClearAllRecentsClick = {
                            storageManager.clearRecentFiles()
                            recentFiles = emptyList()
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppScreen.EDITOR -> {
                    EditorScreen(
                        fileName = currentFileName,
                        initialContent = currentContent,
                        settings = settings,
                        onBack = {
                            currentScreen = AppScreen.HOME
                        },
                        onSave = { updatedText ->
                            val uri = currentUri
                            if (uri != null) {
                                try {
                                    storageManager.writeTextToUri(uri, updatedText)
                                    currentContent = updatedText
                                } catch (e: Exception) {
                                    // Fallback to Save As if write failed
                                    pendingSaveAsContent = updatedText
                                    createDocumentLauncher.launch(currentFileName)
                                }
                            } else {
                                // First time saving an unsaved file: prompt Save As
                                pendingSaveAsContent = updatedText
                                createDocumentLauncher.launch(currentFileName)
                            }
                        },
                        onSaveAs = { updatedText ->
                            pendingSaveAsContent = updatedText
                            createDocumentLauncher.launch(currentFileName)
                        },
                        onCopy = { text ->
                            storageManager.copyToClipboard(text)
                        },
                        onShare = { name, text ->
                            storageManager.shareJson(name, text)
                        },
                        onOpenSettings = {
                            showSettingsDialog = true
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppScreen.TEMPLATES -> {
                    TemplatesScreen(
                        onBack = { currentScreen = AppScreen.HOME },
                        onSelectTemplate = { template ->
                            currentUri = null
                            currentFileName = template.defaultFileName
                            currentContent = template.content
                            currentScreen = AppScreen.EDITOR
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        // Offline Dialogs
        if (showManifestGenerator) {
            ManifestGeneratorDialog(
                onDismiss = { showManifestGenerator = false },
                onGenerate = { fileName, content ->
                    showManifestGenerator = false
                    currentUri = null
                    currentFileName = fileName
                    currentContent = content
                    currentScreen = AppScreen.EDITOR
                }
            )
        }

        if (showUuidTool) {
            UuidToolDialog(
                onDismiss = { showUuidTool = false },
                onCopyUuid = { uuid ->
                    storageManager.copyToClipboard(uuid, "Bedrock UUID")
                }
            )
        }

        if (showSettingsDialog) {
            SettingsDialog(
                currentSettings = settings,
                onDismiss = { showSettingsDialog = false },
                onSaveSettings = { updated ->
                    storageManager.saveSettings(updated)
                    settings = updated
                },
                onResetSettings = {
                    val def = storageManager.resetSettings()
                    settings = def
                }
            )
        }
    }
}
