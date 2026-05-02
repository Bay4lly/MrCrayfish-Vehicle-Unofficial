package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public abstract class ObjectEntityBlock extends BaseEntityBlock
{
    public ObjectEntityBlock(BlockBehaviour.Properties properties)
    {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter reader, List<Component> list, TooltipFlag flag)
    {
        appendHoverText(this, stack, reader, list, flag);
    }

    public static void appendHoverText(Block block, ItemStack stack, @Nullable BlockGetter reader, List<Component> list, TooltipFlag flag)
    {
        if(Screen.hasShiftDown())
        {
            list.addAll(RenderUtil.lines(Component.translatable(block.getDescriptionId() + ".info"), 150));
        }
        else
        {
            list.add(Component.translatable("vehicle.info_help").withStyle(ChatFormatting.YELLOW));
        }
    }
}
