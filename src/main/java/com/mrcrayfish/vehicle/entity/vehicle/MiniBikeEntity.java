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
public class MiniBikeEntity extends MotorcycleEntity
{
    public MiniBikeEntity(EntityType<? extends MiniBikeEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.setMaxSpeed(18F);
        this.setFuelCapacity(15000F);
        this.setFuelConsumption(0.375F);
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_GO_KART_ENGINE.get();
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.5F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.8F;
    }

    @Override
    public boolean shouldShowEngineSmoke()
    {
        return true;
    }

    @Override
    public Vec3 getEngineSmokePosition()
    {
        return new Vec3(0, 0.55, 0);
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
    public boolean shouldRenderFuelPort()
    {
        return false;
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }
}
