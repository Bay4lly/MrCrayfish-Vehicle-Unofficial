package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class SprayCanItem extends Item implements IDyeable
{
    public SprayCanItem(Item.Properties properties)
    {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag)
    {
        if(Screen.hasShiftDown())
        {
            tooltip.addAll(RenderUtil.lines(Component.translatable(this.getDescriptionId() + ".info"), 150));
        }
        else
        {
            if(this.hasColor(stack))
            {
                tooltip.add(Component.literal(String.format("#%06X", this.getColor(stack))).withStyle(ChatFormatting.BLUE));
            }
            else
            {
                tooltip.add(Component.translatable(this.getDescriptionId() + ".empty").withStyle(ChatFormatting.RED));
            }
            tooltip.add(Component.translatable("vehicle.info_help").withStyle(ChatFormatting.YELLOW));
        }
    }

    public static CompoundTag getStackTag(ItemStack stack)
    {
        if (stack.getTag() == null)
        {
            stack.setTag(new CompoundTag());
        }
        if (stack.getItem() instanceof SprayCanItem)
        {
            SprayCanItem sprayCan = (SprayCanItem) stack.getItem();
            CompoundTag compound = stack.getTag();
            if (compound != null)
            {
                if (!compound.contains("RemainingSprays", Tag.TAG_INT))
                {
                    compound.putInt("RemainingSprays", sprayCan.getCapacity(stack));
                }
            }
        }
        return stack.getTag();
    }

    @Override
    public boolean isBarVisible(ItemStack stack)
    {
        CompoundTag compound = stack.getTag();
        if (compound != null && compound.contains("RemainingSprays", Tag.TAG_INT))
        {
            int remainingSprays = compound.getInt("RemainingSprays");
            return this.hasColor(stack) && remainingSprays < this.getCapacity(stack);
        }
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack)
    {
        CompoundTag compound = stack.getTag();
        if (compound != null && compound.contains("RemainingSprays", Tag.TAG_INT))
        {
            return Mth.clamp(1 - (compound.getInt("RemainingSprays") / this.getCapacity(stack)), 0, 1);
        }
        return 0;
    }

    public float getRemainingSprays(ItemStack stack)
    {
        CompoundTag compound = stack.getTag();
        if (compound != null && compound.contains("RemainingSprays", Tag.TAG_INT))
        {
            return compound.getInt("RemainingSprays") / (float) this.getCapacity(stack);
        }
        return 0.0F;
    }

    public int getCapacity(ItemStack stack)
    {
        CompoundTag compound = stack.getTag();
        if (compound != null && compound.contains("Capacity", Tag.TAG_INT))
        {
            return compound.getInt("Capacity");
        }
        return Config.SERVER.sprayCanCapacity.get();
    }

    public void refill(ItemStack stack)
    {
        CompoundTag compound = getStackTag(stack);
        compound.putInt("RemainingSprays", this.getCapacity(stack));
    }
}
