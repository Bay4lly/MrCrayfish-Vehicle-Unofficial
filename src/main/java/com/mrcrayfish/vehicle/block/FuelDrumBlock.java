/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.MapColor
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.fluids.FluidUtil
 */
package com.mrcrayfish.vehicle.block;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.FuelDrumTileEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;

public class FuelDrumBlock
extends BaseEntityBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
    private static final VoxelShape[] SHAPE = new VoxelShape[]{Block.box((double)0.0, (double)1.0, (double)1.0, (double)16.0, (double)15.0, (double)15.0), Block.box((double)1.0, (double)0.0, (double)1.0, (double)15.0, (double)16.0, (double)15.0), Block.box((double)1.0, (double)1.0, (double)0.0, (double)15.0, (double)15.0, (double)16.0)};

    public MapCodec<? extends FuelDrumBlock> codec() {
        return MapCodec.unit(this);
    }

    public FuelDrumBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0f));
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AXIS, Direction.Axis.Y).setValue(INVERTED, Boolean.valueOf(false)));
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE[((Direction.Axis)state.getValue(AXIS)).ordinal()];
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE[((Direction.Axis)state.getValue(AXIS)).ordinal()];
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag advanced) {
        if (Screen.hasShiftDown()) {
            list.addAll(RenderUtil.lines((FormattedText)Component.translatable((String)(((FuelDrumBlock)((Object)ModBlocks.FUEL_DRUM.get())).getDescriptionId() + ".info")), 150));
        } else {
            CompoundTag blockEntityTag;
            CustomData customData = (CustomData)stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (customData != null && (blockEntityTag = customData.copyTag()).contains("FluidName", 8)) {
                String fluidName = blockEntityTag.getString("FluidName");
                Fluid fluid = (Fluid)BuiltInRegistries.FLUID.get(ResourceLocation.parse((String)fluidName));
                int amount = blockEntityTag.getInt("Amount");
                if (fluid != null && amount > 0) {
                    list.add((Component)Component.translatable((String)fluid.getFluidType().getDescriptionId()).withStyle(ChatFormatting.BLUE));
                    list.add((Component)Component.literal((String)(amount + " / " + this.getCapacity() + "mb")).withStyle(ChatFormatting.GRAY));
                }
            }
            list.add((Component)Component.translatable((String)"vehicle.info_help").withStyle(ChatFormatting.YELLOW));
        }
    }

    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player playerEntity, BlockHitResult result) {
        if (!world.isClientSide() && FluidUtil.interactWithFluidHandler((Player)playerEntity, (InteractionHand)InteractionHand.MAIN_HAND, (Level)world, (BlockPos)pos, (Direction)result.getDirection())) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                switch ((Direction.Axis)state.getValue(AXIS)) {
                    case X: {
                        return (BlockState)state.setValue(AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        return (BlockState)state.setValue(AXIS, Direction.Axis.X);
                    }
                }
                return state;
            }
        }
        return state;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AXIS});
        builder.add(new Property[]{INVERTED});
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean inverted = context.getClickedFace().getAxisDirection() == Direction.AxisDirection.NEGATIVE;
        return this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis()).setValue(INVERTED, Boolean.valueOf(inverted));
    }

    public int getCapacity() {
        return (Integer)Config.SERVER.fuelDrumCapacity.get();
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FuelDrumTileEntity(pos, state);
    }
}

