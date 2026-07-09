/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.mrcrayfish.vehicle.util;

import com.mrcrayfish.vehicle.block.RotatedObjectBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public class StateHelper {
    public static Block getBlock(LevelReader world, BlockPos pos, Direction facing, RelativeDirection dir) {
        BlockPos target = StateHelper.getBlockPosRelativeTo(world, pos, facing, dir);
        return world.getBlockState(target).getBlock();
    }

    public static RelativeDirection getRotation(LevelReader world, BlockPos pos, Direction facing, RelativeDirection dir) {
        BlockPos target = StateHelper.getBlockPosRelativeTo(world, pos, facing, dir);
        Direction other = (Direction)world.getBlockState(target).getValue((Property)RotatedObjectBlock.DIRECTION);
        return StateHelper.getDirectionRelativeTo(facing, other);
    }

    public static boolean isAirBlock(LevelReader world, BlockPos pos, Direction facing, RelativeDirection dir) {
        BlockPos target = StateHelper.getBlockPosRelativeTo(world, pos, facing, dir);
        return world.getBlockState(target).isAir();
    }

    private static BlockPos getBlockPosRelativeTo(LevelReader world, BlockPos pos, Direction facing, RelativeDirection dir) {
        switch (dir.ordinal()) {
            case 2: {
                return pos.relative(facing.getClockWise());
            }
            case 3: {
                return pos.relative(facing.getCounterClockWise());
            }
            case 0: {
                return pos.relative(facing);
            }
            case 1: {
                return pos.relative(facing.getOpposite());
            }
        }
        return pos;
    }

    private static RelativeDirection getDirectionRelativeTo(Direction thisBlock, Direction otherBlock) {
        int num = thisBlock.get2DDataValue() - otherBlock.get2DDataValue();
        switch (num) {
            case -3: {
                return RelativeDirection.LEFT;
            }
            case -2: {
                return RelativeDirection.UP;
            }
            case -1: {
                return RelativeDirection.RIGHT;
            }
            case 0: {
                return RelativeDirection.DOWN;
            }
            case 1: {
                return RelativeDirection.LEFT;
            }
            case 2: {
                return RelativeDirection.UP;
            }
            case 3: {
                return RelativeDirection.RIGHT;
            }
        }
        return RelativeDirection.NONE;
    }

    public static enum RelativeDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        NONE;

    }
}

