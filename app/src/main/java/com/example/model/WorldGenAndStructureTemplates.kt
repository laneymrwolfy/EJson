package com.example.model

object WorldGenAndStructureTemplates {

    val list: List<BedrockTemplate> = listOf(
        // === WORLD GENERATION FEATURES ===
        BedrockTemplate(
            id = "wg_ore_feature",
            title = "Ore Feature Definition",
            category = TemplateCategory.WORLD_GEN,
            packType = "World Gen",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Places veins of replacement blocks inside target stone/deepslate materials",
            defaultFileName = "ruby_ore_feature.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:ore_feature": {
    "description": {
      "identifier": "custom:ruby_ore_feature"
    },
    "count": 8,
    "replace_rules": [
      {
        "places_block": "custom:ruby_ore",
        "may_replace": [
          "minecraft:stone",
          "minecraft:deepslate"
        ]
      }
    ]
  }
}"""
        ),
        BedrockTemplate(
            id = "wg_single_block_feature",
            title = "Single Block Feature",
            category = TemplateCategory.WORLD_GEN,
            packType = "World Gen",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Places a single block type with may_replace and may_place_on placement filters",
            defaultFileName = "crystal_spire_feature.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:single_block_feature": {
    "description": {
      "identifier": "custom:crystal_spire_feature"
    },
    "places_block": "custom:carved_lantern",
    "enforce_survivability_rules": false,
    "may_place_on": [
      "minecraft:grass_block",
      "minecraft:stone"
    ]
  }
}"""
        ),
        BedrockTemplate(
            id = "wg_scatter_feature",
            title = "Scatter Feature",
            category = TemplateCategory.WORLD_GEN,
            packType = "World Gen",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Scatters another feature over a coordinate distribution grid",
            defaultFileName = "crystal_patch_feature.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:scatter_feature": {
    "description": {
      "identifier": "custom:crystal_patch_feature"
    },
    "places_feature": "custom:crystal_spire_feature",
    "iterations": 10,
    "scatter_chance": 50.0,
    "x": {
      "distribution": "uniform",
      "extent": [0, 15]
    },
    "z": {
      "distribution": "uniform",
      "extent": [0, 15]
    },
    "y": "query.heightmap(variable.worldx, variable.worldz)"
  }
}"""
        ),
        BedrockTemplate(
            id = "wg_aggregate_feature",
            title = "Aggregate Feature (Feature List)",
            category = TemplateCategory.WORLD_GEN,
            packType = "World Gen",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Runs multiple sub-features sequentially in the same chunk pass",
            defaultFileName = "custom_cluster_feature.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:aggregate_feature": {
    "description": {
      "identifier": "custom:dungeon_aggregate_feature"
    },
    "features": [
      "custom:ruby_ore_feature",
      "custom:crystal_spire_feature"
    ]
  }
}"""
        ),
        BedrockTemplate(
            id = "wg_feature_rule",
            title = "Feature Rule (Underground Ore Pass)",
            category = TemplateCategory.WORLD_GEN,
            packType = "World Gen",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Attaches an ore feature to world generation passes and biome tags",
            defaultFileName = "ruby_ore_feature_rule.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:feature_rules": {
    "description": {
      "identifier": "custom:ruby_ore_feature_rule",
      "places_feature": "custom:ruby_ore_feature"
    },
    "conditions": {
      "placement_pass": "underground_pass",
      "minecraft:biome_filter": [
        {
          "test": "has_biome_tag",
          "operator": "==",
          "value": "overworld"
        }
      ]
    },
    "distribution": {
      "iterations": 12,
      "x": {
        "distribution": "uniform",
        "extent": [0, 16]
      },
      "y": {
        "distribution": "uniform",
        "extent": [-64, 32]
      },
      "z": {
        "distribution": "uniform",
        "extent": [0, 16]
      }
    }
  }
}"""
        ),
        BedrockTemplate(
            id = "wg_biome_definition",
            title = "Custom Biome Definition",
            category = TemplateCategory.WORLD_GEN,
            packType = "World Gen",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Custom biome JSON with climate, sky color, surface blocks, and tags",
            defaultFileName = "ruby_plains.biome.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:biome": {
    "description": {
      "identifier": "custom:ruby_plains"
    },
    "components": {
      "minecraft:climate": {
        "downfall": 0.4,
        "temperature": 0.8
      },
      "minecraft:overworld_height": {
        "noise_type": "lowlands"
      },
      "minecraft:surface_parameters": {
        "top_material": "minecraft:grass_block",
        "mid_material": "minecraft:dirt",
        "sea_floor_material": "minecraft:gravel",
        "foundation_material": "minecraft:stone"
      },
      "minecraft:sky_color": "#78A7FF",
      "minecraft:water_color": "#3F76E4",
      "minecraft:fog_appearance": {
        "fog_identifier": "minecraft:fog_default"
      },
      "minecraft:tags": {
        "tags": [
          "overworld",
          "plains"
        ]
      }
    }
  }
}"""
        ),

        // === STRUCTURES (VERY IMPORTANT) ===
        // NOTE: .mcstructure files are binary NBT, NOT ordinary JSON.
        // Bedrock uses JSON for placement, rules, template pools, and generation.
        BedrockTemplate(
            id = "struct_feature_rule",
            title = "Structure Placement Feature Rule",
            category = TemplateCategory.STRUCTURES,
            packType = "Structures",
            targetVersion = "Bedrock 1.26.40.5",
            description = "JSON Feature Rule placing a .mcstructure file in biomes at surface level",
            defaultFileName = "custom_house_feature_rule.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:feature_rules": {
    "description": {
      "identifier": "custom:custom_house_feature_rule",
      "places_feature": "custom:custom_house_feature"
    },
    "conditions": {
      "placement_pass": "surface_pass",
      "minecraft:biome_filter": [
        {
          "all_of": [
            {
              "test": "has_biome_tag",
              "operator": "==",
              "value": "overworld"
            },
            {
              "test": "has_biome_tag",
              "operator": "==",
              "value": "plains"
            }
          ]
        }
      ]
    },
    "distribution": {
      "iterations": 1,
      "scatter_chance": 4.0,
      "x": {
        "distribution": "uniform",
        "extent": [0, 16]
      },
      "z": {
        "distribution": "uniform",
        "extent": [0, 16]
      },
      "y": "query.heightmap(variable.worldx, variable.worldz)"
    }
  }
}"""
        ),
        BedrockTemplate(
            id = "struct_placement_feature",
            title = "Structure Feature (Calling .mcstructure)",
            category = TemplateCategory.STRUCTURES,
            packType = "Structures",
            targetVersion = "Bedrock 1.26.40.5",
            description = "JSON feature referencing a .mcstructure asset file by identifier with rotation/mirror",
            defaultFileName = "custom_house_feature.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:structure_template_feature": {
    "description": {
      "identifier": "custom:custom_house_feature"
    },
    "structure_name": "mystructure:custom_house",
    "adjustment_radius": 4,
    "facing_direction": "random",
    "constraints": {
      "grounded": {},
      "unburied": {},
      "block_intersection": {
        "block_allowlist": [
          "minecraft:air",
          "minecraft:grass_block",
          "minecraft:dirt"
        ]
      }
    }
  }
}"""
        ),
        BedrockTemplate(
            id = "struct_scatter_config",
            title = "Town / Village Scatter Configuration",
            category = TemplateCategory.STRUCTURES,
            packType = "Structures",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Scatter JSON coordinating multiple building structures with radius checks",
            defaultFileName = "town_cluster_scatter.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:scatter_feature": {
    "description": {
      "identifier": "custom:town_cluster_scatter"
    },
    "places_feature": "custom:custom_house_feature",
    "iterations": 3,
    "scatter_chance": 15.0,
    "x": {
      "distribution": "gaussian",
      "extent": [0, 32]
    },
    "z": {
      "distribution": "gaussian",
      "extent": [0, 32]
    },
    "y": "query.heightmap(variable.worldx, variable.worldz)"
  }
}"""
        ),
        BedrockTemplate(
            id = "struct_template_pool",
            title = "Jigsaw Structure Template Pool",
            category = TemplateCategory.STRUCTURES,
            packType = "Structures",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Template pool definition for jigsaw block procedural structure generation",
            defaultFileName = "custom_village_pool.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:template_pool": {
    "description": {
      "identifier": "custom:village_houses_pool"
    },
    "elements": [
      {
        "weight": 3,
        "element": {
          "element_type": "minecraft:single_pool_element",
          "location": "mystructure:small_house",
          "projection": "rigid",
          "processors": "minecraft:empty"
        }
      },
      {
        "weight": 1,
        "element": {
          "element_type": "minecraft:single_pool_element",
          "location": "mystructure:blacksmith_house",
          "projection": "rigid",
          "processors": "minecraft:empty"
        }
      }
    ]
  }
}"""
        )
    )
}
