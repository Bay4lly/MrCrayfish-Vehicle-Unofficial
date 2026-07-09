/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.neoforged.neoforge.fluids.BaseFlowingFluid
 *  net.neoforged.neoforge.fluids.BaseFlowingFluid$Properties
 */
package com.mrcrayfish.vehicle.fluid;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModFluidTypes;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class Fuelium
extends BaseFlowingFluid {
    public Fuelium() {
        super(new BaseFlowingFluid.Properties(ModFluidTypes.FUELIUM, ModFluids.FUELIUM, ModFluids.FLOWING_FUELIUM).block(ModBlocks.FUELIUM));
    }

    public Item getBucket() {
        return (Item)ModItems.FUELIUM_BUCKET.get();
    }

    public static class Flowing
    extends Fuelium {
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(new Property[]{LEVEL});
        }

        public int getAmount(FluidState state) {
            return (Integer)state.getValue((Property)LEVEL);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source
    extends Fuelium {
        public boolean isSource(FluidState state) {
            return true;
        }

        public int getAmount(FluidState state) {
            return 8;
        }
    }
}

