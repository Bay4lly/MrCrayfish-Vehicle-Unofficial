/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.world.item.DyeItem
 *  net.minecraft.world.item.ItemStack
 */
package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.init.ModDataComponents;
import java.util.List;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

public interface IDyeable {
    default public boolean hasColor(ItemStack stack) {
        return stack.has((DataComponentType)ModDataComponents.COLOR.get());
    }

    default public int getColor(ItemStack stack) {
        return (Integer)stack.getOrDefault(ModDataComponents.COLOR.get(), -1);
    }

    default public void setColor(ItemStack stack, int color) {
        stack.set((DataComponentType)ModDataComponents.COLOR.get(), color);
    }

    public static ItemStack dyeStack(ItemStack stack, List<DyeItem> dyes) {
        ItemStack resultStack = ItemStack.EMPTY;
        int[] combinedColors = new int[3];
        int maxColor = 0;
        int colorCount = 0;
        IDyeable dyeable = null;
        if (stack.getItem() instanceof IDyeable) {
            dyeable = (IDyeable)stack.getItem();
            resultStack = stack.copy();
            resultStack.setCount(1);
            if (dyeable.hasColor(stack)) {
                int color = dyeable.getColor(resultStack);
                float red = (float)(color >> 16 & 0xFF) / 255.0f;
                float green = (float)(color >> 8 & 0xFF) / 255.0f;
                float blue = (float)(color & 0xFF) / 255.0f;
                maxColor = (int)((float)maxColor + Math.max(red, Math.max(green, blue)) * 255.0f);
                combinedColors[0] = (int)((float)combinedColors[0] + red * 255.0f);
                combinedColors[1] = (int)((float)combinedColors[1] + green * 255.0f);
                combinedColors[2] = (int)((float)combinedColors[2] + blue * 255.0f);
                ++colorCount;
            }
            for (DyeItem dyeitem : dyes) {
                int color = dyeitem.getDyeColor().getTextureDiffuseColor();
                int red = color >> 16 & 0xFF;
                int green = color >> 8 & 0xFF;
                int blue = color & 0xFF;
                maxColor += Math.max(red, Math.max(green, blue));
                combinedColors[0] = combinedColors[0] + red;
                combinedColors[1] = combinedColors[1] + green;
                combinedColors[2] = combinedColors[2] + blue;
                ++colorCount;
            }
        }
        if (dyeable == null) {
            return ItemStack.EMPTY;
        }
        int red = combinedColors[0] / colorCount;
        int green = combinedColors[1] / colorCount;
        int blue = combinedColors[2] / colorCount;
        float averageColor = (float)maxColor / (float)colorCount;
        float maxValue = Math.max(red, Math.max(green, blue));
        red = (int)((float)red * averageColor / maxValue);
        green = (int)((float)green * averageColor / maxValue);
        blue = (int)((float)blue * averageColor / maxValue);
        int finalColor = (red << 8) + green;
        finalColor = (finalColor << 8) + blue;
        dyeable.setColor(resultStack, finalColor);
        return resultStack;
    }

    public static int getColorFromStack(ItemStack stack) {
        if (stack.getItem() instanceof IDyeable) {
            return ((IDyeable)stack.getItem()).getColor(stack);
        }
        return -1;
    }
}

