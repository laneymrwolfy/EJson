package com.example.model

import java.util.UUID

enum class TemplateCategory(val displayName: String) {
    BEHAVIOR_PACK("Behavior Pack"),
    RESOURCE_PACK("Resource Pack"),
    ENTITIES("Entities"),
    ITEMS("Items"),
    BLOCKS("Blocks"),
    RECIPES("Recipes"),
    LOOT_TRADING("Loot & Trading"),
    WORLD_GEN("World Generation"),
    STRUCTURES("Structures"),
    ANIMATION_RENDERING("Animations & Rendering"),
    PARTICLES_SOUNDS("Particles & Sounds"),
    UI_TEXTURES("UI & Textures"),
    MANIFEST("Manifests"),
    GENERIC("Generic JSON"),
    OTHER("Other")
}

data class BedrockTemplate(
    val id: String,
    val title: String,
    val category: TemplateCategory,
    val packType: String = "Bedrock",
    val targetVersion: String = "Bedrock 1.26.40.5",
    val description: String,
    val defaultFileName: String,
    val content: String
)

object TemplateLibrary {

    fun generateManifest(
        name: String,
        description: String,
        isBehavior: Boolean,
        minEngineVersion: List<Int> = listOf(1, 26, 40)
    ): String {
        return ManifestAndGenericTemplates.generateManifest(
            name = name,
            description = description,
            isBehavior = isBehavior,
            minEngineVersion = minEngineVersion
        )
    }

    val templates: List<BedrockTemplate> by lazy {
        ManifestAndGenericTemplates.list +
                BehaviorTemplates.list +
                ResourceTemplates.list +
                WorldGenAndStructureTemplates.list
    }

    fun getByCategory(category: TemplateCategory): List<BedrockTemplate> {
        return templates.filter { it.category == category }
    }

    fun getByPackType(packType: String): List<BedrockTemplate> {
        return templates.filter { it.packType.equals(packType, ignoreCase = true) }
    }

    fun getBlankTemplate(): BedrockTemplate {
        return templates.firstOrNull { it.id == "blank_json" }
            ?: BedrockTemplate(
                id = "blank_json",
                title = "Blank JSON Object",
                category = TemplateCategory.GENERIC,
                packType = "Generic",
                targetVersion = "Generic JSON",
                description = "Starts with empty valid JSON object {}",
                defaultFileName = "untitled.json",
                content = "{\n  \n}"
            )
    }
}
