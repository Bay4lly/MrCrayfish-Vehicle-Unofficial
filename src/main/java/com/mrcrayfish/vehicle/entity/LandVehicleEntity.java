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
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageDrift;
import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class LandVehicleEntity
extends PoweredVehicleEntity {
    private static final EntityDataAccessor<Boolean> DRIFTING = SynchedEntityData.defineId(LandVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.BOOLEAN);
    public float drifting;
    public float additionalYaw;
    public float prevAdditionalYaw;
    public float frontWheelRotation;
    public float prevFrontWheelRotation;
    public float rearWheelRotation;
    public float prevRearWheelRotation;

    public LandVehicleEntity(EntityType<?> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DRIFTING, false);
    }

    @Override
    public void onUpdateVehicle() {
        super.onUpdateVehicle();
        this.updateWheels();
    }

    @Override
    public void updateVehicle() {
        this.prevAdditionalYaw = this.additionalYaw;
        this.prevFrontWheelRotation = this.frontWheelRotation;
        this.prevRearWheelRotation = this.rearWheelRotation;
        this.updateDrifting();
    }

    @Override
    public void onClientUpdate() {
        super.onClientUpdate();
        LivingEntity entity = this.getControllingPassenger();
        if (entity != null && entity.equals((Object)Minecraft.getInstance().player)) {
            boolean drifting = VehicleHelper.isDrifting();
            if (this.isDrifting() != drifting) {
                this.setDrifting(drifting);
                PacketHandler.sendToServer(new MessageDrift(drifting));
            }
        }
    }

    @Override
    public void updateVehicleMotion() {
        float currentSpeed = this.currentSpeed;
        if (this.speedMultiplier > 1.0f) {
            this.speedMultiplier = 1.0f;
        }
        currentSpeed += currentSpeed * this.speedMultiplier;
        VehicleProperties properties = this.getProperties();
        if (properties.getFrontAxelVec() != null && properties.getRearAxelVec() != null) {
            PoweredVehicleEntity.AccelerationDirection acceleration = this.getAcceleration();
            if (acceleration == PoweredVehicleEntity.AccelerationDirection.CHARGING && this.charging) {
                PartPosition bodyPosition = properties.getBodyPosition();
                Vec3 frontAxel = properties.getFrontAxelVec().scale(0.0625).scale(bodyPosition.getScale());
                Vec3 nextFrontAxel = frontAxel.yRot(this.turnAngle / 20.0f * ((float)Math.PI / 180));
                Vec3 deltaAxel = frontAxel.subtract(nextFrontAxel).yRot(-this.getYRot() * ((float)Math.PI / 180));
                double deltaYaw = -this.turnAngle / 20.0f;
                this.setYRot((float)((double)this.getYRot() + deltaYaw));
                this.deltaYaw = (float)(-deltaYaw);
                this.vehicleMotionX = (float)deltaAxel.x();
                if (!this.launching) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.08, 0.0));
                }
                this.vehicleMotionZ = (float)deltaAxel.z();
                return;
            }
            PartPosition bodyPosition = properties.getBodyPosition();
            Vec3 nextFrontAxelVec = new Vec3(0.0, 0.0, (double)(currentSpeed / 20.0f)).yRot(this.wheelAngle * ((float)Math.PI / 180));
            nextFrontAxelVec = nextFrontAxelVec.add(properties.getFrontAxelVec().scale(0.0625).scale(bodyPosition.getScale()));
            Vec3 nextRearAxelVec = new Vec3(0.0, 0.0, (double)(currentSpeed / 20.0f));
            nextRearAxelVec = nextRearAxelVec.add(properties.getRearAxelVec().scale(0.0625).scale(bodyPosition.getScale()));
            double deltaYaw = Math.toDegrees(Math.atan2(nextRearAxelVec.z - nextFrontAxelVec.z, nextRearAxelVec.x - nextFrontAxelVec.x)) + 90.0;
            if (this.isRearWheelSteering()) {
                deltaYaw -= 180.0;
            }
            this.setYRot((float)((double)this.getYRot() + deltaYaw));
            this.deltaYaw = (float)(-deltaYaw);
            Vec3 nextVehicleVec = nextFrontAxelVec.add(nextRearAxelVec).scale(0.5);
            nextVehicleVec = nextVehicleVec.subtract(properties.getFrontAxelVec().add(properties.getRearAxelVec()).scale(0.0625).scale(bodyPosition.getScale()).scale(0.5));
            nextVehicleVec = nextVehicleVec.scale(bodyPosition.getScale()).yRot((-this.getYRot() + 90.0f) * ((float)Math.PI / 180));
            float targetRotation = (float)Math.toDegrees(Math.atan2(nextVehicleVec.z, nextVehicleVec.x));
            float f1 = Mth.sin((float)(targetRotation * ((float)Math.PI / 180))) / 20.0f * (float)(currentSpeed > 0.0f ? 1 : -1);
            float f2 = Mth.cos((float)(targetRotation * ((float)Math.PI / 180))) / 20.0f * (float)(currentSpeed > 0.0f ? 1 : -1);
            this.vehicleMotionX = -currentSpeed * f1;
            if (!this.launching) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.08, 0.0));
            }
            this.vehicleMotionZ = currentSpeed * f2;
        } else {
            float f1 = Mth.sin((float)(this.getYRot() * ((float)Math.PI / 180))) / 20.0f;
            float f2 = Mth.cos((float)(this.getYRot() * ((float)Math.PI / 180))) / 20.0f;
            this.vehicleMotionX = -currentSpeed * f1;
            if (!this.launching) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.08, 0.0));
            }
            this.vehicleMotionZ = currentSpeed * f2;
        }
    }

    @Override
    protected void updateTurning() {
        this.turnAngle = this.level().isClientSide() ? VehicleHelper.getTargetTurnAngle(this, this.isDrifting()) : 0.0f;
        this.wheelAngle = this.turnAngle * Math.max(0.45f, 1.0f - Math.abs(this.currentSpeed / 20.0f));
        VehicleProperties properties = this.getProperties();
        if (properties.getFrontAxelVec() == null || properties.getRearAxelVec() == null) {
            this.deltaYaw = this.wheelAngle * (this.currentSpeed / 30.0f) / 2.0f;
        }
        if (this.level().isClientSide) {
            this.targetWheelAngle = this.isDrifting() ? -35.0f * (this.turnAngle / (float)this.getMaxTurnAngle()) * this.getNormalSpeed() : this.wheelAngle - 35.0f * (this.turnAngle / (float)this.getMaxTurnAngle()) * this.drifting;
            this.renderWheelAngle += (this.targetWheelAngle - this.renderWheelAngle) * (this.isDrifting() ? 0.35f : 0.5f);
        }
    }

    private void updateDrifting() {
        PoweredVehicleEntity.TurnDirection turnDirection = this.getTurnDirection();
        if (this.getControllingPassenger() != null && this.isDrifting()) {
            if (turnDirection != PoweredVehicleEntity.TurnDirection.FORWARD) {
                PoweredVehicleEntity.AccelerationDirection acceleration = this.getAcceleration();
                if (acceleration == PoweredVehicleEntity.AccelerationDirection.FORWARD) {
                    this.currentSpeed *= 0.975f;
                }
                this.drifting = Math.min(1.0f, this.drifting + 0.025f);
            }
        } else {
            this.drifting *= 0.95f;
        }
        this.additionalYaw = 25.0f * this.drifting * (this.turnAngle / (float)this.getMaxTurnAngle()) * Math.min(this.getActualMaxSpeed(), this.getActualSpeed() * 2.0f);
        this.deltaYaw = this.wheelAngle * (this.currentSpeed / 30.0f) / (this.isDrifting() ? 1.5f : 2.0f);
    }

    public void updateWheels() {
        Wheel rearWheel;
        VehicleProperties properties = this.getProperties();
        double wheelCircumference = 24.0;
        double vehicleScale = properties.getBodyPosition().getScale();
        double speed = this.getSpeed();
        Wheel frontWheel = properties.getFirstFrontWheel();
        if (frontWheel != null && !this.charging) {
            double frontWheelCircumference = wheelCircumference * vehicleScale * (double)frontWheel.getScaleY();
            double rotation = speed * 16.0 / frontWheelCircumference;
            this.frontWheelRotation = (float)((double)this.frontWheelRotation - rotation * 20.0);
        }
        if ((rearWheel = properties.getFirstRearWheel()) != null) {
            double rearWheelCircumference = wheelCircumference * vehicleScale * (double)rearWheel.getScaleY();
            double rotation = speed * 16.0 / rearWheelCircumference;
            this.rearWheelRotation = (float)((double)this.rearWheelRotation - rotation * 20.0);
        }
    }

    @Override
    public void createParticles() {
        if (this.canDrive()) {
            super.createParticles();
        }
    }

    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (this.getControllingPassenger() == null) {
            this.setYRot(this.getYRot() - this.additionalYaw);
            this.additionalYaw = 0.0f;
            this.drifting = 0.0f;
        }
    }

    public void setDrifting(boolean drifting) {
        this.entityData.set(DRIFTING, drifting);
    }

    public boolean isDrifting() {
        return (Boolean)this.entityData.get(DRIFTING);
    }

    @Override
    protected float getModifiedAccelerationSpeed() {
        if (this.trailer != null) {
            if (this.trailer.getPassengers().size() > 0) {
                return super.getModifiedAccelerationSpeed() * 0.5f;
            }
            return super.getModifiedAccelerationSpeed() * 0.8f;
        }
        return super.getModifiedAccelerationSpeed();
    }

    @Override
    public float getModifiedRotationYaw() {
        return this.getYRot() - this.additionalYaw;
    }

    public boolean isRearWheelSteering() {
        VehicleProperties properties = this.getProperties();
        return properties.getFrontAxelVec() != null && properties.getRearAxelVec() != null && properties.getFrontAxelVec().z < properties.getRearAxelVec().z;
    }

    @Override
    protected boolean canCharge() {
        return true;
    }

    public boolean canWheelie() {
        return true;
    }
}

