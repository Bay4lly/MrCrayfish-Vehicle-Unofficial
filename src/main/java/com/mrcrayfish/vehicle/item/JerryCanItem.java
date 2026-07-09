/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
 */
package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.util.FluidUtils;
import com.mrcrayfish.vehicle.util.RenderUtil;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class JerryCanItem
extends Item {
    private final DecimalFormat FUEL_FORMAT = new DecimalFormat("0.#%");
    private final Supplier<Integer> capacitySupplier;

    public JerryCanItem(Supplier<Integer> capacity, Item.Properties properties) {
        super(properties);
        this.capacitySupplier = capacity;
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            tooltip.addAll(RenderUtil.lines((FormattedText)Component.translatable((String)(this.getDescriptionId() + ".info")), 150));
        } else {
            IFluidHandlerItem handler = (IFluidHandlerItem)stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (handler != null) {
                FluidStack fluidStack = handler.getFluidInTank(0);
                if (!fluidStack.isEmpty()) {
                    tooltip.add((Component)fluidStack.getDisplayName().copy().withStyle(ChatFormatting.BLUE));
                    tooltip.add((Component)Component.literal((String)(this.getCurrentFuel(stack) + " / " + String.valueOf(this.capacitySupplier.get()) + "mb")).withStyle(ChatFormatting.GRAY));
                } else {
                    tooltip.add((Component)Component.translatable((String)"item.vehicle.jerry_can.empty").withStyle(ChatFormatting.RED));
                }
            }
            tooltip.add((Component)Component.literal((String)(String.valueOf(ChatFormatting.YELLOW) + I18n.get((String)"vehicle.info_help", (Object[])new Object[0]))));
        }
    }

    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        IFluidHandlerItem itemHandler;
        IFluidHandler blockHandler;
        BlockEntity tileEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (tileEntity != null && context.getPlayer() != null && (blockHandler = (IFluidHandler)context.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, context.getClickedPos(), context.getClickedFace())) != null && (itemHandler = (IFluidHandlerItem)stack.getCapability(Capabilities.FluidHandler.ITEM)) != null) {
            if (context.getPlayer().isCrouching()) {
                FluidUtils.transferFluid(blockHandler, (IFluidHandler)itemHandler, this.getFillRate());
            } else {
                FluidUtils.transferFluid((IFluidHandler)itemHandler, blockHandler, this.getFillRate());
            }
            return InteractionResult.SUCCESS;
        }
        return super.onItemUseFirst(stack, context);
    }

    public int getCurrentFuel(ItemStack stack) {
        IFluidHandlerItem handler = (IFluidHandlerItem)stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler != null) {
            return handler.getFluidInTank(0).getAmount();
        }
        return 0;
    }

    public int getCapacity() {
        return this.capacitySupplier.get();
    }

    public int getFillRate() {
        return (Integer)Config.SERVER.jerryCanFillRate.get();
    }

    public boolean isBarVisible(ItemStack stack) {
        return this.getCurrentFuel(stack) < this.capacitySupplier.get();
    }

    public int getBarWidth(ItemStack stack) {
        return Math.round((float)this.getCurrentFuel(stack) * 13.0f / (float)this.capacitySupplier.get().intValue());
    }

    public int getBarColor(ItemStack stack) {
        if (this.getCurrentFuel(stack) <= 0) {
            return 0;
        }
        IFluidHandlerItem handler = (IFluidHandlerItem)stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler != null) {
            int color = IClientFluidTypeExtensions.of((Fluid)handler.getFluidInTank(0).getFluid()).getTintColor();
            if (color == -1) {
                color = FluidUtils.getAverageFluidColor(handler.getFluidInTank(0).getFluid());
            }
            return color;
        }
        return 0;
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}

