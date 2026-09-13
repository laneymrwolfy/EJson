package com.example.model

object BehaviorTemplates {

    val list: List<BedrockTemplate> = listOf(
        // === ENTITIES ===
        BedrockTemplate(
            id = "bp_entity",
            title = "Custom Entity (Mob)",
            category = TemplateCategory.ENTITIES,
            packType = "Behavior Pack",
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
            id = "bp_entity_boss",
            title = "Boss Entity Definition",
            category = TemplateCategory.ENTITIES,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Boss monster with boss bar, damage sensor, knockback resistance, and ranged attack",
            defaultFileName = "custom_boss.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:entity": {
    "description": {
      "identifier": "custom:titan_boss",
      "is_spawnable": true,
      "is_summonable": true
    },
    "components": {
      "minecraft:type_family": {
        "family": ["custom", "monster", "boss"]
      },
      "minecraft:boss": {
        "name": "Titan of the Depths",
        "hud_range": 64.0,
        "should_darken_sky": true
      },
      "minecraft:health": {
        "value": 300,
        "max": 300
      },
      "minecraft:knockback_resistance": {
        "value": 1.0
      },
      "minecraft:damage_sensor": {
        "triggers": [
          {
            "cause": "fall",
            "deals_damage": false
          }
        ]
      },
      "minecraft:behavior.ranged_attack": {
        "priority": 1,
        "attack_interval_min": 2.0,
        "attack_interval_max": 4.0,
        "attack_radius": 24.0
      },
      "minecraft:physics": {}
    }
  }
}"""
        ),

        // === ITEMS ===
        BedrockTemplate(
            id = "bp_item_weapon",
            title = "Custom Weapon Item",
            category = TemplateCategory.ITEMS,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "1.26.40 component item with durability, damage, hand equipped, and enchantable",
            defaultFileName = "ruby_sword.json",
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
            id = "bp_item_food",
            title = "Custom Consumable Food Item",
            category = TemplateCategory.ITEMS,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Food item with nutrition, saturation, eating sound, and status effects",
            defaultFileName = "golden_berry.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:item": {
    "description": {
      "identifier": "custom:golden_berry",
      "menu_category": {
        "category": "nature",
        "group": "itemGroup.name.miscFood"
      }
    },
    "components": {
      "minecraft:icon": {
        "textures": {
          "default": "golden_berry"
        }
      },
      "minecraft:display_name": {
        "value": "Golden Berry"
      },
      "minecraft:max_stack_size": 64,
      "minecraft:use_modifiers": {
        "use_duration": 1.6,
        "movement_modifier": 0.35
      },
      "minecraft:food": {
        "nutrition": 6,
        "saturation_modifier": "supernatural",
        "can_always_eat": true
      }
    }
  }
}"""
        ),
        BedrockTemplate(
            id = "bp_item_tool",
            title = "Custom Mining Tool (Pickaxe)",
            category = TemplateCategory.ITEMS,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Custom pickaxe with digger component, mining speed multipliers, and durability",
            defaultFileName = "ruby_pickaxe.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:item": {
    "description": {
      "identifier": "custom:ruby_pickaxe",
      "menu_category": {
        "category": "equipment",
        "group": "itemGroup.name.pickaxe"
      }
    },
    "components": {
      "minecraft:icon": {
        "textures": {
          "default": "ruby_pickaxe"
        }
      },
      "minecraft:max_stack_size": 1,
      "minecraft:hand_equipped": true,
      "minecraft:durability": {
        "max_durability": 2031
      },
      "minecraft:digger": {
        "use_efficiency": true,
        "destroy_speeds": [
          {
            "block": {
              "tags": "query.any_tag('stone', 'metal', 'rock')"
            },
            "speed": 9
          }
        ]
      },
      "minecraft:enchantable": {
        "value": 18,
        "slot": "pickaxe"
      }
    }
  }
}"""
        ),

        // === BLOCKS ===
        BedrockTemplate(
            id = "bp_block_ore",
            title = "Custom Block (Ore)",
            category = TemplateCategory.BLOCKS,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Custom block with destructible mining, friction, light emission, and material instances",
            defaultFileName = "ruby_ore.json",
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
            id = "bp_block_permutations",
            title = "Block with States & Permutations",
            category = TemplateCategory.BLOCKS,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Block with cardinal direction states, permutations, and collision box",
            defaultFileName = "carved_lantern.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:block": {
    "description": {
      "identifier": "custom:carved_lantern",
      "states": {
        "custom:lit": [false, true]
      }
    },
    "permutations": [
      {
        "condition": "q.block_state('custom:lit') == true",
        "components": {
          "minecraft:light_emission": 15
        }
      }
    ],
    "components": {
      "minecraft:destructible_by_mining": {
        "seconds_to_destroy": 1.5
      },
      "minecraft:selection_box": {
        "origin": [-7, 0, -7],
        "size": [14, 14, 14]
      },
      "minecraft:material_instances": {
        "*": {
          "texture": "carved_lantern",
          "render_method": "alpha_test"
        }
      }
    }
  }
}"""
        ),

        // === RECIPES ===
        BedrockTemplate(
            id = "bp_recipe_shaped",
            title = "Shaped Crafting Recipe",
            category = TemplateCategory.RECIPES,
            packType = "Behavior Pack",
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
            id = "bp_recipe_shapeless",
            title = "Shapeless Crafting Recipe",
            category = TemplateCategory.RECIPES,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Shapeless crafting table recipe combining ingredients anywhere on the grid",
            defaultFileName = "ruby_block_recipe.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:recipe_shapeless": {
    "description": {
      "identifier": "custom:ruby_from_block"
    },
    "tags": ["crafting_table"],
    "ingredients": [
      {
        "item": "custom:ruby_block",
        "count": 1
      }
    ],
    "result": {
      "item": "custom:ruby",
      "count": 9
    }
  }
}"""
        ),
        BedrockTemplate(
            id = "bp_recipe_furnace",
            title = "Furnace Smelting Recipe",
            category = TemplateCategory.RECIPES,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Standard furnace smelting recipe taking input item to output result",
            defaultFileName = "smelt_ruby_ore.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:recipe_furnace": {
    "description": {
      "identifier": "custom:smelt_ruby_ore"
    },
    "tags": ["furnace"],
    "input": "custom:ruby_ore",
    "output": "custom:ruby"
  }
}"""
        ),
        BedrockTemplate(
            id = "bp_recipe_blast_furnace",
            title = "Blast Furnace Recipe",
            category = TemplateCategory.RECIPES,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "High speed blast furnace smelting recipe for ores and metals",
            defaultFileName = "blast_ruby_ore.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:recipe_furnace": {
    "description": {
      "identifier": "custom:blast_ruby_ore"
    },
    "tags": ["blast_furnace"],
    "input": "custom:ruby_ore",
    "output": "custom:ruby"
  }
}"""
        ),
        BedrockTemplate(
            id = "bp_recipe_brewing",
            title = "Brewing Stand Recipe",
            category = TemplateCategory.RECIPES,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Brewing recipe combining reagent and input potion to yield new output potion",
            defaultFileName = "brew_haste_potion.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:recipe_brewing_mix": {
    "description": {
      "identifier": "custom:brew_haste_potion"
    },
    "tags": ["brewing_stand"],
    "input": "minecraft:potion_type:awkward",
    "reagent": "custom:ruby",
    "output": "minecraft:potion_type:swiftness"
  }
}"""
        ),
        BedrockTemplate(
            id = "bp_recipe_smithing",
            title = "Smithing Transform Recipe",
            category = TemplateCategory.RECIPES,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Smithing table upgrade recipe with template, base equipment, and addition ingot",
            defaultFileName = "upgrade_ruby_sword.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:recipe_smithing_transform": {
    "description": {
      "identifier": "custom:upgrade_ruby_sword"
    },
    "tags": ["smithing_table"],
    "template": "minecraft:netherite_upgrade_smithing_template",
    "base": "minecraft:diamond_sword",
    "addition": "custom:ruby",
    "result": "custom:ruby_sword"
  }
}"""
        ),
        BedrockTemplate(
            id = "bp_recipe_stonecutter",
            title = "Stonecutter Recipe",
            category = TemplateCategory.RECIPES,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Stonecutter 1-to-many cutting recipe for custom decorative blocks",
            defaultFileName = "cut_ruby_block.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:recipe_shapeless": {
    "description": {
      "identifier": "custom:cut_ruby_block"
    },
    "tags": ["stonecutter"],
    "ingredients": [
      {
        "item": "custom:ruby_block"
      }
    ],
    "result": {
      "item": "custom:ruby_tiles",
      "count": 4
    }
  }
}"""
        ),

        // === LOOT & TRADING ===
        BedrockTemplate(
            id = "bp_loot_table",
            title = "Entity Drop Loot Table",
            category = TemplateCategory.LOOT_TRADING,
            packType = "Behavior Pack",
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
            id = "bp_loot_chest",
            title = "Dungeon Chest Loot Table",
            category = TemplateCategory.LOOT_TRADING,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Chests and structure containers loot table with multiple weighted pools",
            defaultFileName = "dungeon_chest_loot.json",
            content = """{
  "pools": [
    {
      "rolls": {
        "min": 2,
        "max": 5
      },
      "entries": [
        {
          "type": "item",
          "name": "minecraft:iron_ingot",
          "weight": 20,
          "functions": [
            {
              "function": "set_count",
              "count": { "min": 1, "max": 4 }
            }
          ]
        },
        {
          "type": "item",
          "name": "custom:ruby",
          "weight": 5,
          "functions": [
            {
              "function": "set_count",
              "count": { "min": 1, "max": 2 }
            }
          ]
        }
      ]
    }
  ]
}"""
        ),
        BedrockTemplate(
            id = "bp_trade_table",
            title = "Villager Trading Table",
            category = TemplateCategory.LOOT_TRADING,
            packType = "Behavior Pack",
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

        // === SPAWN RULES ===
        BedrockTemplate(
            id = "bp_spawn_rule",
            title = "Entity Spawn Rules",
            category = TemplateCategory.BEHAVIOR_PACK,
            packType = "Behavior Pack",
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

        // === ANIMATION CONTROLLER (BEHAVIOR) ===
        BedrockTemplate(
            id = "bp_anim_controller",
            title = "Behavior Animation Controller",
            category = TemplateCategory.ANIMATION_RENDERING,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Server-side state machine triggering events and running slash commands on transitions",
            defaultFileName = "controller.animation.custom.json",
            content = """{
  "format_version": "1.26.40",
  "animation_controllers": {
    "controller.animation.custom.state_machine": {
      "initial_state": "default",
      "states": {
        "default": {
          "transitions": [
            {
              "powered": "query.is_powered"
            }
          ]
        },
        "powered": {
          "on_entry": [
            "/particle minecraft:huge_explosion_emitter ~ ~ ~"
          ],
          "transitions": [
            {
              "default": "!query.is_powered"
            }
          ]
        }
      }
    }
  }
}"""
        ),

        // === CAMERA DEFINITION ===
        BedrockTemplate(
            id = "bp_camera",
            title = "Camera Preset Definition",
            category = TemplateCategory.BEHAVIOR_PACK,
            packType = "Behavior Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Custom camera perspective preset controlling offset, rotation, and player listeners",
            defaultFileName = "custom_camera.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:camera": {
    "description": {
      "identifier": "custom:isometric_camera"
    },
    "extend": "minecraft:free",
    "pos": [0, 10, -10],
    "rot": [45, 0]
  }
}"""
        )
    )
}
