package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.MotorcycleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Author: MrCrayfish
 */
public class DirtBikeEntity extends MotorcycleEntity
{
    public DirtBikeEntity(EntityType<? extends DirtBikeEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.setMaxSpeed(18F);
        this.setMaxTurnAngle(35);
        this.setFuelCapacity(20000F);
        this.setFuelConsumption(0.35F);
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_DIRT_BIKE_ENGINE.get();
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.85F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.5F;
    }

    @Override
    public boolean shouldShowEngineSmoke()
    {
        return true;
    }

    @Override
    public Vec3 getEngineSmokePosition()
    {
        return new Vec3(-0.0625, 1.25, -1);
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean shouldRenderEngine()
    {
        return true;
    }

    @Override
    public FuelPortType getFuelPortType()
    {
        return FuelPortType.SMALL;
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }
}
