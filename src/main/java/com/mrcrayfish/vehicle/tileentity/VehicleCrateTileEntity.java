package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.util.CommonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.SynchedEntityData.DataItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Random;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class VehicleCrateTileEntity extends TileEntitySynced
{
    private static final Random RAND = new Random();

    private ResourceLocation entityId;
    private int color = VehicleEntity.DYE_TO_COLOR[0];
    private ItemStack engineStack = ItemStack.EMPTY;
    private ItemStack wheelStack = ItemStack.EMPTY;
    private boolean opened = false;
    private int timer;
    private UUID opener;

    @OnlyIn(Dist.CLIENT)
    private Entity entity;

    public VehicleCrateTileEntity(BlockPos pos, BlockState state)
    {
        super(ModTileEntities.VEHICLE_CRATE.get(), pos, state);
    }

    public void setEntityId(ResourceLocation entityId)
    {
        this.entityId = entityId;
        this.setChanged();
    }

    public ResourceLocation getEntityId()
    {
        return entityId;
    }

    public void open(UUID opener)
    {
        if(this.entityId != null)
        {
            this.opened = true;
            this.opener = opener;
            this.syncToClient();
        }
    }

    public boolean isOpened()
    {
        return opened;
    }

    public int getTimer()
    {
        return timer;
    }

    @OnlyIn(Dist.CLIENT)
    public <E extends Entity> E getEntity()
    {
        return (E) entity;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VehicleCrateTileEntity blockEntity)
    {
        if(blockEntity.opened)
        {
            blockEntity.timer += 5;
            if(blockEntity.level != null && blockEntity.level.isClientSide())
            {
                if(blockEntity.entityId != null && blockEntity.entity == null)
                {
                    EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(blockEntity.entityId);
                    if(entityType != null)
                    {
                        blockEntity.entity = entityType.create(blockEntity.level);
                        if(blockEntity.entity != null)
                        {
                            VehicleHelper.playSound(SoundEvents.ITEM_BREAK, blockEntity.worldPosition, 1.0F, 0.5F);
                            Collection<DataItem<?>> entryList = blockEntity.entity.getEntityData().itemsById.values();
                            entryList.forEach(dataEntry -> blockEntity.entity.onSyncedDataUpdated(dataEntry.getAccessor()));
                            if(blockEntity.entity instanceof VehicleEntity)
                            {
                                ((VehicleEntity) blockEntity.entity).setColor(blockEntity.color);
                            }
                            if(blockEntity.entity instanceof PoweredVehicleEntity)
                            {
                                PoweredVehicleEntity entityPoweredVehicle = (PoweredVehicleEntity) blockEntity.entity;
                                if(blockEntity.engineStack != null)
                                {
                                    entityPoweredVehicle.setEngineStack(blockEntity.engineStack);
                                }
                                if(!blockEntity.wheelStack.isEmpty())
                                {
                                    entityPoweredVehicle.setWheelStack(blockEntity.wheelStack);
                                }
                            }
                        }
                        else
                        {
                            blockEntity.entityId = null;
                        }
                    }
                    else
                    {
                        blockEntity.entityId = null;
                    }
                }
                if(blockEntity.timer == 90 || blockEntity.timer == 110 || blockEntity.timer == 130 || blockEntity.timer == 150)
                {
                    float pitch = (float) (0.9F + 0.2F * RAND.nextDouble());
                    VehicleHelper.playSound(ModSounds.BLOCK_VEHICLE_CRATE_PANEL_LAND.get(), blockEntity.worldPosition, 1.0F, pitch);
                }
                if(blockEntity.timer == 150)
                {
                    VehicleHelper.playSound(SoundEvents.GENERIC_EXPLODE, blockEntity.worldPosition, 1.0F, 1.0F);
                    blockEntity.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, false, blockEntity.worldPosition.getX() + 0.5, blockEntity.worldPosition.getY() + 0.5, blockEntity.worldPosition.getZ() + 0.5, 0, 0, 0);
                }
            }
            if(!blockEntity.level.isClientSide && blockEntity.timer > 250)
            {
                BlockState state1 = blockEntity.level.getBlockState(blockEntity.worldPosition);
                Direction facing = state1.getValue(VehicleCrateBlock.DIRECTION);
                EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(blockEntity.entityId);
                if(entityType != null)
                {
                    Entity entity = entityType.create(blockEntity.level);
                    if(entity != null)
                    {
                        if(entity instanceof VehicleEntity)
                        {
                            ((VehicleEntity) entity).setColor(blockEntity.color);
                        }
                        if(blockEntity.opener != null && entity instanceof PoweredVehicleEntity)
                        {
                            PoweredVehicleEntity poweredVehicle = (PoweredVehicleEntity) entity;
                            poweredVehicle.setOwner(blockEntity.opener);
                            if(!blockEntity.engineStack.isEmpty())
                            {
                                poweredVehicle.setEngineStack(blockEntity.engineStack);
                            }
                            if(!blockEntity.wheelStack.isEmpty())
                            {
                                poweredVehicle.setWheelStack(blockEntity.wheelStack);
                            }
                        }
                        entity.absMoveTo(blockEntity.worldPosition.getX() + 0.5, blockEntity.worldPosition.getY(), blockEntity.worldPosition.getZ() + 0.5, facing.get2DDataValue() * 90F + 180F, 0F);
                        entity.setYHeadRot(facing.get2DDataValue() * 90F + 180F);
                        blockEntity.level.addFreshEntity(entity);
                    }
                    blockEntity.level.setBlockAndUpdate(blockEntity.worldPosition, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);
        if(compound.contains("Vehicle", Tag.TAG_STRING))
        {
            this.entityId = new ResourceLocation(compound.getString("Vehicle"));
        }
        if(compound.contains("Color", Tag.TAG_INT))
        {
            this.color = compound.getInt("Color");
        }
        if(compound.contains("EngineStack", Tag.TAG_COMPOUND))
        {
            this.engineStack = ItemStack.of(compound.getCompound("EngineStack"));
        }
        else if(compound.getBoolean("Creative"))
        {
            VehicleProperties properties = VehicleProperties.get(this.entityId);
            EngineItem engineItem = VehicleRegistry.getEngineItem(properties.getEngineType(), EngineTier.IRON);
            this.engineStack = engineItem != null ? new ItemStack(engineItem) : ItemStack.EMPTY;
        }
        if(compound.contains("WheelStack", Tag.TAG_COMPOUND))
        {
            this.wheelStack = ItemStack.of(compound.getCompound("WheelStack"));
        }
        else
        {
            this.wheelStack = new ItemStack(ModItems.STANDARD_WHEEL.get());
        }
        if(compound.contains("Opener", Tag.TAG_STRING))
        {
            this.opener = compound.getUUID("Opener");
        }
        if(compound.contains("Opened", Tag.TAG_BYTE))
        {
            this.opened = compound.getBoolean("Opened");
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound)
    {
        super.saveAdditional(compound);
        if(this.entityId != null)
        {
            compound.putString("Vehicle", this.entityId.toString());
        }
        if(this.opener != null)
        {
            compound.putUUID("Opener", this.opener);
        }
        if(!this.engineStack.isEmpty())
        {
            CommonUtils.writeItemStackToTag(compound, "EngineStack", this.engineStack);
        }
        if(!this.wheelStack.isEmpty())
        {
            CommonUtils.writeItemStackToTag(compound, "WheelStack", this.wheelStack);
        }
        compound.putInt("Color", this.color);
        compound.putBoolean("Opened", this.opened);
    }

    @Override
    public AABB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }
}
