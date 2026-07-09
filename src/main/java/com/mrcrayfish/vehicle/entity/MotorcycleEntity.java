/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class MotorcycleEntity
extends LandVehicleEntity {
    public float prevLeanAngle;
    public float leanAngle;

    public MotorcycleEntity(EntityType<?> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    @Override
    public void tick() {
        this.prevLeanAngle = this.leanAngle;
        super.tick();
        this.leanAngle = this.turnAngle / (float)this.getMaxTurnAngle();
    }
}

