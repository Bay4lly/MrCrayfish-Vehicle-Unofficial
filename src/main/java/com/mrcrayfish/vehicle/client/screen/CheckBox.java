/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.mrcrayfish.vehicle.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public class CheckBox
extends AbstractWidget {
    private static final ResourceLocation GUI = ResourceLocation.parse((String)"vehicle:textures/gui/components.png");
    private boolean toggled = false;

    public CheckBox(int left, int top, Component title) {
        super(left, top, 8, 8, title);
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public void renderWidget(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        matrixStack.blit(GUI, this.getX(), this.getY(), 0, 0, 8, 8);
        if (this.toggled) {
            matrixStack.blit(GUI, this.getX(), this.getY() - 1, 8, 0, 9, 8);
        }
        matrixStack.drawString(Minecraft.getInstance().font, this.getMessage().getString(), this.getX() + 12, this.getY(), 0xFFFFFF);
    }

    public void onClick(double mouseX, double mouseY) {
        this.toggled = !this.toggled;
    }

    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}

