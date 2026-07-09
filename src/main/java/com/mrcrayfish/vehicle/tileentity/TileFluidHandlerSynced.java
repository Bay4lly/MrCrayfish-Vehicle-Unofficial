/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.Connection
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 */
package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.util.TileEntityUtil;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class TileFluidHandlerSynced
extends BlockEntity {
    protected FluidTank tank;

    public TileFluidHandlerSynced(@Nonnull BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, int capacity) {
        super(tileEntityTypeIn, pos, state);
        this.tank = new FluidTank(capacity){

            protected void onContentsChanged() {
                TileFluidHandlerSynced.this.syncFluidToClient();
            }
        };
    }

    public TileFluidHandlerSynced(@Nonnull BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, int capacity, Predicate<FluidStack> validator) {
        super(tileEntityTypeIn, pos, state);
        this.tank = new FluidTank(capacity, validator){

            protected void onContentsChanged() {
                TileFluidHandlerSynced.this.syncFluidToClient();
            }
        };
    }

    public void syncFluidToClient() {
        if (this.level != null && !this.level.isClientSide) {
            CompoundTag compound = new CompoundTag();
            this.saveAdditional(compound, (HolderLookup.Provider)this.level.registryAccess());
            TileEntityUtil.sendUpdatePacket((BlockEntity)this, compound);
        }
    }

    public void syncFluidToPlayer(ServerPlayer player) {
        if (this.level != null && !this.level.isClientSide) {
            CompoundTag compound = new CompoundTag();
            this.saveAdditional(compound, (HolderLookup.Provider)this.level.registryAccess());
            TileEntityUtil.sendUpdatePacket((BlockEntity)this, compound);
        }
    }

    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.tank.readFromNBT(registries, tag);
    }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.tank.writeToNBT(registries, tag);
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithId(registries);
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create((BlockEntity)this);
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag nbt = pkt.getTag();
        if (nbt != null) {
            this.loadAdditional(nbt, registries);
        }
    }

    public FluidTank getFluidTank() {
        return this.tank;
    }
}

