package com.example.util

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.util.UUID

data class JsonValidationResult(
    val isValid: Boolean,
    val errorLine: Int? = null,
    val errorColumn: Int? = null,
    val errorMessage: String? = null,
    val snippet: String? = null
)

data class BedrockAnalysis(
    val isBedrock: Boolean = false,
    val category: String = "Generic JSON",
    val packType: String = "Unknown",
    val formatVersion: String? = null,
    val isTargetVersion126: Boolean = false,
    val identifier: String? = null,
    val componentCount: Int = 0,
    val uuids: List<String> = emptyList(),
    val duplicateUuids: List<String> = emptyList(),
    val notes: List<String> = emptyList()
)

object JsonEngine {

    fun generateUuid(): String {
        return UUID.randomUUID().toString()
    }

    /**
     * Validates generic JSON with line and column detection.
     */
    fun validate(jsonString: String): JsonValidationResult {
        val trimmed = jsonString.trim()
        if (trimmed.isEmpty()) {
            return JsonValidationResult(
                isValid = false,
                errorLine = 1,
                errorColumn = 1,
                errorMessage = "Document is empty."
            )
        }

        val tokener = JSONTokener(jsonString)
        try {
            val value = tokener.nextValue()
            // Make sure there is no trailing non-whitespace characters
            while (tokener.more()) {
                val ch = tokener.next()
                if (!ch.isWhitespace()) {
                    val (line, col) = calculateLineAndCol(jsonString, jsonString.length - 1)
                    return JsonValidationResult(
                        isValid = false,
                        errorLine = line,
                        errorColumn = col,
                        errorMessage = "Unexpected token '$ch' after top-level JSON value.",
                        snippet = getSnippet(jsonString, line)
                    )
                }
            }

            if (value !is JSONObject && value !is JSONArray) {
                return JsonValidationResult(
                    isValid = false,
                    errorLine = 1,
                    errorColumn = 1,
                    errorMessage = "Top-level value must be an Object {} or Array [].",
                    snippet = getSnippet(jsonString, 1)
                )
            }

            return JsonValidationResult(isValid = true)
        } catch (e: Exception) {
            val msg = e.message ?: "Invalid JSON syntax."
            // JSONTokener in Android produces messages like:
            // "Expected ':' at character 45 of ..." or "Unterminated string at character 12 of ..."
            val charIndex = extractCharIndex(msg)
            val (line, col) = if (charIndex != null && charIndex in jsonString.indices) {
                calculateLineAndCol(jsonString, charIndex)
            } else {
                guessErrorLocation(jsonString)
            }

            val cleanMessage = msg.substringBefore(" of ")
                .replace("org.json.JSONException: ", "")
                .trim()

            return JsonValidationResult(
                isValid = false,
                errorLine = line,
                errorColumn = col,
                errorMessage = cleanMessage.ifEmpty { "JSON syntax error" },
                snippet = getSnippet(jsonString, line)
            )
        }
    }

    private fun extractCharIndex(message: String): Int? {
        val regex = Regex("at character (\\d+)")
        val match = regex.find(message)
        return match?.groupValues?.get(1)?.toIntOrNull()
    }

    private fun calculateLineAndCol(text: String, charIndex: Int): Pair<Int, Int> {
        val target = charIndex.coerceIn(0, text.length)
        var line = 1
        var col = 1
        for (i in 0 until target) {
            if (text[i] == '\n') {
                line++
                col = 1
            } else {
                col++
            }
        }
        return Pair(line, col)
    }

    private fun guessErrorLocation(text: String): Pair<Int, Int> {
        // Fallback: check unmatched braces or quotes
        var line = 1
        var col = 1
        var inString = false
        var escaped = false

        for (i in text.indices) {
            val c = text[i]
            if (c == '\n') {
                line++
                col = 1
            } else {
                col++
            }

            if (escaped) {
                escaped = false
                continue
            }
            if (c == '\\') {
                escaped = true
                continue
            }
            if (c == '"') {
                inString = !inString
            }
        }
        return Pair(line, col)
    }

    private fun getSnippet(text: String, line: Int): String? {
        val lines = text.lines()
        val index = line - 1
        if (index in lines.indices) {
            return lines[index].trim()
        }
        return null
    }

