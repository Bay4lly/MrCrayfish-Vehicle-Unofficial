package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public class DuneBuggyEntity extends LandVehicleEntity
{
    public DuneBuggyEntity(EntityType<? extends DuneBuggyEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.setMaxSpeed(10);
        this.setMaxUpStep(0.5F);
        this.setFuelCapacity(5000F);
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.set(COLOR, 0xF2B116);
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_BUMPER_CAR_ENGINE.get();
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }
}
