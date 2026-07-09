/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class GolfCartEntity
extends LandVehicleEntity {
    public GolfCartEntity(EntityType<? extends GolfCartEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_BUMPER_CAR_ENGINE.get();
    }

    @Override
    public float getMinEnginePitch() {
        return 0.6f;
    }

    @Override
    public float getMaxEnginePitch() {
        return 1.4f;
    }

    @Override
    public boolean canBeColored() {
        return true;
    }

    @Override
    public boolean canMountTrailer() {
        return false;
    }
}

