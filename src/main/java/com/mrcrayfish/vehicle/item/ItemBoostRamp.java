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

/**
 * Author: MrCrayfish
 */
public class ItemBoostRamp extends BlockItem
{
    public ItemBoostRamp(Block block)
    {
        super(block, new Item.Properties());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
        if(context.getClickedFace() == Direction.UP)
        {
            BlockState state = context.getLevel().getBlockState(context.getClickedPos());
            Block block = state.getBlock();
            if(block instanceof BoostRampBlock)
            {
                if(!state.getValue(BoostRampBlock.STACKED))
                {
                    context.getLevel().setBlockAndUpdate(context.getClickedPos(), block.defaultBlockState().setValue(BoostRampBlock.DIRECTION, state.getValue(BoostRampBlock.DIRECTION)).setValue(BoostRampBlock.STACKED, true));
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.onItemUseFirst(stack, context);
    }
}
