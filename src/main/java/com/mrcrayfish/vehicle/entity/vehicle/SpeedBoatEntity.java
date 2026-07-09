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

import com.mrcrayfish.vehicle.entity.BoatEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class SpeedBoatEntity
extends BoatEntity {
    public SpeedBoatEntity(EntityType<? extends SpeedBoatEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxSpeed(20.0f);
        this.setTurnSensitivity(10);
        this.setFuelCapacity(25000.0f);
        this.setFuelConsumption(0.75f);
    }

    @Override
    public void createParticles() {
        if (this.state == BoatEntity.State.IN_WATER && this.getAcceleration() == PoweredVehicleEntity.AccelerationDirection.FORWARD) {
            int i;
            for (i = 0; i < 5; ++i) {
                this.level().addParticle((ParticleOptions)ParticleTypes.SPLASH, this.getX() + ((double)this.random.nextFloat() - 0.5) * (double)this.getBbWidth(), this.getBoundingBox().minY + 0.1, this.getZ() + ((double)this.random.nextFloat() - 0.5) * (double)this.getBbWidth(), -this.getDeltaMovement().x * 4.0, 1.5, -this.getDeltaMovement().z * 4.0);
            }
            for (i = 0; i < 5; ++i) {
                this.level().addParticle((ParticleOptions)ParticleTypes.BUBBLE, this.getX() + ((double)this.random.nextFloat() - 0.5) * (double)this.getBbWidth(), this.getBoundingBox().minY + 0.1, this.getZ() + ((double)this.random.nextFloat() - 0.5) * (double)this.getBbWidth(), -this.getDeltaMovement().x * 2.0, 0.0, -this.getDeltaMovement().z * 2.0);
            }
        }
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_SPEED_BOAT_ENGINE.get();
    }

    @Override
    public float getMinEnginePitch() {
        return 1.0f;
    }

    @Override
    public float getMaxEnginePitch() {
        return 2.0f;
    }

    @Override
    public boolean canBeColored() {
        return true;
    }

    @Override
    public boolean isLockable() {
        return false;
    }

    @Override
    public PoweredVehicleEntity.FuelPortType getFuelPortType() {
        return PoweredVehicleEntity.FuelPortType.SMALL;
    }
}

