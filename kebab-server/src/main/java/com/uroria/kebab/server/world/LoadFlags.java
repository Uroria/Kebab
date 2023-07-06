package com.uroria.kebab.server.world;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LoadFlags {
    public final long BIOMES               = 0x00001;
    public final long HEIGHTMAPS           = 0x00002;
    public final long CARVING_MASKS        = 0x00004;
    public final long ENTITIES             = 0x00008;
    public final long TILE_ENTITIES        = 0x00010;
    public final long TILE_TICKS           = 0x00040;
    public final long LIQUID_TICKS         = 0x00080;
    public final long TO_BE_TICKED         = 0x00100;
    public final long POST_PROCESSING      = 0x00200;
    public final long STRUCTURES           = 0x00400;
    public final long BLOCK_LIGHTS         = 0x00800;
    public final long BLOCK_STATES         = 0x01000;
    public final long SKY_LIGHT            = 0x02000;
    public final long LIGHTS               = 0x04000;
    public final long LIQUIDS_TO_BE_TICKED = 0x08000;
    public final long RAW                  = 0x10000;

    public final long ALL_DATA             = 0xffffffffffffffffL;
}
