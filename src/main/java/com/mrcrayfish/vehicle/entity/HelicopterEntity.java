/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.network.syncher.SynchedEntityData$Builder
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.entity.IEngineTier;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAltitude;
import com.mrcrayfish.vehicle.network.message.MessageTravelProperties;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class HelicopterEntity
extends PoweredVehicleEntity {
    private static final EntityDataAccessor<Integer> ALTITUDE_CHANGE = SynchedEntityData.defineId(HelicopterEntity.class, (EntityDataSerializer)EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> LIFT = SynchedEntityData.defineId(HelicopterEntity.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAVEL_DIRECTION = SynchedEntityData.defineId(HelicopterEntity.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAVEL_SPEED = SynchedEntityData.defineId(HelicopterEntity.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    private float lift;
    private float bladeSpeed;
    public float bladeRotation;
    public float prevBladeRotation;
    public float prevBodyRotationX;
    public float prevBodyRotationY;
    public float prevBodyRotationZ;
    public float bodyRotationX;
    public float bodyRotationY;
    public float bodyRotationZ;
    public float dirX;
    public float dirZ;

    protected HelicopterEntity(EntityType<?> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.setMaxSpeed(18.0f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ALTITUDE_CHANGE, AltitudeChange.NONE.ordinal());
        builder.define(LIFT, Float.valueOf(0.0f));
        builder.define(TRAVEL_DIRECTION, Float.valueOf(0.0f));
        builder.define(TRAVEL_SPEED, Float.valueOf(0.0f));
    }

    @Override
    public SoundEvent getEngineSound() {
        return null;
    }

    @Override
    public void updateVehicleMotion() {
        LivingEntity entity = this.getControllingPassenger();
        if (entity != null && this.isFlying()) {
            float deltaYaw;
            for (deltaYaw = entity.getYHeadRot() % 360.0f - this.getYRot(); deltaYaw < -180.0f; deltaYaw += 360.0f) {
            }
            while (deltaYaw >= 180.0f) {
                deltaYaw -= 360.0f;
            }
            this.setYRot(this.getYRot() + deltaYaw * 0.15f);
        }
        float travelDirection = this.getTravelDirection();
        if (this.getAcceleration() != PoweredVehicleEntity.AccelerationDirection.NONE || this.getTurnDirection() != PoweredVehicleEntity.TurnDirection.FORWARD) {
            float newDirX = Mth.sin((float)(travelDirection * ((float)Math.PI / 180))) / 20.0f;
            float newDirZ = Mth.cos((float)(travelDirection * ((float)Math.PI / 180))) / 20.0f;
            this.dirX += (newDirX - this.dirX) * 0.05f;
            this.dirZ += (newDirZ - this.dirZ) * 0.05f;
        }
        this.vehicleMotionX = -this.currentSpeed * this.dirX;
        this.vehicleMotionZ = this.currentSpeed * this.dirZ;
        Vec3 motion = this.getDeltaMovement();
        double motionY = motion.y();
        this.updateLift();
        if (this.isFueled()) {
            motionY = this.lift * this.getBladeSpeedNormal();
            motionY -= 0.05 + (1.0 - (double)this.getBladeSpeedNormal()) * 0.45;
        } else {
            motionY -= 0.08 - 0.08 * (double)this.getBladeSpeedNormal();
        }
        this.setDeltaMovement(motion.x(), motionY, motion.z());
    }

    @Override
    protected void updateSpeed() {
        this.currentSpeed = this.getSpeed();
        Optional<IEngineTier> optional = this.getEngineTier();
        if (this.getControllingPassenger() != null && optional.isPresent()) {
            if (!this.isFlying()) {
                this.currentSpeed = (float)((double)this.currentSpeed * 0.75);
                return;
            }
            if (this.canDrive()) {
                if (this.getTravelSpeed() != 0.0f) {
                    float maxSpeed = this.getActualMaxSpeed() * this.getTravelSpeed();
                    if (this.currentSpeed < maxSpeed) {
                        IEngineTier engineTier = optional.get();
                        this.currentSpeed += this.getModifiedAccelerationSpeed() * engineTier.getAccelerationMultiplier();
                        if (this.currentSpeed > maxSpeed) {
                            this.currentSpeed = maxSpeed;
                        }
                    }
                    if (this.currentSpeed > maxSpeed) {
                        this.currentSpeed *= 0.975f;
                    }
                } else {
                    this.currentSpeed = (float)((double)this.currentSpeed * 0.95);
                }
            } else {
                this.currentSpeed = (float)((double)this.currentSpeed * 0.9);
            }
        } else {
            this.currentSpeed = (float)((double)this.currentSpeed * 0.5);
        }
    }

    @Override
    public void updateVehicle() {
        this.prevBladeRotation = this.bladeRotation;
        if (this.canDrive() && this.getControllingPassenger() != null) {
            this.bladeSpeed += 0.5f;
            if (this.bladeSpeed > 60.0f) {
                this.bladeSpeed = 60.0f;
            }
        } else {
            this.bladeSpeed *= 0.98f;
        }
        this.bladeRotation += this.bladeSpeed;
    }

    protected void updateLift() {
        AltitudeChange altitudeChange = this.getAltitudeChange();
        this.lift = altitudeChange == AltitudeChange.POSITIVE ? (this.lift += 0.05f) : (altitudeChange == AltitudeChange.NEGATIVE ? (this.lift -= 0.05f) : (this.lift *= 0.85f));
        this.lift = Mth.clamp((float)this.lift, (float)-0.5f, (float)0.25f);
        this.setLift(this.lift);
    }

    @Override
    public void onClientUpdate() {
        super.onClientUpdate();
        this.prevBodyRotationX = this.bodyRotationX;
        this.prevBodyRotationY = this.bodyRotationY;
        this.prevBodyRotationZ = this.bodyRotationZ;
        LivingEntity entity = this.getControllingPassenger();
        if (entity != null && entity.equals((Object)Minecraft.getInstance().player)) {
            AltitudeChange altitudeChange = VehicleHelper.getAltitudeChange();
            if (this.getAltitudeChange() != altitudeChange) {
                this.setAltitudeChange(altitudeChange);
                PacketHandler.sendToServer(new MessageAltitude(altitudeChange));
            }
            float travelDirection = VehicleHelper.getTravelDirection(this);
            float travelSpeed = VehicleHelper.getTravelSpeed(this);
            this.setTravelDirection(travelDirection);
            this.setTravelSpeed(travelSpeed);
            PacketHandler.sendToServer(new MessageTravelProperties(travelSpeed, travelDirection));
        }
        if (this.isFlying()) {
            this.bodyRotationX = this.dirX * 20.0f * 35.0f * this.getActualSpeed();
            this.bodyRotationZ = this.dirZ * 20.0f * 35.0f * this.getActualSpeed();
        } else {
            this.bodyRotationX *= 0.5f;
            this.bodyRotationZ *= 0.5f;
        }
    }

    @Override
    public void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        passenger.setYRot(this.getYRot());
    }

    @Override
    protected void updateTurning() {
    }

    public double getPassengersRidingOffset() {
        return 0.0;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    public void setAltitudeChange(AltitudeChange altitudeChange) {
        this.entityData.set(ALTITUDE_CHANGE, altitudeChange.ordinal());
    }

    public AltitudeChange getAltitudeChange() {
        return AltitudeChange.values()[(Integer)this.entityData.get(ALTITUDE_CHANGE)];
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

    public float getBladeSpeedNormal() {
        return this.bladeSpeed / 60.0f;
    }

    @Override
    public boolean canChangeWheels() {
        return false;
    }

    public float getTravelDirection() {
        return ((Float)this.entityData.get(TRAVEL_DIRECTION)).floatValue();
    }

    public void setTravelDirection(float travelDirection) {
        this.entityData.set(TRAVEL_DIRECTION, Float.valueOf(travelDirection));
    }

    public float getTravelSpeed() {
        return ((Float)this.entityData.get(TRAVEL_SPEED)).floatValue();
    }

    public void setTravelSpeed(float travelSpeed) {
        this.entityData.set(TRAVEL_SPEED, Float.valueOf(travelSpeed));
    }

    public static enum AltitudeChange {
        POSITIVE,
        NEGATIVE,
        NONE;


        public static AltitudeChange fromInput(boolean up, boolean down) {
            return up && !down ? POSITIVE : (down && !up ? NEGATIVE : NONE);
        }
    }
}

