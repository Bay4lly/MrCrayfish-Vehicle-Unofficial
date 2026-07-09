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

public class MiniBusEntity
extends LandVehicleEntity {
    public MiniBusEntity(EntityType<? extends MiniBusEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxSpeed(15.0f);
        this.setTurnSensitivity(4);
        this.setFuelCapacity(30000.0f);
        this.setFuelConsumption(0.375f);
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_MINI_BUS_ENGINE.get();
    }

    @Override
    public float getMinEnginePitch() {
        return 0.75f;
    }

    @Override
    public float getMaxEnginePitch() {
        return 1.25f;
    }

    @Override
    public boolean canBeColored() {
        return true;
    }

    @Override
    public boolean canTowTrailer() {
        return true;
    }
}

