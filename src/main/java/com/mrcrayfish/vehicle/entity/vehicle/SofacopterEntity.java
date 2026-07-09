/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class SofacopterEntity
extends HelicopterEntity {
    public SofacopterEntity(EntityType<? extends SofacopterEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setFuelCapacity(40000.0f);
        this.setFuelConsumption(0.5f);
        this.entityData.set(COLOR, 11546150);
    }

    @Override
    public boolean canBeColored() {
        return false;
    }
}

