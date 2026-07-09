/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.BlockRenderDispatcher
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.JackTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class JackBlockRenderer
implements BlockEntityRenderer<JackTileEntity> {
    public JackBlockRenderer(BlockEntityRendererProvider.Context dispatcher) {
    }

    public int getViewDistance() {
        return 65536;
    }

    public void render(JackTileEntity jack, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, int i1) {
        Entity passenger;
        if (!jack.hasLevel()) {
            return;
        }
        matrixStack.pushPose();
        BlockPos pos = jack.getBlockPos();
        BlockState state = jack.getLevel().getBlockState(pos);
        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.0, 0.5);
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        matrixStack.translate(-0.5, 0.0, -0.5);
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        BakedModel model = dispatcher.getBlockModel(state);
        VertexConsumer builder = renderTypeBuffer.getBuffer(RenderType.cutout());
        dispatcher.getModelRenderer().tesselateBlock((BlockAndTintGetter)jack.getLevel(), model, state, pos, matrixStack, builder, true, RandomSource.create(), state.getSeed(pos), OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
        matrixStack.pushPose();
        float progress = ((float)jack.prevLiftProgress + (float)(jack.liftProgress - jack.prevLiftProgress) * partialTicks) / 20.0f;
        matrixStack.translate(0.0, 0.5 * (double)progress, 0.0);
        BlockRenderDispatcher dispatcher2 = Minecraft.getInstance().getBlockRenderer();
        BlockState defaultState = ((Block)ModBlocks.JACK_HEAD.get()).defaultBlockState();
        BakedModel model2 = dispatcher2.getBlockModel(((Block)ModBlocks.JACK_HEAD.get()).defaultBlockState());
        VertexConsumer builder2 = renderTypeBuffer.getBuffer(RenderType.cutout());
        dispatcher2.getModelRenderer().tesselateBlock((BlockAndTintGetter)jack.getLevel(), model2, defaultState, pos, matrixStack, builder2, false, jack.getLevel().random, 0L, light);
        matrixStack.popPose();
        matrixStack.pushPose();
        EntityJack jackEntity = jack.getJack();
        if (jackEntity != null && jackEntity.getPassengers().size() > 0 && (passenger = (Entity)jackEntity.getPassengers().get(0)) instanceof VehicleEntity && passenger.isAlive()) {
            matrixStack.translate(0.0, 0.0625, 0.0);
            matrixStack.translate(0.5, 0.5, 0.5);
            float progress2 = ((float)jack.prevLiftProgress + (float)(jack.liftProgress - jack.prevLiftProgress) * partialTicks) / 20.0f;
            matrixStack.translate(0.0, 0.5 * (double)progress2, 0.0);
            VehicleEntity vehicle = (VehicleEntity)passenger;
            Vec3 heldOffset = vehicle.getProperties().getHeldOffset().yRot(passenger.getYRot() * ((float)Math.PI / 180));
            matrixStack.translate(-heldOffset.z * 0.0625, -heldOffset.y * 0.0625, -heldOffset.x * 0.0625);
            matrixStack.mulPose(Axis.YP.rotationDegrees(-passenger.getYRot()));
            AbstractVehicleRenderer wrapper = VehicleRenderRegistry.getRenderer(vehicle.getType());
            if (wrapper != null) {
                wrapper.setupTransformsAndRender(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);
            }
        }
        matrixStack.popPose();
        matrixStack.popPose();
    }
}

