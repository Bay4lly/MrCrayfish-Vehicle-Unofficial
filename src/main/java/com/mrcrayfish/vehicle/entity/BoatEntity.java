package com.mrcrayfish.vehicle.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class BoatEntity extends PoweredVehicleEntity
{
    protected State state;
    protected State previousState;
    private double waterLevel;

    public BoatEntity(EntityType<?> entityType, Level worldIn)
    {
        super(entityType, worldIn);
        this.setMaxTurnAngle(65);
    }

    @Override
    public boolean canChangeWheels()
    {
        return false;
    }

    @Override
    public void updateVehicleMotion()
    {
        if(this.state == State.IN_WATER || this.state == State.UNDER_WATER)
        {
            if(this.state == State.UNDER_WATER)
            {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.08, 0));
            }
            else
            {
                double floatingY = ((this.waterLevel - 0.35D + (0.25D * Math.min(1.0F, getNormalSpeed())) - this.getY())) / (double) this.getBbHeight();
                this.setDeltaMovement(this.getDeltaMovement().add(0, floatingY * 0.05, 0));
                if(Math.abs(floatingY) < 0.1 && this.getDeltaMovement().y > 0 && Math.abs(this.getDeltaMovement().y) < 0.1)
                {
                    this.setPos(this.getX(), this.waterLevel - 0.35 + (0.25 * Math.min(1.0F, getNormalSpeed())), this.getZ());
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
                }
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.75, 1.0));
            }

            float f1 = Mth.sin(this.getYRot() * 0.017453292F) / 20F;
            float f2 = Mth.cos(this.getYRot() * 0.017453292F) / 20F;
            this.vehicleMotionX = (-currentSpeed * f1);
            this.vehicleMotionZ = (currentSpeed * f2);
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 1.0, 0.5));
        }
        else if(this.state == State.IN_AIR)
        {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08, 0));
            if(this.previousState == State.UNDER_WATER || this.previousState == State.IN_WATER)
            {
                this.setDeltaMovement(new Vec3(this.vehicleMotionX, this.getDeltaMovement().y, this.vehicleMotionZ));
                this.vehicleMotionX = 0;
                this.vehicleMotionZ = 0;
            }
        }
        else
        {
            this.vehicleMotionX *= 0.75F;
            this.vehicleMotionZ *= 0.75F;
        }
    }

    @Override
    public void updateVehicle()
    {
        this.previousState = this.state;
        this.state = this.getState();
        if( this.state == State.IN_AIR)
        {
            this.deltaYaw *= 2;
        }
    }

    private boolean checkInWater()
    {
        AABB boundingBox = this.getBoundingBox();
        int minX = Mth.floor(boundingBox.minX);
        int maxX = Mth.ceil(boundingBox.maxX);
        int minY = Mth.floor(boundingBox.minY);
        int maxY = Mth.ceil(boundingBox.minY + 0.001D);
        int minZ = Mth.floor(boundingBox.minZ);
        int maxZ = Mth.ceil(boundingBox.maxZ);
        boolean inWater = false;
        this.waterLevel = Double.MIN_VALUE;

        BlockPos.MutableBlockPos pooledMutable = new BlockPos.MutableBlockPos();
        for(int x = minX; x < maxX; x++)
        {
            for(int y = minY; y < maxY; y++)
            {
                for(int z = minZ; z < maxZ; z++)
                {
                    pooledMutable.set(x, y, z);
                    FluidState fluidState = this.level().getFluidState(pooledMutable);
                    if(fluidState.is(FluidTags.WATER))
                    {
                        float waterLevel = (float) y + fluidState.getHeight(this.level(), pooledMutable);
                        this.waterLevel = Math.max((double) waterLevel, this.waterLevel);
                        inWater |= boundingBox.minY < (double) waterLevel;
                    }
                }
            }
        }

        return inWater;
    }

    @Nullable
    private State getUnderwaterState()
    {
        AABB axisalignedbb = this.getBoundingBox();
        double height = axisalignedbb.maxY + 0.001D;
        int minX = Mth.floor(axisalignedbb.minX);
        int maxX = Mth.ceil(axisalignedbb.maxX);
        int minY = Mth.floor(axisalignedbb.maxY);
        int maxY = Mth.ceil(height);
        int minZ = Mth.floor(axisalignedbb.minZ);
        int maxZ = Mth.ceil(axisalignedbb.maxZ);
        boolean underWater = false;

        BlockPos.MutableBlockPos pooledMutable = new BlockPos.MutableBlockPos();
        for(int x = minX; x < maxX; x++)
        {
            for(int y = minY; y < maxY; y++)
            {
                for(int z = minZ; z < maxZ; z++)
                {
                    pooledMutable.set(x, y, z);
                    FluidState fluidState = this.level().getFluidState(pooledMutable);
                    if(fluidState.is(FluidTags.WATER) && height < (double) ((float) pooledMutable.getY() + fluidState.getHeight(this.level(), pooledMutable)))
                    {
                        if(!fluidState.isSource())
                        {
                            return State.UNDER_FLOWING_WATER;
                        }
                        underWater = true;
                    }
                }
            }
        }

        return underWater ? State.UNDER_WATER : null;
    }

    protected State getState()
    {
        State state = this.getUnderwaterState();
        if(state != null)
        {
            return state;
        }
        else if(this.checkInWater())
        {
            return State.IN_WATER;
        }
        else if(this.onGround())
        {
            return State.ON_LAND;
        }
        else
        {
            return State.IN_AIR;
        }
    }

    @Override
    protected void updateGroundState()
    {
        this.wheelsOnGround = this.getState() == State.IN_WATER || this.getState() == State.UNDER_WATER;
    }

    protected enum State
    {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        ON_LAND,
        IN_AIR;
    }
}
