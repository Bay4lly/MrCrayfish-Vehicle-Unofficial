/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.block.BoostRampBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class ItemBoostRamp
extends BlockItem {
    public ItemBoostRamp(Block block) {
        super(block, new Item.Properties());
    }

    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        BlockState state;
        Block block;
        if (context.getClickedFace() == Direction.UP && (block = (state = context.getLevel().getBlockState(context.getClickedPos())).getBlock()) instanceof BoostRampBlock) {
            if (!((Boolean)state.getValue((Property)BoostRampBlock.STACKED)).booleanValue()) {
                context.getLevel().setBlockAndUpdate(context.getClickedPos(), (BlockState)block.defaultBlockState().setValue(BoostRampBlock.DIRECTION, ((state.getValue(BoostRampBlock.DIRECTION)))).setValue(BoostRampBlock.STACKED, Boolean.valueOf(true)));
            }
            return InteractionResult.SUCCESS;
        }
        return super.onItemUseFirst(stack, context);
    }
}

