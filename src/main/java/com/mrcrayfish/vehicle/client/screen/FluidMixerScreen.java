/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.mrcrayfish.vehicle.client.screen;

import com.google.common.collect.Lists;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.inventory.container.FluidMixerContainer;
import com.mrcrayfish.vehicle.tileentity.FluidMixerTileEntity;
import com.mrcrayfish.vehicle.util.FluidUtils;
import com.mrcrayfish.vehicle.util.RenderUtil;
import java.util.Arrays;
import java.util.Collections;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidMixerScreen
extends AbstractContainerScreen<FluidMixerContainer> {
    private static final ResourceLocation GUI = ResourceLocation.parse((String)"vehicle:textures/gui/fluid_mixer.png");
    private Inventory playerInventory;
    private FluidMixerTileEntity fluidMixerTileEntity;

    public FluidMixerScreen(FluidMixerContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.playerInventory = playerInventory;
        this.fluidMixerTileEntity = container.getFluidExtractor();
        this.imageWidth = 176;
        this.imageHeight = 180;
    }

    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        FluidStack stack;
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        if (this.fluidMixerTileEntity.getBlazeFluidStack() != null) {
            stack = this.fluidMixerTileEntity.getBlazeFluidStack();
            if (this.isMouseWithinRegion(startX + 33, startY + 17, 16, 29, mouseX, mouseY)) {
                if (stack.getAmount() > 0) {
                    matrixStack.renderTooltip(this.font, Lists.transform(Arrays.asList(Component.translatable((String)stack.getDisplayName().getString()), Component.literal((String)(ChatFormatting.GRAY.toString() + this.fluidMixerTileEntity.getBlazeLevel() + "/" + this.fluidMixerTileEntity.getBlazeTank().getCapacity() + " mB"))), Component::getVisualOrderText), mouseX, mouseY);
                } else {
                    matrixStack.renderTooltip(this.font, Lists.transform(Collections.singletonList(Component.literal((String)"No Fluid")), Component::getVisualOrderText), mouseX, mouseY);
                }
            }
        }
        if (this.fluidMixerTileEntity.getEnderSapFluidStack() != null) {
            stack = this.fluidMixerTileEntity.getEnderSapFluidStack();
            if (this.isMouseWithinRegion(startX + 33, startY + 52, 16, 29, mouseX, mouseY)) {
                if (stack.getAmount() > 0) {
                    matrixStack.renderTooltip(this.font, Lists.transform(Arrays.asList(Component.translatable((String)stack.getDisplayName().getString()), Component.literal((String)(ChatFormatting.GRAY.toString() + this.fluidMixerTileEntity.getEnderSapLevel() + "/" + this.fluidMixerTileEntity.getEnderSapTank().getCapacity() + " mB"))), Component::getVisualOrderText), mouseX, mouseY);
                } else {
                    matrixStack.renderTooltip(this.font, Lists.transform(Collections.singletonList(Component.literal((String)"No Fluid")), Component::getVisualOrderText), mouseX, mouseY);
                }
            }
        }
        if (this.fluidMixerTileEntity.getFueliumFluidStack() != null) {
            stack = this.fluidMixerTileEntity.getFueliumFluidStack();
            if (this.isMouseWithinRegion(startX + 151, startY + 20, 16, 59, mouseX, mouseY)) {
                if (stack.getAmount() > 0) {
                    matrixStack.renderTooltip(this.font, Lists.transform(Arrays.asList(Component.literal((String)stack.getDisplayName().getString()), Component.literal((String)(ChatFormatting.GRAY.toString() + this.fluidMixerTileEntity.getFueliumLevel() + "/" + this.fluidMixerTileEntity.getFueliumTank().getCapacity() + " mB"))), Component::getVisualOrderText), mouseX, mouseY);
                } else {
                    matrixStack.renderTooltip(this.font, Lists.transform(Collections.singletonList(Component.literal((String)"No Fluid")), Component::getVisualOrderText), mouseX, mouseY);
                }
            }
        }
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    protected void renderLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        matrixStack.drawString(this.font, this.title, 8, 6, 0x404040, false);
        matrixStack.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    protected void renderBg(GuiGraphics matrixStack, float partialTicks, int mouseX, int mouseY) {
        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        matrixStack.blit(GUI, startX, startY, 0, 0, this.imageWidth, this.imageHeight);
        if (this.fluidMixerTileEntity.getRemainingFuel() >= 0) {
            int remainingFuel = (int)(14.0 * ((double)this.fluidMixerTileEntity.getRemainingFuel() / (double)this.fluidMixerTileEntity.getFuelMaxProgress()));
            matrixStack.blit(GUI, startX + 9, startY + 31 + 14 - remainingFuel, 176, 14 - remainingFuel, 14, remainingFuel + 1);
        }
        if (this.fluidMixerTileEntity.canMix()) {
            int colorFade;
            int alpha;
            int blazeColorRGB = FluidUtils.getAverageFluidColor(this.fluidMixerTileEntity.getBlazeFluidStack().getFluid());
            int sapColorRGB = FluidUtils.getAverageFluidColor(this.fluidMixerTileEntity.getEnderSapFluidStack().getFluid());
            int blazeColor = 0x82000000 | blazeColorRGB;
            int sapColor = 0x82000000 | sapColorRGB;
            int redBlaze = blazeColor >> 16 & 0xFF;
            int greenBlaze = blazeColor >> 8 & 0xFF;
            int blueBlaze = blazeColor & 0xFF;
            int redSap = sapColor >> 16 & 0xFF;
            int greenSap = sapColor >> 8 & 0xFF;
            int blueSap = sapColor & 0xFF;
            int statrColorRGB = ((redBlaze + redSap) / 2 & 0xFF) << 16 | ((greenBlaze + greenSap) / 2 & 0xFF) << 8 | (blueBlaze + blueSap) / 2 & 0xFF;
            int statrColor = 0x82000000 | statrColorRGB;
            int fluidColor = 0x82000000 | FluidUtils.getAverageFluidColor((Fluid)ModFluids.FUELIUM.get());
            double extractionPercentage = (double)this.fluidMixerTileEntity.getExtractionProgress() / (double)((Integer)Config.SERVER.mixerMixTime.get()).intValue();
            double lenghtItem = 76.0;
            double lenghtHorizontal = 12.0;
            double lenghtVerticle = 8.0;
            double lenghtNode = 10.0;
            double lenghtTotal = lenghtItem + lenghtHorizontal + lenghtVerticle + lenghtNode * 2.0;
            double percentageStart = 0.0;
            double percentageHorizontal = Mth.clamp((double)((extractionPercentage - percentageStart) / (lenghtHorizontal / lenghtTotal)), (double)0.0, (double)1.0);
            int left = startX + 51;
            int top = startY + 27;
            RenderUtil.drawGradientRectHorizontal(left, top, (int)((double)left + 12.0 * percentageHorizontal), top + 8, blazeColor, blazeColor);
            RenderUtil.drawGradientRectHorizontal(left, top += 36, (int)((double)left + 12.0 * percentageHorizontal), top + 8, sapColor, sapColor);
            percentageStart += lenghtHorizontal / lenghtTotal;
            left += 12;
            top -= 37;
            if (extractionPercentage >= percentageStart) {
                alpha = (int)(130.0 * Mth.clamp((double)((extractionPercentage - percentageStart) / (lenghtNode / lenghtTotal)), (double)0.0, (double)1.0));
                colorFade = alpha << 24 | blazeColorRGB;
                RenderUtil.drawGradientRectHorizontal(left, top, left + 10, top + 10, colorFade, colorFade);
                colorFade = alpha << 24 | sapColorRGB;
                RenderUtil.drawGradientRectHorizontal(left, top += 36, left + 10, top + 10, colorFade, colorFade);
            }
            percentageStart += lenghtNode / lenghtTotal;
            ++left;
            top -= 26;
            if (extractionPercentage >= percentageStart) {
                double percentageVerticle = Mth.clamp((double)((extractionPercentage - percentageStart) / (lenghtVerticle / lenghtTotal)), (double)0.0, (double)1.0);
                RenderUtil.drawGradientRectHorizontal(left, top, left + 8, (int)((double)top + 8.0 * percentageVerticle), blazeColor, blazeColor);
                RenderUtil.drawGradientRectHorizontal(left, (int)((double)(top += 26) - 8.0 * percentageVerticle), left + 8, top, sapColor, sapColor);
            }
            percentageStart += lenghtVerticle / lenghtTotal;
            --left;
            top -= 18;
            if (extractionPercentage >= percentageStart) {
                alpha = (int)(130.0 * Mth.clamp((double)((extractionPercentage - percentageStart) / (lenghtNode / lenghtTotal)), (double)0.0, (double)1.0));
                colorFade = alpha << 24 | statrColorRGB;
                RenderUtil.drawGradientRectHorizontal(left, top, left + 10, top + 10, colorFade, colorFade);
            }
            if (extractionPercentage >= (percentageStart += lenghtNode / lenghtTotal)) {
                left = startX + 73;
                top = startY + 36;
                int right = left + 76;
                int bottom = top + 26;
                double percentageItem = Mth.clamp((double)((extractionPercentage - percentageStart) / (lenghtItem / lenghtTotal)), (double)0.0, (double)1.0);
                RenderUtil.drawGradientRectHorizontal(left, top, right, bottom, statrColor, fluidColor);
                matrixStack.blit(GUI, left, top, 176, 14, 76, 26);
                int extractionProgress = (int)(76.0 * percentageItem + 1.0);
                matrixStack.blit(GUI, left + extractionProgress, top, 73 + extractionProgress, 36, 76 - extractionProgress, 26);
            }
        }
        this.drawSmallFluidTank(this.fluidMixerTileEntity.getBlazeFluidStack(), matrixStack, startX + 33, startY + 17, (double)this.fluidMixerTileEntity.getBlazeLevel() / (double)this.fluidMixerTileEntity.getBlazeTank().getCapacity());
        this.drawSmallFluidTank(this.fluidMixerTileEntity.getEnderSapFluidStack(), matrixStack, startX + 33, startY + 52, (double)this.fluidMixerTileEntity.getEnderSapLevel() / (double)this.fluidMixerTileEntity.getEnderSapTank().getCapacity());
        this.drawFluidTank(this.fluidMixerTileEntity.getFueliumFluidStack(), matrixStack, startX + 151, startY + 20, (double)this.fluidMixerTileEntity.getFueliumLevel() / (double)this.fluidMixerTileEntity.getFueliumTank().getCapacity());
    }

    private void drawFluidTank(FluidStack fluid, GuiGraphics matrixStack, int x, int y, double level) {
        FluidUtils.drawFluidTankInGUI(fluid, x, y, level, 59);
        matrixStack.blit(GUI, x, y, 176, 44, 16, 59);
    }

    private void drawSmallFluidTank(FluidStack fluid, GuiGraphics matrixStack, int x, int y, double level) {
        FluidUtils.drawFluidTankInGUI(fluid, x, y, level, 29);
        matrixStack.blit(GUI, x, y, 176, 44, 16, 29);
    }

    private boolean isMouseWithinRegion(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}

