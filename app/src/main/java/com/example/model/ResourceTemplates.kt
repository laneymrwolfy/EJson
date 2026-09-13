package com.example.model

object ResourceTemplates {

    val list: List<BedrockTemplate> = listOf(
        // === CLIENT ENTITY ===
        BedrockTemplate(
            id = "rp_client_entity",
            title = "Client Entity Definition",
            category = TemplateCategory.RESOURCE_PACK,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Client-side entity linking materials, textures, geometries, animations, and render controllers",
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
      "animations": {
        "walk": "animation.custom_entity.walk",
        "idle": "animation.custom_entity.idle"
      },
      "animation_controllers": [
        { "walk_controller": "controller.animation.custom_entity.movement" }
      ],
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

        // === RENDER CONTROLLER ===
        BedrockTemplate(
            id = "rp_render_controller",
            title = "Render Controller",
            category = TemplateCategory.ANIMATION_RENDERING,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Maps client entity geometry, materials, textures, and bone part visibility dynamically",
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
      ],
      "part_visibility": [
        { "horns": "query.is_powered" }
      ]
    }
  }
}"""
        ),

        // === CLIENT ANIMATION ===
        BedrockTemplate(
            id = "rp_animation",
            title = "Client Model Animation",
            category = TemplateCategory.ANIMATION_RENDERING,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Keyframe skeletal animation for custom entity bones (rotation, position, scale)",
            defaultFileName = "custom_entity.animation.json",
            content = """{
  "format_version": "1.26.40",
  "animations": {
    "animation.custom_entity.walk": {
      "loop": true,
      "animation_length": 1.0,
      "bones": {
        "left_leg": {
          "rotation": {
            "0.0": [0, 0, 0],
            "0.25": [30, 0, 0],
            "0.5": [0, 0, 0],
            "0.75": [-30, 0, 0],
            "1.0": [0, 0, 0]
          }
        },
        "right_leg": {
          "rotation": {
            "0.0": [0, 0, 0],
            "0.25": [-30, 0, 0],
            "0.5": [0, 0, 0],
            "0.75": [30, 0, 0],
            "1.0": [0, 0, 0]
          }
        }
      }
    }
  }
}"""
        ),

        // === CLIENT ANIMATION CONTROLLER ===
        BedrockTemplate(
            id = "rp_client_anim_controller",
            title = "Client Animation Controller",
            category = TemplateCategory.ANIMATION_RENDERING,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "State machine for playing, blending, and transitioning client model animations",
            defaultFileName = "controller.animation.custom_entity.json",
            content = """{
  "format_version": "1.26.40",
  "animation_controllers": {
    "controller.animation.custom_entity.movement": {
      "initial_state": "idle",
      "states": {
        "idle": {
          "animations": ["idle"],
          "transitions": [
            { "walk": "query.modified_move_speed > 0.1" }
          ]
        },
        "walk": {
          "animations": ["walk"],
          "blend_transition": 0.2,
          "transitions": [
            { "idle": "query.modified_move_speed <= 0.1" }
          ]
        }
      }
    }
  }
}"""
        ),

        // === ATTACHABLE ===
        BedrockTemplate(
            id = "rp_attachable",
            title = "Attachable Item Definition",
            category = TemplateCategory.RESOURCE_PACK,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Client visual representation of hand-held items, 3D armor, and tools",
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

        // === PARTICLE EMITTER ===
        BedrockTemplate(
            id = "rp_particle",
            title = "Particle Emitter Definition",
            category = TemplateCategory.PARTICLES_SOUNDS,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Particle effect with lifetime expressions, billboard rendering, and curves",
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

        // === SOUND DEFINITIONS ===
        BedrockTemplate(
            id = "rp_sound_definitions",
            title = "Sound Definitions",
            category = TemplateCategory.PARTICLES_SOUNDS,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "sound_definitions.json assigning audio file paths and sound categories to event IDs",
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
    },
    "custom.titan.ambient": {
      "category": "hostile",
      "sounds": [
        {
          "name": "sounds/custom/titan_growl",
          "volume": 1.2,
          "pitch": 0.8
        }
      ]
    }
  }
}"""
        ),

        // === TEXTURE DICTIONARIES ===
        BedrockTemplate(
            id = "rp_item_texture",
            title = "Item Texture Dictionary",
            category = TemplateCategory.UI_TEXTURES,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "item_texture.json mapping item short names to texture file paths",
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
        BedrockTemplate(
            id = "rp_terrain_texture",
            title = "Terrain Texture Dictionary",
            category = TemplateCategory.UI_TEXTURES,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "terrain_texture.json mapping block short names to block texture file paths",
            defaultFileName = "terrain_texture.json",
            content = """{
  "resource_pack_name": "custom_rp",
  "texture_name": "atlas.terrain",
  "padding": 8,
  "num_mip_levels": 4,
  "texture_data": {
    "ruby_ore": {
      "textures": "textures/blocks/ruby_ore"
    },
    "ruby_block": {
      "textures": "textures/blocks/ruby_block"
    }
  }
}"""
        ),
        BedrockTemplate(
            id = "rp_flipbook_textures",
            title = "Flipbook Textures Animation",
            category = TemplateCategory.UI_TEXTURES,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "flipbook_textures.json controlling frame rates and sequences for animated block/item textures",
            defaultFileName = "flipbook_textures.json",
            content = """[
  {
    "flipbook_texture": "textures/blocks/ruby_ore_animated",
    "atlas_tile": "ruby_ore",
    "ticks_per_frame": 4,
    "frames": [0, 1, 2, 3, 2, 1]
  }
]"""
        ),

        // === UI JSON ===
        BedrockTemplate(
            id = "rp_ui_defs",
            title = "UI Definitions (_ui_defs.json)",
            category = TemplateCategory.UI_TEXTURES,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Root list of custom UI screen JSON files loaded by the Bedrock client UI engine",
            defaultFileName = "_ui_defs.json",
            content = """{
  "ui_defs": [
    "ui/custom_hud.json"
  ]
}"""
        ),
        BedrockTemplate(
            id = "rp_custom_ui",
            title = "Custom UI Screen JSON",
            category = TemplateCategory.UI_TEXTURES,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Bedrock JSON UI panel with controls, textures, label text, and layout anchors",
            defaultFileName = "custom_hud.json",
            content = """{
  "namespace": "custom_ui",
  "hud_overlay": {
    "type": "panel",
    "size": ["100%", "100%"],
    "controls": [
      {
        "status_label": {
          "type": "label",
          "text": "EJson Active",
          "color": [1.0, 0.82, 0.23],
          "anchor_from": "top_left",
          "anchor_to": "top_left",
          "offset": [10, 10]
        }
      }
    ]
  }
}"""
        ),

        // === FOG DEFINITION ===
        BedrockTemplate(
            id = "rp_fog",
            title = "Fog Definition",
            category = TemplateCategory.RESOURCE_PACK,
            packType = "Resource Pack",
            targetVersion = "Bedrock 1.26.40.5",
            description = "Volumetric fog parameters, density, and colors for biomes and custom dimensions",
            defaultFileName = "custom_fog.json",
            content = """{
  "format_version": "1.26.40",
  "minecraft:fog": {
    "description": {
      "identifier": "custom:crystal_cave_fog"
    },
    "distance": {
      "air": {
        "fog_start": 8.0,
        "fog_end": 48.0,
        "fog_color": "#2A1845",
        "render_distance_type": "render"
      }
    }
  }
}"""
        )
    )
}
