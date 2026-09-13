package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.example.model.EditorSettings
import com.example.model.RecentFile
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class StorageManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ejson_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_RECENTS = "key_recent_files"
        private const val KEY_THEME = "key_theme_mode"
        private const val KEY_FONT_SIZE = "key_font_size"
        private const val KEY_LINE_NUMBERS = "key_line_numbers"
        private const val KEY_WORD_WRAP = "key_word_wrap"
        private const val KEY_SYNTAX_HL = "key_syntax_highlight"
        private const val KEY_INDENT = "key_indent_spaces"
        private const val KEY_CONFIRM_UNSAVED = "key_confirm_unsaved"
    }

    // === FILE I/O ===

    fun readTextFromUri(uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } ?: throw IllegalStateException("Unable to open input stream for URI: $uri")
    }

    fun writeTextToUri(uri: Uri, content: String) {
        context.contentResolver.openOutputStream(uri, "wt")?.use { stream ->
            stream.bufferedWriter(Charsets.UTF_8).use { writer ->
                writer.write(content)
                writer.flush()
            }
        } ?: throw IllegalStateException("Unable to open output stream for URI: $uri")
    }

    fun queryFileInfo(uri: Uri): Pair<String, Long> {
        var name = "untitled.json"
        var size = 0L

        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (nameIndex != -1) {
                            name = cursor.getString(nameIndex) ?: name
                        }
                        if (sizeIndex != -1 && !cursor.isNull(sizeIndex)) {
                            size = cursor.getLong(sizeIndex)
                        }
                    }
                }
            } catch (ignored: Exception) {
            }
        } else if (uri.scheme == "file") {
            name = uri.lastPathSegment ?: name
            try {
                uri.path?.let { p ->
                    val f = File(p)
                    if (f.exists()) size = f.length()
                }
            } catch (ignored: Exception) {
            }
        }
        return Pair(name, size)
    }

    // === CLIPBOARD ===

    fun copyToClipboard(text: String, label: String = "EJson Content") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    // === SHARE ===

    fun shareJson(fileName: String, content: String) {
        try {
            val sharedDir = File(context.cacheDir, "shared")
            if (!sharedDir.exists()) sharedDir.mkdirs()

            val safeName = if (fileName.endsWith(".json", ignoreCase = true)) fileName else "$fileName.json"
            val file = File(sharedDir, safeName)
            FileOutputStream(file).use { fos ->
                fos.bufferedWriter(Charsets.UTF_8).use { writer ->
                    writer.write(content)
                    writer.flush()
                }
            }

            val fileUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, safeName)
                putExtra(Intent.EXTRA_TEXT, content)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share JSON via").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            // Fallback to text sharing
            val textIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, fileName)
                putExtra(Intent.EXTRA_TEXT, content)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(textIntent, "Share JSON"))
        }
    }

    // === RECENT FILES PERSISTENCE ===

    fun getRecentFiles(): List<RecentFile> {
        val raw = prefs.getString(KEY_RECENTS, null) ?: return emptyList()
        val list = mutableListOf<RecentFile>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    RecentFile(
                        uriString = obj.getString("uri"),
                        fileName = obj.getString("name"),
                        category = obj.optString("cat", "Generic JSON"),
                        lastOpened = obj.optLong("time", System.currentTimeMillis()),
                        sizeBytes = obj.optLong("size", 0L)
                    )
                )
            }
        } catch (e: Exception) {
            // In case of corrupt format, clear
        }
        return list
    }

    fun addRecentFile(recent: RecentFile) {
        val current = getRecentFiles().toMutableList()
        // Remove existing entry for same uri
        current.removeAll { it.uriString == recent.uriString }
        current.add(0, recent)
        // Keep up to 25 recent files
        val trimmed = current.take(25)
        val arr = JSONArray()
        for (item in trimmed) {
            val obj = JSONObject()
            obj.put("uri", item.uriString)
            obj.put("name", item.fileName)
            obj.put("cat", item.category)
            obj.put("time", item.lastOpened)
            obj.put("size", item.sizeBytes)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_RECENTS, arr.toString()).apply()
    }

    fun removeRecentFile(uriString: String) {
        val current = getRecentFiles().filter { it.uriString != uriString }
        val arr = JSONArray()
        for (item in current) {
            val obj = JSONObject()
            obj.put("uri", item.uriString)
            obj.put("name", item.fileName)
            obj.put("cat", item.category)
            obj.put("time", item.lastOpened)
            obj.put("size", item.sizeBytes)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_RECENTS, arr.toString()).apply()
    }

    fun clearRecentFiles() {
        prefs.edit().remove(KEY_RECENTS).apply()
    }

    // === SETTINGS PERSISTENCE ===

    fun getSettings(): EditorSettings {
        return EditorSettings(
            themeMode = prefs.getString(KEY_THEME, "dark") ?: "dark",
            fontSizeSp = prefs.getInt(KEY_FONT_SIZE, 14),
            showLineNumbers = prefs.getBoolean(KEY_LINE_NUMBERS, true),
            wordWrap = prefs.getBoolean(KEY_WORD_WRAP, false),
            syntaxHighlighting = prefs.getBoolean(KEY_SYNTAX_HL, true),
            indentSpaces = prefs.getInt(KEY_INDENT, 2),
            confirmUnsavedChanges = prefs.getBoolean(KEY_CONFIRM_UNSAVED, true)
        )
    }

    fun saveSettings(settings: EditorSettings) {
        prefs.edit()
            .putString(KEY_THEME, settings.themeMode)
            .putInt(KEY_FONT_SIZE, settings.fontSizeSp)
            .putBoolean(KEY_LINE_NUMBERS, settings.showLineNumbers)
            .putBoolean(KEY_WORD_WRAP, settings.wordWrap)
            .putBoolean(KEY_SYNTAX_HL, settings.syntaxHighlighting)
            .putInt(KEY_INDENT, settings.indentSpaces)
            .putBoolean(KEY_CONFIRM_UNSAVED, settings.confirmUnsavedChanges)
            .apply()
    }

    fun resetSettings(): EditorSettings {
        prefs.edit()
            .remove(KEY_THEME)
            .remove(KEY_FONT_SIZE)
            .remove(KEY_LINE_NUMBERS)
            .remove(KEY_WORD_WRAP)
            .remove(KEY_SYNTAX_HL)
            .remove(KEY_INDENT)
            .remove(KEY_CONFIRM_UNSAVED)
            .apply()
        return getSettings()
    }
}
