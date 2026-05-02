package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Author: MrCrayfish
 */
public class GoKartEntity extends LandVehicleEntity
{
    public GoKartEntity(EntityType<? extends GoKartEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.setMaxSpeed(20F);
        this.setMaxUpStep(0.625F);
        this.setFuelConsumption(0.5F);
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_GO_KART_ENGINE.get();
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.8F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.6F;
    }

    @Override
    public boolean shouldShowEngineSmoke()
    {
        return true;
    }

    @Override
    public Vec3 getEngineSmokePosition()
    {
        return new Vec3(0, 0.55, -0.9);
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
