/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.tileentity.TileEntitySynced;
import com.mrcrayfish.vehicle.util.TileEntityUtil;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PipeTileEntity
extends TileEntitySynced {
    protected Set<BlockPos> pumps = new HashSet<BlockPos>();
    protected boolean[] disabledConnections = new boolean[Direction.values().length];

    public PipeTileEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)ModTileEntities.FLUID_PIPE.get(), pos, state);
    }

    public PipeTileEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
        super(tileEntityType, pos, state);
    }

    public void addPump(BlockPos pos) {
        this.pumps.add(pos);
    }

    public void removePump(BlockPos pos) {
        this.pumps.remove(pos);
    }

    public Set<BlockPos> getPumps() {
        return this.pumps;
    }

    public boolean[] getDisabledConnections() {
        return this.disabledConnections;
    }

    public void setConnectionState(Direction direction, boolean state) {
        this.disabledConnections[direction.get3DDataValue()] = state;
        this.syncDisabledConnections();
    }

    public boolean isConnectionDisabled(Direction direction) {
        return this.disabledConnections[direction.get3DDataValue()];
    }

    public void syncDisabledConnections() {
        if (this.level != null && !this.level.isClientSide()) {
            CompoundTag compound = new CompoundTag();
            this.saveAdditional(compound, (HolderLookup.Provider)this.level.registryAccess());
            TileEntityUtil.sendUpdatePacket((BlockEntity)this, compound);
        }
    }

    public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        if (compound.contains("DisabledConnections", 7)) {
            byte[] connections = compound.getByteArray("DisabledConnections");
            for (int i = 0; i < connections.length; ++i) {
                this.disabledConnections[i] = connections[i] == 1;
            }
        }
    }

    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        this.writeConnections(compound);
    }

    private void writeConnections(CompoundTag compound) {
        byte[] connections = new byte[this.disabledConnections.length];
        for (int i = 0; i < connections.length; ++i) {
            connections[i] = (byte)(this.disabledConnections[i] ? 1 : 0);
        }
        compound.putByteArray("DisabledConnections", connections);
    }
}

