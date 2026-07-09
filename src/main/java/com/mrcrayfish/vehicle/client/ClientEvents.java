/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.client.event.RenderHighlightEvent$Block
 */
package com.mrcrayfish.vehicle.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrcrayfish.vehicle.block.FluidPumpBlock;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

public class ClientEvents {
    @SubscribeEvent
    public void renderCustomBlockHighlights(RenderHighlightEvent.Block event) {
        FluidPumpBlock fluidPumpBlock;
        BlockPos pos;
        BlockHitResult target = event.getTarget();
        Entity entity = event.getCamera().getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        Level world = player.level();
        BlockState state = world.getBlockState(pos = target.getBlockPos());
        if (state.getBlock() == ModBlocks.FLUID_PUMP.get() && player.getMainHandItem().getItem() == ModItems.WRENCH.get() && (fluidPumpBlock = (FluidPumpBlock)state.getBlock()).isLookingAtHousing(state, target.getLocation().add((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ())))) {
            event.setCanceled(true);
            VoxelShape baseShape = FluidPumpBlock.PUMP_BOX[((Direction)state.getValue((Property)FluidPumpBlock.DIRECTION)).getOpposite().get3DDataValue()];
            VertexConsumer builder = event.getMultiBufferSource().getBuffer(RenderType.lines());
            PoseStack matrixStack = event.getPoseStack();
            matrixStack.pushPose();
            Vec3 position = event.getCamera().getPosition();
            matrixStack.translate(-position.x, -position.y, -position.z);
            matrixStack.translate((float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
            EntityRayTracer.renderShape(matrixStack, builder, baseShape, 0.0f, 1.0f, 0.0f, 1.0f);
            EntityRayTracer.renderShape(matrixStack, builder, fluidPumpBlock.getPipeShape(state, (BlockGetter)world, pos), 0.0f, 0.0f, 0.0f, 0.4f);
            matrixStack.popPose();
        }
    }

    private void boxRenderGlStart() {
    }

    private void boxRenderGlEnd() {
    }
}

