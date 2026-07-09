/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.event.tick.LevelTickEvent$Post
 *  net.neoforged.neoforge.event.tick.ServerTickEvent$Post
 */
package com.mrcrayfish.vehicle.common;

import com.mrcrayfish.vehicle.block.FluidPipeBlock;
import com.mrcrayfish.vehicle.tileentity.PipeTileEntity;
import com.mrcrayfish.vehicle.tileentity.PumpTileEntity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class FluidNetworkHandler {
    private static FluidNetworkHandler instance;
    private boolean dirty = false;
    private Map<ResourceKey<Level>, Set<BlockPos>> pipeUpdateMap = new HashMap<ResourceKey<Level>, Set<BlockPos>>();

    public static FluidNetworkHandler instance() {
        if (instance == null) {
            instance = new FluidNetworkHandler();
        }
        return instance;
    }

    private FluidNetworkHandler() {
    }

    public void addPipeForUpdate(PipeTileEntity tileEntity) {
        if (!(tileEntity instanceof PumpTileEntity)) {
            this.dirty = true;
            this.pipeUpdateMap.computeIfAbsent((ResourceKey<Level>)tileEntity.getLevel().dimension(), key -> new HashSet()).add(tileEntity.getBlockPos());
        }
    }

    @SubscribeEvent
    public void onServerTick(LevelTickEvent.Post event) {
        if (!this.dirty) {
            return;
        }
        Set<BlockPos> positions = this.pipeUpdateMap.remove(event.getLevel().dimension());
        if (positions != null) {
            positions.forEach(pos -> {
                BlockEntity tileEntity = event.getLevel().getBlockEntity(pos);
                if (tileEntity instanceof PipeTileEntity) {
                    PipeTileEntity pipeTileEntity = (PipeTileEntity)tileEntity;
                    BlockState state = pipeTileEntity.getBlockState();
                    boolean disabled = pipeTileEntity.getPumps().isEmpty() || event.getLevel().hasNeighborSignal(pos);
                    event.getLevel().setBlock(pos, (BlockState)state.setValue(FluidPipeBlock.DISABLED, Boolean.valueOf(disabled)), 10);
                }
            });
        }
        if (this.pipeUpdateMap.isEmpty()) {
            this.dirty = false;
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        this.dirty = false;
    }
}

