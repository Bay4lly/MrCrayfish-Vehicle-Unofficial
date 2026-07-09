/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.network.syncher.SynchedEntityData$Builder
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 */
package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.entity.IEngineTier;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageFlaps;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public abstract class PlaneEntity
extends PoweredVehicleEntity {
    private static final EntityDataAccessor<Integer> FLAP_DIRECTION = SynchedEntityData.defineId(PlaneEntity.class, (EntityDataSerializer)EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> LIFT = SynchedEntityData.defineId(PlaneEntity.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    private float lift;
    public float prevBodyRotationX;
    public float prevBodyRotationY;
    public float prevBodyRotationZ;
    public float bodyRotationX;
    public float bodyRotationY;
    public float bodyRotationZ;

    protected PlaneEntity(EntityType<?> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.setAccelerationSpeed(0.5f);
        this.setMaxSpeed(25.0f);
        this.setTurnSensitivity(5);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FLAP_DIRECTION, FlapDirection.NONE.ordinal());
        builder.define(LIFT, Float.valueOf(0.0f));
    }

    @Override
    public void updateVehicleMotion() {
        float f1 = Mth.sin((float)(this.getYRot() * ((float)Math.PI / 180))) / 20.0f;
        float f2 = Mth.cos((float)(this.getYRot() * ((float)Math.PI / 180))) / 20.0f;
        this.updateLift();
        this.vehicleMotionX = -this.currentSpeed * f1;
        this.vehicleMotionZ = this.currentSpeed * f2;
        this.setDeltaMovement(this.getDeltaMovement().add(0.0, (double)this.lift - 0.05, 0.0));
    }

    @Override
    public void onClientUpdate() {
        super.onClientUpdate();
        this.prevBodyRotationX = this.bodyRotationX;
        this.prevBodyRotationY = this.bodyRotationY;
        this.prevBodyRotationZ = this.bodyRotationZ;
        LivingEntity entity = this.getControllingPassenger();
        if (entity != null && entity.equals((Object)Minecraft.getInstance().player)) {
            FlapDirection flapDirection = VehicleHelper.getFlapDirection();
            if (this.getFlapDirection() != flapDirection) {
                this.setFlapDirection(flapDirection);
                PacketHandler.sendToServer(new MessageFlaps(flapDirection));
            }
        }
        if (this.isFlying()) {
            this.bodyRotationX = (float)Math.toDegrees(Math.atan2(this.getDeltaMovement().y(), this.currentSpeed / 20.0f));
            this.bodyRotationZ = this.turnAngle / (float)this.getMaxTurnAngle() * 20.0f;
        } else {
            this.bodyRotationX *= 0.5f;
            this.bodyRotationZ *= 0.5f;
        }
    }

    @Override
    protected void updateSpeed() {
        this.lift = 0.0f;
        this.currentSpeed = this.getSpeed();
        Optional<IEngineTier> optional = this.getEngineTier();
        if (this.getControllingPassenger() != null && optional.isPresent()) {
            PoweredVehicleEntity.AccelerationDirection acceleration = this.getAcceleration();
            if (this.canDrive() && acceleration == PoweredVehicleEntity.AccelerationDirection.FORWARD) {
                if (this.getDeltaMovement().y() < 0.0) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.95, 1.0));
                }
                IEngineTier engineTier = optional.get();
                float accelerationSpeed = this.getModifiedAccelerationSpeed() * engineTier.getAccelerationMultiplier();
                if (this.currentSpeed < this.getActualMaxSpeed()) {
                    this.currentSpeed += accelerationSpeed;
                }
                this.lift = 0.051f * (Math.min(this.currentSpeed, 15.0f) / 15.0f);
            } else if (acceleration == PoweredVehicleEntity.AccelerationDirection.REVERSE) {
                this.currentSpeed = this.isFlying() ? (this.currentSpeed *= 0.95f) : (this.currentSpeed *= 0.9f);
            }
            if (acceleration != PoweredVehicleEntity.AccelerationDirection.FORWARD) {
                this.currentSpeed = this.isFlying() ? (this.currentSpeed *= 0.995f) : (this.currentSpeed *= 0.98f);
                this.lift = 0.04f * (Math.min(this.currentSpeed, 15.0f) / 15.0f);
            }
        } else {
            this.currentSpeed = this.isFlying() ? (this.currentSpeed *= 0.98f) : (this.currentSpeed *= 0.85f);
        }
    }

    @Override
    protected void updateTurning() {
        PoweredVehicleEntity.TurnDirection direction = this.getTurnDirection();
        if (this.getControllingPassenger() != null && direction != PoweredVehicleEntity.TurnDirection.FORWARD) {
            this.turnAngle += (float)(direction.dir * this.getTurnSensitivity());
            if (Math.abs(this.turnAngle) > (float)this.getMaxTurnAngle()) {
                this.turnAngle = this.getMaxTurnAngle() * direction.dir;
            }
        } else {
            this.turnAngle = (float)((double)this.turnAngle * 0.95);
        }
        this.wheelAngle = this.isFlying() ? this.turnAngle * Math.max(0.25f, 1.0f - Math.abs(Math.min(this.currentSpeed, 30.0f) / 30.0f)) : this.turnAngle * Math.abs(Math.min(this.currentSpeed, 30.0f) / 30.0f);
        this.deltaYaw = this.wheelAngle;
        this.deltaYaw = this.isFlying() ? (float)((double)this.deltaYaw * 0.5) : (float)((double)this.deltaYaw * (0.5 * (0.5 + 0.5 * (double)(1.0f - Math.min(this.currentSpeed, 15.0f) / 15.0f))));
    }

    public void updateLift() {
        FlapDirection flapDirection = this.getFlapDirection();
        if (flapDirection == FlapDirection.UP) {
            this.lift += 0.04f * (Math.min(Math.max(this.currentSpeed - 5.0f, 0.0f), 15.0f) / 15.0f);
        } else if (flapDirection == FlapDirection.DOWN) {
            this.lift -= 0.06f * (Math.min(this.currentSpeed, 15.0f) / 15.0f);
        }
        this.setLift(this.lift);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("FlapDirection", this.getFlapDirection().ordinal());
        compound.putFloat("Lift", this.getLift());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("FlapDirection", 3)) {
            this.setFlapDirection(FlapDirection.values()[compound.getInt("FlapDirection")]);
        }
        if (compound.contains("Lift", 5)) {
            this.setLift(compound.getFloat("Lift"));
        }
    }

    public void setFlapDirection(FlapDirection flapDirection) {
        this.entityData.set(FLAP_DIRECTION, flapDirection.ordinal());
    }

    public FlapDirection getFlapDirection() {
        return FlapDirection.values()[(Integer)this.entityData.get(FLAP_DIRECTION)];
    }

    public float getLift() {
        return ((Float)this.entityData.get(LIFT)).floatValue();
    }

    public void setLift(float lift) {
        this.entityData.set(LIFT, Float.valueOf(lift));
    }

    public boolean isFlying() {
        return !this.onGround();
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean canChangeWheels() {
        return false;
    }

    public static enum FlapDirection {
        UP,
        DOWN,
        NONE;


        public static FlapDirection fromInput(boolean up, boolean down) {
            return up && !down ? UP : (down && !up ? DOWN : NONE);
        }
    }
}

