/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvents
 *  net.neoforged.neoforge.common.SoundActions
 *  net.neoforged.neoforge.fluids.FluidType
 *  net.neoforged.neoforge.fluids.FluidType$Properties
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  net.neoforged.neoforge.registries.NeoForgeRegistries$Keys
 */
package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.fluid.SimpleFluidType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluidTypes {
    public static final DeferredRegister<FluidType> REGISTER = DeferredRegister.create((ResourceKey)NeoForgeRegistries.Keys.FLUID_TYPES, (String)"vehicle");
    public static final DeferredHolder<FluidType, FluidType> FUELIUM = REGISTER.register("fuelium", () -> new SimpleFluidType(FluidType.Properties.create().density(900).viscosity(900).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_EMPTY), ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"block/fuelium_still"), ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"block/fuelium_flowing")));
    public static final DeferredHolder<FluidType, FluidType> ENDER_SAP = REGISTER.register("ender_sap", () -> new SimpleFluidType(FluidType.Properties.create().viscosity(3000).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_EMPTY), ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"block/blaze_juice_still"), ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"block/blaze_juice_flowing")));
    public static final DeferredHolder<FluidType, FluidType> BLAZE_JUICE = REGISTER.register("blaze_juice", () -> new SimpleFluidType(FluidType.Properties.create().viscosity(800).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_EMPTY), ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"block/ender_sap_still"), ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"block/ender_sap_flowing")));
}

