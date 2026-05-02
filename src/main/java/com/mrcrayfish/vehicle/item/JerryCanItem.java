package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.util.FluidUtils;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        if(Screen.hasShiftDown())
        {
            tooltip.addAll(RenderUtil.lines(Component.translatable(this.getDescriptionId() + ".info"), 150));
        }
        else if(worldIn != null)
        {
            stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler ->
            {
                FluidStack fluidStack = handler.getFluidInTank(0);
                if(!fluidStack.isEmpty())
                {
                    tooltip.add(Component.translatable(fluidStack.getTranslationKey()).withStyle(ChatFormatting.BLUE));
                    tooltip.add(Component.literal(this.getCurrentFuel(stack) + " / " + this.capacitySupplier.get() + "mb").withStyle(ChatFormatting.GRAY));
                }
                else
                {
                    tooltip.add(Component.translatable("item.vehicle.jerry_can.empty").withStyle(ChatFormatting.RED));
                }
            });
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
            LazyOptional<IFluidHandler> lazyOptional = tileEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, context.getClickedFace());
            if(lazyOptional.isPresent())
            {
                Optional<IFluidHandler> optional = lazyOptional.resolve();
                if(optional.isPresent())
                {
                    IFluidHandler source = optional.get();
                    Optional<IFluidHandlerItem> itemOptional = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
                    if(itemOptional.isPresent())
                    {
                        if(context.getPlayer().isCrouching())
                        {
                            FluidUtils.transferFluid(source, itemOptional.get(), this.getFillRate());
                        }
                        else
                        {
                            FluidUtils.transferFluid(itemOptional.get(), source, this.getFillRate());
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.onItemUseFirst(stack, context);
    }

    public int getCurrentFuel(ItemStack stack)
    {
        Optional<IFluidHandlerItem> optional = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
        return optional.map(handler -> handler.getFluidInTank(0).getAmount()).orElse(0);
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
        Optional<IFluidHandlerItem> optional = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
        return optional.map(handler -> {
            int color = IClientFluidTypeExtensions.of(handler.getFluidInTank(0).getFluid()).getTintColor();
            if(color == 0xFFFFFFFF) color = FluidUtils.getAverageFluidColor(handler.getFluidInTank(0).getFluid());
            return color;
        }).orElse(0);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new FluidHandlerItemStack(stack, this.capacitySupplier.get());
    }
}
