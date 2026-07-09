/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class BoatEntity
extends PoweredVehicleEntity {
    protected State state;
    protected State previousState;
    private double waterLevel;

    public BoatEntity(EntityType<?> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.setMaxTurnAngle(65);
    }

    @Override
    public boolean canChangeWheels() {
        return false;
    }

    @Override
    public void updateVehicleMotion() {
        if (this.state == State.IN_WATER || this.state == State.UNDER_WATER) {
            if (this.state == State.UNDER_WATER) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.08, 0.0));
            } else {
                double floatingY = (this.waterLevel - 0.35 + 0.25 * (double)Math.min(1.0f, this.getNormalSpeed()) - this.getY()) / (double)this.getBbHeight();
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, floatingY * 0.05, 0.0));
                if (Math.abs(floatingY) < 0.1 && this.getDeltaMovement().y > 0.0 && Math.abs(this.getDeltaMovement().y) < 0.1) {
                    this.setPos(this.getX(), this.waterLevel - 0.35 + 0.25 * (double)Math.min(1.0f, this.getNormalSpeed()), this.getZ());
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
                }
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.75, 1.0));
            }
            float f1 = Mth.sin((float)(this.getYRot() * ((float)Math.PI / 180))) / 20.0f;
            float f2 = Mth.cos((float)(this.getYRot() * ((float)Math.PI / 180))) / 20.0f;
            this.vehicleMotionX = -this.currentSpeed * f1;
            this.vehicleMotionZ = this.currentSpeed * f2;
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 1.0, 0.5));
        } else if (this.state == State.IN_AIR) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.08, 0.0));
            if (this.previousState == State.UNDER_WATER || this.previousState == State.IN_WATER) {
                this.setDeltaMovement(new Vec3((double)this.vehicleMotionX, this.getDeltaMovement().y, (double)this.vehicleMotionZ));
                this.vehicleMotionX = 0.0f;
                this.vehicleMotionZ = 0.0f;
            }
        } else {
            this.vehicleMotionX *= 0.75f;
            this.vehicleMotionZ *= 0.75f;
        }
    }

    @Override
    public void updateVehicle() {
        this.previousState = this.state;
        this.state = this.getState();
        if (this.state == State.IN_AIR) {
            this.deltaYaw *= 2.0f;
        }
    }

    private boolean checkInWater() {
        AABB boundingBox = this.getBoundingBox();
        int minX = Mth.floor((double)boundingBox.minX);
        int maxX = Mth.ceil((double)boundingBox.maxX);
        int minY = Mth.floor((double)boundingBox.minY);
        int maxY = Mth.ceil((double)(boundingBox.minY + 0.001));
        int minZ = Mth.floor((double)boundingBox.minZ);
        int maxZ = Mth.ceil((double)boundingBox.maxZ);
        boolean inWater = false;
        this.waterLevel = Double.MIN_VALUE;
        BlockPos.MutableBlockPos pooledMutable = new BlockPos.MutableBlockPos();
        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                for (int z = minZ; z < maxZ; ++z) {
                    pooledMutable.set(x, y, z);
                    FluidState fluidState = this.level().getFluidState((BlockPos)pooledMutable);
                    if (!fluidState.is(FluidTags.WATER)) continue;
                    float waterLevel = (float)y + fluidState.getHeight((BlockGetter)this.level(), (BlockPos)pooledMutable);
                    this.waterLevel = Math.max((double)waterLevel, this.waterLevel);
                    inWater |= boundingBox.minY < (double)waterLevel;
                }
            }
        }
        return inWater;
    }

    @Nullable
    private State getUnderwaterState() {
        AABB axisalignedbb = this.getBoundingBox();
        double height = axisalignedbb.maxY + 0.001;
        int minX = Mth.floor((double)axisalignedbb.minX);
        int maxX = Mth.ceil((double)axisalignedbb.maxX);
        int minY = Mth.floor((double)axisalignedbb.maxY);
        int maxY = Mth.ceil((double)height);
        int minZ = Mth.floor((double)axisalignedbb.minZ);
        int maxZ = Mth.ceil((double)axisalignedbb.maxZ);
        boolean underWater = false;
        BlockPos.MutableBlockPos pooledMutable = new BlockPos.MutableBlockPos();
        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                for (int z = minZ; z < maxZ; ++z) {
                    pooledMutable.set(x, y, z);
                    FluidState fluidState = this.level().getFluidState((BlockPos)pooledMutable);
                    if (!fluidState.is(FluidTags.WATER) || !(height < (double)((float)pooledMutable.getY() + fluidState.getHeight((BlockGetter)this.level(), (BlockPos)pooledMutable)))) continue;
                    if (!fluidState.isSource()) {
                        return State.UNDER_FLOWING_WATER;
                    }
                    underWater = true;
                }
            }
        }
        return underWater ? State.UNDER_WATER : null;
    }

    protected State getState() {
        State state = this.getUnderwaterState();
        if (state != null) {
            return state;
        }
        if (this.checkInWater()) {
            return State.IN_WATER;
        }
        if (this.onGround()) {
            return State.ON_LAND;
        }
        return State.IN_AIR;
    }

    @Override
    protected void updateGroundState() {
        this.wheelsOnGround = this.getState() == State.IN_WATER || this.getState() == State.UNDER_WATER;
    }

    protected static enum State {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        ON_LAND,
        IN_AIR;

    }
}

