/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.syncher.SynchedEntityData$DataItem
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
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
import com.mrcrayfish.vehicle.tileentity.TileEntitySynced;
import com.mrcrayfish.vehicle.util.CommonUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class VehicleCrateTileEntity
extends TileEntitySynced {
    private static final Random RAND = new Random();
    private ResourceLocation entityId;
    private int color = VehicleEntity.DYE_TO_COLOR[0];
    private ItemStack engineStack = ItemStack.EMPTY;
    private ItemStack wheelStack = ItemStack.EMPTY;
    private boolean opened = false;
    private int timer;
    private UUID opener;
    @OnlyIn(value=Dist.CLIENT)
    private Entity entity;

    public VehicleCrateTileEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)ModTileEntities.VEHICLE_CRATE.get(), pos, state);
    }

    public void setEntityId(ResourceLocation entityId) {
        this.entityId = entityId;
        this.setChanged();
    }

    public ResourceLocation getEntityId() {
        return this.entityId;
    }

    public void setEngineStack(ItemStack stack) {
        this.engineStack = stack;
    }

    public void setWheelStack(ItemStack stack) {
        this.wheelStack = stack;
    }

    public void open(UUID opener) {
        if (this.entityId != null) {
            this.opened = true;
            this.opener = opener;
            this.syncToClient();
        }
    }

    public boolean isOpened() {
        return this.opened;
    }

    public int getTimer() {
        return this.timer;
    }

    @OnlyIn(value=Dist.CLIENT)
    public <E extends Entity> E getEntity() {
        return (E)this.entity;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VehicleCrateTileEntity blockEntity) {
        if (blockEntity.opened) {
            blockEntity.timer += 5;
            if (blockEntity.level != null && blockEntity.level.isClientSide()) {
                if (blockEntity.entityId != null && blockEntity.entity == null) {
                    EntityType entityType = (EntityType)BuiltInRegistries.ENTITY_TYPE.get(blockEntity.entityId);
                    if (entityType != null) {
                        blockEntity.entity = entityType.create(blockEntity.level);
                        if (blockEntity.entity != null) {
                            VehicleHelper.playSound(SoundEvents.ITEM_BREAK, blockEntity.worldPosition, 1.0f, 0.5f);
                            List<SynchedEntityData.DataItem> entryList = Arrays.asList(blockEntity.entity.getEntityData().itemsById);
                            entryList.forEach(dataEntry -> blockEntity.entity.onSyncedDataUpdated(dataEntry.getAccessor()));
                            if (blockEntity.entity instanceof VehicleEntity) {
                                ((VehicleEntity)blockEntity.entity).setColor(blockEntity.color);
                            }
                            if (blockEntity.entity instanceof PoweredVehicleEntity) {
                                PoweredVehicleEntity entityPoweredVehicle = (PoweredVehicleEntity)blockEntity.entity;
                                if (blockEntity.engineStack != null) {
                                    entityPoweredVehicle.setEngineStack(blockEntity.engineStack);
                                }
                                if (!blockEntity.wheelStack.isEmpty()) {
                                    entityPoweredVehicle.setWheelStack(blockEntity.wheelStack);
                                }
                            }
                        } else {
                            blockEntity.entityId = null;
                        }
                    } else {
                        blockEntity.entityId = null;
                    }
                }
                if (blockEntity.timer == 90 || blockEntity.timer == 110 || blockEntity.timer == 130 || blockEntity.timer == 150) {
                    float pitch = (float)((double)0.9f + (double)0.2f * RAND.nextDouble());
                    VehicleHelper.playSound((SoundEvent)ModSounds.BLOCK_VEHICLE_CRATE_PANEL_LAND.get(), blockEntity.worldPosition, 1.0f, pitch);
                }
                if (blockEntity.timer == 150) {
                    VehicleHelper.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), blockEntity.worldPosition, 1.0f, 1.0f);
                    blockEntity.level.addParticle((ParticleOptions)ParticleTypes.EXPLOSION_EMITTER, false, (double)blockEntity.worldPosition.getX() + 0.5, (double)blockEntity.worldPosition.getY() + 0.5, (double)blockEntity.worldPosition.getZ() + 0.5, 0.0, 0.0, 0.0);
                }
            }
            if (!blockEntity.level.isClientSide && blockEntity.timer > 250) {
                BlockState state1 = blockEntity.level.getBlockState(blockEntity.worldPosition);
                Direction facing = (Direction)state1.getValue((Property)VehicleCrateBlock.DIRECTION);
                EntityType entityType = (EntityType)BuiltInRegistries.ENTITY_TYPE.get(blockEntity.entityId);
                if (entityType != null) {
                    Entity entity = entityType.create(blockEntity.level);
                    if (entity != null) {
                        if (entity instanceof VehicleEntity) {
                            ((VehicleEntity)entity).setColor(blockEntity.color);
                        }
                        if (blockEntity.opener != null && entity instanceof PoweredVehicleEntity) {
                            PoweredVehicleEntity poweredVehicle = (PoweredVehicleEntity)entity;
                            poweredVehicle.setOwner(blockEntity.opener);
                            if (!blockEntity.engineStack.isEmpty()) {
                                poweredVehicle.setEngineStack(blockEntity.engineStack);
                            }
                            if (!blockEntity.wheelStack.isEmpty()) {
                                poweredVehicle.setWheelStack(blockEntity.wheelStack);
                            }
                        }
                        entity.absMoveTo((double)blockEntity.worldPosition.getX() + 0.5, (double)blockEntity.worldPosition.getY(), (double)blockEntity.worldPosition.getZ() + 0.5, (float)facing.get2DDataValue() * 90.0f + 180.0f, 0.0f);
                        entity.setYHeadRot((float)facing.get2DDataValue() * 90.0f + 180.0f);
                        blockEntity.level.addFreshEntity(entity);
                    }
                    blockEntity.level.setBlockAndUpdate(blockEntity.worldPosition, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        if (compound.contains("Vehicle", 8)) {
            this.entityId = ResourceLocation.parse((String)compound.getString("Vehicle"));
        }
        if (compound.contains("Color", 3)) {
            this.color = compound.getInt("Color");
        }
        if (compound.contains("EngineStack", 10)) {
            this.engineStack = CommonUtils.readItemStackFromTag(registries, compound, "EngineStack");
        } else if (compound.getBoolean("Creative")) {
            VehicleProperties properties = VehicleProperties.get(this.entityId);
            EngineItem engineItem = VehicleRegistry.getEngineItem(properties.getEngineType(), EngineTier.IRON);
            this.engineStack = engineItem != null ? new ItemStack((ItemLike)engineItem) : ItemStack.EMPTY;
        }
        this.wheelStack = compound.contains("WheelStack", 10) ? CommonUtils.readItemStackFromTag(registries, compound, "WheelStack") : new ItemStack((ItemLike)ModItems.STANDARD_WHEEL.get());
        if (compound.contains("Opener", 8)) {
            this.opener = compound.getUUID("Opener");
        }
        if (compound.contains("Opened", 1)) {
            this.opened = compound.getBoolean("Opened");
        }
    }

    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        if (this.entityId != null) {
            compound.putString("Vehicle", this.entityId.toString());
        }
        if (this.opener != null) {
            compound.putUUID("Opener", this.opener);
        }
        if (!this.engineStack.isEmpty()) {
            CommonUtils.writeItemStackToTag(registries, compound, "EngineStack", this.engineStack);
        }
        if (!this.wheelStack.isEmpty()) {
            CommonUtils.writeItemStackToTag(registries, compound, "WheelStack", this.wheelStack);
        }
        compound.putInt("Color", this.color);
        compound.putBoolean("Opened", this.opened);
    }
}

