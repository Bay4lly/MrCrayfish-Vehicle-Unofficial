package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public class GolfCartEntity extends LandVehicleEntity
{
    public GolfCartEntity(EntityType<? extends GolfCartEntity> type, Level worldIn)
    {
        super(type, worldIn);
        //TODO figure out electric vehicles
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_BUMPER_CAR_ENGINE.get();
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.6F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.4F;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean canMountTrailer()
    {
        return false;
    }
}
