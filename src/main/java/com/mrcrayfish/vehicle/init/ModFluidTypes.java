package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.fluid.SimpleFluidType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidType.Properties;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Author: MrCrayfish
 */
public class ModFluidTypes
{
    public static final DeferredRegister<FluidType> REGISTER = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Reference.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> FUELIUM = REGISTER.register("fuelium", () -> new SimpleFluidType(Properties.create().density(900).viscosity(900).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_EMPTY), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fuelium_still"), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fuelium_flowing")));
    public static final DeferredHolder<FluidType, FluidType> ENDER_SAP = REGISTER.register("ender_sap", () -> new SimpleFluidType(Properties.create().viscosity(3000).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_EMPTY), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/blaze_juice_still"), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/blaze_juice_flowing")));
    public static final DeferredHolder<FluidType, FluidType> BLAZE_JUICE = REGISTER.register("blaze_juice", () -> new SimpleFluidType(Properties.create().viscosity(800).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_EMPTY), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/ender_sap_still"), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/ender_sap_flowing")));
}