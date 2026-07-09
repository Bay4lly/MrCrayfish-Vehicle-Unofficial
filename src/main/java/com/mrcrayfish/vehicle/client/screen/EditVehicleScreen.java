/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.blaze3d.platform.Lighting
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package com.mrcrayfish.vehicle.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.CachedVehicle;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.inventory.container.EditVehicleContainer;
import com.mrcrayfish.vehicle.util.CommonUtils;
import java.util.Arrays;
import java.util.Collections;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class EditVehicleScreen
extends AbstractContainerScreen<EditVehicleContainer> {
    private static final ResourceLocation GUI_TEXTURES = ResourceLocation.parse((String)"vehicle:textures/gui/edit_vehicle.png");
    private final Inventory playerInventory;
    private final Container vehicleInventory;
    private final CachedVehicle cachedVehicle;
    private boolean showHelp = true;
    private int windowZoom = 10;
    private int windowX;
    private int windowY;
    private float windowRotationX;
    private float windowRotationY;
    private boolean mouseGrabbed;
    private int mouseGrabbedButton;
    private int mouseClickedX;
    private int mouseClickedY;

    public EditVehicleScreen(EditVehicleContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.playerInventory = playerInventory;
        this.vehicleInventory = container.getVehicleInventory();
        this.cachedVehicle = new CachedVehicle(container.getVehicle().getType());
        this.imageHeight = 184;
    }

    protected void renderBg(GuiGraphics matrixStack, float partialTicks, int mouseX, int mouseY) {
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;
        matrixStack.blit(GUI_TEXTURES, left, top, 0, 0, this.imageWidth, this.imageHeight);
        if (this.cachedVehicle.getProperties().getEngineType() != EngineType.NONE) {
            if (this.vehicleInventory.getItem(0).isEmpty()) {
                matrixStack.blit(GUI_TEXTURES, left + 8, top + 17, 176, 0, 16, 16);
            }
        } else if (this.vehicleInventory.getItem(0).isEmpty()) {
            matrixStack.blit(GUI_TEXTURES, left + 8, top + 17, 176, 32, 16, 16);
        }
        if (this.cachedVehicle.getProperties().canChangeWheels()) {
            if (this.vehicleInventory.getItem(1).isEmpty()) {
                matrixStack.blit(GUI_TEXTURES, left + 8, top + 35, 176, 16, 16, 16);
            }
        } else if (this.vehicleInventory.getItem(1).isEmpty()) {
            matrixStack.blit(GUI_TEXTURES, left + 8, top + 35, 176, 32, 16, 16);
        }
    }

    protected void renderLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        matrixStack.drawString(this.font, this.title, 8, 6, 0x404040, false);
        matrixStack.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 0x404040, false);
        AbstractVehicleRenderer<com.mrcrayfish.vehicle.entity.PoweredVehicleEntity> renderer = (AbstractVehicleRenderer<com.mrcrayfish.vehicle.entity.PoweredVehicleEntity>)this.cachedVehicle.getRenderer();
        if (renderer != null) {
            int startX = (this.width - this.imageWidth) / 2;
            int startY = (this.height - this.imageHeight) / 2;
            matrixStack.pose().pushPose();
            matrixStack.pose().translate((float)(startX + 96), (float)(startY + 78), 1050.0f);
            matrixStack.pose().scale(-1.0f, -1.0f, -1.0f);
            matrixStack.enableScissor(startX + 26, startY + 17, startX + 168, startY + 87);
            PoseStack poseStack = new PoseStack();
            poseStack.translate(0.0, 0.0, 1000.0);
            poseStack.translate((float)(this.windowX - (this.mouseGrabbed && this.mouseGrabbedButton == 0 ? mouseX - this.mouseClickedX : 0)), 0.0f, 0.0f);
            poseStack.translate(0.0f, (float)(this.windowY - (this.mouseGrabbed && this.mouseGrabbedButton == 0 ? mouseY - this.mouseClickedY : 0)), 0.0f);
            Quaternionf quaternion = Axis.XP.rotationDegrees(-10.0f);
            quaternion.mul((Quaternionfc)Axis.XP.rotationDegrees(this.windowRotationY - (float)(this.mouseGrabbed && this.mouseGrabbedButton == 1 ? mouseY - this.mouseClickedY : 0)));
            quaternion.mul((Quaternionfc)Axis.YP.rotationDegrees(this.windowRotationX + (float)(this.mouseGrabbed && this.mouseGrabbedButton == 1 ? mouseX - this.mouseClickedX : 0)));
            quaternion.mul((Quaternionfc)Axis.YP.rotationDegrees(135.0f));
            poseStack.mulPose(quaternion);
            poseStack.scale((float)this.windowZoom / 10.0f, (float)this.windowZoom / 10.0f, (float)this.windowZoom / 10.0f);
            poseStack.scale(22.0f, 22.0f, 22.0f);
            PartPosition position = this.cachedVehicle.getProperties().getDisplayPosition();
            poseStack.scale((float)position.getScale(), (float)position.getScale(), (float)position.getScale());
            poseStack.mulPose(Axis.XP.rotationDegrees((float)position.getRotX()));
            poseStack.mulPose(Axis.YP.rotationDegrees((float)position.getRotY()));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float)position.getRotZ()));
            poseStack.translate(position.getX(), position.getY(), position.getZ());
            Lighting.setupForEntityInInventory();
            EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
            renderManager.setRenderShadow(false);
            renderManager.overrideCameraOrientation(quaternion);
            MultiBufferSource.BufferSource renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
            RenderSystem.runAsFancy(() -> renderer.setupTransformsAndRender(((EditVehicleContainer)this.menu).getVehicle(), poseStack, (MultiBufferSource)renderTypeBuffer, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true), 0xF000F0));
            renderTypeBuffer.endBatch();
            renderManager.setRenderShadow(true);
            poseStack.popPose();
            matrixStack.disableScissor();
            matrixStack.pose().popPose();
            Lighting.setupFor3DItems();
        }
        if (this.showHelp) {
            matrixStack.pose().pushPose();
            matrixStack.pose().scale(0.5f, 0.5f, 0.5f);
            matrixStack.drawString(this.font, I18n.get((String)"container.edit_vehicle.window_help", (Object[])new Object[0]), 56, 38, 0xFFFFFF);
            matrixStack.pose().popPose();
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        if (CommonUtils.isMouseWithin((int)mouseX, (int)mouseY, startX + 26, startY + 17, 142, 70)) {
            if (scrollY < 0.0 && this.windowZoom > 0) {
                this.showHelp = false;
                --this.windowZoom;
            } else if (scrollY > 0.0) {
                this.showHelp = false;
                ++this.windowZoom;
            }
        }
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        if (CommonUtils.isMouseWithin((int)mouseX, (int)mouseY, startX + 26, startY + 17, 142, 70) && !this.mouseGrabbed && (button == 0 || button == 1)) {
            this.mouseGrabbed = true;
            this.mouseGrabbedButton = button == 1 ? 1 : 0;
            this.mouseClickedX = (int)mouseX;
            this.mouseClickedY = (int)mouseY;
            this.showHelp = false;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.mouseGrabbed) {
            if (this.mouseGrabbedButton == 0 && button == 0) {
                this.mouseGrabbed = false;
                this.windowX = (int)((double)this.windowX - (mouseX - (double)this.mouseClickedX));
                this.windowY = (int)((double)this.windowY - (mouseY - (double)this.mouseClickedY));
            } else if (this.mouseGrabbedButton == 1 && button == 1) {
                this.mouseGrabbed = false;
                this.windowRotationX = (float)((double)this.windowRotationX + (mouseX - (double)this.mouseClickedX));
                this.windowRotationY = (float)((double)this.windowRotationY - (mouseY - (double)this.mouseClickedY));
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        if (this.vehicleInventory.getItem(0).isEmpty() && CommonUtils.isMouseWithin(mouseX, mouseY, startX + 7, startY + 16, 18, 18)) {
            if (this.cachedVehicle.getProperties().getEngineType() != EngineType.NONE) {
                matrixStack.renderTooltip(this.font, Lists.transform(Collections.singletonList(Component.literal((String)"Engine")), Component::getVisualOrderText), mouseX, mouseY);
            } else {
                matrixStack.renderTooltip(this.font, Lists.transform(Arrays.asList(Component.literal((String)"Engine"), Component.literal((String)(String.valueOf(ChatFormatting.GRAY) + "Not applicable"))), Component::getVisualOrderText), mouseX, mouseY);
            }
        }
        if (this.vehicleInventory.getItem(1).isEmpty() && CommonUtils.isMouseWithin(mouseX, mouseY, startX + 7, startY + 34, 18, 18)) {
            if (this.cachedVehicle.getProperties().canChangeWheels()) {
                matrixStack.renderTooltip(this.font, Lists.transform(Collections.singletonList(Component.literal((String)"Wheels")), Component::getVisualOrderText), mouseX, mouseY);
            } else {
                matrixStack.renderTooltip(this.font, Lists.transform(Arrays.asList(Component.literal((String)"Wheels"), Component.literal((String)(String.valueOf(ChatFormatting.GRAY) + "Not applicable"))), Component::getVisualOrderText), mouseX, mouseY);
            }
        }
    }
}

