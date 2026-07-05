package com.mrcrayfish.vehicle.block;

import com.google.common.base.Strings;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.tileentity.VehicleCrateTileEntity;
import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class VehicleCrateBlock extends RotatedObjectBlock
{

    @Override
    public MapCodec<? extends VehicleCrateBlock> codec() { return MapCodec.unit(this); }
    public static final List<ResourceLocation> REGISTERED_CRATES = new ArrayList<>();
    private static final VoxelShape PANEL = box(0, 0, 0, 16, 2, 16);

    public VehicleCrateBlock()
    {
        super(BlockBehaviour.Properties.of().mapColor(DyeColor.LIGHT_GRAY).dynamicShape().noOcclusion().strength(1.5F, 5.0F));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos)
    {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if(te instanceof VehicleCrateTileEntity && ((VehicleCrateTileEntity)te).isOpened())
            return PANEL;
        return Shapes.block();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos)
    {
        return this.isBelowBlockTopSolid(reader, pos) && this.canOpen(reader, pos);
    }

    private boolean canOpen(LevelReader reader, BlockPos pos)
    {
        for(Direction side : Direction.Plane.HORIZONTAL)
        {
            BlockPos adjacentPos = pos.relative(side);
            BlockState state = reader.getBlockState(adjacentPos);
            if(state.isAir())
                continue;
            if(!state.canBeReplaced() || this.isBelowBlockTopSolid(reader, adjacentPos))
            {
                return false;
            }
        }
        return true;
    }

    private boolean isBelowBlockTopSolid(LevelReader reader, BlockPos pos)
    {
        return reader.getBlockState(pos.below()).isFaceSturdy(reader, pos.below(), Direction.UP);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player playerEntity, BlockHitResult result)
    {
        if(result.getDirection() == Direction.UP && playerEntity.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND).getItem() == ModItems.WRENCH.get())
        {
            this.openCrate(world, pos, state, playerEntity);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity livingEntity, ItemStack stack)
    {
        net.minecraft.world.item.component.CustomData customData = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if(customData != null && world.getBlockEntity(pos) instanceof VehicleCrateTileEntity crate)
        {
            net.minecraft.nbt.CompoundTag tag = customData.copyTag();
            if(tag.contains("Vehicle", net.minecraft.nbt.Tag.TAG_STRING))
            {
                crate.setEntityId(net.minecraft.resources.ResourceLocation.parse(tag.getString("Vehicle")));
                if(tag.getBoolean("Creative"))
                {
                    VehicleProperties properties = VehicleProperties.get(crate.getEntityId());
                    if(properties != null)
                    {
                        EngineItem engineItem = VehicleRegistry.getEngineItem(properties.getEngineType(), EngineTier.IRON);
                        if(engineItem != null) crate.setEngineStack(new net.minecraft.world.item.ItemStack(engineItem));
                        crate.setWheelStack(new net.minecraft.world.item.ItemStack(com.mrcrayfish.vehicle.init.ModItems.STANDARD_WHEEL.get()));
                    }
                }
            }
        }
        if(livingEntity instanceof Player && ((Player) livingEntity).isCreative())
        {
            this.openCrate(world, pos, state, livingEntity);
        }
    }

    private void openCrate(Level world, BlockPos pos, BlockState state, LivingEntity placer)
    {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity instanceof VehicleCrateTileEntity && this.canOpen(world, pos))
        {
            if(world.isClientSide)
            {
                this.spawnCrateOpeningParticles((ClientLevel) world, pos, state);
            }
            else
            {
                ((VehicleCrateTileEntity) tileEntity).open(placer.getUUID());
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnCrateOpeningParticles(ClientLevel world, BlockPos pos, BlockState state)
    {
        double y = 0.875;
        double x, z;
        TerrainParticle.Provider factory = new TerrainParticle.Provider();
        for(int j = 0; j < 4; ++j)
        {
            for(int l = 0; l < 4; ++l)
            {
                x = (j + 0.5D) / 4.0D;
                z = (l + 0.5D) / 4.0D;
                Minecraft.getInstance().particleEngine.add(factory.createParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), world, pos.getX() + x, pos.getY() + y, pos.getZ() + z, x - 0.5D, y - 0.5D, z - 0.5D));
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new VehicleCrateTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, ModTileEntities.VEHICLE_CRATE.get(), VehicleCrateTileEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> list, TooltipFlag advanced)
    {
        Component vehicleName = EntityType.PIG.getDescription();
        net.minecraft.world.item.component.CustomData customData = stack.get(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA);
        if(customData == null) customData = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if(customData != null)
        {
            CompoundTag blockEntityTag = customData.copyTag();
            String entityType = blockEntityTag.getString("Vehicle");
            if(!Strings.isNullOrEmpty(entityType))
            {
                vehicleName = EntityType.byString(entityType).orElse(EntityType.PIG).getDescription();
            }
        }
        if(Screen.hasShiftDown())
        {
            list.addAll(RenderUtil.lines(Component.translatable(this.getDescriptionId() + ".info", vehicleName), 150));
        }
        else
        {
            list.add(vehicleName.copy().withStyle(ChatFormatting.BLUE));
            list.add(Component.translatable("vehicle.info_help").withStyle(ChatFormatting.YELLOW));
        }
    }

    public static ItemStack create(net.minecraft.core.HolderLookup.Provider registries, ResourceLocation entityId, int color, ItemStack engine, ItemStack wheel)
    {
        CompoundTag blockEntityTag = new CompoundTag();
        blockEntityTag.putString("Vehicle", entityId.toString());
        blockEntityTag.putString("id", "vehicle:vehicle_crate");
        blockEntityTag.putInt("Color", color);
        if (engine != null && !engine.isEmpty())
        {
            com.mrcrayfish.vehicle.util.CommonUtils.writeItemStackToTag(registries, blockEntityTag, "EngineStack", engine);
        }
        if (wheel != null && !wheel.isEmpty())
        {
            com.mrcrayfish.vehicle.util.CommonUtils.writeItemStackToTag(registries, blockEntityTag, "WheelStack", wheel);
        }
        ItemStack stack = new ItemStack(ModBlocks.VEHICLE_CRATE.get());
        stack.set(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA, net.minecraft.world.item.component.CustomData.of(blockEntityTag));
        return stack;
    }

    public static synchronized void registerVehicle(ResourceLocation id)
    {
        if(!REGISTERED_CRATES.contains(id))
        {
            REGISTERED_CRATES.add(id);
            Collections.sort(REGISTERED_CRATES);
        }
    }
}
