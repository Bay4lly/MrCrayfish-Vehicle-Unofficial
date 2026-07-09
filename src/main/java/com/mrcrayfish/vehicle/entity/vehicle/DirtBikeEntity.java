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

import com.mrcrayfish.vehicle.entity.MotorcycleEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DirtBikeEntity
extends MotorcycleEntity {
    public DirtBikeEntity(EntityType<? extends DirtBikeEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxSpeed(18.0f);
        this.setMaxTurnAngle(35);
        this.setFuelCapacity(20000.0f);
        this.setFuelConsumption(0.35f);
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_DIRT_BIKE_ENGINE.get();
    }

    @Override
    public float getMinEnginePitch() {
        return 0.85f;
    }

    @Override
    public float getMaxEnginePitch() {
        return 1.5f;
    }

    @Override
    public boolean shouldShowEngineSmoke() {
        return true;
    }

    @Override
    public Vec3 getEngineSmokePosition() {
        return new Vec3(-0.0625, 1.25, -1.0);
    }

    @Override
    public boolean canBeColored() {
        return true;
    }

    @Override
    public boolean shouldRenderEngine() {
        return true;
    }

    @Override
    public PoweredVehicleEntity.FuelPortType getFuelPortType() {
        return PoweredVehicleEntity.FuelPortType.SMALL;
    }

    @Override
    public boolean isLockable() {
        return false;
    }
}

