package com.uroria.kebab.api.position;

import com.google.common.primitives.Doubles;
import com.uroria.kebab.api.world.World;
import com.uroria.kebab.common.NumberConversions;
import com.uroria.kebab.common.ValidUtils;
import lombok.Getter;

import java.util.Random;

public class Vector implements Cloneable {
    private static final Random RANDOM = new Random();
    private static final double EPSILON = 0.000001;

    protected @Getter double x, y, z;

    public Vector() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector add(Vector vec) {
        if (vec == null) throw new NullPointerException("Vector cannot be null");
        x += vec.x;
        y += vec.y;
        z += vec.z;
        return this;
    }

    public Vector subtract(Vector vec) {
        if (vec == null) throw new NullPointerException("Vector cannot be null");
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
        return this;
    }

    public Vector multiply(Vector vec) {
        if (vec == null) throw new NullPointerException("Vector cannot be null");
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;
        return this;
    }

    public Vector divide(Vector vec) {
        if (vec == null) throw new NullPointerException("Vector cannot be null");
        x /= vec.x;
        y /= vec.y;
        z /= vec.z;
        return this;
    }

    public Vector copy(Vector vec) {
        if (vec == null) throw new NullPointerException("Vector cannot be null");
        x = vec.x;
        y = vec.y;
        z = vec.z;
        return this;
    }

    public double length() {
        return Math.sqrt(NumberConversions.square(x) + NumberConversions.square(y) + NumberConversions.square(z));
    }

    public double lengthSquared() {
        return NumberConversions.square(x) + NumberConversions.square(y) + NumberConversions.square(z);
    }

    public double distance(Vector o) {
        if (o == null) throw new NullPointerException("Vector cannot be null");
        return Math.sqrt(NumberConversions.square(x - o.x) + NumberConversions.square(y - o.y) + NumberConversions.square(z - o.z));
    }

    public double distanceSquared(Vector o) {
        if (o == null) throw new NullPointerException("Vector cannot be null");
        return NumberConversions.square(x - o.x) + NumberConversions.square(y - o.y) + NumberConversions.square(z - o.z);
    }

    public float angle(Vector other) {
        if (other == null) throw new NullPointerException("Vector cannot be null");
        double dot = Doubles.constrainToRange(dot(other) / (length() * other.length()), -1.0, 1.0);

        return (float) Math.acos(dot);
    }

    public Vector midpoint(Vector other) {
        if (other == null) throw new NullPointerException("Vector cannot be null");
        x = (x + other.x) / 2;
        y = (y + other.y) / 2;
        z = (z + other.z) / 2;
        return this;
    }

    public Vector getMidpoint(Vector other) {
        if (other == null) throw new NullPointerException("Vector cannot be null");
        double x = (this.x + other.x) / 2;
        double y = (this.y + other.y) / 2;
        double z = (this.z + other.z) / 2;
        return new Vector(x, y, z);
    }

    public Vector multiply(int m) {
        x *= m;
        y *= m;
        z *= m;
        return this;
    }

    public Vector multiply(double m) {
        x *= m;
        y *= m;
        z *= m;
        return this;
    }

    public Vector multiply(float m) {
        x *= m;
        y *= m;
        z *= m;
        return this;
    }