    /**
     * Pretty print formatting with custom indent.
     */
    fun format(jsonString: String, indentSpaces: Int = 2): String {
        val trimmed = jsonString.trim()
        if (trimmed.isEmpty()) return jsonString
        return try {
            val tokener = JSONTokener(trimmed)
            when (val value = tokener.nextValue()) {
                is JSONObject -> value.toString(indentSpaces)
                is JSONArray -> value.toString(indentSpaces)
                else -> jsonString
            }
        } catch (e: Exception) {
            jsonString
        }
    }

    /**
     * Compact minifying JSON.
     */
    fun minify(jsonString: String): String {
        val trimmed = jsonString.trim()
        if (trimmed.isEmpty()) return jsonString
        return try {
            val tokener = JSONTokener(trimmed)
            when (val value = tokener.nextValue()) {
                is JSONObject -> value.toString()
                is JSONArray -> value.toString()
                else -> jsonString
            }
        } catch (e: Exception) {
            jsonString
        }
    }

    /**
     * Deep Minecraft Bedrock schema & version assistance.
     * Evaluates format_version, identifies pack type, checks UUID uniqueness.
     */
    fun analyzeBedrock(rawJson: String): BedrockAnalysis {
        val trimmed = rawJson.trim()
        if (trimmed.isEmpty()) return BedrockAnalysis()

        return try {
            val tokener = JSONTokener(trimmed)
            val root = tokener.nextValue() as? JSONObject ?: return BedrockAnalysis(
                isBedrock = false,
                category = "Generic JSON Array",
                packType = "Non-Bedrock"
            )

            var isBedrock = false
            var category = "Generic JSON Object"
            var packType = "General"
            var formatVersion: String? = null
            var identifier: String? = null
            var componentCount = 0
            val uuids = mutableListOf<String>()
            val notes = mutableListOf<String>()

            // Extract format_version
            if (root.has("format_version")) {
                isBedrock = true
                val fv = root.get("format_version")
                formatVersion = when (fv) {
                    is JSONArray -> {
                        val list = mutableListOf<Int>()
                        for (i in 0 until fv.length()) {
                            list.add(fv.optInt(i))
                        }
                        list.joinToString(".")
                    }
                    else -> fv.toString()
                }
            }

            // Check Manifest
            if (root.has("header") && root.has("modules")) {
                isBedrock = true
                category = "Pack Manifest"
                val header = root.optJSONObject("header")
                val headerUuid = header?.optString("uuid")
                if (!headerUuid.isNullOrEmpty()) uuids.add(headerUuid)

                val modules = root.optJSONArray("modules")
                var hasBehavior = false
                var hasResource = false
                var hasSkin = false

                if (modules != null) {
                    for (i in 0 until modules.length()) {
                        val mod = modules.optJSONObject(i) ?: continue
                        val type = mod.optString("type")
                        val modUuid = mod.optString("uuid")
                        if (!modUuid.isNullOrEmpty()) uuids.add(modUuid)

                        when (type.lowercase()) {
                            "data", "script" -> hasBehavior = true
                            "resources" -> hasResource = true
                            "skin_pack" -> hasSkin = true
                        }
                    }
                }

                packType = when {
                    hasBehavior && hasResource -> "Hybrid Pack"
                    hasBehavior -> "Behavior Pack"
                    hasResource -> "Resource Pack"
                    hasSkin -> "Skin Pack"
                    else -> "Add-On Manifest"
                }

                val minEngine = header?.optJSONArray("min_engine_version")
                if (minEngine != null) {
                    val vStr = (0 until minEngine.length()).map { minEngine.optInt(it) }.joinToString(".")
                    notes.add("Min Engine Version: $vStr")
                    if (vStr.startsWith("1.26.40")) {
                        notes.add("Target Bedrock 1.26.40.5 compatible.")
                    }
                } else {
                    notes.add("Tip: Add 'min_engine_version': [1, 26, 40] for Bedrock 1.26.40.5.")
                }
            }

            // Check Behavior Pack Entity
            if (root.has("minecraft:entity")) {
                isBedrock = true
                category = "Entity Definition"
                packType = "Behavior Pack"
                val entityObj = root.optJSONObject("minecraft:entity")
                val desc = entityObj?.optJSONObject("description")
                identifier = desc?.optString("identifier")
                val comps = entityObj?.optJSONObject("components")
                if (comps != null) {
                    componentCount = comps.length()
                }
            }

            // Check Item
            if (root.has("minecraft:item")) {
                isBedrock = true
                category = "Item Definition"
                packType = "Behavior Pack"
                val itemObj = root.optJSONObject("minecraft:item")
                val desc = itemObj?.optJSONObject("description")
                identifier = desc?.optString("identifier")
                val comps = itemObj?.optJSONObject("components")
                if (comps != null) {
                    componentCount = comps.length()
                }
            }

            // Check Block
            if (root.has("minecraft:block")) {
                isBedrock = true
                category = "Block Definition"
                packType = "Behavior Pack"
                val blockObj = root.optJSONObject("minecraft:block")
                val desc = blockObj?.optJSONObject("description")
                identifier = desc?.optString("identifier")
                val comps = blockObj?.optJSONObject("components")
                if (comps != null) {
                    componentCount = comps.length()
                }
            }

            // Check Recipes
            if (root.has("minecraft:recipe_shaped") || root.has("minecraft:recipe_shapeless") || root.has("minecraft:recipe_furnace")) {
                isBedrock = true
                category = "Crafting Recipe"
                packType = "Behavior Pack"
                val rObj = root.optJSONObject("minecraft:recipe_shaped")
                    ?: root.optJSONObject("minecraft:recipe_shapeless")
                    ?: root.optJSONObject("minecraft:recipe_furnace")
                identifier = rObj?.optJSONObject("description")?.optString("identifier")
            }

            // Check Loot Tables
            if (root.has("pools")) {
                isBedrock = true
                category = "Loot Table"
                packType = "Behavior Pack"
                val pools = root.optJSONArray("pools")
                notes.add("Pools: ${pools?.length() ?: 0}")
            }

            // Check Spawn Rules
            if (root.has("minecraft:spawn_rules")) {
                isBedrock = true
                category = "Spawn Rules"
                packType = "Behavior Pack"
                identifier = root.optJSONObject("minecraft:spawn_rules")
                    ?.optJSONObject("description")?.optString("identifier")
            }

            // Check Client Entity (Resource Pack)
            if (root.has("minecraft:client_entity")) {
                isBedrock = true
                category = "Client Entity"
                packType = "Resource Pack"
                identifier = root.optJSONObject("minecraft:client_entity")
                    ?.optJSONObject("description")?.optString("identifier")
            }

            // Check Render Controllers
            if (root.has("render_controllers")) {
                isBedrock = true
                category = "Render Controller"
                packType = "Resource Pack"
            }

            // Check Attachables
            if (root.has("minecraft:attachable")) {
                isBedrock = true
                category = "Attachable"
                packType = "Resource Pack"
                identifier = root.optJSONObject("minecraft:attachable")
                    ?.optJSONObject("description")?.optString("identifier")
            }

            // Check Particle Effect
            if (root.has("particle_effect")) {
                isBedrock = true
                category = "Particle Effect"
                packType = "Resource Pack"
                identifier = root.optJSONObject("particle_effect")
                    ?.optJSONObject("description")?.optString("identifier")
            }

            // Check Sound Definitions
            if (root.has("sound_definitions")) {
                isBedrock = true
                category = "Sound Definitions"
                packType = "Resource Pack"
            }

            // Check Textures
            if (root.has("texture_data") || root.has("resource_pack_name")) {
                isBedrock = true
                category = "Texture Atlas"
                packType = "Resource Pack"
            }

            // Check World Gen Feature / Feature Rules
            if (root.has("minecraft:feature_rules") || root.has("minecraft:ore_feature") || root.has("minecraft:aggregate_feature")) {
                isBedrock = true
                category = if (root.has("minecraft:feature_rules")) "Feature Rule" else "World Feature"
                packType = "World Generation"
            }

            // UUID Duplication check
            val duplicates = uuids.groupBy { it.lowercase() }
                .filter { it.value.size > 1 }
                .keys.toList()

            if (duplicates.isNotEmpty()) {
                notes.add("Warning: Duplicate UUIDs detected! Bedrock requires unique UUIDs.")
            }

            val isTarget126 = formatVersion != null && (formatVersion.contains("1.26") || formatVersion == "2")

            BedrockAnalysis(
                isBedrock = isBedrock,
                category = category,
                packType = packType,
                formatVersion = formatVersion,
                isTargetVersion126 = isTarget126,
                identifier = identifier,
                componentCount = componentCount,
                uuids = uuids,
                duplicateUuids = duplicates,
                notes = notes
            )
        } catch (e: Exception) {
            BedrockAnalysis(isBedrock = false, category = "Invalid / Unparsed JSON")
        }
    }
}
