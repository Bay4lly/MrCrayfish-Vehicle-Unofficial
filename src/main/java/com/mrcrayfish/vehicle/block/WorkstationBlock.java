/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.Util
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.MapColor
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.mrcrayfish.vehicle.block;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.vehicle.block.RotatedObjectBlock;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import com.mrcrayfish.vehicle.util.VoxelShapeHelper;
import java.util.ArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WorkstationBlock
extends RotatedObjectBlock {
    private static final VoxelShape SHAPE = (VoxelShape)Util.make(() -> {
        ArrayList<VoxelShape> shapes = new ArrayList<VoxelShape>();
        shapes.add(Block.box((double)0.0, (double)1.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0));
        shapes.add(Block.box((double)1.0, (double)0.0, (double)1.0, (double)3.0, (double)1.0, (double)3.0));
        shapes.add(Block.box((double)1.0, (double)0.0, (double)13.0, (double)3.0, (double)1.0, (double)15.0));
        shapes.add(Block.box((double)13.0, (double)0.0, (double)1.0, (double)15.0, (double)1.0, (double)3.0));
        shapes.add(Block.box((double)13.0, (double)0.0, (double)13.0, (double)15.0, (double)1.0, (double)15.0));
        return VoxelShapeHelper.combineAll(shapes);
    });

    public MapCodec<? extends WorkstationBlock> codec() {
        return MapCodec.unit(this);
    }

    public WorkstationBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0f));
    }

    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player playerEntity, BlockHitResult result) {
        BlockEntity tileEntity;
        if (!world.isClientSide && (tileEntity = world.getBlockEntity(pos)) instanceof MenuProvider) {
            ((ServerPlayer)playerEntity).openMenu((MenuProvider)tileEntity, pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WorkstationTileEntity(pos, state);
    }
}

