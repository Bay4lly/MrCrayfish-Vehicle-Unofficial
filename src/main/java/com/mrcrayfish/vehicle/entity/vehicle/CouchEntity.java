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

public class CouchEntity
extends LandVehicleEntity {
    public CouchEntity(EntityType<? extends CouchEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxSpeed(10.0f);
        this.entityData.set(COLOR, 11546150);
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_ATV_ENGINE.get();
    }

    @Override
    public boolean isLockable() {
        return false;
    }
}

