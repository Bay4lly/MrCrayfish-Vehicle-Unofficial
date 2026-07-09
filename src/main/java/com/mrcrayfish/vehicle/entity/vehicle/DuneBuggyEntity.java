/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class DuneBuggyEntity
extends LandVehicleEntity {
    public DuneBuggyEntity(EntityType<? extends DuneBuggyEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxSpeed(10.0f);
        this.stepHeight = 0.5f;
        this.setFuelCapacity(5000.0f);
        this.entityData.set(COLOR, 15905046);
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_BUMPER_CAR_ENGINE.get();
    }

    @Override
    public boolean isLockable() {
        return false;
    }
}

