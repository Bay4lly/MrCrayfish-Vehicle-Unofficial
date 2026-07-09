/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.block.RotatedObjectBlock;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.VehicleCrateTileEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.tuple.Pair;

public class VehicleCrateRenderer
implements BlockEntityRenderer<VehicleCrateTileEntity> {
    public VehicleCrateRenderer(BlockEntityRendererProvider.Context dispatcher) {
    }

    public int getViewDistance() {
        return 65536;
    }

    public void render(VehicleCrateTileEntity crate, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        BlockState state = crate.getLevel().getBlockState(crate.getBlockPos());
        if (state.getBlock() != ModBlocks.VEHICLE_CRATE.get()) {
            return;
        }
        matrixStack.pushPose();
        Direction facing = (Direction)state.getValue((Property)RotatedObjectBlock.DIRECTION);
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.mulPose(Axis.YP.rotationDegrees((float)facing.get2DDataValue() * -90.0f + 180.0f));
        matrixStack.translate(-0.5, -0.5, -0.5);
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)InventoryMenu.BLOCK_ATLAS);
        matrixStack.pushPose();
        light = LevelRenderer.getLightColor((BlockAndTintGetter)crate.getLevel(), (BlockPos)crate.getBlockPos().above());
        if (crate.isOpened() && crate.getTimer() > 150) {
            double progress = Math.min(1.0, (double)Math.max(0.0f, (float)(crate.getTimer() - 150) + 5.0f * partialTicks) / 50.0);
            matrixStack.translate(0.0, -0.25 * progress, 0.0);
        }
        for (int i = 0; i < 4; ++i) {
            matrixStack.pushPose();
            matrixStack.translate(0.5, 0.0, 0.5);
            matrixStack.mulPose(Axis.YP.rotationDegrees(90.0f * (float)i));
            matrixStack.translate(0.0, 0.0, 0.5);
            if (crate.isOpened()) {
                double progress = Math.min(1.0, (double)Math.max(0.0f, (float)(crate.getTimer() - i * 20) + 5.0f * partialTicks) / 90.0);
                double angle = progress * progress * 90.0;
                double rotation = 1.0 - Math.cos(Math.toRadians(angle));
                matrixStack.mulPose(Axis.XP.rotationDegrees((float)rotation * 90.0f));
            }
            matrixStack.translate(0.0, 0.5, 0.0);
            matrixStack.translate(0.0, 0.0, -0.1249375);
            RenderUtil.renderColoredModel(SpecialModels.VEHICLE_CRATE_SIDE.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }
        if (!crate.isOpened()) {
            matrixStack.pushPose();
            matrixStack.translate(0.5, 0.5, 0.5);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-90.0f));
            matrixStack.translate(0.0, 0.0, 0.3750625);
            RenderUtil.renderColoredModel(SpecialModels.VEHICLE_CRATE_TOP.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }
        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.mulPose(Axis.XP.rotationDegrees(90.0f));
        matrixStack.translate(0.0, 0.0, 0.37424999999999997);
        RenderUtil.renderColoredModel(SpecialModels.VEHICLE_CRATE_SIDE.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
        matrixStack.popPose();
        if (crate.getEntity() != null && crate.isOpened()) {
            matrixStack.translate(0.5f, 0.0f, 0.5f);
            double progress = Math.min(1.0, (double)Math.max(0.0f, (float)(crate.getTimer() - 150) + 5.0f * partialTicks) / 100.0);
            Pair<Float, Float> scaleAndOffset = EntityRayTracer.instance().getCrateScaleAndOffset((EntityType<? extends VehicleEntity>)crate.getEntity().getType());
            float scaleStart = ((Float)scaleAndOffset.getLeft()).floatValue();
            float scale = scaleStart + (1.0f - scaleStart) * (float)progress;
            matrixStack.translate(0.0, 0.0, (double)((Float)scaleAndOffset.getRight()).floatValue() * (1.0 - progress) * (double)scale);
            if (crate.getTimer() >= 150) {
                matrixStack.translate(0.0, Math.sin(Math.PI * progress) * 5.0, 0.0);
                matrixStack.mulPose(Axis.YP.rotationDegrees((float)(720.0 * progress)));
            }
            matrixStack.translate(0.0, 0.125 * (1.0 - progress), 0.0);
            matrixStack.scale(scale, scale, scale);
            EntityRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(crate.getEntity());
            renderer.render(crate.getEntity(), 0.0f, partialTicks, matrixStack, renderTypeBuffer, light);
        }
        matrixStack.popPose();
    }
}

