package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.world.storage.loot.functions.CopyFluidTanks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

/**
 * Author: MrCrayfish
 */
public class ModLootFunctions
{
    public static final LootItemFunctionType COPY_FLUID_TANKS = register("copy_fluid_tanks", new CopyFluidTanks.Serializer());

    // Load class
    public static void init()
    {
    }

    private static LootItemFunctionType register(String id, Serializer<? extends LootItemFunction> serializer)
    {
        return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, new ResourceLocation(Reference.MOD_ID, id), new LootItemFunctionType(serializer));
    }
}