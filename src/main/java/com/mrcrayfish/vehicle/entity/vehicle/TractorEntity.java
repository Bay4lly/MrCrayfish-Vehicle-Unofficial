/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TractorEntity
extends LandVehicleEntity {
    public TractorEntity(EntityType<? extends TractorEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxSpeed(6.0f);
        this.setTurnSensitivity(3);
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_TRACTOR_ENGINE.get();
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
    public PoweredVehicleEntity.FuelPortType getFuelPortType() {
        return PoweredVehicleEntity.FuelPortType.DEFAULT;
    }

    @Override
    public boolean shouldRenderEngine() {
        return true;
    }

    @Override
    public boolean shouldShowEngineSmoke() {
        return true;
    }

    @Override
    public Vec3 getEngineSmokePosition() {
        return new Vec3(-0.125, 1.9375, 1.125);
    }

    @Override
    public boolean canTowTrailer() {
        return true;
    }

    @Override
    public boolean canMountTrailer() {
        return false;
    }

    @Override
    public boolean canBeColored() {
        return true;
    }
}

