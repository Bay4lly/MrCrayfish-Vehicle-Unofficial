package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.util.FluidUtils;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class JerryCanItem extends Item
{
    private final DecimalFormat FUEL_FORMAT = new DecimalFormat("0.#%");

    private final Supplier<Integer> capacitySupplier;

    public JerryCanItem(Supplier<Integer> capacity, Item.Properties properties)
    {
        super(properties);
        this.capacitySupplier = capacity;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn)
    {
        if(Screen.hasShiftDown())
        {
            tooltip.addAll(RenderUtil.lines(Component.translatable(this.getDescriptionId() + ".info"), 150));
        }
        else
        {
            IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if(handler != null)
            {
                FluidStack fluidStack = handler.getFluidInTank(0);
                if(!fluidStack.isEmpty())
                {
                    tooltip.add(fluidStack.getDisplayName().copy().withStyle(ChatFormatting.BLUE));
                    tooltip.add(Component.literal(this.getCurrentFuel(stack) + " / " + this.capacitySupplier.get() + "mb").withStyle(ChatFormatting.GRAY));
                }
                else
                {
                    tooltip.add(Component.translatable("item.vehicle.jerry_can.empty").withStyle(ChatFormatting.RED));
                }
            }
            tooltip.add(Component.literal(ChatFormatting.YELLOW + I18n.get("vehicle.info_help")));
        }
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
        // This is such ugly code
        BlockEntity tileEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if(tileEntity != null && context.getPlayer() != null)
        {
            IFluidHandler blockHandler = context.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, context.getClickedPos(), context.getClickedFace());
            if(blockHandler != null)
            {
                IFluidHandlerItem itemHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
                if(itemHandler != null)
                {
                    if(context.getPlayer().isCrouching())
                    {
                        FluidUtils.transferFluid(blockHandler, itemHandler, this.getFillRate());
                    }
                    else
                    {
                        FluidUtils.transferFluid(itemHandler, blockHandler, this.getFillRate());
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.onItemUseFirst(stack, context);
    }

    public int getCurrentFuel(ItemStack stack)
    {
        IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if(handler != null) return handler.getFluidInTank(0).getAmount();
        return 0;
    }

    public int getCapacity()
    {
        return this.capacitySupplier.get();
    }

    public int getFillRate()
    {
        return Config.SERVER.jerryCanFillRate.get();
    }

    @Override
    public boolean isBarVisible(ItemStack stack)
    {
        return this.getCurrentFuel(stack) < this.capacitySupplier.get();
    }

    @Override
    public int getBarWidth(ItemStack stack)
    {
        // See super method for magic constant
        return Math.round(this.getCurrentFuel(stack) * 13.0F / this.capacitySupplier.get());
    }

    @Override
    public int getBarColor(ItemStack stack)
    {
        if (this.getCurrentFuel(stack) <= 0)
        {
            return 0;
        }
        IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if(handler != null)
        {
            int color = IClientFluidTypeExtensions.of(handler.getFluidInTank(0).getFluid()).getTintColor();
            if(color == 0xFFFFFFFF) color = FluidUtils.getAverageFluidColor(handler.getFluidInTank(0).getFluid());
            return color;
        }
        return 0;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged;
    }
}
