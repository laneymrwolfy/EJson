package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EditorSettings
import com.example.ui.components.CodeEditor
import com.example.util.BedrockAnalysis
import com.example.util.JsonEngine
import com.example.util.JsonValidationResult
import com.example.ui.theme.EJsonDarkBg
import com.example.ui.theme.EJsonDarkBorder
import com.example.ui.theme.EJsonDarkCard
import com.example.ui.theme.EJsonGreen
import com.example.ui.theme.EJsonRed
import com.example.ui.theme.EJsonTextMuted
import com.example.ui.theme.EJsonTextSecondary
import com.example.ui.theme.EJsonYellow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    fileName: String,
    initialContent: String,
    settings: EditorSettings,
    onBack: (hasUnsaved: Boolean) -> Unit,
    onSave: (String) -> Unit,
    onSaveAs: (String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String, String) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var editorValue by remember {
        mutableStateOf(TextFieldValue(initialContent, selection = TextRange(0)))
    }
    var isDirty by remember { mutableStateOf(false) }

    // Undo / Redo history
    val undoStack = remember { mutableStateListOf<TextFieldValue>() }
    val redoStack = remember { mutableStateListOf<TextFieldValue>() }

    // Validation State
    var validationResult by remember { mutableStateOf<JsonValidationResult?>(null) }
    var bedrockAnalysis by remember { mutableStateOf(BedrockAnalysis()) }

    // Search and Replace State
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var replaceQuery by remember { mutableStateOf("") }
    var showReplaceRow by remember { mutableStateOf(false) }
    var matchCount by remember { mutableStateOf(0) }
    var currentMatchIndex by remember { mutableStateOf(0) }

    // Overflow Menu & Dialogs
    var showMenu by remember { mutableStateOf(false) }
    var showGoToLineDialog by remember { mutableStateOf(false) }
    var goToLineText by remember { mutableStateOf("") }
    var showBedrockInspector by remember { mutableStateOf(false) }
    var showInsertSnippetDialog by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }

    // Re-analyze when content changes
    LaunchedEffect(editorValue.text) {
        bedrockAnalysis = JsonEngine.analyzeBedrock(editorValue.text)
    }

    // Update match count for search
    LaunchedEffect(searchQuery, editorValue.text) {
        if (searchQuery.isNotEmpty()) {
            val text = editorValue.text
            var count = 0
            var idx = text.indexOf(searchQuery, 0, ignoreCase = true)
            while (idx >= 0) {
                count++
                idx = text.indexOf(searchQuery, idx + searchQuery.length, ignoreCase = true)
            }
            matchCount = count
            currentMatchIndex = if (count > 0) 1 else 0
        } else {
            matchCount = 0
            currentMatchIndex = 0
        }
    }

    fun updateContent(newValue: TextFieldValue, recordHistory: Boolean = true) {
        if (recordHistory && newValue.text != editorValue.text) {
            if (undoStack.size > 50) undoStack.removeAt(0)
            undoStack.add(editorValue)
            redoStack.clear()
            isDirty = true
        }
        editorValue = newValue
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val prev = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(editorValue)
            editorValue = prev
            isDirty = true
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(editorValue)
            editorValue = next
            isDirty = true
        }
    }

    fun formatCode() {
        val formatted = JsonEngine.format(editorValue.text, settings.indentSpaces)
        if (formatted != editorValue.text) {
            updateContent(TextFieldValue(formatted, TextRange(0)))
            coroutineScope.launch {
                snackbarHostState.showSnackbar("JSON formatted (${settings.indentSpaces} spaces)")
            }
        }
    }

    fun minifyCode() {
        val minified = JsonEngine.minify(editorValue.text)
        if (minified != editorValue.text) {
            updateContent(TextFieldValue(minified, TextRange(0)))
            coroutineScope.launch {
                snackbarHostState.showSnackbar("JSON minified")
            }
        }
    }

    fun runValidation() {
        val result = JsonEngine.validate(editorValue.text)
        validationResult = result
        coroutineScope.launch {
            if (result.isValid) {
                snackbarHostState.showSnackbar("JSON is valid! No syntax errors.")
            } else {
                val loc = if (result.errorLine != null) " at line ${result.errorLine}, col ${result.errorColumn}" else ""
                snackbarHostState.showSnackbar("Error$loc: ${result.errorMessage}")
            }
        }
    }

    fun goToLine(lineNum: Int) {
        val lines = editorValue.text.lines()
        val targetLine = lineNum.coerceIn(1, lines.size)
        var charOffset = 0
        for (i in 0 until targetLine - 1) {
            charOffset += lines[i].length + 1
        }
        editorValue = editorValue.copy(selection = TextRange(charOffset, charOffset))
    }

    fun findNext(forward: Boolean = true) {
        if (searchQuery.isEmpty()) return
        val text = editorValue.text
        val current = editorValue.selection.start
        val nextIdx = if (forward) {
            val searchFrom = (current + 1).coerceAtMost(text.length)
            val found = text.indexOf(searchQuery, searchFrom, ignoreCase = true)
            if (found >= 0) found else text.indexOf(searchQuery, 0, ignoreCase = true)
        } else {
            val searchFrom = (current - 1).coerceAtLeast(0)
            val found = text.lastIndexOf(searchQuery, searchFrom, ignoreCase = true)
            if (found >= 0) found else text.lastIndexOf(searchQuery, text.length, ignoreCase = true)
        }

        if (nextIdx >= 0) {
            editorValue = editorValue.copy(selection = TextRange(nextIdx, nextIdx + searchQuery.length))
        }
    }

    fun replaceCurrent() {
        if (searchQuery.isEmpty()) return
        val text = editorValue.text
        val sel = editorValue.selection
        if (sel.length > 0 && text.substring(sel.start, sel.end).equals(searchQuery, ignoreCase = true)) {
            val newText = text.replaceRange(sel.start, sel.end, replaceQuery)
            updateContent(TextFieldValue(newText, TextRange(sel.start, sel.start + replaceQuery.length)))
            findNext(true)
        } else {
            findNext(true)
        }
    }

    fun replaceAllMatches() {
        if (searchQuery.isEmpty()) return
        val text = editorValue.text
        val newText = text.replace(searchQuery, replaceQuery, ignoreCase = true)
        updateContent(TextFieldValue(newText, TextRange(0)))
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Replaced all occurrences of '$searchQuery'")
        }
    }

    fun insertSnippet(snippet: String) {
        val text = editorValue.text
        val sel = editorValue.selection
        val newText = text.replaceRange(sel.start, sel.end, snippet)
        updateContent(TextFieldValue(newText, TextRange(sel.start + snippet.length)))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isDirty) "$fileName *" else fileName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (bedrockAnalysis.isBedrock) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(3.dp),
                                    color = if (bedrockAnalysis.isTargetVersion126) Color(0xFF1B381D) else Color(0xFF2E2600)
                                ) {
                                    Text(
                                        text = bedrockAnalysis.formatVersion ?: "Bedrock",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (bedrockAnalysis.isTargetVersion126) Color(0xFF81C784) else EJsonYellow,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (bedrockAnalysis.isBedrock) "${bedrockAnalysis.packType} • ${bedrockAnalysis.category}" else "Generic JSON",
                            fontSize = 11.sp,
                            color = EJsonTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isDirty && settings.confirmUnsavedChanges) {
                                showUnsavedDialog = true
                            } else {
                                onBack(isDirty)
                            }
                        },
                        modifier = Modifier.testTag("editor_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Search toggle
                    IconButton(
                        onClick = { isSearchVisible = !isSearchVisible },
                        modifier = Modifier.testTag("search_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Find & Replace",
                            tint = if (isSearchVisible) EJsonYellow else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Validate quick action
                    IconButton(
                        onClick = { runValidation() },
                        modifier = Modifier.testTag("validate_button")
                    ) {
                        Icon(
                            imageVector = if (validationResult?.isValid == false) Icons.Default.ErrorOutline else Icons.Default.Verified,
                            contentDescription = "Validate JSON",
                            tint = if (validationResult?.isValid == false) EJsonRed else EJsonYellow
                        )
                    }

                    // Overflow Menu
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.testTag("editor_overflow_button")
                    ) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Tools")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Format / Pretty Print") },
                            leadingIcon = { Icon(Icons.Default.FormatAlignLeft, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                formatCode()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Minify JSON") },
                            leadingIcon = { Icon(Icons.Default.UnfoldLess, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                minifyCode()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Validate JSON") },
                            leadingIcon = { Icon(Icons.Default.Verified, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                runValidation()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Go to Line") },
                            leadingIcon = { Icon(Icons.Default.FindReplace, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                showGoToLineDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Bedrock Inspector") },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                showBedrockInspector = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Insert Snippet") },
                            leadingIcon = { Icon(Icons.Default.FormatAlignLeft, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                showInsertSnippetDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Select All") },
                            onClick = {
                                showMenu = false
                                editorValue = editorValue.copy(selection = TextRange(0, editorValue.text.length))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear Editor") },
                            onClick = {
                                showMenu = false
                                updateContent(TextFieldValue("", TextRange(0)))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Editor Settings") },
                            onClick = {
                                showMenu = false
                                onOpenSettings()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EJsonDarkBg
                )
            )
        },
        bottomBar = {
            // Bottom Action Bar: [ Save ] [ Save As ] [ Copy ] [ Share ] + Undo/Redo
            Surface(
                color = Color(0xFF14161B),
                border = androidx.compose.foundation.BorderStroke(1.dp, EJsonDarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(
                            onClick = {
                                onSave(editorValue.text)
                                isDirty = false
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("File saved successfully")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EJsonYellow,
                                contentColor = Color(0xFF14161B)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("save_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        TextButton(
                            onClick = { onSaveAs(editorValue.text) },
                            modifier = Modifier.testTag("save_as_button")
                        ) {
                            Text("Save As", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        }

                        IconButton(
                            onClick = {
                                onCopy(editorValue.text)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Copied JSON to clipboard")
                                }
                            },
                            modifier = Modifier.testTag("copy_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy JSON", tint = MaterialTheme.colorScheme.onSurface)
                        }

                        IconButton(
                            onClick = { onShare(fileName, editorValue.text) },
                            modifier = Modifier.testTag("share_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share JSON", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        IconButton(
                            onClick = { undo() },
                            enabled = undoStack.isNotEmpty(),
                            modifier = Modifier.testTag("undo_button")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Undo",
                                tint = if (undoStack.isNotEmpty()) MaterialTheme.colorScheme.onSurface else EJsonTextMuted
                            )
                        }

                        IconButton(
                            onClick = { redo() },
                            enabled = redoStack.isNotEmpty(),
                            modifier = Modifier.testTag("redo_button")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Redo,
                                contentDescription = "Redo",
                                tint = if (redoStack.isNotEmpty()) MaterialTheme.colorScheme.onSurface else EJsonTextMuted
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search and Replace Banner
            AnimatedVisibility(visible = isSearchVisible) {
                Surface(
                    color = Color(0xFF191B22),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EJsonDarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Find in JSON...", fontSize = 13.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("search_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EJsonYellow,
                                    unfocusedBorderColor = EJsonDarkBorder
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = { findNext(true) })
                            )

                            if (searchQuery.isNotEmpty()) {
                                Text(
                                    text = "$matchCount matches",
                                    fontSize = 11.sp,
                                    color = EJsonTextSecondary
                                )
                            }

                            IconButton(onClick = { findNext(false) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Previous")
                            }

                            IconButton(onClick = { findNext(true) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next")
                            }

                            IconButton(
                                onClick = { showReplaceRow = !showReplaceRow },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.FindReplace,
                                    contentDescription = "Toggle Replace",
                                    tint = if (showReplaceRow) EJsonYellow else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(
                                onClick = {
                                    isSearchVisible = false
                                    searchQuery = ""
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close Search")
                            }
                        }

                        if (showReplaceRow) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = replaceQuery,
                                    onValueChange = { replaceQuery = it },
                                    placeholder = { Text("Replace with...", fontSize = 13.sp) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .testTag("replace_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EJsonYellow,
                                        unfocusedBorderColor = EJsonDarkBorder
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
                                )

                                Button(
                                    onClick = { replaceCurrent() },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Text("Replace", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { replaceAllMatches() },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Text("All", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Error banner if invalid
            validationResult?.let { res ->
                if (!res.isValid) {
                    Surface(
                        color = Color(0xFF331414),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = EJsonRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "Line ${res.errorLine ?: 1}, Col ${res.errorColumn ?: 1}: ${res.errorMessage}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFFFCDD2)
                                    )
                                    if (!res.snippet.isNullOrEmpty()) {
                                        Text(
                                            text = "> ${res.snippet}",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = EJsonTextSecondary
                                        )
                                    }
                                }
                            }

                            if (res.errorLine != null) {
                                TextButton(onClick = { goToLine(res.errorLine) }) {
                                    Text("Go to", fontSize = 12.sp, color = EJsonYellow)
                                }
                            }

                            IconButton(
                                onClick = { validationResult = null },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = EJsonTextMuted)
                            }
                        }
                    }
                }
            }

            // Code Editor
            CodeEditor(
                textFieldValue = editorValue,
                onValueChange = { updateContent(it) },
                settings = settings,
                searchQuery = searchQuery,
                highlightedErrorLine = validationResult?.takeIf { !it.isValid }?.errorLine,
                modifier = Modifier.weight(1f)
            )
        }
    }

    // Go to Line Dialog
    if (showGoToLineDialog) {
        AlertDialog(
            onDismissRequest = { showGoToLineDialog = false },
            title = { Text("Go to Line") },
            text = {
                OutlinedTextField(
                    value = goToLineText,
                    onValueChange = { goToLineText = it.filter { ch -> ch.isDigit() } },
                    placeholder = { Text("e.g. 42") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = goToLineText.toIntOrNull()
                        if (num != null) {
                            goToLine(num)
                        }
                        showGoToLineDialog = false
                        goToLineText = ""
                    }
                ) {
                    Text("Go")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoToLineDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Bedrock Inspector Dialog
    if (showBedrockInspector) {
        AlertDialog(
            onDismissRequest = { showBedrockInspector = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = EJsonYellow)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Minecraft Bedrock Info")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Pack Context: ${bedrockAnalysis.packType}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Category: ${bedrockAnalysis.category}",
                        fontSize = 13.sp
                    )
                    Text(
                        text = "format_version: ${bedrockAnalysis.formatVersion ?: "Not specified"}",
                        fontSize = 13.sp,
                        color = if (bedrockAnalysis.isTargetVersion126) EJsonGreen else MaterialTheme.colorScheme.onSurface
                    )
                    if (!bedrockAnalysis.identifier.isNullOrEmpty()) {
                        Text(
                            text = "Identifier: ${bedrockAnalysis.identifier}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFF81C784)
                        )
                    }
                    if (bedrockAnalysis.componentCount > 0) {
                        Text(
                            text = "Components detected: ${bedrockAnalysis.componentCount}",
                            fontSize = 13.sp
                        )
                    }
                    if (bedrockAnalysis.uuids.isNotEmpty()) {
                        Text(
                            text = "Manifest UUIDs (${bedrockAnalysis.uuids.size}):",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        bedrockAnalysis.uuids.forEach { u ->
                            Text(
                                text = u,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = EJsonYellow
                            )
                        }
                    }
                    if (bedrockAnalysis.notes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Assistance Notes:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        bedrockAnalysis.notes.forEach { note ->
                            Text(
                                text = "• $note",
                                fontSize = 12.sp,
                                color = if (note.contains("Warning")) EJsonRed else EJsonTextSecondary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showBedrockInspector = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Insert Snippet Dialog
    if (showInsertSnippetDialog) {
        val snippets = listOf(
            "New UUID v4" to "\"${JsonEngine.generateUuid()}\"",
            "Bedrock 1.26.40.5 format_version" to "\"format_version\": \"1.26.40\",",
            "min_engine_version [1, 26, 40]" to "\"min_engine_version\": [1, 26, 40],",
            "Empty Object {}" to "{}",
            "Empty Array []" to "[]"
        )
        AlertDialog(
            onDismissRequest = { showInsertSnippetDialog = false },
            title = { Text("Insert Bedrock Snippet") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    snippets.forEach { (label, code) ->
                        Card(
                            onClick = {
                                insertSnippet(code)
                                showInsertSnippetDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = EJsonDarkCard)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text(
                                    text = code,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = EJsonYellow,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showInsertSnippetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Unsaved Changes Confirmation Dialog
    if (showUnsavedDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedDialog = false },
            title = { Text("Unsaved Changes") },
            text = { Text("You have unsaved changes in $fileName. Discard edits and leave?") },
            confirmButton = {
                Button(
                    onClick = {
                        showUnsavedDialog = false
                        onBack(false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Discard & Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnsavedDialog = false }) {
                    Text("Keep Editing")
                }
            }
        )
    }
}
