package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.init.ModDataComponents;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public interface IDyeable
{
    default boolean hasColor(ItemStack stack)
    {
        return stack.has(ModDataComponents.COLOR.get());
    }

    default int getColor(ItemStack stack)
    {
        return stack.getOrDefault(ModDataComponents.COLOR.get(), -1);
    }

    default void setColor(ItemStack stack, int color)
    {
        stack.set(ModDataComponents.COLOR.get(), color);
    }

    public static ItemStack dyeStack(ItemStack stack, List<DyeItem> dyes)
    {
        ItemStack resultStack = ItemStack.EMPTY;
        int[] combinedColors = new int[3];
        int maxColor = 0;
        int colorCount = 0;
        IDyeable dyeable = null;
        if(stack.getItem() instanceof IDyeable)
        {
            dyeable = (IDyeable) stack.getItem();
            resultStack = stack.copy();
            resultStack.setCount(1);
            if(dyeable.hasColor(stack))
            {
                int color = dyeable.getColor(resultStack);
                float red = (float) (color >> 16 & 255) / 255.0F;
                float green = (float) (color >> 8 & 255) / 255.0F;
                float blue = (float) (color & 255) / 255.0F;
                maxColor = (int) ((float) maxColor + Math.max(red, Math.max(green, blue)) * 255.0F);
                combinedColors[0] = (int) ((float) combinedColors[0] + red * 255.0F);
                combinedColors[1] = (int) ((float) combinedColors[1] + green * 255.0F);
                combinedColors[2] = (int) ((float) combinedColors[2] + blue * 255.0F);
                colorCount++;
            }

            for(DyeItem dyeitem : dyes)
            {
                int color = dyeitem.getDyeColor().getTextureDiffuseColor();
                int red = (color >> 16) & 255;
                int green = (color >> 8) & 255;
                int blue = color & 255;
                maxColor += Math.max(red, Math.max(green, blue));
                combinedColors[0] += red;
                combinedColors[1] += green;
                combinedColors[2] += blue;
                colorCount++;
            }
        }

        if(dyeable == null)
        {
            return ItemStack.EMPTY;
        }
        else
        {
            int red = combinedColors[0] / colorCount;
            int green = combinedColors[1] / colorCount;
            int blue = combinedColors[2] / colorCount;
            float averageColor = (float) maxColor / (float) colorCount;
            float maxValue = (float) Math.max(red, Math.max(green, blue));
            red = (int) ((float) red * averageColor / maxValue);
            green = (int) ((float) green * averageColor / maxValue);
            blue = (int) ((float) blue * averageColor / maxValue);
            int finalColor = (red << 8) + green;
            finalColor = (finalColor << 8) + blue;
            dyeable.setColor(resultStack, finalColor);
            return resultStack;
        }
    }

    static int getColorFromStack(ItemStack stack)
    {
        if(stack.getItem() instanceof IDyeable)
        {
            return ((IDyeable) stack.getItem()).getColor(stack);
        }
        return -1;
    }
}
