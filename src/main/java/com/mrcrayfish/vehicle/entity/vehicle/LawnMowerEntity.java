/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.BushBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.trailer.StorageTrailerEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LawnMowerEntity
extends LandVehicleEntity {
    public LawnMowerEntity(EntityType<? extends LawnMowerEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxSpeed(8.0f);
        this.setFuelCapacity(5000.0f);
    }

    @Override
    public void updateVehicle() {
        super.updateVehicle();
        if (!this.level().isClientSide && this.getControllingPassenger() != null) {
            AABB axisAligned = this.getBoundingBox().inflate(0.25);
            Vec3 lookVec = this.getLookAngle().scale(0.5);
            int minX = Mth.floor((double)(axisAligned.minX + lookVec.x));
            int maxX = Mth.ceil((double)(axisAligned.maxX + lookVec.x));
            int minZ = Mth.floor((double)(axisAligned.minZ + lookVec.z));
            int maxZ = Mth.ceil((double)(axisAligned.maxZ + lookVec.z));
            for (int x = minX; x < maxX; ++x) {
                for (int z = minZ; z < maxZ; ++z) {
                    BlockPos pos = new BlockPos(x, (int)(axisAligned.minY + 0.5), z);
                    BlockState state = this.level().getBlockState(pos);
                    StorageTrailerEntity trailer = null;
                    if (this.getTrailer() instanceof StorageTrailerEntity) {
                        trailer = (StorageTrailerEntity)this.getTrailer();
                    }
                    if (state.getBlock() instanceof net.minecraft.world.level.block.BushBlock) {
                        List<ItemStack> drops = Block.getDrops((BlockState)state, (ServerLevel)((ServerLevel)this.level()), (BlockPos)pos, (net.minecraft.world.level.block.entity.BlockEntity)null);
                        for (ItemStack stack : drops) {
                            this.addItemToStorage(trailer, stack);
                        }
                        this.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        this.level().playSound(null, pos, state.getBlock().getSoundType(state, (LevelReader)this.level(), pos, (Entity)this).getBreakSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
                        this.level().levelEvent(2001, pos, Block.getId((BlockState)state));
                    }
                }
            }
        }
    }

    private void addItemToStorage(StorageTrailerEntity storageTrailer, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (storageTrailer != null && storageTrailer.getInventory() != null) {
            StorageInventory storage = storageTrailer.getInventory();
            stack = storage.addItem(stack);
            if (!stack.isEmpty()) {
                if (storageTrailer.getTrailer() instanceof StorageTrailerEntity) {
                    this.addItemToStorage((StorageTrailerEntity)storageTrailer.getTrailer(), stack);
                } else {
                    this.spawnItemStack(this.level(), stack);
                }
            }
        } else {
            this.spawnItemStack(this.level(), stack);
        }
    }

    private void spawnItemStack(Level worldIn, ItemStack stack) {
        while (!stack.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(worldIn, this.xo, this.yo, this.zo, stack.split(this.random.nextInt(21) + 10));
            itemEntity.setPickUpDelay(20);
            itemEntity.setDeltaMovement(-this.getDeltaMovement().x / 4.0, this.random.nextGaussian() * 0.05 + 0.2, -this.getDeltaMovement().z / 4.0);
            worldIn.addFreshEntity((Entity)itemEntity);
        }
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_ATV_ENGINE.get();
    }

    @Override
    public boolean canBeColored() {
        return true;
    }

    @Override
    public boolean canTowTrailer() {
        return true;
    }

    @Override
    public boolean isLockable() {
        return false;
    }
}

