package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrcrayfish.vehicle.block.FluidPumpBlock;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.tileentity.PumpTileEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

/**
 * Author: MrCrayfish
 */
public class FluidPumpRenderer implements BlockEntityRenderer<PumpTileEntity>
{
    private final BlockEntityRenderDispatcher renderer;

    public FluidPumpRenderer(BlockEntityRendererProvider.Context dispatcher)
    {
        this.renderer = dispatcher.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(PumpTileEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, int overlay)
    {
        Entity entity = this.renderer.camera.getEntity();
        if(!(entity instanceof Player))
            return;

        Player player = (Player) entity;
        if(player.getMainHandItem().getItem() != ModItems.WRENCH.get())
            return;

        this.renderInteractableBox(tileEntity, matrixStack, renderTypeBuffer);

        if(this.renderer.cameraHitResult == null || this.renderer.cameraHitResult.getType() != HitResult.Type.BLOCK)
            return;

        BlockHitResult result = (BlockHitResult) this.renderer.cameraHitResult;
        if(!result.getBlockPos().equals(tileEntity.getBlockPos()))
            return;

        BlockPos pos = tileEntity.getBlockPos();
        BlockState state = tileEntity.getBlockState();
        FluidPumpBlock fluidPumpBlock = (FluidPumpBlock) state.getBlock();
        if(!fluidPumpBlock.isLookingAtHousing(state, this.renderer.cameraHitResult.getLocation().add(-pos.getX(), -pos.getY(), -pos.getZ())))
            return;

        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5, 0.5);

        Direction direction = state.getValue(FluidPumpBlock.DIRECTION);
        matrixStack.translate(-direction.getStepX() * 0.35, -direction.getStepY() * 0.35, -direction.getStepZ() * 0.35);

        matrixStack.mulPose(this.renderer.camera.rotation());
        matrixStack.scale(-0.015F, -0.015F, 0.015F);
        Matrix4f matrix4f = matrixStack.last().pose();
        Font fontRenderer = this.renderer.font;
        Component text = Component.translatable(tileEntity.getPowerMode().getKey());
        float x = (-fontRenderer.width(text) / 2.0F);
        // FIXME
        fontRenderer.drawInBatch(text, x, 0, -1, false, matrix4f, renderTypeBuffer, DisplayMode.SEE_THROUGH, 0, 15728880);
        matrixStack.popPose();
    }

    private void renderInteractableBox(PumpTileEntity tileEntity, PoseStack matrixStack, MultiBufferSource renderTypeBuffer)
    {
        if(this.renderer.cameraHitResult != null && this.renderer.cameraHitResult.getType() == HitResult.Type.BLOCK)
        {
            BlockHitResult result = (BlockHitResult) this.renderer.cameraHitResult;
            if(result.getBlockPos().equals(tileEntity.getBlockPos()))
            {
                BlockPos pos = tileEntity.getBlockPos();
                BlockState state = tileEntity.getBlockState();
                FluidPumpBlock fluidPumpBlock = (FluidPumpBlock) state.getBlock();
                if(fluidPumpBlock.isLookingAtHousing(state, this.renderer.cameraHitResult.getLocation().add(-pos.getX(), -pos.getY(), -pos.getZ())))
                {
                    return;
                }
            }
        }

        BlockState state = tileEntity.getBlockState();
        VoxelShape shape = FluidPumpBlock.PUMP_BOX[state.getValue(FluidPumpBlock.DIRECTION).getOpposite().get3DDataValue()];
        VertexConsumer builder = renderTypeBuffer.getBuffer(RenderType.lines());
        EntityRayTracer.renderShape(matrixStack, builder, shape, 1.0F, 0.77F, 0.29F, 1.0F);
    }
}
