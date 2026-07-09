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
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.block;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.vehicle.block.RotatedObjectBlock;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.tileentity.BoostTileEntity;
import com.mrcrayfish.vehicle.util.Bounds;
import com.mrcrayfish.vehicle.util.StateHelper;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SteepBoostRampBlock
extends RotatedObjectBlock {
    public static final BooleanProperty LEFT = BooleanProperty.create((String)"left");
    public static final BooleanProperty RIGHT = BooleanProperty.create((String)"right");
    private static final AABB COLLISION_BASE = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);
    private static final AABB[] COLLISION_ONE = new Bounds(1, 1, 0, 16, 2, 16).getRotatedBounds();
    private static final AABB[] COLLISION_TWO = new Bounds(2, 2, 0, 16, 3, 16).getRotatedBounds();
    private static final AABB[] COLLISION_THREE = new Bounds(3, 3, 0, 16, 4, 16).getRotatedBounds();
    private static final AABB[] COLLISION_FOUR = new Bounds(4, 4, 0, 16, 5, 16).getRotatedBounds();
    private static final AABB[] COLLISION_FIVE = new Bounds(5, 5, 0, 16, 6, 16).getRotatedBounds();
    private static final AABB[] COLLISION_SIX = new Bounds(6, 6, 0, 16, 7, 16).getRotatedBounds();
    private static final AABB[] COLLISION_SEVEN = new Bounds(7, 7, 0, 16, 8, 16).getRotatedBounds();
    private static final AABB[] COLLISION_EIGHT = new Bounds(8, 9, 0, 16, 9, 16).getRotatedBounds();
    private static final AABB[] COLLISION_NINE = new Bounds(9, 10, 0, 16, 10, 16).getRotatedBounds();
    private static final AABB[] COLLISION_TEN = new Bounds(10, 11, 0, 16, 11, 16).getRotatedBounds();
    private static final AABB[] COLLISION_ELEVEN = new Bounds(11, 12, 0, 16, 12, 16).getRotatedBounds();
    private static final AABB[] COLLISION_TWELVE = new Bounds(12, 13, 0, 16, 13, 16).getRotatedBounds();
    private static final AABB[] COLLISION_THIRTEEN = new Bounds(13, 14, 0, 16, 14, 16).getRotatedBounds();
    private static final AABB[] COLLISION_FOURTEEN = new Bounds(14, 15, 0, 16, 15, 16).getRotatedBounds();
    private static final AABB[] COLLISION_FIFTEEN = new Bounds(15, 15, 0, 16, 16, 16).getRotatedBounds();

    public MapCodec<? extends SteepBoostRampBlock> codec() {
        return MapCodec.unit(this);
    }

    public SteepBoostRampBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.0f));
    }

    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        Direction facing;
        if (entity instanceof PoweredVehicleEntity && entity.getControllingPassenger() != null && (facing = (Direction)state.getValue((Property)DIRECTION)) == entity.getDirection()) {
            PoweredVehicleEntity poweredVehicle;
            float speedMultiplier = 0.0f;
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof BoostTileEntity) {
                speedMultiplier = ((BoostTileEntity)tileEntity).getSpeedMultiplier();
            }
            if (!(poweredVehicle = (PoweredVehicleEntity)entity).isBoosting()) {
                world.playSound(null, pos, (SoundEvent)ModSounds.BLOCK_BOOST_PAD_BOOST.get(), SoundSource.BLOCKS, 2.0f, 0.5f);
            }
            poweredVehicle.setBoosting(true);
            poweredVehicle.setLaunching(3);
            poweredVehicle.currentSpeed = poweredVehicle.getActualMaxSpeed();
            poweredVehicle.speedMultiplier = speedMultiplier;
            Vec3 motion = poweredVehicle.getDeltaMovement();
            poweredVehicle.setDeltaMovement(new Vec3(motion.x, (double)(poweredVehicle.currentSpeed / 20.0f) + 0.1, motion.z));
        }
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
        return this.getRampState(state, world, pos, (Direction)state.getValue((Property)DIRECTION));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.getRampState(this.defaultBlockState(), (LevelAccessor)context.getLevel(), context.getClickedPos(), context.getHorizontalDirection());
    }

    private BlockState getRampState(BlockState state, LevelAccessor world, BlockPos pos, Direction facing) {
        state = (BlockState)state.setValue(LEFT, Boolean.valueOf(false));
        state = (BlockState)state.setValue(RIGHT, Boolean.valueOf(false));
        if (StateHelper.getBlock((LevelReader)world, pos, facing, StateHelper.RelativeDirection.LEFT) == this && StateHelper.getRotation((LevelReader)world, pos, facing, StateHelper.RelativeDirection.LEFT) == StateHelper.RelativeDirection.DOWN) {
            state = (BlockState)state.setValue(RIGHT, Boolean.valueOf(true));
        }
        if (StateHelper.getBlock((LevelReader)world, pos, facing, StateHelper.RelativeDirection.RIGHT) == this && StateHelper.getRotation((LevelReader)world, pos, facing, StateHelper.RelativeDirection.RIGHT) == StateHelper.RelativeDirection.DOWN) {
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
        return new BoostTileEntity(pos, state, 1.0f);
    }
}

