package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.fluid.SimpleFluidType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidType.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: MrCrayfish
 */
public class ModFluidTypes
{
    public static final DeferredRegister<FluidType> REGISTER = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Reference.MOD_ID);

    public static final RegistryObject<FluidType> FUELIUM = REGISTER.register("fuelium", () -> new SimpleFluidType(Properties.create().density(900).viscosity(900).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_EMPTY), new ResourceLocation(Reference.MOD_ID, "block/fuelium_still"), new ResourceLocation(Reference.MOD_ID, "block/fuelium_flowing")));
    public static final RegistryObject<FluidType> ENDER_SAP = REGISTER.register("ender_sap", () -> new SimpleFluidType(Properties.create().viscosity(3000).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_EMPTY), new ResourceLocation(Reference.MOD_ID, "block/blaze_juice_still"), new ResourceLocation(Reference.MOD_ID, "block/blaze_juice_flowing")));
    public static final RegistryObject<FluidType> BLAZE_JUICE = REGISTER.register("blaze_juice", () -> new SimpleFluidType(Properties.create().viscosity(800).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_EMPTY), new ResourceLocation(Reference.MOD_ID, "block/ender_sap_still"), new ResourceLocation(Reference.MOD_ID, "block/ender_sap_flowing")));
}