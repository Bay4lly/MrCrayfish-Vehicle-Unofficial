/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 */
package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.block.FluidExtractorBlock;
import com.mrcrayfish.vehicle.tileentity.FluidExtractorTileEntity;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FluidExtractorRenderer
implements BlockEntityRenderer<FluidExtractorTileEntity> {
    private static final FluidUtils.FluidSides FLUID_SIDES = new FluidUtils.FluidSides(Direction.WEST, Direction.EAST, Direction.SOUTH, Direction.UP);

    public FluidExtractorRenderer(BlockEntityRendererProvider.Context dispatcher) {
    }

    public void render(FluidExtractorTileEntity fluidExtractor, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, int p_225616_6_) {
        FluidTank tank = fluidExtractor.getFluidTank();
        if (tank.isEmpty()) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5, 0.5);
        Direction direction = (Direction)fluidExtractor.getBlockState().getValue((Property)FluidExtractorBlock.DIRECTION);
        matrixStack.mulPose(Axis.YP.rotationDegrees((float)direction.get2DDataValue() * -90.0f - 90.0f));
        matrixStack.translate(-0.5, -0.5, -0.5);
        float height = 12.0f * (float)tank.getFluidAmount() / (float)tank.getCapacity();
        FluidUtils.drawFluidInLevel(tank, fluidExtractor.getLevel(), fluidExtractor.getBlockPos(), matrixStack, renderTypeBuffer, 0.5625f, 0.125f, 6.25E-4f, 0.436875f, height * 0.0625f, 0.99875f, light, FLUID_SIDES);
        matrixStack.popPose();
    }
}

