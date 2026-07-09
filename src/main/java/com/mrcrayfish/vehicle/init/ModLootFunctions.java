/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.storage.loot.functions.LootItemFunctionType
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.world.storage.loot.functions.CopyFluidTanks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModLootFunctions {
    private static final DeferredRegister<LootItemFunctionType<?>> REGISTER = DeferredRegister.create((ResourceKey)Registries.LOOT_FUNCTION_TYPE, (String)"vehicle");
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<CopyFluidTanks>> COPY_FLUID_TANKS = REGISTER.register("copy_fluid_tanks", () -> new LootItemFunctionType(CopyFluidTanks.CODEC));

    public static void init(IEventBus modBus) {
        REGISTER.register(modBus);
    }
}

