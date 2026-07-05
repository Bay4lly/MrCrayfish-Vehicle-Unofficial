package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.world.storage.loot.functions.CopyFluidTanks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Author: MrCrayfish
 */
public class ModLootFunctions
{
    private static final DeferredRegister<LootItemFunctionType<?>> REGISTER =
        DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, Reference.MOD_ID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<CopyFluidTanks>> COPY_FLUID_TANKS =
        REGISTER.register("copy_fluid_tanks", () -> new LootItemFunctionType<>(CopyFluidTanks.CODEC));

    public static void init(IEventBus modBus)
    {
        REGISTER.register(modBus);
    }
}
