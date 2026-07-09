/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.block.FluidPipeBlock;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.tileentity.PipeTileEntity;
import com.mrcrayfish.vehicle.tileentity.PumpTileEntity;
import com.mrcrayfish.vehicle.util.VoxelShapeHelper;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FluidPumpBlock
extends FluidPipeBlock {
    public static final DirectionProperty DIRECTION = BlockStateProperties.FACING;
    public static final VoxelShape[] PUMP_BOX = new VoxelShape[]{Block.box((double)3.0, (double)0.0, (double)3.0, (double)13.0, (double)4.0, (double)13.0), Block.box((double)3.0, (double)12.0, (double)3.0, (double)13.0, (double)16.0, (double)13.0), Block.box((double)3.0, (double)3.0, (double)0.0, (double)13.0, (double)13.0, (double)4.0), Block.box((double)3.0, (double)3.0, (double)12.0, (double)13.0, (double)13.0, (double)16.0), Block.box((double)0.0, (double)3.0, (double)3.0, (double)4.0, (double)13.0, (double)13.0), Block.box((double)12.0, (double)3.0, (double)3.0, (double)16.0, (double)13.0, (double)13.0)};

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return this.getPumpShape(state, worldIn, pos);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return this.getPumpShape(state, worldIn, pos);
    }

    protected VoxelShape getPumpShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        ArrayList<VoxelShape> shapes = new ArrayList<VoxelShape>();
        shapes.add(super.getPipeShape(state, worldIn, pos));
        shapes.add(PUMP_BOX[this.getCollisionFacing(state).get3DDataValue()]);
        return VoxelShapeHelper.combineAll(shapes);
    }

    protected Direction getCollisionFacing(BlockState state) {
        return ((Direction)state.getValue((Property)DIRECTION)).getOpposite();
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult result) {
        PipeTileEntity tileEntity;
        if (super.useWithoutItem(state, world, pos, player, result) == InteractionResult.SUCCESS) {
            return InteractionResult.SUCCESS;
        }
        if (!world.isClientSide() && (tileEntity = FluidPumpBlock.getPipeTileEntity((BlockGetter)world, pos)) instanceof PumpTileEntity) {
            PumpTileEntity pumpTileEntity = (PumpTileEntity)tileEntity;
            Vec3 localHitVec = result.getLocation().add((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()));
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ModItems.WRENCH.get() && this.isLookingAtHousing(state, localHitVec)) {
                pumpTileEntity.cyclePowerMode();
                this.invalidatePipeNetwork(world, pos);
                Vec3 vec = result.getLocation();
                world.playSound(null, vec.x(), vec.y(), vec.z(), SoundEvents.NETHERITE_BLOCK_HIT, SoundSource.BLOCKS, 1.0f, 0.5f + 0.1f * world.random.nextFloat());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public boolean isLookingAtHousing(BlockState state, Vec3 hitVec) {
        VoxelShape shape = PUMP_BOX[this.getCollisionFacing(state).get3DDataValue()];
        AABB boundingBox = shape.bounds();
        return boundingBox.inflate(0.001).contains(hitVec);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState replaceState, boolean what) {
        BlockEntity tileEntity;
        if (!state.is(replaceState.getBlock()) && (tileEntity = world.getBlockEntity(pos)) instanceof PumpTileEntity) {
            ((PumpTileEntity)tileEntity).removePumpFromPipes();
        }
        super.onRemove(state, world, pos, replaceState, what);
    }

    @Override
    protected void invalidatePipeNetwork(Level world, BlockPos pos) {
        super.invalidatePipeNetwork(world, pos);
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof PumpTileEntity) {
            ((PumpTileEntity)tileEntity).invalidatePipeNetwork();
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockState state = (BlockState)this.defaultBlockState().setValue(DIRECTION, face);
        state = this.getPipeState(state, (LevelAccessor)world, pos);
        state = this.getDisabledState(state, world, pos);
        return state;
    }

    @Override
    public BlockState getDisabledState(BlockState state, Level world, BlockPos pos) {
        boolean disabled = false;
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof PumpTileEntity) {
            PumpTileEntity pump = (PumpTileEntity)tileEntity;
            disabled = !pump.getPowerMode().test(pump);
        }
        state = (BlockState)state.setValue(DISABLED, Boolean.valueOf(disabled));
        return state;
    }

    @Override
    protected boolean canPipeConnectTo(BlockState state, LevelAccessor world, BlockPos pos, Direction direction) {
        if (direction == ((Direction)state.getValue((Property)DIRECTION)).getOpposite()) {
            return false;
        }
        return super.canPipeConnectTo(state, world, pos, direction);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(new Property[]{DIRECTION});
    }

    @Override
    @Nullable
    public PumpTileEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PumpTileEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : FluidPumpBlock.createTickerHelper(type, (BlockEntityType)((BlockEntityType)ModTileEntities.FLUID_PUMP.get()), PumpTileEntity::serverTick);
    }
}

