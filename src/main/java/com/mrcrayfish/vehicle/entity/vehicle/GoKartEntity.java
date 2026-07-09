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
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GoKartEntity
extends LandVehicleEntity {
    public GoKartEntity(EntityType<? extends GoKartEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxSpeed(20.0f);
        this.stepHeight = 0.625f;
        this.setFuelConsumption(0.5f);
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_GO_KART_ENGINE.get();
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
    public boolean shouldShowEngineSmoke() {
        return true;
    }

    @Override
    public Vec3 getEngineSmokePosition() {
        return new Vec3(0.0, 0.55, -0.9);
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
    public boolean shouldRenderFuelPort() {
        return false;
    }

    @Override
    public boolean isLockable() {
        return false;
    }
}