    public double dot(Vector other) {
        if (other == null) throw new NullPointerException("Vector cannot be null");
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector crossProduct(Vector o) {
        if (o == null) throw new NullPointerException("Vector cannot be null");
        double newX = y * o.z - o.y * z;
        double newY = z * o.x - o.z * x;
        double newZ = x * o.y - o.x * y;

        x = newX;
        y = newY;
        z = newZ;
        return this;
    }

    public Vector getCrossProduct(Vector o) {
        if (o == null) throw new NullPointerException("Vector cannot be null");
        double x = this.y * o.z - o.y * this.z;
        double y = this.z * o.x - o.z * this.x;
        double z = this.x * o.y - o.x * this.y;
        return new Vector(x, y, z);
    }

    public Vector normalize() {
        double length = length();

        x /= length;
        y /= length;
        z /= length;

        return this;
    }

    public Vector zero() {
        x = 0;
        y = 0;
        z = 0;
        return this;
    }

    Vector normalizeZeros() {
        if (x == -0.0D) x = 0.0D;
        if (y == -0.0D) y = 0.0D;
        if (z == -0.0D) z = 0.0D;
        return this;
    }

    public boolean isInAABB(Vector min, Vector max) {
        if (min == null) throw new NullPointerException("Vector min cannot be null");
        if (max == null) throw new NullPointerException("Vector max cannot be null");
        return x >= min.x && x <= max.x && y >= min.y && y <= max.y && z >= min.z && z <= max.z;
    }

    public boolean isInSphere(Vector origin, double radius) {
        if (origin == null) throw new NullPointerException("Vector cannot be null");
        return (NumberConversions.square(origin.x - x) + NumberConversions.square(origin.y - y) + NumberConversions.square(origin.z - z)) <= NumberConversions.square(radius);
    }

    public boolean isNormalized() {
        return Math.abs(this.lengthSquared() - 1) < getEpsilon();
    }

    public Vector rotateAroundX(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double y = angleCos * getY() - angleSin * getZ();
        double z = angleSin * getY() + angleCos * getZ();
        return setY(y).setZ(z);
    }

    public Vector rotateAroundY(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double x = angleCos * getX() + angleSin * getZ();
        double z = -angleSin * getX() + angleCos * getZ();
        return setX(x).setZ(z);
    }

    public Vector rotateAroundZ(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double x = angleCos * getX() - angleSin * getY();
        double y = angleSin * getX() + angleCos * getY();
        return setX(x).setY(y);
    }

    public Vector rotateAroundAxis(Vector axis, double angle) throws IllegalArgumentException {
        ValidUtils.notNull(axis, "Axis vector cannot be null");

        return rotateAroundNonUnitAxis(axis.isNormalized() ? axis : axis.clone().normalize(), angle);
    }

    public Vector rotateAroundNonUnitAxis(Vector axis, double angle) throws IllegalArgumentException {
        ValidUtils.notNull(axis, "Axis vector cannot be null");

        double x = getX(), y = getY(), z = getZ();
        double x2 = axis.getX(), y2 = axis.getY(), z2 = axis.getZ();

        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);
        double dotProduct = this.dot(axis);

        double xPrime = x2 * dotProduct * (1d - cosTheta)
                + x * cosTheta
                + (-z2 * y + y2 * z) * sinTheta;
        double yPrime = y2 * dotProduct * (1d - cosTheta)
                + y * cosTheta
                + (z2 * x - x2 * z) * sinTheta;
        double zPrime = z2 * dotProduct * (1d - cosTheta)
                + z * cosTheta
                + (-y2 * x + x2 * y) * sinTheta;

        return setX(xPrime).setY(yPrime).setZ(zPrime);
    }

    public int getBlockX() {
        return NumberConversions.floor(x);
    }

    public int getBlockY() {
        return NumberConversions.floor(y);
    }


    public int getBlockZ() {
        return NumberConversions.floor(z);
    }

    public Vector setX(int x) {
        this.x = x;
        return this;
    }

    public Vector setX(double x) {
        this.x = x;
        return this;
    }

    public Vector setX(float x) {
        this.x = x;
        return this;
    }

    public Vector setY(int y) {
        this.y = y;
        return this;
    }

    public Vector setY(double y) {
        this.y = y;
        return this;
    }

    public Vector setY(float y) {
        this.y = y;
        return this;
    }

    public Vector setZ(int z) {
        this.z = z;
        return this;
    }

    public Vector setZ(double z) {
        this.z = z;
        return this;
    }

    public Vector setZ(float z) {
        this.z = z;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Vector other)) {
            return false;
        }
        return Math.abs(x - other.x) < EPSILON && Math.abs(y - other.y) < EPSILON && Math.abs(z - other.z) < EPSILON && (this.getClass().equals(obj.getClass()));
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 79 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }

    @Override
    public Vector clone() {
        try {
            return (Vector) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    public Location toLocation(World world, float yaw, float pitch) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void checkFinite() throws IllegalArgumentException {
        NumberConversions.checkFinite(x, "x not finite");
        NumberConversions.checkFinite(y, "y not finite");
        NumberConversions.checkFinite(z, "z not finite");
    }

    public static double getEpsilon() {
        return EPSILON;
    }

    public static Vector getMinimum(Vector v1, Vector v2) {
        ValidUtils.notNull(v1, "Vector 1 cannot be null");
        ValidUtils.notNull(v2, "Vector 2 cannot be null");
        return new Vector(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y), Math.min(v1.z, v2.z));
    }

    public static Vector getMaximum(Vector v1, Vector v2) {
        ValidUtils.notNull(v1, "Vector 1 cannot be null");
        ValidUtils.notNull(v2, "Vector 2 cannot be null");
        return new Vector(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y), Math.max(v1.z, v2.z));
    }

    public static Vector getRandom() {
        return new Vector(RANDOM.nextDouble(), RANDOM.nextDouble(), RANDOM.nextDouble());
    }
}
