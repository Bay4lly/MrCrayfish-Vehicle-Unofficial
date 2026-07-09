/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 */
package com.mrcrayfish.vehicle.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class CommonUtils {
    public static CompoundTag getOrCreateStackTag(ItemStack stack) {
        CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY);
        return data.copyTag();
    }

    public static void writeItemStackToTag(HolderLookup.Provider registries, CompoundTag compound, String key, ItemStack stack) {
        if (!stack.isEmpty()) {
            compound.put(key, stack.save(registries, (Tag)new CompoundTag()));
        }
    }

    public static ItemStack readItemStackFromTag(HolderLookup.Provider registries, CompoundTag compound, String key) {
        if (compound.contains(key, 10)) {
            return ItemStack.parse((HolderLookup.Provider)registries, (Tag)compound.getCompound(key)).orElse(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    public static void sendInfoMessage(Player player, String message) {
        if (player instanceof ServerPlayer) {
            player.displayClientMessage((Component)Component.translatable((String)message), true);
        }
    }

    public static boolean isMouseWithin(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}

