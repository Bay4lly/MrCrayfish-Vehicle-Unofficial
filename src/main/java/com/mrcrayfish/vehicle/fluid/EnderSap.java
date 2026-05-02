package com.mrcrayfish.vehicle.fluid;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModFluidTypes;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

/**
 * Author: MrCrayfish
 */
public abstract class EnderSap extends ForgeFlowingFluid
{
    public EnderSap()
    {
        super(new Properties(ModFluidTypes.ENDER_SAP, ModFluids.ENDER_SAP, ModFluids.FLOWING_ENDER_SAP).block(ModBlocks.ENDER_SAP));
    }

    @Override
    public Item getBucket()
    {
        return ModItems.ENDER_SAP_BUCKET.get();
    }

    public static class Source extends EnderSap
    {
        @Override
        public boolean isSource(FluidState state)
        {
            return true;
        }

        @Override
        public int getAmount(FluidState state)
        {
            return 8;
        }
    }

    public static class Flowing extends EnderSap
    {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder)
        {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state)
        {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state)
        {
            return false;
        }
    }
}
