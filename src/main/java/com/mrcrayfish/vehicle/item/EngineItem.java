package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.entity.IEngineTier;
import com.mrcrayfish.vehicle.entity.IEngineType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class EngineItem extends PartItem
{
    private IEngineType type;
    private IEngineTier tier;

    public EngineItem(IEngineType type, IEngineTier tier, Item.Properties properties)
    {
        super(properties);
        VehicleRegistry.registerEngine(type, tier, this);
        this.type = type;
        this.tier = tier;
    }

    public IEngineType getEngineType()
    {
        return this.type;
    }

    public IEngineTier getEngineTier()
    {
        return this.tier;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        tooltip.add(Component.translatable("vehicle.engine_info.acceleration").append(": ").withStyle(ChatFormatting.YELLOW).append(Component.literal(this.tier.getAccelerationMultiplier() + "x").withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.translatable("vehicle.engine_info.additional_max_speed").append(": ").withStyle(ChatFormatting.YELLOW).append(Component.literal((this.tier.getAdditionalMaxSpeed()) + "bps").withStyle(ChatFormatting.GRAY)));
    }
}
