/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.item.EngineItem;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;

public interface IEngineTier {
    default public float getAccelerationMultiplier() {
        return 1.0f;
    }

    default public float getAdditionalMaxSpeed() {
        return 0.0f;
    }

    public static Optional<IEngineTier> fromStack(ItemStack stack) {
        if (stack.getItem() instanceof EngineItem) {
            return Optional.of(((EngineItem)stack.getItem()).getEngineTier());
        }
        return Optional.empty();
    }
}

