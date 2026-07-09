/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 */
package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.common.SurfaceHelper;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ShoppingCartEntity
extends LandVehicleEntity {
    private Player pusher;

    public ShoppingCartEntity(EntityType<? extends ShoppingCartEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxTurnAngle(90);
        this.setTurnSensitivity(15);
        this.setFuelCapacity(0.0f);
        this.setFuelConsumption(0.0f);
        this.setRequiresFuel(false);
    }

    @Override
    public void tick() {
        if (this.pusher != null) {
            this.yRotO = this.getYRot();
            this.xo = this.getX();
            this.yo = this.getY();
            this.zo = this.getZ();
            float x = Mth.sin((float)(-this.pusher.getYRot() * ((float)Math.PI / 180))) * 1.3f;
            float z = Mth.cos((float)(-this.pusher.getYRot() * ((float)Math.PI / 180))) * 1.3f;
            this.setPos(this.pusher.getX() + (double)x, this.pusher.getY(), this.pusher.getZ() + (double)z);
            this.xOld = this.getX();
            this.yOld = this.getY();
            this.zOld = this.getZ();
            this.setYRot(this.pusher.getYRot());
        } else {
            super.tick();
        }
    }

    @Override
    public SoundEvent getEngineSound() {
        return null;
    }

    @Override
    public boolean canDrive() {
        return this.isEnginePowered();
    }

    @Override
    protected void updateSpeed() {
        float surfaceModifier = SurfaceHelper.getSurfaceModifier(this);
        this.currentSpeed = this.getSpeed();
        PoweredVehicleEntity.AccelerationDirection acceleration = this.getAcceleration();
        if (acceleration != PoweredVehicleEntity.AccelerationDirection.CHARGING) {
            this.charging = false;
        }
        if (this.getControllingPassenger() != null) {
            if (this.canDrive()) {
                boolean charging;
                boolean bl = charging = this.canCharge() && acceleration == PoweredVehicleEntity.AccelerationDirection.CHARGING && Math.abs(this.currentSpeed) < 0.5f;
                if (acceleration == PoweredVehicleEntity.AccelerationDirection.FORWARD || charging || this.charging) {
                    if (!this.charging) {
                        this.charging = charging;
                    }
                    if (this.wheelsOnGround || this.canAccelerateInAir()) {
                        float maxSpeed = this.getActualMaxSpeed() * surfaceModifier * this.getPower();
                        if (this.currentSpeed < maxSpeed) {
                            this.currentSpeed += this.getModifiedAccelerationSpeed();
                            if (this.currentSpeed > maxSpeed) {
                                this.currentSpeed = maxSpeed;
                            }
                        }
                        if (this.currentSpeed > maxSpeed) {
                            this.currentSpeed *= 0.975f;
                        }
                        return;
                    }
                } else if (acceleration == PoweredVehicleEntity.AccelerationDirection.REVERSE && (this.wheelsOnGround || this.canAccelerateInAir())) {
                    float maxSpeed = -4.0f * surfaceModifier * this.getPower();
                    if (this.currentSpeed > maxSpeed) {
                        this.currentSpeed -= this.getModifiedAccelerationSpeed();
                        if (this.currentSpeed < maxSpeed) {
                            this.currentSpeed = maxSpeed;
                        }
                    }
                    if (this.currentSpeed < maxSpeed) {
                        this.currentSpeed *= 0.975f;
                    }
                    return;
                }
            }
            this.currentSpeed = this.wheelsOnGround || this.canAccelerateInAir() ? (float)((double)this.currentSpeed * 0.9) : (float)((double)this.currentSpeed * 0.98);
        } else {
            this.currentSpeed = this.wheelsOnGround ? (float)((double)this.currentSpeed * 0.85) : (float)((double)this.currentSpeed * 0.98);
        }
    }

    @Override
    public boolean isLockable() {
        return false;
    }

    @Override
    public boolean canBeColored() {
        return true;
    }
}

