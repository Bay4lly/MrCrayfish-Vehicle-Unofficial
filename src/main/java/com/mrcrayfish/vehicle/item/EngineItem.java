/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.entity.IEngineTier;
import com.mrcrayfish.vehicle.entity.IEngineType;
import com.mrcrayfish.vehicle.item.PartItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EngineItem
extends PartItem {
    private IEngineType type;
    private IEngineTier tier;

    public EngineItem(IEngineType type, IEngineTier tier, Item.Properties properties) {
        super(properties);
        VehicleRegistry.registerEngine(type, tier, this);
        this.type = type;
        this.tier = tier;
    }

    public IEngineType getEngineType() {
        return this.type;
    }

    public IEngineTier getEngineTier() {
        return this.tier;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add((Component)Component.translatable((String)"vehicle.engine_info.acceleration").append(": ").withStyle(ChatFormatting.YELLOW).append((Component)Component.literal((String)(this.tier.getAccelerationMultiplier() + "x")).withStyle(ChatFormatting.GRAY)));
        tooltip.add((Component)Component.translatable((String)"vehicle.engine_info.additional_max_speed").append(": ").withStyle(ChatFormatting.YELLOW).append((Component)Component.literal((String)(this.tier.getAdditionalMaxSpeed() + "bps")).withStyle(ChatFormatting.GRAY)));
    }
}

