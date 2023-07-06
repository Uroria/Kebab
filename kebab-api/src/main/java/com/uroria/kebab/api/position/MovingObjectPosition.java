package com.uroria.kebab.api.position;

import com.uroria.kebab.api.entity.Entity;
import com.uroria.kebab.common.ValidUtils;
import lombok.Getter;

public abstract class MovingObjectPosition {
    protected @Getter final Vector location;

    protected MovingObjectPosition(Vector vector) {
        ValidUtils.notNull(vector, "Vector cannot be null");
        this.location = vector;
    }

    public double distanceTo(Entity entity) {
        ValidUtils.notNull(entity, "Entity cannot be null");
        double d0 = this.location.getX() - entity.getLocation().getX();
        double d1 = this.location.getY() - entity.getLocation().getY();
        double d2 = this.location.getZ() - entity.getLocation().getZ();
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public abstract MovingObjectPosition.MovingObjectType getType();

    public enum MovingObjectType {
        MISS,
        BLOCK,
        ENTITY
    }
}
