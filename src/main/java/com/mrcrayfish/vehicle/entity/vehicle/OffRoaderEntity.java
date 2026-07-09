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

public class OffRoaderEntity
extends LandVehicleEntity {
    public OffRoaderEntity(EntityType<? extends OffRoaderEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxSpeed(18.0f);
        this.setFuelCapacity(25000.0f);
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_SPEED_BOAT_ENGINE.get();
    }

    @Override
    public float getMinEnginePitch() {
        return 0.8f;
    }

    @Override
    public float getMaxEnginePitch() {
        return 1.6f;
    }

    @Override
    public boolean canBeColored() {
        return true;
    }

    @Override
    public boolean canMountTrailer() {
        return false;
    }
}

