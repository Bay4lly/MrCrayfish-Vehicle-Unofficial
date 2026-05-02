package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public class ATVEntity extends LandVehicleEntity
{
    public ATVEntity(EntityType<? extends ATVEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.setMaxSpeed(15);
        this.setFuelCapacity(20000F);
    }

    @Override
    public FuelPortType getFuelPortType()
    {
        return FuelPortType.SMALL;
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_ATV_ENGINE.get();
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean canTowTrailer()
    {
        return true;
    }
}
