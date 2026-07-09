/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.TerrainParticle$Provider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Plane
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.mrcrayfish.vehicle.block;

import com.google.common.base.Strings;
import com.mojang.serialization.MapCodec;
import com.mrcrayfish.vehicle.block.RotatedObjectBlock;
import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.tileentity.VehicleCrateTileEntity;
import com.mrcrayfish.vehicle.util.CommonUtils;
import com.mrcrayfish.vehicle.util.RenderUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class VehicleCrateBlock
extends RotatedObjectBlock {
    public static final List<ResourceLocation> REGISTERED_CRATES = new ArrayList<ResourceLocation>();
    private static final VoxelShape PANEL = VehicleCrateBlock.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)2.0, (double)16.0);

    public MapCodec<? extends VehicleCrateBlock> codec() {
        return MapCodec.unit(this);
    }

    public VehicleCrateBlock() {
        super(BlockBehaviour.Properties.of().mapColor(DyeColor.LIGHT_GRAY).dynamicShape().noOcclusion().strength(1.5f, 5.0f));
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof VehicleCrateTileEntity && ((VehicleCrateTileEntity)te).isOpened()) {
            return PANEL;
        }
        return Shapes.block();
    }

    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        return this.isBelowBlockTopSolid(reader, pos) && this.canOpen(reader, pos);
    }

    private boolean canOpen(LevelReader reader, BlockPos pos) {
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockPos adjacentPos = pos.relative(side);
            BlockState state = reader.getBlockState(adjacentPos);
            if (state.isAir() || state.canBeReplaced() && !this.isBelowBlockTopSolid(reader, adjacentPos)) continue;
            return false;
        }
        return true;
    }

    private boolean isBelowBlockTopSolid(LevelReader reader, BlockPos pos) {
        return reader.getBlockState(pos.below()).isFaceSturdy((BlockGetter)reader, pos.below(), Direction.UP);
    }

    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player playerEntity, BlockHitResult result) {
        if (result.getDirection() == Direction.UP && playerEntity.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ModItems.WRENCH.get()) {
            this.openCrate(world, pos, state, (LivingEntity)playerEntity);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity livingEntity, ItemStack stack) {
        BlockEntity blockEntity;
        CustomData customData = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && (blockEntity = world.getBlockEntity(pos)) instanceof VehicleCrateTileEntity) {
            VehicleCrateTileEntity crate = (VehicleCrateTileEntity)blockEntity;
            CompoundTag tag = customData.copyTag();
            if (tag.contains("Vehicle", 8)) {
                VehicleProperties properties;
                crate.setEntityId(ResourceLocation.parse((String)tag.getString("Vehicle")));
                if (tag.getBoolean("Creative") && (properties = VehicleProperties.get(crate.getEntityId())) != null) {
                    EngineItem engineItem = VehicleRegistry.getEngineItem(properties.getEngineType(), EngineTier.IRON);
                    if (engineItem != null) {
                        crate.setEngineStack(new ItemStack((ItemLike)engineItem));
                    }
                    crate.setWheelStack(new ItemStack((ItemLike)ModItems.STANDARD_WHEEL.get()));
                }
            }
        }
        if (livingEntity instanceof Player && ((Player)livingEntity).isCreative()) {
            this.openCrate(world, pos, state, livingEntity);
        }
    }

    private void openCrate(Level world, BlockPos pos, BlockState state, LivingEntity placer) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof VehicleCrateTileEntity && this.canOpen((LevelReader)world, pos)) {
            if (world.isClientSide) {
                this.spawnCrateOpeningParticles((ClientLevel)world, pos, state);
            } else {
                ((VehicleCrateTileEntity)tileEntity).open(placer.getUUID());
            }
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    private void spawnCrateOpeningParticles(ClientLevel world, BlockPos pos, BlockState state) {
        double y = 0.875;
        TerrainParticle.Provider factory = new TerrainParticle.Provider();
        for (int j = 0; j < 4; ++j) {
            for (int l = 0; l < 4; ++l) {
                double x = ((double)j + 0.5) / 4.0;
                double z = ((double)l + 0.5) / 4.0;
                Minecraft.getInstance().particleEngine.add(factory.createParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), world, (double)pos.getX() + x, (double)pos.getY() + y, (double)pos.getZ() + z, x - 0.5, y - 0.5, z - 0.5));
            }
        }
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VehicleCrateTileEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return VehicleCrateBlock.createTickerHelper(type, (BlockEntityType)((BlockEntityType)ModTileEntities.VEHICLE_CRATE.get()), VehicleCrateTileEntity::tick);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag advanced) {
        CompoundTag blockEntityTag;
        String entityType;
        Component vehicleName = EntityType.PIG.getDescription();
        CustomData customData = (CustomData)stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData == null) {
            customData = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
        }
        if (customData != null && !Strings.isNullOrEmpty((String)(entityType = (blockEntityTag = customData.copyTag()).getString("Vehicle")))) {
            vehicleName = EntityType.byString((String)entityType).orElse(EntityType.PIG).getDescription();
        }
        if (Screen.hasShiftDown()) {
            list.addAll(RenderUtil.lines((FormattedText)Component.translatable((String)(this.getDescriptionId() + ".info"), (Object[])new Object[]{vehicleName}), 150));
        } else {
            list.add((Component)vehicleName.copy().withStyle(ChatFormatting.BLUE));
            list.add((Component)Component.translatable((String)"vehicle.info_help").withStyle(ChatFormatting.YELLOW));
        }
    }

    public static ItemStack create(HolderLookup.Provider registries, ResourceLocation entityId, int color, ItemStack engine, ItemStack wheel) {
        CompoundTag blockEntityTag = new CompoundTag();
        blockEntityTag.putString("Vehicle", entityId.toString());
        blockEntityTag.putString("id", "vehicle:vehicle_crate");
        blockEntityTag.putInt("Color", color);
        if (engine != null && !engine.isEmpty()) {
            CommonUtils.writeItemStackToTag(registries, blockEntityTag, "EngineStack", engine);
        }
        if (wheel != null && !wheel.isEmpty()) {
            CommonUtils.writeItemStackToTag(registries, blockEntityTag, "WheelStack", wheel);
        }
        ItemStack stack = new ItemStack((ItemLike)ModBlocks.VEHICLE_CRATE.get());
        stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of((CompoundTag)blockEntityTag));
        return stack;
    }

    public static synchronized void registerVehicle(ResourceLocation id) {
        if (!REGISTERED_CRATES.contains(id)) {
            REGISTERED_CRATES.add(id);
            Collections.sort(REGISTERED_CRATES);
        }
    }
}

