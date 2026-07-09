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
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MiniBikeEntity
extends MotorcycleEntity {
    public MiniBikeEntity(EntityType<? extends MiniBikeEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxSpeed(18.0f);
        this.setFuelCapacity(15000.0f);
        this.setFuelConsumption(0.375f);
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_GO_KART_ENGINE.get();
    }

    @Override
    public float getMinEnginePitch() {
        return 0.5f;
    }

    @Override
    public float getMaxEnginePitch() {
        return 1.8f;
    }

    @Override
    public boolean shouldShowEngineSmoke() {
        return true;
    }

    @Override
    public Vec3 getEngineSmokePosition() {
        return new Vec3(0.0, 0.55, 0.0);
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

