/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.level.material.FlowingFluid
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.fluid.BlazeJuice;
import com.mrcrayfish.vehicle.fluid.EnderSap;
import com.mrcrayfish.vehicle.fluid.Fuelium;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFluids {
    public static final DeferredRegister<Fluid> REGISTER = DeferredRegister.create((Registry)BuiltInRegistries.FLUID, (String)"vehicle");
    public static final DeferredHolder<Fluid, Fluid> FUELIUM = REGISTER.register("fuelium", Fuelium.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_FUELIUM = REGISTER.register("flowing_fuelium", Fuelium.Flowing::new);
    public static final DeferredHolder<Fluid, Fluid> ENDER_SAP = REGISTER.register("ender_sap", EnderSap.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_ENDER_SAP = REGISTER.register("flowing_ender_sap", EnderSap.Flowing::new);
    public static final DeferredHolder<Fluid, Fluid> BLAZE_JUICE = REGISTER.register("blaze_juice", BlazeJuice.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_BLAZE_JUICE = REGISTER.register("flowing_blaze_juice", BlazeJuice.Flowing::new);
}

