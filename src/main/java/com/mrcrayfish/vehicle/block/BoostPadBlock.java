/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.MapColor
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.mrcrayfish.vehicle.block;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.vehicle.block.RotatedObjectBlock;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.tileentity.BoostTileEntity;
import com.mrcrayfish.vehicle.util.StateHelper;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BoostPadBlock
extends RotatedObjectBlock {
    public static final BooleanProperty LEFT = BooleanProperty.create((String)"left");
    public static final BooleanProperty RIGHT = BooleanProperty.create((String)"right");
    protected static final VoxelShape SHAPE = Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)1.0, (double)16.0);

    public MapCodec<? extends BoostPadBlock> codec() {
        return MapCodec.unit(this);
    }

    public BoostPadBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.6f));
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue(DIRECTION, Direction.NORTH).setValue(LEFT, Boolean.valueOf(false))).setValue(RIGHT, Boolean.valueOf(false)));
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        Direction facing;
        if (entityIn instanceof PoweredVehicleEntity && entityIn.getControllingPassenger() != null && (facing = (Direction)state.getValue((Property)DIRECTION)) == entityIn.getDirection()) {
            PoweredVehicleEntity poweredVehicle;
            float speedMultiplier = 0.0f;
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof BoostTileEntity) {
                speedMultiplier = ((BoostTileEntity)tileEntity).getSpeedMultiplier();
            }
            if (!(poweredVehicle = (PoweredVehicleEntity)entityIn).isBoosting()) {
                worldIn.playSound(null, pos, (SoundEvent)ModSounds.BLOCK_BOOST_PAD_BOOST.get(), SoundSource.BLOCKS, 1.0f, 0.5f);
            }
            poweredVehicle.setBoosting(true);
            poweredVehicle.currentSpeed = poweredVehicle.getActualMaxSpeed();
            poweredVehicle.speedMultiplier = speedMultiplier;
        }
    }

    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos pos, BlockPos facingPos) {
        return this.getBoostPadState(state, (Direction)state.getValue((Property)DIRECTION), worldIn, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.getBoostPadState(super.getStateForPlacement(context), context.getHorizontalDirection(), (LevelAccessor)context.getLevel(), context.getClickedPos());
    }

    private BlockState getBoostPadState(BlockState state, Direction direction, LevelAccessor world, BlockPos pos) {
        if (StateHelper.getBlock((LevelReader)world, pos, direction, StateHelper.RelativeDirection.LEFT) == this && StateHelper.getRotation((LevelReader)world, pos, direction, StateHelper.RelativeDirection.LEFT) == StateHelper.RelativeDirection.DOWN) {
            state = (BlockState)state.setValue(RIGHT, Boolean.valueOf(true));
        }
        if (StateHelper.getBlock((LevelReader)world, pos, direction, StateHelper.RelativeDirection.RIGHT) == this && StateHelper.getRotation((LevelReader)world, pos, direction, StateHelper.RelativeDirection.RIGHT) == StateHelper.RelativeDirection.DOWN) {
            state = (BlockState)state.setValue(LEFT, Boolean.valueOf(true));
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(new Property[]{LEFT});
        builder.add(new Property[]{RIGHT});
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BoostTileEntity(pos, state, 0.5f);
    }
}

