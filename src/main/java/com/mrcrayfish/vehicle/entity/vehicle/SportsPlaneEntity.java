/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 */
package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.PlaneEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class SportsPlaneEntity
extends PlaneEntity {
    public float wheelSpeed;
    public float wheelRotation;
    public float prevWheelRotation;
    public float propellerSpeed;
    public float propellerRotation;
    public float prevPropellerRotation;

    public SportsPlaneEntity(EntityType<? extends SportsPlaneEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setAccelerationSpeed(0.5f);
        this.setMaxSpeed(25.0f);
        this.setMaxTurnAngle(25);
        this.setTurnSensitivity(2);
        this.setFuelCapacity(75000.0f);
        this.setFuelConsumption(1.0f);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(1.5);
    }

    @Override
    public void updateVehicle() {
        this.prevWheelRotation = this.wheelRotation;
        this.prevPropellerRotation = this.propellerRotation;
        this.wheelSpeed = this.onGround() ? this.currentSpeed / 30.0f : (this.wheelSpeed *= 0.95f);
        this.wheelRotation -= 90.0f * this.wheelSpeed;
        if (this.canDrive() && this.getControllingPassenger() != null) {
            this.propellerSpeed += 1.0f;
            if (this.propellerSpeed > 120.0f) {
                this.propellerSpeed = 120.0f;
            }
        } else {
            this.propellerSpeed *= 0.95f;
        }
        this.propellerRotation += this.propellerSpeed;
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_SPORTS_PLANE_ENGINE.get();
    }

    @Override
    public boolean canBeColored() {
        return true;
    }

    @Override
    protected float getModifiedAccelerationSpeed() {
        return super.getModifiedAccelerationSpeed() * (this.propellerSpeed / 120.0f);
    }

    @Override
    public boolean canMountTrailer() {
        return false;
    }
}

