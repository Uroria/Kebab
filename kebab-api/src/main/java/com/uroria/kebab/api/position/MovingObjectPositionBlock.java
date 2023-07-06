package com.uroria.kebab.api.position;

import com.uroria.kebab.api.block.BlockFace;
import com.uroria.kebab.api.block.BlockPosition;
import com.uroria.kebab.common.ValidUtils;
import lombok.Getter;

public class MovingObjectPositionBlock extends MovingObjectPosition {
    private @Getter final BlockFace direction;
    private @Getter final BlockPosition blockPosition;
    private final boolean miss, inside;

    public static MovingObjectPosition miss(Vector vector, BlockFace direction, BlockPosition position) {
        ValidUtils.notNull(vector, "Vector cannot be null");
        ValidUtils.notNull(direction, "Direction cannot be null");
        ValidUtils.notNull(position, "BlockPosition cannot be null");
        return new MovingObjectPositionBlock(true, vector, direction, position, false);
    }

    public MovingObjectPositionBlock(Vector vector, BlockFace direction, BlockPosition blockPosition, boolean flag) {
        this(false, vector, direction, blockPosition, flag);
    }

    private MovingObjectPositionBlock(boolean flag, Vector vector, BlockFace direction, BlockPosition blockPosition, boolean flag1) {
        super(vector);
        this.miss = flag;
        this.direction = direction;
        this.blockPosition = blockPosition;
        this.inside = flag1;
    }

    public MovingObjectPositionBlock withDirection(BlockFace direction) {
        ValidUtils.notNull(direction, "BlockFace cannot be null");
        return new MovingObjectPositionBlock(this.miss, this.location, direction, this.blockPosition, this.inside);
    }

    public MovingObjectPositionBlock withPosition(BlockPosition position) {
        ValidUtils.notNull(position, "BlockPosition cannot be null");
        return new MovingObjectPositionBlock(this.miss, this.location, this.direction, position, this.inside);
    }

    @Override
    public MovingObjectType getType() {
        return this.miss ? MovingObjectType.MISS : MovingObjectType.BLOCK;
    }

    public boolean isInside() {
        return this.inside;
    }
}
