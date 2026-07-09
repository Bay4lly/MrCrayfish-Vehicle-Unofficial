/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.PlaneEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class BathEntity
extends PlaneEntity {
    public BathEntity(EntityType<? extends BathEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setFuelConsumption(0.0f);
    }

    @Override
    public SoundEvent getEngineSound() {
        return null;
    }

    @Override
    public void updateVehicle() {
        if (this.isFlying() && this.getControllingPassenger() != null) {
            for (int i = 0; i < 4; ++i) {
                this.level().addParticle((ParticleOptions)ParticleTypes.DRIPPING_WATER, this.getX() - 0.25 + 0.5 * this.random.nextGaussian(), this.getY() + 0.5 * this.random.nextGaussian(), this.getZ() - 0.25 + 0.5 * this.random.nextGaussian(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public boolean canBeColored() {
        return false;
    }

    @Override
    public boolean isLockable() {
        return false;
    }
}

