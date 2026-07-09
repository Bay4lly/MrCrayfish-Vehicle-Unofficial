/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
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
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LeverBlock
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.MapColor
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  org.apache.commons.lang3.tuple.ImmutablePair
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.mrcrayfish.vehicle.block;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.vehicle.block.FluidPumpBlock;
import com.mrcrayfish.vehicle.block.ObjectEntityBlock;
import com.mrcrayfish.vehicle.common.FluidNetworkHandler;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.item.WrenchItem;
import com.mrcrayfish.vehicle.tileentity.PipeTileEntity;
import com.mrcrayfish.vehicle.tileentity.PumpTileEntity;
import com.mrcrayfish.vehicle.util.VoxelShapeHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class FluidPipeBlock
extends ObjectEntityBlock {
    public static final BooleanProperty[] CONNECTED_PIPES = new BooleanProperty[]{BlockStateProperties.DOWN, BlockStateProperties.UP, BlockStateProperties.NORTH, BlockStateProperties.SOUTH, BlockStateProperties.WEST, BlockStateProperties.EAST};
    public static final BooleanProperty DISABLED = BooleanProperty.create((String)"disabled");
    protected static final VoxelShape CENTER = Block.box((double)5.0, (double)5.0, (double)5.0, (double)11.0, (double)11.0, (double)11.0);
    protected static final VoxelShape[] SIDES = new VoxelShape[]{Block.box((double)5.0, (double)0.0, (double)5.0, (double)11.0, (double)5.0, (double)11.0), Block.box((double)5.0, (double)11.0, (double)5.0, (double)11.0, (double)16.0, (double)11.0), Block.box((double)5.0, (double)5.0, (double)0.0, (double)11.0, (double)11.0, (double)5.0), Block.box((double)5.0, (double)5.0, (double)11.0, (double)11.0, (double)11.0, (double)16.0), Block.box((double)0.0, (double)5.0, (double)5.0, (double)5.0, (double)11.0, (double)11.0), Block.box((double)11.0, (double)5.0, (double)5.0, (double)16.0, (double)11.0, (double)11.0), CENTER};

    public MapCodec<? extends FluidPipeBlock> codec() {
        return MapCodec.unit(this);
    }

    public FluidPipeBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.NETHERITE_BLOCK).strength(0.5f));
        BlockState defaultState = this.getStateDefinition().any().setValue(DISABLED, Boolean.valueOf(true));
        for (BooleanProperty property : CONNECTED_PIPES) {
            defaultState = defaultState.setValue(property, Boolean.valueOf(false));
        }
        this.registerDefaultState(defaultState);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    public static PipeTileEntity getPipeTileEntity(BlockGetter world, BlockPos pos) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        return tileEntity instanceof PipeTileEntity ? (PipeTileEntity)tileEntity : null;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return this.getPipeShape(state, worldIn, pos);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return this.getPipeShape(state, worldIn, pos);
    }

    public VoxelShape getPipeShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        ArrayList<VoxelShape> shapes = new ArrayList<VoxelShape>();
        boolean[] disabledConnections = this.getDisabledConnections(worldIn, pos);
        for (int i = 0; i < Direction.values().length; ++i) {
            if (!((Boolean)state.getValue((Property)CONNECTED_PIPES[i])).booleanValue() || disabledConnections[i]) continue;
            shapes.add(SIDES[i]);
        }
        shapes.addAll(Arrays.asList(SIDES).subList(Direction.values().length, SIDES.length));
        return VoxelShapeHelper.combineAll(shapes);
    }

    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult result) {
        PipeTileEntity pipe = FluidPipeBlock.getPipeTileEntity((BlockGetter)world, pos);
        Pair<AABB, Direction> hit = this.getConnectionBox(world, pos, state, player, InteractionHand.MAIN_HAND, result.getDirection(), result.getLocation(), pipe);
        if (pipe != null && hit != null) {
            Direction direction = (Direction)hit.getRight();
            boolean enabled = !pipe.isConnectionDisabled(direction);
            pipe.setConnectionState(direction, enabled);
            BlockState newState = (BlockState)state.setValue(CONNECTED_PIPES[direction.get3DDataValue()], Boolean.valueOf(!enabled));
            world.setBlockAndUpdate(pos, newState);
            world.sendBlockUpdated(pos, state, newState, 0);
            this.invalidatePipeNetwork(world, pos);
            BlockPos relativePos = pos.relative(direction);
            PipeTileEntity adjacentPipe = FluidPipeBlock.getPipeTileEntity((BlockGetter)world, relativePos);
            if (adjacentPipe != null) {
                Direction opposite = direction.getOpposite();
                adjacentPipe.setConnectionState(opposite, enabled);
                BlockState relativeState = adjacentPipe.getBlockState();
                BlockState newRelativeState = (BlockState)relativeState.setValue(CONNECTED_PIPES[opposite.get3DDataValue()], Boolean.valueOf(!enabled));
                world.setBlockAndUpdate(relativePos, newRelativeState);
                world.sendBlockUpdated(relativePos, relativeState, newRelativeState, 0);
                FluidPipeBlock relativeBlock = (FluidPipeBlock)relativeState.getBlock();
                relativeBlock.invalidatePipeNetwork(world, relativePos);
            }
            world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.IRON_GOLEM_STEP, SoundSource.BLOCKS, 1.0f, 2.0f);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Nullable
    protected Pair<AABB, Direction> getConnectionBox(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction facing, Vec3 hitVec, @Nullable PipeTileEntity pipe) {
        Vec3 localHitVec = hitVec.add((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()));
        if (pipe == null || !(player.getItemInHand(hand).getItem() instanceof WrenchItem)) {
            return null;
        }
        for (int i = 0; i < Direction.values().length + 1; ++i) {
            BlockPos adjacentPos;
            BlockState adjacentState;
            Block adjacentBlock;
            boolean isCenter;
            boolean bl = isCenter = i == Direction.values().length;
            if (!isCenter && !((Boolean)state.getValue((Property)CONNECTED_PIPES[i])).booleanValue() || !SIDES[i].bounds().inflate(0.001).contains(localHitVec)) continue;
            if (!isCenter) {
                facing = Direction.from3DDataValue((int)i);
            } else if (!((Boolean)state.getValue((Property)CONNECTED_PIPES[facing.get3DDataValue()])).booleanValue() && (adjacentBlock = (adjacentState = world.getBlockState(adjacentPos = pos.relative(facing))).getBlock()) != ModBlocks.FLUID_PIPE.get() && adjacentBlock != ModBlocks.FLUID_PUMP.get() && world.getCapability(Capabilities.FluidHandler.BLOCK, adjacentPos, facing.getOpposite()) == null) {
                return null;
            }
            if (world.getBlockState(pos.relative(facing)).getBlock() == Blocks.LEVER) continue;
            return new ImmutablePair<>(SIDES[i].bounds().move(pos), facing);
        }
        return null;
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState newState, boolean what) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        PipeTileEntity tileEntity = this.newBlockEntity(pos, state);
        if (tileEntity != null) {
            for (Direction direction : Direction.values()) {
                BlockEntity relativeTileEntity = world.getBlockEntity(pos.relative(direction));
                if (!(relativeTileEntity instanceof PipeTileEntity)) continue;
                tileEntity.getDisabledConnections()[direction.get3DDataValue()] = ((PipeTileEntity)relativeTileEntity).isConnectionDisabled(direction.getOpposite());
            }
            world.setBlockEntity((BlockEntity)tileEntity);
            FluidNetworkHandler.instance().addPipeForUpdate(tileEntity);
        }
    }

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean p_220069_6_) {
        boolean disabled = (Boolean)this.getDisabledState(state, world, pos).getValue((Property)DISABLED);
        if ((Boolean)state.getValue((Property)DISABLED) != disabled) {
            this.invalidatePipeNetwork(world, pos);
            if (state.getBlock() instanceof FluidPumpBlock) {
                world.setBlock(pos, (BlockState)state.setValue(DISABLED, Boolean.valueOf(disabled)), 10);
            }
        }
        BlockState newState = this.getPipeState(state, (LevelAccessor)world, pos);
        for (Direction direction : Direction.values()) {
            int index = direction.get3DDataValue();
            if (newState.getValue((Property)CONNECTED_PIPES[index]) == state.getValue((Property)CONNECTED_PIPES[index])) continue;
            this.invalidatePipeNetwork(world, pos);
            break;
        }
    }

    protected BlockState getDisabledState(BlockState state, Level world, BlockPos pos) {
        boolean disabled = world.hasNeighborSignal(pos);
        state = (BlockState)state.setValue(DISABLED, Boolean.valueOf(disabled));
        return state;
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState replaceState, boolean what) {
        if (!state.is(replaceState.getBlock())) {
            this.invalidatePipeNetwork(world, pos);
            super.onRemove(state, world, pos, replaceState, what);
        }
    }

    protected void invalidatePipeNetwork(Level world, BlockPos pos) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof PipeTileEntity) {
            Set<BlockPos> pumps = ((PipeTileEntity)tileEntity).getPumps();
            pumps.forEach(pumpPos -> {
                BlockEntity te = world.getBlockEntity(pumpPos);
                if (te instanceof PumpTileEntity) {
                    ((PumpTileEntity)te).invalidatePipeNetwork();
                }
            });
        }
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
        return this.getPipeState(state, world, pos);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState();
        state = this.getPipeState(state, (LevelAccessor)world, pos);
        state = this.getDisabledState(state, world, pos);
        state = this.getPlacedDisabledState(state, world, pos);
        return state;
    }

    protected BlockState getPlacedDisabledState(BlockState state, Level world, BlockPos pos) {
        if (!((Boolean)state.getValue((Property)DISABLED)).booleanValue()) {
            state = (BlockState)state.setValue(DISABLED, Boolean.valueOf(true));
            for (Direction direction : Direction.values()) {
                BlockState relativeState;
                PipeTileEntity pipeTileEntity;
                BlockPos relativePos = pos.relative(direction);
                BlockEntity relativeTileEntity = world.getBlockEntity(relativePos);
                if (!(relativeTileEntity instanceof PipeTileEntity) || (pipeTileEntity = (PipeTileEntity)relativeTileEntity).getDisabledConnections()[direction.getOpposite().get3DDataValue()] || ((Boolean)(relativeState = pipeTileEntity.getBlockState()).getValue((Property)DISABLED)).booleanValue() || relativeState.getBlock() instanceof FluidPumpBlock && relativeState.getValue((Property)FluidPumpBlock.DIRECTION) == direction) continue;
                state = (BlockState)state.setValue(DISABLED, Boolean.valueOf(false));
                break;
            }
        }
        return state;
    }

    protected BlockState getPipeState(BlockState state, LevelAccessor world, BlockPos pos) {
        boolean[] disabledConnections = this.getDisabledConnections((BlockGetter)world, pos);
        for (Direction direction : Direction.values()) {
            state = (BlockState)state.setValue(CONNECTED_PIPES[direction.get3DDataValue()], Boolean.valueOf(false));
            if (disabledConnections[direction.get3DDataValue()] && world.getBlockState(pos.relative(direction)).getBlock() != Blocks.LEVER) continue;
            state = (BlockState)state.setValue(CONNECTED_PIPES[direction.get3DDataValue()], Boolean.valueOf(this.canPipeConnectTo(state, world, pos, direction)));
        }
        return state;
    }

    protected boolean canPipeConnectTo(BlockState state, LevelAccessor world, BlockPos pos, Direction direction) {
        Level lvl;
        BlockPos relativePos = pos.relative(direction);
        BlockEntity adjacentTileEntity = world.getBlockEntity(relativePos);
        if (adjacentTileEntity instanceof PipeTileEntity) {
            BlockState relativeState = world.getBlockState(relativePos);
            if (relativeState.getBlock() instanceof FluidPumpBlock && relativeState.getValue((Property)FluidPumpBlock.DIRECTION) == direction) {
                return false;
            }
            return !((PipeTileEntity)adjacentTileEntity).isConnectionDisabled(direction.getOpposite());
        }
        if (adjacentTileEntity != null && world instanceof Level && (lvl = (Level)world).getCapability(Capabilities.FluidHandler.BLOCK, relativePos, null, adjacentTileEntity, direction.getOpposite()) != null) {
            return true;
        }
        BlockState adjacentState = world.getBlockState(relativePos);
        if (adjacentState.getBlock() == Blocks.LEVER) {
            AttachFace attachFace = (AttachFace)adjacentState.getValue((Property)LeverBlock.FACE);
            if (direction.getAxis() != Direction.Axis.Y) {
                return adjacentState.getValue((Property)LeverBlock.FACING) == direction && attachFace == AttachFace.WALL;
            }
            if (direction == Direction.UP && attachFace == AttachFace.FLOOR) {
                return true;
            }
            return direction == Direction.DOWN && attachFace == AttachFace.CEILING;
        }
        return false;
    }

    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return Shapes.block();
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add((Property[])CONNECTED_PIPES);
        builder.add(new Property[]{DISABLED});
    }

    public PipeTileEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PipeTileEntity(pos, state);
    }

    protected boolean[] getDisabledConnections(BlockGetter reader, BlockPos pos) {
        PipeTileEntity tileEntity = FluidPipeBlock.getPipeTileEntity(reader, pos);
        return tileEntity != null ? tileEntity.getDisabledConnections() : new boolean[Direction.values().length];
    }
}



