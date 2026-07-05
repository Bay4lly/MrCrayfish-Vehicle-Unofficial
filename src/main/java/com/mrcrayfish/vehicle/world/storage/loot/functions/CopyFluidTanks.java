package com.mrcrayfish.vehicle.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.init.ModLootFunctions;
import com.mrcrayfish.vehicle.tileentity.IFluidTankWriter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import com.mrcrayfish.vehicle.tileentity.TileFluidHandlerSynced;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

/**
 * Author: MrCrayfish
 */
public class CopyFluidTanks extends LootItemConditionalFunction
{
    public static final com.mojang.serialization.MapCodec<CopyFluidTanks> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(instance ->
        commonFields(instance).apply(instance, CopyFluidTanks::new)
    );

    private CopyFluidTanks(java.util.List<LootItemCondition> conditionsIn)
    {
        super(conditionsIn);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context)
    {
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if(state != null && stack.getItem() == state.getBlock().asItem())
        {
            BlockEntity tileEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
            if(tileEntity != null)
            {
                CompoundTag tileEntityTag = new CompoundTag();
                IFluidHandler handler = context.getLevel().getCapability(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK, tileEntity.getBlockPos(), tileEntity.getBlockState(), tileEntity, null);
                if(handler instanceof FluidTank)
                {
                    FluidTank tank = (FluidTank) handler;
                    if(!tank.isEmpty())
                    {
                        tank.writeToNBT(context.getLevel().registryAccess(), tileEntityTag);
                    }
                }
                else if(tileEntity instanceof TileFluidHandlerSynced)
                {
                    FluidTank tank = ((TileFluidHandlerSynced) tileEntity).getFluidTank();
                    if(!tank.isEmpty())
                    {
                        tank.writeToNBT(context.getLevel().registryAccess(), tileEntityTag);
                    }
                }
                else if(tileEntity instanceof IFluidTankWriter)
                {
                    IFluidTankWriter writer = (IFluidTankWriter) tileEntity;
                    if(!writer.areTanksEmpty())
                    {
                        writer.writeTanks(context.getLevel().registryAccess(), tileEntityTag);
                    }
                }

                if(!tileEntityTag.isEmpty())
                {
                    net.minecraft.world.item.component.CustomData customData = stack.get(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA);
                    CompoundTag compound = customData != null ? customData.copyTag() : new CompoundTag();
                    compound.merge(tileEntityTag);
                    stack.set(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA, net.minecraft.world.item.component.CustomData.of(compound));
                }
            }
        }
        return stack;
    }

    @Override
    public LootItemFunctionType getType()
    {
        return ModLootFunctions.COPY_FLUID_TANKS.get();
    }

    public static CopyFluidTanks.Builder copyFluidTanks()
    {
        return new CopyFluidTanks.Builder();
    }

    public static class Builder extends LootItemConditionalFunction.Builder<CopyFluidTanks.Builder>
    {
        private Builder() {}

        protected CopyFluidTanks.Builder getThis()
        {
            return this;
        }

        public LootItemFunction build()
        {
            return new CopyFluidTanks(this.getConditions());
        }
    }
}
