/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.storage.loot.LootContext
 *  net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction
 *  net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction$Builder
 *  net.minecraft.world.level.storage.loot.functions.LootItemFunction
 *  net.minecraft.world.level.storage.loot.functions.LootItemFunctionType
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParams
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 */
package com.mrcrayfish.vehicle.world.storage.loot.functions;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.vehicle.init.ModLootFunctions;
import com.mrcrayfish.vehicle.tileentity.IFluidTankWriter;
import com.mrcrayfish.vehicle.tileentity.TileFluidHandlerSynced;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class CopyFluidTanks
extends LootItemConditionalFunction {
    public static final MapCodec<CopyFluidTanks> CODEC = RecordCodecBuilder.mapCodec(instance -> CopyFluidTanks.commonFields(instance).apply(instance, CopyFluidTanks::new));

    private CopyFluidTanks(List<LootItemCondition> conditionsIn) {
        super(conditionsIn);
    }

    protected ItemStack run(ItemStack stack, LootContext context) {
        BlockEntity tileEntity;
        BlockState state = (BlockState)context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (state != null && stack.getItem() == state.getBlock().asItem() && (tileEntity = (BlockEntity)context.getParamOrNull(LootContextParams.BLOCK_ENTITY)) != null) {
            IFluidTankWriter writer;
            CompoundTag tileEntityTag = new CompoundTag();
            IFluidHandler handler = (IFluidHandler)context.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, tileEntity.getBlockPos(), tileEntity.getBlockState(), tileEntity, null);
            if (handler instanceof FluidTank) {
                FluidTank tank = (FluidTank)handler;
                if (!tank.isEmpty()) {
                    tank.writeToNBT((HolderLookup.Provider)context.getLevel().registryAccess(), tileEntityTag);
                }
            } else if (tileEntity instanceof TileFluidHandlerSynced) {
                FluidTank tank = ((TileFluidHandlerSynced)tileEntity).getFluidTank();
                if (!tank.isEmpty()) {
                    tank.writeToNBT((HolderLookup.Provider)context.getLevel().registryAccess(), tileEntityTag);
                }
            } else if (tileEntity instanceof IFluidTankWriter && !(writer = (IFluidTankWriter)tileEntity).areTanksEmpty()) {
                writer.writeTanks((HolderLookup.Provider)context.getLevel().registryAccess(), tileEntityTag);
            }
            if (!tileEntityTag.isEmpty()) {
                CustomData customData = (CustomData)stack.get(DataComponents.BLOCK_ENTITY_DATA);
                CompoundTag compound = customData != null ? customData.copyTag() : new CompoundTag();
                compound.merge(tileEntityTag);
                stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of((CompoundTag)compound));
            }
        }
        return stack;
    }

    public LootItemFunctionType getType() {
        return (LootItemFunctionType)ModLootFunctions.COPY_FLUID_TANKS.get();
    }

    public static Builder copyFluidTanks() {
        return new Builder();
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private Builder() {
        }

        protected Builder getThis() {
            return this;
        }

        public LootItemFunction build() {
            return new CopyFluidTanks(this.getConditions());
        }
    }
}

