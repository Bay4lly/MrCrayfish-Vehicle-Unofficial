/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 */
package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.util.HermiteInterpolator;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.tileentity.TileEntitySynced;
import com.mrcrayfish.vehicle.util.TileEntityUtil;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class GasPumpTileEntity
extends TileEntitySynced {
    private int fuelingEntityId;
    private Player fuelingEntity;
    private HermiteInterpolator cachedSpline;
    private boolean recentlyUsed;

    public GasPumpTileEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)ModTileEntities.GAS_PUMP.get(), pos, state);
    }

    public HermiteInterpolator getCachedSpline() {
        return this.cachedSpline;
    }

    public void setCachedSpline(HermiteInterpolator cachedSpline) {
        this.cachedSpline = cachedSpline;
    }

    public boolean isRecentlyUsed() {
        return this.recentlyUsed;
    }

    public void setRecentlyUsed(boolean recentlyUsed) {
        this.recentlyUsed = recentlyUsed;
    }

    @Nullable
    public FluidTank getTank() {
        BlockEntity tileEntity = this.level.getBlockEntity(this.worldPosition.below());
        if (tileEntity instanceof GasPumpTankTileEntity) {
            return ((GasPumpTankTileEntity)tileEntity).getFluidTank();
        }
        return null;
    }

    public Player getFuelingEntity() {
        return this.fuelingEntity;
    }

    public void setFuelingEntity(@Nullable Player entity) {
        if (!this.level.isClientSide) {
            if (this.fuelingEntity != null) {
                ModDataKeys.GAS_PUMP.setValue(this.fuelingEntity, Optional.empty());
            }
            this.fuelingEntity = null;
            this.fuelingEntityId = -1;
            if (entity != null) {
                this.fuelingEntityId = entity.getId();
                ModDataKeys.GAS_PUMP.setValue((Player)entity, Optional.of(this.getBlockPos()));
            }
            this.syncToClient();
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GasPumpTileEntity blockEntity) {
        if (blockEntity.fuelingEntityId != -1) {
            if (blockEntity.fuelingEntity == null) {
                Entity entity = blockEntity.level.getEntity(blockEntity.fuelingEntityId);
                if (entity instanceof Player) {
                    blockEntity.fuelingEntity = (Player)entity;
                } else if (!blockEntity.level.isClientSide) {
                    blockEntity.fuelingEntityId = -1;
                    blockEntity.syncFuelingEntity();
                }
            }
        } else if (blockEntity.level.isClientSide && blockEntity.fuelingEntity != null) {
            blockEntity.fuelingEntity = null;
        }
        if (!(blockEntity.level.isClientSide || blockEntity.fuelingEntity == null || !(Math.sqrt(blockEntity.fuelingEntity.distanceToSqr((double)blockEntity.worldPosition.getX() + 0.5, (double)blockEntity.worldPosition.getY() + 0.5, (double)blockEntity.worldPosition.getZ() + 0.5)) > (Double)Config.SERVER.maxHoseDistance.get()) && blockEntity.fuelingEntity.isAlive())) {
            if (blockEntity.fuelingEntity.isAlive()) {
                blockEntity.level.playSound(null, blockEntity.fuelingEntity.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            ModDataKeys.GAS_PUMP.setValue(blockEntity.fuelingEntity, Optional.empty());
            blockEntity.fuelingEntityId = -1;
            blockEntity.fuelingEntity = null;
            blockEntity.syncFuelingEntity();
        }
    }

    public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        if (compound.contains("FuelingEntity", 3)) {
            this.fuelingEntityId = compound.getInt("FuelingEntity");
        }
    }

    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        compound.putInt("FuelingEntity", this.fuelingEntityId);
    }

    private void syncFuelingEntity() {
        CompoundTag compound = new CompoundTag();
        this.saveAdditional(compound, (HolderLookup.Provider)this.level.registryAccess());
        TileEntityUtil.sendUpdatePacket((BlockEntity)this, compound);
    }
}

