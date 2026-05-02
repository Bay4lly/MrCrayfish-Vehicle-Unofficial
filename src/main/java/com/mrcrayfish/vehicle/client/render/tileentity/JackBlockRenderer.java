package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Author: MrCrayfish
 */
public class JackBlockRenderer implements BlockEntityRenderer<JackTileEntity>
{
    public JackBlockRenderer(BlockEntityRendererProvider.Context dispatcher) {}

    @Override
    public int getViewDistance()
    {
        return 65536;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void render(JackTileEntity jack, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, int i1)
    {
        if(!jack.hasLevel())
            return;

        matrixStack.pushPose();

        BlockPos pos = jack.getBlockPos();
        BlockState state = jack.getLevel().getBlockState(pos);

        matrixStack.pushPose();
        {
            matrixStack.translate(0.5, 0.0, 0.5);
            matrixStack.mulPose(Axis.YP.rotationDegrees(180F));
            matrixStack.translate(-0.5, 0.0, -0.5);
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            BakedModel model = dispatcher.getBlockModel(state);
            VertexConsumer builder = renderTypeBuffer.getBuffer(RenderType.cutout());
            dispatcher.getModelRenderer().tesselateBlock(jack.getLevel(), model, state, pos, matrixStack, builder, true, RandomSource.create(), state.getSeed(pos), OverlayTexture.NO_OVERLAY);
        }
        matrixStack.popPose();

        matrixStack.pushPose();
        {
            float progress = (jack.prevLiftProgress + (jack.liftProgress - jack.prevLiftProgress) * partialTicks) / (float) JackTileEntity.MAX_LIFT_PROGRESS;
            matrixStack.translate(0, 0.5 * progress, 0);

            //Render the head
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            BlockState defaultState = ModBlocks.JACK_HEAD.get().defaultBlockState();
            BakedModel model = dispatcher.getBlockModel(ModBlocks.JACK_HEAD.get().defaultBlockState());
            VertexConsumer builder = renderTypeBuffer.getBuffer(RenderType.cutout());
            dispatcher.getModelRenderer().tesselateBlock(jack.getLevel(), model, defaultState, pos, matrixStack, builder, false, jack.getLevel().random, 0L, light);
        }
        matrixStack.popPose();

        matrixStack.pushPose();
        {
            Entity jackEntity = jack.getJack();
            if(jackEntity != null && jackEntity.getPassengers().size() > 0)
            {
                Entity passenger = jackEntity.getPassengers().get(0);
                if(passenger instanceof VehicleEntity && passenger.isAlive())
                {
                    matrixStack.translate(0, 1 * 0.0625, 0);
                    matrixStack.translate(0.5, 0.5, 0.5);
                    float progress = (jack.prevLiftProgress + (jack.liftProgress - jack.prevLiftProgress) * partialTicks) / (float) JackTileEntity.MAX_LIFT_PROGRESS;
                    matrixStack.translate(0, 0.5 * progress, 0);

                    VehicleEntity vehicle = (VehicleEntity) passenger;
                    Vec3 heldOffset = vehicle.getProperties().getHeldOffset().yRot(passenger.getYRot() * 0.017453292F);
                    matrixStack.translate(-heldOffset.z * 0.0625, -heldOffset.y * 0.0625, -heldOffset.x * 0.0625);
                    matrixStack.mulPose(Axis.YP.rotationDegrees(-passenger.getYRot()));

                    AbstractVehicleRenderer wrapper = VehicleRenderRegistry.getRenderer(vehicle.getType());
                    if(wrapper != null)
                    {
                        wrapper.setupTransformsAndRender(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);
                    }
                }
            }
        }
        matrixStack.popPose();

        matrixStack.popPose();
    }
}
