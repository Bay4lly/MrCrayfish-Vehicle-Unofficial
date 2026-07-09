/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.joml.Matrix4f
 */
package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrcrayfish.vehicle.block.FluidPumpBlock;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.tileentity.PumpTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

public class FluidPumpRenderer
implements BlockEntityRenderer<PumpTileEntity> {
    private final BlockEntityRenderDispatcher renderer;

    public FluidPumpRenderer(BlockEntityRendererProvider.Context dispatcher) {
        this.renderer = dispatcher.getBlockEntityRenderDispatcher();
    }

    public void render(PumpTileEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        Entity entity = this.renderer.camera.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        if (player.getMainHandItem().getItem() != ModItems.WRENCH.get()) {
            return;
        }
        this.renderInteractableBox(tileEntity, matrixStack, renderTypeBuffer);
        if (this.renderer.cameraHitResult == null || this.renderer.cameraHitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockHitResult result = (BlockHitResult)this.renderer.cameraHitResult;
        if (!result.getBlockPos().equals((Object)tileEntity.getBlockPos())) {
            return;
        }
        BlockPos pos = tileEntity.getBlockPos();
        BlockState state = tileEntity.getBlockState();
        FluidPumpBlock fluidPumpBlock = (FluidPumpBlock)state.getBlock();
        if (!fluidPumpBlock.isLookingAtHousing(state, this.renderer.cameraHitResult.getLocation().add((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ())))) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5, 0.5);
        Direction direction = (Direction)state.getValue((Property)FluidPumpBlock.DIRECTION);
        matrixStack.translate((double)(-direction.getStepX()) * 0.35, (double)(-direction.getStepY()) * 0.35, (double)(-direction.getStepZ()) * 0.35);
        matrixStack.mulPose(this.renderer.camera.rotation());
        matrixStack.scale(-0.015f, -0.015f, 0.015f);
        Matrix4f matrix4f = matrixStack.last().pose();
        Font fontRenderer = Minecraft.getInstance().font;
        MutableComponent text = Component.translatable((String)tileEntity.getPowerMode().getKey());
        float x = (float)(-fontRenderer.width((FormattedText)text)) / 2.0f;
        fontRenderer.drawInBatch((Component)text, x, 0.0f, -1, false, matrix4f, renderTypeBuffer, Font.DisplayMode.SEE_THROUGH, 0, 0xF000F0);
        matrixStack.popPose();
    }

    private void renderInteractableBox(PumpTileEntity tileEntity, PoseStack matrixStack, MultiBufferSource renderTypeBuffer) {
        BlockHitResult result;
        if (this.renderer.cameraHitResult != null && this.renderer.cameraHitResult.getType() == HitResult.Type.BLOCK && (result = (BlockHitResult)this.renderer.cameraHitResult).getBlockPos().equals((Object)tileEntity.getBlockPos())) {
            BlockPos pos = tileEntity.getBlockPos();
            BlockState state = tileEntity.getBlockState();
            FluidPumpBlock fluidPumpBlock = (FluidPumpBlock)state.getBlock();
            if (fluidPumpBlock.isLookingAtHousing(state, this.renderer.cameraHitResult.getLocation().add((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ())))) {
                return;
            }
        }
        BlockState state = tileEntity.getBlockState();
        VoxelShape shape = FluidPumpBlock.PUMP_BOX[((Direction)state.getValue((Property)FluidPumpBlock.DIRECTION)).getOpposite().get3DDataValue()];
        VertexConsumer builder = renderTypeBuffer.getBuffer(RenderType.lines());
        EntityRayTracer.renderShape(matrixStack, builder, shape, 1.0f, 0.77f, 0.29f, 1.0f);
    }
}

