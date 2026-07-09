/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.item.WheelItem;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;

public interface IWheelType {
    default public float getRoadMultiplier() {
        return 1.0f;
    }

    default public float getDirtMultiplier() {
        return 1.0f;
    }

    default public float getSnowMultiplier() {
        return 1.0f;
    }

    default public void applyPhysics(PoweredVehicleEntity vehicle) {
    }

    public static Optional<IWheelType> fromStack(ItemStack stack) {
        if (stack.getItem() instanceof WheelItem) {
            return Optional.of(((WheelItem)stack.getItem()).getWheelType());
        }
        return Optional.empty();
    }
}

