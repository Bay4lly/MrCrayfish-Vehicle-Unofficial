package com.mrcrayfish.vehicle.common;

import com.mrcrayfish.vehicle.block.FluidPipeBlock;
import com.mrcrayfish.vehicle.tileentity.PipeTileEntity;
import com.mrcrayfish.vehicle.tileentity.PumpTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * InteractionHandles updating the disabled state of pipes. This runs after everything has ticked
 * to avoid race conditions.
 *
 * Author: MrCrayfish
 */
public class FluidNetworkHandler
{
    private static FluidNetworkHandler instance;

    public static FluidNetworkHandler instance()
    {
        if(instance == null)
        {
            instance = new FluidNetworkHandler();
        }
        return instance;
    }

    private boolean dirty = false;
    private Map<ResourceKey<Level>, Set<BlockPos>> pipeUpdateMap = new HashMap<>();

    private FluidNetworkHandler() {}

    public void addPipeForUpdate(PipeTileEntity tileEntity)
    {
        if(!(tileEntity instanceof PumpTileEntity))
        {
            this.dirty = true;
            this.pipeUpdateMap.computeIfAbsent(tileEntity.getLevel().dimension(), key -> new HashSet<>()).add(tileEntity.getBlockPos());
        }
    }

    @SubscribeEvent
    public void onServerTick(LevelTickEvent.Post event)
    {
        if(!this.dirty)
            return;

        Set<BlockPos> positions = this.pipeUpdateMap.remove(event.getLevel().dimension());
        if(positions != null)
        {
            positions.forEach(pos ->
            {
                BlockEntity tileEntity = event.getLevel().getBlockEntity(pos);
                if(tileEntity instanceof PipeTileEntity)
                {
                    PipeTileEntity pipeTileEntity = (PipeTileEntity) tileEntity;
                    BlockState state = pipeTileEntity.getBlockState();
                    boolean disabled = pipeTileEntity.getPumps().isEmpty() || event.getLevel().hasNeighborSignal(pos);
                    event.getLevel().setBlock(pos, state.setValue(FluidPipeBlock.DISABLED, disabled), Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
                }
            });
        }

        if(this.pipeUpdateMap.isEmpty())
        {
            this.dirty = false;
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event)
    {
        this.dirty = false;
    }
}
