package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.util.VoxelShapeHelper;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class GasPumpBlock extends RotatedObjectBlock
{
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    private static final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public GasPumpBlock()
    {
        super(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK).mapColor(MapColor.METAL).strength(1.0F));
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(TOP, false));
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    private VoxelShape getShape(BlockState state)
    {
        if (SHAPES.containsKey(state))
        {
            return SHAPES.get(state);
        }
        Direction direction = state.getValue(DIRECTION);
        boolean top = state.getValue(TOP);
        List<VoxelShape> shapes = new ArrayList<>();
        if (top)
        {
            shapes.add(VoxelShapeHelper.getRotatedShapes(VoxelShapeHelper.rotate(Block.box(3, -16, 0, 13, 15, 16), Direction.EAST))[direction.get2DDataValue()]);
        }
        else
        {
            shapes.add(VoxelShapeHelper.getRotatedShapes(VoxelShapeHelper.rotate(Block.box(3, 0, 0, 13, 31, 16), Direction.EAST))[direction.get2DDataValue()]);
        }
        VoxelShape shape = VoxelShapeHelper.combineAll(shapes);
        SHAPES.put(state, shape);
        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context)
    {
        return this.getShape(state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player playerEntity, InteractionHand hand, BlockHitResult result)
    {
        if(world.isClientSide())
        {
            return InteractionResult.SUCCESS;
        }

        if(state.getValue(TOP))
        {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if(tileEntity instanceof GasPumpTileEntity)
            {
                GasPumpTileEntity gasPump = (GasPumpTileEntity) tileEntity;
                if(gasPump.getFuelingEntity() != null && gasPump.getFuelingEntity().getId() == playerEntity.getId())
                {
                    gasPump.setFuelingEntity(null);
                    world.playSound(null, pos, ModSounds.BLOCK_GAS_PUMP_NOZZLE_PUT_DOWN.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                else if(state.getValue(DIRECTION).getClockWise().equals(result.getDirection()))
                {
                    gasPump.setFuelingEntity(playerEntity);
                    world.playSound(null, pos, ModSounds.BLOCK_GAS_PUMP_NOZZLE_PICK_UP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
            return InteractionResult.SUCCESS;
        }
        else
        {
            // If the player has picked up the nozzle, we should handle filling progressively
            // on the client/server continuous interaction system instead of allowing
            // FluidUtil to perform an immediate transfer which behaves badly under Kilt/Fabric.
            if(ModDataKeys.GAS_PUMP.getValue(playerEntity).isPresent())
            {
                return InteractionResult.SUCCESS;
            }

            if(FluidUtil.interactWithFluidHandler(playerEntity, hand, world, pos, result.getDirection()))
            {
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos)
    {
        return reader.isEmptyBlock(pos) && reader.isEmptyBlock(pos.above());
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        worldIn.setBlockAndUpdate(pos.above(), state.setValue(TOP, true));
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player)
    {
        if (!world.isClientSide())
        {
            boolean top = state.getValue(TOP);
            BlockPos blockpos = pos.relative(top ? Direction.DOWN : Direction.UP);
            BlockState blockstate = world.getBlockState(blockpos);
            if (blockstate.getBlock() == state.getBlock() && blockstate.getValue(TOP) != top)
            {
                world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                world.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        if (state.getValue(TOP))
        {
            Vec3 origin = builder.getOptionalParameter(LootContextParams.ORIGIN);
            if (origin != null)
            {
                BlockPos pos = BlockPos.containing(origin);
                BlockEntity tileEntity = builder.getLevel().getBlockEntity(pos.below());
                if (tileEntity != null)
                {
                    builder = builder.withParameter(LootContextParams.BLOCK_ENTITY, tileEntity);
                }
            }
        }
        return super.getDrops(state, builder);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(TOP);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        if (state.getValue(TOP))
        {
            return new GasPumpTileEntity(pos, state);
        }
        return new GasPumpTankTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return state.getValue(TOP) ? createTickerHelper(type, ModTileEntities.GAS_PUMP.get(), GasPumpTileEntity::tick) : null;
    }
}