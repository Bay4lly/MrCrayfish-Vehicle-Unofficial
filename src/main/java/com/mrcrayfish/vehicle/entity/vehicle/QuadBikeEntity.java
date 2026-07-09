/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class QuadBikeEntity
extends LandVehicleEntity {
    public QuadBikeEntity(EntityType<? extends QuadBikeEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxSpeed(15.0f);
        this.setFuelCapacity(20000.0f);
    }

    @Override
    public PoweredVehicleEntity.FuelPortType getFuelPortType() {
        return PoweredVehicleEntity.FuelPortType.SMALL;
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_QUAD_BIKE_ENGINE.get();
    }

    @Override
    public boolean canBeColored() {
        return true;
    }

    @Override
    public boolean canTowTrailer() {
        return true;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean shouldRenderEngine() {
        return true;
    }
}

