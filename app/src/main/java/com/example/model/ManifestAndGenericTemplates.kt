package com.example.model

import java.util.UUID

object ManifestAndGenericTemplates {

    fun generateManifest(
        name: String,
        description: String,
        isBehavior: Boolean,
        minEngineVersion: List<Int> = listOf(1, 26, 40)
    ): String {
        val headerUuid = UUID.randomUUID().toString()
        val moduleUuid = UUID.randomUUID().toString()
        val moduleType = if (isBehavior) "data" else "resources"
        val engineVer = minEngineVersion.joinToString(", ")

        return """{
  "format_version": 2,
  "header": {
    "name": "$name",
    "description": "$description",
    "uuid": "$headerUuid",
    "version": [1, 0, 0],
    "min_engine_version": [$engineVer]
  },
  "modules": [
    {
      "description": "$description module",
      "type": "$moduleType",
      "uuid": "$moduleUuid",
      "version": [1, 0, 0]
    }
  ]
}"""
    }

    val list: List<BedrockTemplate> = listOf(
        BedrockTemplate(
            id = "blank_json",
            title = "Blank JSON Object",
            category = TemplateCategory.GENERIC,
            packType = "Generic",
            targetVersion = "Generic JSON",
            description = "Starts with an empty valid JSON object {}",
            defaultFileName = "untitled.json",
            content = "{\n  \n}"
        ),
        BedrockTemplate(
            id = "blank_array",
            title = "Blank JSON Array",
            category = TemplateCategory.GENERIC,
            packType = "Generic",
            targetVersion = "Generic JSON",
            description = "Starts with an empty valid JSON array []",
            defaultFileName = "data.json",
            content = "[\n  \n]"
        ),
        BedrockTemplate(
            id = "manifest_behavior",
            title = "Behavior Pack Manifest",
            category = TemplateCategory.MANIFEST,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Core manifest definition for Bedrock behavior packs with UUIDs and min_engine_version [1, 26, 40]",
            defaultFileName = "manifest.json",
            content = """{
  "format_version": 2,
  "header": {
    "name": "Custom Behavior Pack",
    "description": "Created with EJson Bedrock Studio",
    "uuid": "8b5a0a30-80ea-4c4f-9e73-b3c9b7e7d011",
    "version": [1, 0, 0],
    "min_engine_version": [1, 26, 40]
  },
  "modules": [
    {
      "description": "Custom Behaviors",
      "type": "data",
      "uuid": "4f3c7891-62a2-4a55-b0db-6e792c3a5ef2",
      "version": [1, 0, 0]
    }
  ]
}"""
        ),
        BedrockTemplate(
            id = "manifest_resource",
            title = "Resource Pack Manifest",
            category = TemplateCategory.MANIFEST,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Resource pack manifest with resources module type and min_engine_version [1, 26, 40]",
            defaultFileName = "manifest.json",
            content = """{
  "format_version": 2,
  "header": {
    "name": "Custom Resource Pack",
    "description": "Textures and models for Bedrock 1.26.40.5",
    "uuid": "9c6b1b41-91fb-5d5e-af84-c4dab8f8e122",
    "version": [1, 0, 0],
    "min_engine_version": [1, 26, 40]
  },
  "modules": [
    {
      "description": "Resources",
      "type": "resources",
      "uuid": "5e4d89a2-73b3-5b66-c1ec-7f8a3d4b6fa3",
      "version": [1, 0, 0]
    }
  ]
}"""
        ),
        BedrockTemplate(
            id = "manifest_script",
            title = "Script API Manifest (Gametest / Server)",
            category = TemplateCategory.MANIFEST,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Behavior pack manifest with @minecraft/server TypeScript/JavaScript scripting module",
            defaultFileName = "manifest.json",
            content = """{
  "format_version": 2,
  "header": {
    "name": "Scripting Behavior Pack",
    "description": "Bedrock Server Script API pack",
    "uuid": "2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e",
    "version": [1, 0, 0],
    "min_engine_version": [1, 26, 40]
  },
  "modules": [
    {
      "description": "Script Module",
      "type": "script",
      "language": "javascript",
      "entry": "scripts/main.js",
      "uuid": "3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f",
      "version": [1, 0, 0]
    }
  ],
  "dependencies": [
    {
      "module_name": "@minecraft/server",
      "version": "1.13.0"
    },
    {
      "module_name": "@minecraft/server-ui",
      "version": "1.3.0"
    }
  ]
}"""
        ),
        BedrockTemplate(
            id = "manifest_skin_pack",
            title = "Skin Pack Manifest",
            category = TemplateCategory.MANIFEST,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Skin pack manifest with skins module definition and UUIDs",
            defaultFileName = "manifest.json",
            content = """{
  "format_version": 1,
  "header": {
    "name": "Custom Skin Pack",
    "uuid": "3e4a9082-1d7c-489e-97c2-8491c498ae61",
    "version": [1, 0, 0]
  },
  "modules": [
    {
      "type": "skin_pack",
      "uuid": "7a1b3294-84c1-4ef3-b09a-14d8721c5f39",
      "version": [1, 0, 0]
    }
  ]
}"""
        ),
        BedrockTemplate(
            id = "generic_key_value",
            title = "Generic Config Dictionary",
            category = TemplateCategory.GENERIC,
            packType = "Generic",
            targetVersion = "Generic JSON",
            description = "Standard key-value dictionary with nested options",
            defaultFileName = "config.json",
            content = """{
  "server_name": "Bedrock Server",
  "port": 19132,
  "max_players": 10,
  "pvp_enabled": true,
  "difficulty": "normal",
  "allowed_packs": [
    "custom_bp",
    "custom_rp"
  ]
}"""
        )
    )
}
