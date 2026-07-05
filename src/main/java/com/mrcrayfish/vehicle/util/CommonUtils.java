package com.mrcrayfish.vehicle.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class CommonUtils
{
    public static CompoundTag getOrCreateStackTag(ItemStack stack)
    {
        net.minecraft.world.item.component.CustomData data = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY);
        return data.copyTag();
    }

    public static void writeItemStackToTag(HolderLookup.Provider registries, CompoundTag compound, String key, ItemStack stack)
    {
        if(!stack.isEmpty())
        {
            compound.put(key, stack.save(registries, new CompoundTag()));
        }
    }

    public static ItemStack readItemStackFromTag(HolderLookup.Provider registries, CompoundTag compound, String key)
    {
        if(compound.contains(key, Tag.TAG_COMPOUND))
        {
            return ItemStack.parse(registries, compound.getCompound(key)).orElse(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    public static void sendInfoMessage(Player player, String message)
    {
        if(player instanceof ServerPlayer)
        {
            player.displayClientMessage(Component.translatable(message), true);
        }
    }

    public static boolean isMouseWithin(int mouseX, int mouseY, int x, int y, int width, int height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
