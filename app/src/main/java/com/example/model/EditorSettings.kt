package com.example.model

data class EditorSettings(
    val themeMode: String = "dark", // "dark", "light", "system"
    val fontSizeSp: Int = 14,
    val showLineNumbers: Boolean = true,
    val wordWrap: Boolean = false,
    val syntaxHighlighting: Boolean = true,
    val indentSpaces: Int = 2,
    val confirmUnsavedChanges: Boolean = true
)
