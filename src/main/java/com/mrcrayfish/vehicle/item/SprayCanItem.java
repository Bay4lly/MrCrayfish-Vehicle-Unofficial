/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.component.CustomData
 */
package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.item.IDyeable;
import com.mrcrayfish.vehicle.util.RenderUtil;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

public class SprayCanItem
extends Item
implements IDyeable {
    public SprayCanItem(Item.Properties properties) {
        super(properties);
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.addAll(RenderUtil.lines((FormattedText)Component.translatable((String)(this.getDescriptionId() + ".info")), 150));
        } else {
            if (this.hasColor(stack)) {
                tooltip.add((Component)Component.literal((String)String.format("#%06X", this.getColor(stack))).withStyle(ChatFormatting.BLUE));
            } else {
                tooltip.add((Component)Component.translatable((String)(this.getDescriptionId() + ".empty")).withStyle(ChatFormatting.RED));
            }
            tooltip.add((Component)Component.translatable((String)"vehicle.info_help").withStyle(ChatFormatting.YELLOW));
        }
    }

    public static CompoundTag getStackTag(ItemStack stack) {
        CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY);
        CompoundTag compound = data.copyTag();
        Item item = stack.getItem();
        if (item instanceof SprayCanItem) {
            SprayCanItem sprayCan = (SprayCanItem)item;
            if (!compound.contains("RemainingSprays", 3)) {
                compound.putInt("RemainingSprays", sprayCan.getCapacity(stack));
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of((CompoundTag)compound));
            }
        }
        return compound;
    }

    public boolean isBarVisible(ItemStack stack) {
        CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY);
        CompoundTag compound = data.copyTag();
        if (compound.contains("RemainingSprays", 3)) {
            int remainingSprays = compound.getInt("RemainingSprays");
            return this.hasColor(stack) && remainingSprays < this.getCapacity(stack);
        }
        return true;
    }

    public int getBarWidth(ItemStack stack) {
        CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY);
        CompoundTag compound = data.copyTag();
        if (compound.contains("RemainingSprays", 3)) {
            return Math.round(13.0f * ((float)compound.getInt("RemainingSprays") / (float)this.getCapacity(stack)));
        }
        return 0;
    }

    public float getRemainingSprays(ItemStack stack) {
        CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY);
        CompoundTag compound = data.copyTag();
        if (compound.contains("RemainingSprays", 3)) {
            return (float)compound.getInt("RemainingSprays") / (float)this.getCapacity(stack);
        }
        return 0.0f;
    }

    public int getCapacity(ItemStack stack) {
        CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY);
        CompoundTag compound = data.copyTag();
        if (compound.contains("Capacity", 3)) {
            return compound.getInt("Capacity");
        }
        return (Integer)Config.SERVER.sprayCanCapacity.get();
    }

    public void refill(ItemStack stack) {
        CompoundTag compound = SprayCanItem.getStackTag(stack);
        compound.putInt("RemainingSprays", this.getCapacity(stack));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of((CompoundTag)compound));
    }
}

