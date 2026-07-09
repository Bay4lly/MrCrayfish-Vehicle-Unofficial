/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class BumperCarEntity
extends LandVehicleEntity {
    public BumperCarEntity(EntityType<? extends BumperCarEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxSpeed(10.0f);
        this.setTurnSensitivity(20);
        this.stepHeight = 0.625f;
    }

    public void push(Entity entityIn) {
        if (entityIn instanceof BumperCarEntity && this.isVehicle()) {
            this.applyBumperCollision((BumperCarEntity)entityIn);
        }
    }

    private void applyBumperCollision(BumperCarEntity entity) {
        this.setDeltaMovement(this.getDeltaMovement().add((double)(this.vehicleMotionX * 2.0f), 0.0, (double)(this.vehicleMotionZ * 2.0f)));
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)ModSounds.ENTITY_BUMPER_CAR_BONK.get(), SoundSource.NEUTRAL, 1.0f, 0.6f + 0.1f * this.getNormalSpeed());
        this.currentSpeed *= 0.25f;
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_BUMPER_CAR_ENGINE.get();
    }

    @Override
    public float getMaxEnginePitch() {
        return 0.8f;
    }

    @Override
    public boolean canBeColored() {
        return true;
    }

    @Override
    public boolean isLockable() {
        return false;
    }
}

