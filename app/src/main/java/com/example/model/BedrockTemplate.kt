package com.example.model

import java.util.UUID

enum class TemplateCategory(val displayName: String) {
    BEHAVIOR_PACK("Behavior Pack"),
    RESOURCE_PACK("Resource Pack"),
    WORLD_GEN("World Generation"),
    OTHER("Generic & Other")
}

data class BedrockTemplate(
    val id: String,
    val title: String,
    val category: TemplateCategory,
    val targetVersion: String,
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

    val templates: List<BedrockTemplate> by lazy {
        listOf(
            // === BEHAVIOR PACK ===
            BedrockTemplate(
                id = "bp_manifest",
                title = "Behavior Pack Manifest",
                category = TemplateCategory.BEHAVIOR_PACK,
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
                id = "bp_entity",
                title = "Custom Entity Definition",
                category = TemplateCategory.BEHAVIOR_PACK,
                targetVersion = "Bedrock 1.26.40.5",
                description = "Server-side entity definition with health, physics, collision box, and movement",
                defaultFileName = "custom_entity.json",
                content = """{
  "format_version": "1.26.40",
  "minecraft:entity": {
    "description": {
      "identifier": "custom:my_entity",
      "is_spawnable": true,
      "is_summonable": true,
      "is_experimental": false
    },
    "component_groups": {
      "custom:angry": {
        "minecraft:behavior.nearest_attackable_target": {
          "priority": 1,
          "entity_types": [
            {
              "filters": {
                "test": "is_family",
                "subject": "other",
                "value": "player"
              },
              "max_dist": 16.0
            }
          ]
        }
      }
    },
    "components": {
      "minecraft:type_family": {
        "family": ["custom", "mob"]
      },
      "minecraft:collision_box": {
        "width": 0.6,
        "height": 1.8
      },
      "minecraft:health": {
        "value": 20,
        "max": 20
      },
      "minecraft:movement": {
        "value": 0.25
      },
      "minecraft:navigation.walk": {
        "can_path_over_water": false,
        "avoid_water": true
      },
      "minecraft:movement.basic": {},
      "minecraft:jump.static": {},
      "minecraft:can_climb": {},
      "minecraft:physics": {},
      "minecraft:pushable": {
        "is_pushable": true,
        "is_pushable_by_piston": true
      }
    },
    "events": {
      "custom:become_angry": {
        "add": {
          "component_groups": ["custom:angry"]
        }
      }
    }
  }
}"""
            ),
            BedrockTemplate(
                id = "bp_item",
                title = "Custom Item Definition",
                category = TemplateCategory.BEHAVIOR_PACK,
                targetVersion = "Bedrock 1.26.40.5",
                description = "Custom item definition with components, max stack, icon, and hand-equipped state",
                defaultFileName = "custom_item.json",
                content = """{
  "format_version": "1.26.40",
  "minecraft:item": {
    "description": {
      "identifier": "custom:ruby_sword",
      "menu_category": {
        "category": "equipment",
        "group": "itemGroup.name.sword"
      }
    },
    "components": {
      "minecraft:icon": {
        "textures": {
          "default": "ruby_sword"
        }
      },
      "minecraft:display_name": {
        "value": "item.custom:ruby_sword.name"
      },
      "minecraft:max_stack_size": 1,
      "minecraft:hand_equipped": true,
      "minecraft:durability": {
        "max_durability": 1561
      },
      "minecraft:damage": 7,
      "minecraft:enchantable": {
        "value": 14,
        "slot": "sword"
      },
      "minecraft:can_destroy_in_creative": false
    }
  }
}"""
            ),
            BedrockTemplate(
                id = "bp_block",
                title = "Custom Block Definition",
                category = TemplateCategory.BEHAVIOR_PACK,
                targetVersion = "Bedrock 1.26.40.5",
                description = "Custom block with destructible mining, friction, light emission, and geometry",
                defaultFileName = "custom_block.json",
                content = """{
  "format_version": "1.26.40",
  "minecraft:block": {
    "description": {
      "identifier": "custom:ruby_ore",
      "menu_category": {
        "category": "nature",
        "group": "itemGroup.name.ore"
      }
    },
    "components": {
      "minecraft:destructible_by_mining": {
        "seconds_to_destroy": 3.0
      },
      "minecraft:destructible_by_explosion": {
        "explosion_resistance": 3.0
      },
      "minecraft:friction": 0.6,
      "minecraft:light_emission": 3,
      "minecraft:material_instances": {
        "*": {
          "texture": "ruby_ore",
          "render_method": "opaque"
        }
      }
    }
  }
}"""
            ),
            BedrockTemplate(
                id = "bp_recipe_shaped",
                title = "Shaped Crafting Recipe",
                category = TemplateCategory.BEHAVIOR_PACK,
                targetVersion = "Bedrock 1.26.40.5",
                description = "3x3 shaped crafting table recipe with pattern and key items",
                defaultFileName = "ruby_sword_recipe.json",
                content = """{
  "format_version": "1.26.40",
  "minecraft:recipe_shaped": {
    "description": {
      "identifier": "custom:ruby_sword_recipe"
    },
    "tags": ["crafting_table"],
    "pattern": [
      " X ",
      " X ",
      " S "
    ],
    "key": {
      "X": {
        "item": "custom:ruby"
      },
      "S": {
        "item": "minecraft:stick"
      }
    },
    "result": {
      "item": "custom:ruby_sword",
      "count": 1
    }
  }
}"""
            ),
            BedrockTemplate(
                id = "bp_loot_table",
                title = "Loot Table",
                category = TemplateCategory.BEHAVIOR_PACK,
                targetVersion = "Bedrock 1.26.40.5",
                description = "Entity/Block drop loot table with rolls, entries, and count functions",
                defaultFileName = "custom_drops.json",
                content = """{
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "type": "item",
          "name": "minecraft:diamond",
          "weight": 1,
          "functions": [
            {
              "function": "set_count",
              "count": {
                "min": 1,
                "max": 3
              }
            }
          ]
        }
      ]
    }
  ]
}"""
            ),
            BedrockTemplate(
                id = "bp_spawn_rule",
                title = "Entity Spawn Rules",
                category = TemplateCategory.BEHAVIOR_PACK,
                targetVersion = "Bedrock 1.26.40.5",
                description = "Conditions and biomes for natural mob spawning",
                defaultFileName = "custom_spawn_rule.json",
                content = """{
  "format_version": "1.26.40",
  "minecraft:spawn_rules": {
    "description": {
      "identifier": "custom:my_entity",
      "population_control": "monster"
    },
    "conditions": [
      {
        "minecraft:spawns_on_surface": {},
        "minecraft:brightness_filter": {
          "min": 0,
          "max": 7,
          "adjust_for_weather": true
        },
        "minecraft:biome_filter": {
          "test": "has_biome_tag",
          "operator": "==",
          "value": "overworld"
        }
      }
    ]
  }
}"""
            ),
            BedrockTemplate(
                id = "bp_trade_table",
                title = "Villager Trading Table",
                category = TemplateCategory.BEHAVIOR_PACK,
                targetVersion = "Bedrock 1.26.40.5",
                description = "Economy trades definition with tiers, wants, and gives",
                defaultFileName = "custom_trades.json",
                content = """{
  "tiers": [
    {
      "total_exp_required": 0,
      "trades": [
        {
          "wants": [
            {
              "item": "minecraft:emerald",
              "quantity": 2
            }
          ],
          "gives": [
            {
              "item": "custom:ruby",
              "quantity": 1
            }
          ],
          "max_uses": 16,
          "trader_exp": 1
        }
      ]
    }
  ]
}"""
            ),

            // === RESOURCE PACK ===
            BedrockTemplate(
                id = "rp_manifest",
                title = "Resource Pack Manifest",
                category = TemplateCategory.RESOURCE_PACK,
                targetVersion = "Bedrock 1.26.40.5",
                description = "Resource pack manifest with resources module type and min_engine_version",
                defaultFileName = "manifest.json",
                content = """{
  "format_version": 2,
  "header": {
    "name": "Custom Resource Pack",
    "description": "Textures and client models for Bedrock 1.26.40.5",
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
                id = "rp_client_entity",
                title = "Client Entity Definition",
                category = TemplateCategory.RESOURCE_PACK,
                targetVersion = "Bedrock 1.26.40.5",
                description = "Client-side entity definition referencing materials, textures, geometry, and render controllers",
                defaultFileName = "custom_entity.client.json",
                content = """{
  "format_version": "1.26.40",
  "minecraft:client_entity": {
    "description": {
      "identifier": "custom:my_entity",
      "materials": {
        "default": "entity_alphatest"
      },
      "textures": {
        "default": "textures/entity/custom_entity"
      },
      "geometry": {
        "default": "geometry.custom_entity"
      },
      "render_controllers": [
        "controller.render.default"
      ],
      "spawn_egg": {
        "base_color": "#FFD13B",
        "overlay_color": "#181B20"
      }
    }
  }
}"""
            ),
            BedrockTemplate(
                id = "rp_render_controller",
                title = "Render Controller",
                category = TemplateCategory.RESOURCE_PACK,
                targetVersion = "Bedrock 1.26.40.5",
                description = "Render controller mapping client entity geometry and material instances",
                defaultFileName = "controller.render.custom.json",
                content = """{
  "format_version": "1.26.40",
  "render_controllers": {
    "controller.render.custom": {
      "geometry": "Geometry.default",
      "materials": [
        { "*": "Material.default" }
      ],
      "textures": [
        "Texture.default"
      ]
    }
  }
}"""
            ),
            BedrockTemplate(
                id = "rp_attachable",
                title = "Attachable Definition",
                category = TemplateCategory.RESOURCE_PACK,
                targetVersion = "Bedrock 1.26.40.5",
                description = "Attachable weapon or armor client definition for custom items",
                defaultFileName = "ruby_sword.attachable.json",
                content = """{
  "format_version": "1.26.40",
  "minecraft:attachable": {
    "description": {
      "identifier": "custom:ruby_sword",
      "materials": {
        "default": "entity_alphatest"
      },
      "textures": {
        "default": "textures/items/ruby_sword"
      },
      "geometry": {
        "default": "geometry.ruby_sword"
      },
      "render_controllers": [
        "controller.render.item_default"
      ]
    }
  }
}"""
            ),
            BedrockTemplate(
                id = "rp_particle",
                title = "Particle Emitter",
                category = TemplateCategory.RESOURCE_PACK,
                targetVersion = "Bedrock 1.26.40.5",
                description = "Bedrock particle emitter with curve components and billboard rendering",
                defaultFileName = "custom_particle.json",
                content = """{
  "format_version": "1.26.40",
  "particle_effect": {
    "description": {
      "identifier": "custom:sparkle_particle",
      "basic_render_parameters": {
        "material": "particles_alpha",
        "texture": "textures/particle/particles"
      }
    },
    "components": {
      "minecraft:emitter_rate_instant": {
        "num_particles": 15
      },
      "minecraft:emitter_lifetime_once": {
        "active_time": 1.0
      },
      "minecraft:emitter_shape_sphere": {
        "radius": 0.5,
        "direction": "outwards"
      },
      "minecraft:particle_lifetime_expression": {
        "max_lifetime": 0.8
      },
      "minecraft:particle_appearance_billboard": {
        "size": [0.1, 0.1],
        "facing_camera_mode": "lookat_xyz"
      }
    }
  }
}"""
            ),
            BedrockTemplate(
                id = "rp_sound_definitions",
                title = "Sound Definitions",
                category = TemplateCategory.RESOURCE_PACK,
                targetVersion = "Bedrock 1.26.40.5",
                description = "Sound definitions assigning audio files to event identifiers",
                defaultFileName = "sound_definitions.json",
                content = """{
  "format_version": "1.26.40",
  "sound_definitions": {
    "custom.ruby_sword.swing": {
      "category": "player",
      "sounds": [
        "sounds/custom/sword_swing1",
        "sounds/custom/sword_swing2"
      ]
    }
  }
}"""
            ),
            BedrockTemplate(
                id = "rp_item_texture",
                title = "Item Texture Definition",
                category = TemplateCategory.RESOURCE_PACK,
                targetVersion = "Bedrock 1.26.40.5",
                description = "item_texture.json mapping item texture shortnames to asset file paths",
                defaultFileName = "item_texture.json",
                content = """{
  "resource_pack_name": "custom_rp",
  "texture_name": "atlas.items",
  "texture_data": {
    "ruby_sword": {
      "textures": "textures/items/ruby_sword"
    },
    "ruby": {
      "textures": "textures/items/ruby"
    }
  }
}"""
            ),

            // === WORLD GENERATION ===
            BedrockTemplate(
                id = "wg_ore_feature",
                title = "Ore Feature Definition",
                category = TemplateCategory.WORLD_GEN,
                targetVersion = "Bedrock 1.26.40.5",
                description = "World generation ore feature placing replacement blocks in stone",
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
                id = "wg_feature_rule",
                title = "Feature Rule",
                category = TemplateCategory.WORLD_GEN,
                targetVersion = "Bedrock 1.26.40.5",
                description = "Feature placement rule controlling distribution, iterations, and biomes",
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

            // === GENERIC & OTHER ===
            BedrockTemplate(
                id = "generic_object",
                title = "Generic JSON Object",
                category = TemplateCategory.OTHER,
                targetVersion = "Generic JSON",
                description = "Clean empty standard JSON object for any JSON editing purpose",
                defaultFileName = "untitled.json",
                content = """{
  "name": "example",
  "enabled": true,
  "version": 1,
  "items": []
}"""
            ),
            BedrockTemplate(
                id = "generic_array",
                title = "Generic JSON Array",
                category = TemplateCategory.OTHER,
                targetVersion = "Generic JSON",
                description = "Clean empty standard JSON array for list-based data",
                defaultFileName = "list.json",
                content = """[
  {
    "id": 1,
    "name": "Item One"
  },
  {
    "id": 2,
    "name": "Item Two"
  }
]"""
            ),
            BedrockTemplate(
                id = "other_skin_pack_manifest",
                title = "Skin Pack Manifest",
                category = TemplateCategory.OTHER,
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
            )
        )
    }
}
