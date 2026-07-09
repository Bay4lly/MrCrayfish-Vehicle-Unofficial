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
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 */
package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.block.RotatedObjectBlock;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class GasPumpTankRenderer
implements BlockEntityRenderer<GasPumpTankTileEntity> {
    private static final FluidUtils.FluidSides FLUID_SIDES = new FluidUtils.FluidSides(Direction.NORTH, Direction.SOUTH, Direction.UP);

    public GasPumpTankRenderer(BlockEntityRendererProvider.Context dispatcher) {
    }

    public void render(GasPumpTankTileEntity gasPump, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        Level world = gasPump.getLevel();
        BlockState state = gasPump.getBlockState();
        if (state.getBlock() != ModBlocks.GAS_PUMP.get()) {
            return;
        }
        FluidTank tank = gasPump.getFluidTank();
        if (tank.isEmpty()) {
            return;
        }
        matrixStack.pushPose();
        Direction direction = (Direction)state.getValue((Property)RotatedObjectBlock.DIRECTION);
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.mulPose(Axis.YP.rotationDegrees((float)direction.get2DDataValue() * -90.0f - 90.0f));
        matrixStack.translate(-0.5, -0.5, -0.5);
        float height = 11.0f * ((float)tank.getFluidAmount() / (float)tank.getCapacity());
        FluidUtils.drawFluidInLevel(tank, world, gasPump.getBlockPos(), matrixStack, renderTypeBuffer, 0.125625f, 0.25f, 0.3125f, 0.74875f, height * 0.0625f, 0.375f, light, FLUID_SIDES);
        matrixStack.popPose();
    }
}

