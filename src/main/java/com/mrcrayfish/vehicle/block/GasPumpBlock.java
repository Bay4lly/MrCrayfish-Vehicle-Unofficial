/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.MapColor
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.level.storage.loot.LootParams$Builder
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParams
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.fluids.FluidUtil
 */
package com.mrcrayfish.vehicle.block;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.vehicle.block.RotatedObjectBlock;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.util.VoxelShapeHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;

public class GasPumpBlock
extends RotatedObjectBlock {
    public static final BooleanProperty TOP = BooleanProperty.create((String)"top");
    private static final Map<BlockState, VoxelShape> SHAPES = new HashMap<BlockState, VoxelShape>();

    public MapCodec<? extends GasPumpBlock> codec() {
        return MapCodec.unit(this);
    }

    public GasPumpBlock() {
        super(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK).mapColor(MapColor.METAL).strength(1.0f));
        this.registerDefaultState((BlockState)((BlockState)this.getStateDefinition().any()).setValue(DIRECTION, Direction.NORTH).setValue(TOP, Boolean.valueOf(false)));
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    private VoxelShape getShape(BlockState state) {
        if (SHAPES.containsKey(state)) {
            return SHAPES.get(state);
        }
        Direction direction = (Direction)state.getValue((Property)DIRECTION);
        boolean top = (Boolean)state.getValue((Property)TOP);
        ArrayList<VoxelShape> shapes = new ArrayList<VoxelShape>();
        if (top) {
            shapes.add(VoxelShapeHelper.getRotatedShapes(VoxelShapeHelper.rotate(Block.box((double)3.0, (double)-16.0, (double)0.0, (double)13.0, (double)15.0, (double)16.0), Direction.EAST))[direction.get2DDataValue()]);
        } else {
            shapes.add(VoxelShapeHelper.getRotatedShapes(VoxelShapeHelper.rotate(Block.box((double)3.0, (double)0.0, (double)0.0, (double)13.0, (double)31.0, (double)16.0), Direction.EAST))[direction.get2DDataValue()]);
        }
        VoxelShape shape = VoxelShapeHelper.combineAll(shapes);
        SHAPES.put(state, shape);
        return shape;
    }

    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return this.getShape(state);
    }

    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player playerEntity, BlockHitResult result) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (((Boolean)state.getValue((Property)TOP)).booleanValue()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof GasPumpTileEntity) {
                GasPumpTileEntity gasPump = (GasPumpTileEntity)tileEntity;
                if (gasPump.getFuelingEntity() != null && gasPump.getFuelingEntity().getId() == playerEntity.getId()) {
                    gasPump.setFuelingEntity(null);
                    world.playSound(null, pos, (SoundEvent)ModSounds.BLOCK_GAS_PUMP_NOZZLE_PUT_DOWN.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                } else if (((Direction)state.getValue((Property)DIRECTION)).getClockWise().equals((Object)result.getDirection())) {
                    gasPump.setFuelingEntity(playerEntity);
                    world.playSound(null, pos, (SoundEvent)ModSounds.BLOCK_GAS_PUMP_NOZZLE_PICK_UP.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (((Optional)ModDataKeys.GAS_PUMP.getValue(playerEntity)).isPresent()) {
            return InteractionResult.SUCCESS;
        }
        if (FluidUtil.interactWithFluidHandler((Player)playerEntity, (InteractionHand)InteractionHand.MAIN_HAND, (Level)world, (BlockPos)pos, (Direction)result.getDirection())) {
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }

    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        return reader.isEmptyBlock(pos) && reader.isEmptyBlock(pos.above());
    }

    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        worldIn.setBlockAndUpdate(pos.above(), (BlockState)state.setValue(TOP, Boolean.valueOf(true)));
    }

    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        boolean top;
        BlockPos blockpos;
        BlockState blockstate;
        if (!world.isClientSide() && (blockstate = world.getBlockState(blockpos = pos.relative((top = ((Boolean)state.getValue((Property)TOP)).booleanValue()) ? Direction.DOWN : Direction.UP))).getBlock() == state.getBlock() && (Boolean)blockstate.getValue((Property)TOP) != top) {
            world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
            world.levelEvent(player, 2001, blockpos, Block.getId((BlockState)blockstate));
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        Vec3 origin;
        if (((Boolean)state.getValue((Property)TOP)).booleanValue() && (origin = (Vec3)builder.getOptionalParameter(LootContextParams.ORIGIN)) != null) {
            BlockPos pos = BlockPos.containing((Position)origin);
            BlockEntity tileEntity = builder.getLevel().getBlockEntity(pos.below());
            if (tileEntity != null) {
                builder = builder.withParameter(LootContextParams.BLOCK_ENTITY, tileEntity);
            }
        }
        return super.getDrops(state, builder);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(new Property[]{TOP});
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (((Boolean)state.getValue((Property)TOP)).booleanValue()) {
            return new GasPumpTileEntity(pos, state);
        }
        return new GasPumpTankTileEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (Boolean)state.getValue((Property)TOP) != false ? GasPumpBlock.createTickerHelper(type, (BlockEntityType)((BlockEntityType)ModTileEntities.GAS_PUMP.get()), GasPumpTileEntity::tick) : null;
    }
}

