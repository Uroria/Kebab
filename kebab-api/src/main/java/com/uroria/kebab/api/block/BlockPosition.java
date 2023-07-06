package com.uroria.kebab.api.block;

import com.uroria.kebab.api.position.Location;
import lombok.Getter;

public final class BlockPosition {
    private @Getter final int x, y, z;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static BlockPosition from(Location location) {
        if (location == null) throw new NullPointerException("Location cannot be null");
        return new BlockPosition((int) Math.floor(location.getX()), (int) Math.floor(location.getY()), (int) Math.floor(location.getZ()));
    }
}
