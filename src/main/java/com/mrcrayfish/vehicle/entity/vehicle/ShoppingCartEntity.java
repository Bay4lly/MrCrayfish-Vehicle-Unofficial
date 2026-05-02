package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.common.SurfaceHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public class ShoppingCartEntity extends LandVehicleEntity
{
    private Player pusher;

    public ShoppingCartEntity(EntityType<? extends ShoppingCartEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.setMaxTurnAngle(90);
        this.setTurnSensitivity(15);
        this.setFuelCapacity(0F);
        this.setFuelConsumption(0F);
        this.setRequiresFuel(false);
    }

    @Override
    public void tick()
    {
        if(this.pusher != null)
        {
            this.yRotO = this.getYRot();
            this.xo = this.getX();
            this.yo = this.getY();
            this.zo = this.getZ();
            float x = Mth.sin(-pusher.getYRot() * 0.017453292F) * 1.3F;
            float z = Mth.cos(-pusher.getYRot() * 0.017453292F) * 1.3F;
            this.setPos(pusher.getX() + x, pusher.getY(), pusher.getZ() + z);
            this.xOld = this.getX();
            this.yOld = this.getY();
            this.zOld = this.getZ();
            this.setYRot(pusher.getYRot());
        }
        else
        {
            super.tick();
        }
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return null;
    }

    @Override
    public boolean canDrive()
    {
        return this.isEnginePowered();
    }

    @Override
    protected void updateSpeed()
    {
        float surfaceModifier = SurfaceHelper.getSurfaceModifier(this);
        this.currentSpeed = this.getSpeed();

        PoweredVehicleEntity.AccelerationDirection acceleration = this.getAcceleration();

        if(acceleration != PoweredVehicleEntity.AccelerationDirection.CHARGING)
        {
            this.charging = false;
        }

        if(this.getControllingPassenger() != null)
        {
            if(this.canDrive())
            {
                boolean charging = this.canCharge() && acceleration == PoweredVehicleEntity.AccelerationDirection.CHARGING && Math.abs(this.currentSpeed) < 0.5F;
                if(acceleration == PoweredVehicleEntity.AccelerationDirection.FORWARD || (charging || this.charging))
                {
                    if(!this.charging)
                    {
                        this.charging = charging;
                    }
                    if(this.wheelsOnGround || this.canAccelerateInAir())
                    {
                        float maxSpeed = this.getActualMaxSpeed() * surfaceModifier * this.getPower();
                        if(this.currentSpeed < maxSpeed)
                        {
                            this.currentSpeed += this.getModifiedAccelerationSpeed();
                            if(this.currentSpeed > maxSpeed)
                            {
                                this.currentSpeed = maxSpeed;
                            }
                        }
                        if(this.currentSpeed > maxSpeed)
                        {
                            this.currentSpeed *= 0.975F;
                        }
                        return;
                    }
                }
                else if(acceleration == PoweredVehicleEntity.AccelerationDirection.REVERSE)
                {
                    if(this.wheelsOnGround || this.canAccelerateInAir())
                    {
                        float maxSpeed = -4.0F * surfaceModifier * this.getPower();
                        if(this.currentSpeed > maxSpeed)
                        {
                            this.currentSpeed -= this.getModifiedAccelerationSpeed();
                            if(this.currentSpeed < maxSpeed)
                            {
                                this.currentSpeed = maxSpeed;
                            }
                        }
                        if(this.currentSpeed < maxSpeed)
                        {
                            this.currentSpeed *= 0.975F;
                        }
                        return;
                    }
                }
            }

            if(this.wheelsOnGround || this.canAccelerateInAir())
            {
                this.currentSpeed *= 0.9;
            }
            else
            {
                this.currentSpeed *= 0.98;
            }
        }
        else if(this.wheelsOnGround)
        {
            this.currentSpeed *= 0.85;
        }
        else
        {
            this.currentSpeed *= 0.98;
        }
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }
}
