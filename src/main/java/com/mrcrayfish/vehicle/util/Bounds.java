/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.phys.AABB
 */
package com.mrcrayfish.vehicle.util;

import com.mrcrayfish.vehicle.util.CollisionHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public class Bounds {
    public double x1;
    public double y1;
    public double z1;
    public double x2;
    public double y2;
    public double z2;

    public Bounds(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public Bounds(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.x1 = (double)x1 * 0.0625;
        this.y1 = (double)y1 * 0.0625;
        this.z1 = (double)z1 * 0.0625;
        this.x2 = (double)x2 * 0.0625;
        this.y2 = (double)y2 * 0.0625;
        this.z2 = (double)z2 * 0.0625;
    }

    public AABB toAABB() {
        return new AABB(this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    public AABB getRotation(Direction facing) {
        return CollisionHelper.getBlockBounds(facing, this);
    }

    public AABB[] getRotatedBounds() {
        AABB boundsNorth = CollisionHelper.getBlockBounds(Direction.NORTH, this);
        AABB boundsEast = CollisionHelper.getBlockBounds(Direction.EAST, this);
        AABB boundsSouth = CollisionHelper.getBlockBounds(Direction.SOUTH, this);
        AABB boundsWest = CollisionHelper.getBlockBounds(Direction.WEST, this);
        return new AABB[]{boundsSouth, boundsWest, boundsNorth, boundsEast};
    }
}

