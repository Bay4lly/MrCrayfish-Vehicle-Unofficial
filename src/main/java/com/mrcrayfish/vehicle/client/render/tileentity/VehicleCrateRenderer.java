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
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Author: MrCrayfish
 */
public class VehicleCrateRenderer implements BlockEntityRenderer<VehicleCrateTileEntity>
{
    public VehicleCrateRenderer(BlockEntityRendererProvider.Context dispatcher) {}

    @Override
    public int getViewDistance()
    {
        return 65536;
    }

    @Override
    public void render(VehicleCrateTileEntity crate, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, int overlay)
    {
        BlockState state = crate.getLevel().getBlockState(crate.getBlockPos());
        if(state.getBlock() != ModBlocks.VEHICLE_CRATE.get())
            return;
        
        matrixStack.pushPose();

        Direction facing = state.getValue(RotatedObjectBlock.DIRECTION);
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.mulPose(Axis.YP.rotationDegrees(facing.get2DDataValue() * -90F + 180F));
        matrixStack.translate(-0.5, -0.5, -0.5);

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS); // FIXME

        matrixStack.pushPose();

        light = LevelRenderer.getLightColor(crate.getLevel(), crate.getBlockPos().above()); //TODO figure out the correct way to calculate light

        if(crate.isOpened() && crate.getTimer() > 150)
        {
            double progress = Math.min(1.0F, Math.max(0, (crate.getTimer() - 150 + 5 * partialTicks)) / 50.0);
            matrixStack.translate(0, (-4 * 0.0625) * progress, 0);
        }

        //Sides panels
        for(int i = 0; i < 4; i++)
        {
            matrixStack.pushPose();
            matrixStack.translate(0.5, 0, 0.5);
            matrixStack.mulPose(Axis.YP.rotationDegrees(90F * i));
            matrixStack.translate(0, 0, 8 * 0.0625);

            if(crate.isOpened())
            {
                double progress = Math.min(1.0, Math.max(0, crate.getTimer() - (i * 20) + 5 * partialTicks) / 90.0);
                double angle = (progress * progress) * 90F;
                double rotation = 1.0 - Math.cos(Math.toRadians(angle));
                matrixStack.mulPose(Axis.XP.rotationDegrees((float) rotation * 90F));
            }
            matrixStack.translate(0.0, 0.5, 0.0);
            matrixStack.translate(0, 0, -1.999 * 0.0625);
            //if(i % 2 == 0) matrixStack.scale(-1, 1, 1);
            RenderUtil.renderColoredModel(SpecialModels.VEHICLE_CRATE_SIDE.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }

        //Render top panel
        if(!crate.isOpened())
        {
            matrixStack.pushPose();
            matrixStack.translate(0.5, 0.5, 0.5);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-90F));
            matrixStack.translate(0, 0, (6.001 * 0.0625));
            RenderUtil.renderColoredModel(SpecialModels.VEHICLE_CRATE_TOP.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }

        //Render bottom panel
        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.mulPose(Axis.XP.rotationDegrees(90F));
        matrixStack.translate(0, 0, (6 * 0.0625) * 0.998);
        RenderUtil.renderColoredModel(SpecialModels.VEHICLE_CRATE_SIDE.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();

        matrixStack.popPose();

        if(crate.getEntity() != null && crate.isOpened())
        {
            matrixStack.translate(0.5F, 0.0F, 0.5F);

            double progress = Math.min(1.0F, Math.max(0, (crate.getTimer() - 150 + 5 * partialTicks)) / 100.0);
            Pair<Float, Float> scaleAndOffset = EntityRayTracer.instance().getCrateScaleAndOffset((EntityType<? extends VehicleEntity>) crate.getEntity().getType());
            float scaleStart = scaleAndOffset.getLeft();
            float scale = scaleStart + (1 - scaleStart) * (float) progress;
            matrixStack.translate(0, 0, scaleAndOffset.getRight() * (1 - progress) * scale);

            if(crate.getTimer() >= 150)
            {
                matrixStack.translate(0, Math.sin(Math.PI * progress) * 5, 0);
                matrixStack.mulPose(Axis.YP.rotationDegrees((float) (720F * progress)));
            }

            matrixStack.translate(0, (2 * 0.0625F) * (1.0F - progress), 0);
            matrixStack.scale(scale, scale, scale);

            EntityRenderer<? extends Entity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(crate.getEntity());
            renderer.render(crate.getEntity(), 0.0F, partialTicks, matrixStack, renderTypeBuffer, light);
        }
        matrixStack.popPose();
    }
}
