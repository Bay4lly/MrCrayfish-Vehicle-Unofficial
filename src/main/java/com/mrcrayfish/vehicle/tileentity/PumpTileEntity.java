/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.mrcrayfish.vehicle.tileentity;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.FluidPipeBlock;
import com.mrcrayfish.vehicle.block.FluidPumpBlock;
import com.mrcrayfish.vehicle.common.FluidNetworkHandler;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.tileentity.PipeTileEntity;
import com.mrcrayfish.vehicle.util.FluidUtils;
import com.mrcrayfish.vehicle.util.TileEntityUtil;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

public class PumpTileEntity
extends PipeTileEntity {
    private int lastHandlerIndex;
    private boolean validatedNetwork;
    private Map<BlockPos, PipeNode> fluidNetwork = new HashMap<BlockPos, PipeNode>();
    private List<Pair<BlockPos, Direction>> fluidHandlers = new ArrayList<Pair<BlockPos, Direction>>();
    private PowerMode powerMode = PowerMode.ALWAYS_ACTIVE;

    public PumpTileEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)ModTileEntities.FLUID_PUMP.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PumpTileEntity blockEntity) {
        if (blockEntity.level != null && !blockEntity.level.isClientSide()) {
            if (!blockEntity.validatedNetwork) {
                blockEntity.validatedNetwork = true;
                blockEntity.generatePipeNetwork();
            }
            blockEntity.pumpFluid();
        }
    }

    public PowerMode getPowerMode() {
        return this.powerMode;
    }

    public Map<BlockPos, PipeNode> getFluidNetwork() {
        return ImmutableMap.copyOf(this.fluidNetwork);
    }

    public void invalidatePipeNetwork() {
        this.validatedNetwork = false;
    }

    private void pumpFluid() {
        if (this.fluidHandlers.isEmpty() || this.level == null) {
            return;
        }
        if (!this.powerMode.test(this)) {
            return;
        }
        List<IFluidHandler> handlers = this.getFluidInteractionHandlersOnNetwork(this.level);
        if (handlers.isEmpty()) {
            return;
        }
        Optional<IFluidHandler> source = this.getSourceFluidInteractionHandler(this.level);
        if (!source.isPresent()) {
            return;
        }
        IFluidHandler sourceInteractionHandler = source.get();
        int outputCount = handlers.size();
        int remainingAmount = Math.min(sourceInteractionHandler.getFluidInTank(0).getAmount(), (Integer)Config.SERVER.pumpTransferAmount.get());
        int splitAmount = remainingAmount / outputCount;
        if (splitAmount > 0) {
            ListIterator<IFluidHandler> it = handlers.listIterator();
            while (it.hasNext()) {
                int transferredAmount = FluidUtils.transferFluid(sourceInteractionHandler, (IFluidHandler)it.next(), splitAmount);
                remainingAmount -= transferredAmount;
                if (transferredAmount >= splitAmount) continue;
                it.remove();
            }
        }
        if (remainingAmount <= 0) {
            return;
        }
        if (handlers.size() == 1) {
            FluidUtils.transferFluid(sourceInteractionHandler, handlers.get(0), remainingAmount);
            return;
        }
        while (remainingAmount > 0 && !handlers.isEmpty()) {
            int index = this.lastHandlerIndex++ % handlers.size();
            int transferred = FluidUtils.transferFluid(sourceInteractionHandler, handlers.get(index), 1);
            remainingAmount -= transferred;
            if (transferred != 0) continue;
            --this.lastHandlerIndex;
            handlers.remove(index);
        }
    }

    private void generatePipeNetwork() {
        Preconditions.checkNotNull((Object)this.level);
        this.removePumpFromPipes();
        this.lastHandlerIndex = 0;
        this.fluidHandlers.clear();
        this.fluidNetwork.clear();
        if (!this.powerMode.test(this)) {
            return;
        }
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<BlockPos>();
        queue.add(this.worldPosition);
        while (!queue.isEmpty()) {
            BlockPos pos2 = (BlockPos)queue.poll();
            for (Direction direction : Direction.values()) {
                BlockState relativeState;
                BlockState selfState;
                BlockPos relativePos = pos2.relative(direction);
                if (visited.contains(relativePos) || (selfState = this.level.getBlockState(pos2)).getBlock() instanceof FluidPipeBlock && (!(selfState.getBlock() instanceof FluidPumpBlock) && this.level.hasNeighborSignal(pos2) || !((Boolean)selfState.getValue((Property)FluidPipeBlock.CONNECTED_PIPES[direction.get3DDataValue()])).booleanValue() || selfState.getBlock() instanceof FluidPumpBlock && ((Boolean)selfState.getValue((Property)FluidPumpBlock.DISABLED)).booleanValue()) || relativePos.equals((Object)this.worldPosition) || !((relativeState = this.level.getBlockState(relativePos)).getBlock() instanceof FluidPipeBlock) || !((Boolean)relativeState.getValue((Property)FluidPipeBlock.CONNECTED_PIPES[direction.getOpposite().get3DDataValue()])).booleanValue()) continue;
                visited.add(relativePos);
                queue.add(relativePos);
            }
        }
        visited.forEach(pos -> this.fluidNetwork.put((BlockPos)pos, new PipeNode()));
        this.fluidNetwork.forEach((pos, node) -> {
            BlockState state = this.level.getBlockState(pos);
            for (Direction direction : Direction.values()) {
                if (!((Boolean)state.getValue((Property)FluidPipeBlock.CONNECTED_PIPES[direction.get3DDataValue()])).booleanValue()) continue;
                BlockEntity selfTileEntity = this.level.getBlockEntity(pos);
                if (selfTileEntity instanceof PipeTileEntity) {
                    PipeTileEntity pipeTileEntity = (PipeTileEntity)selfTileEntity;
                    pipeTileEntity.addPump(this.worldPosition);
                    node.tileEntity = new WeakReference<PipeTileEntity>(pipeTileEntity);
                    FluidNetworkHandler.instance().addPipeForUpdate(pipeTileEntity);
                }
                BlockPos relativePos = pos.relative(direction);
                if (!(state.getBlock() instanceof FluidPumpBlock) && this.level.hasNeighborSignal(pos) || state.getBlock() instanceof FluidPumpBlock && ((Boolean)state.getValue((Property)FluidPumpBlock.DISABLED)).booleanValue() || this.level.getCapability(Capabilities.FluidHandler.BLOCK, relativePos, direction.getOpposite()) == null) continue;
                this.fluidHandlers.add((Pair<BlockPos, Direction>)Pair.of(relativePos, direction.getOpposite()));
            }
        });
        BlockState state = this.getBlockState();
        for (Direction direction : Direction.values()) {
            BlockPos relativePos = this.worldPosition.relative(direction);
            if (direction == ((Direction)state.getValue((Property)FluidPumpBlock.DIRECTION)).getOpposite() || this.level.getCapability(Capabilities.FluidHandler.BLOCK, relativePos, direction.getOpposite()) == null) continue;
            this.fluidHandlers.add((Pair<BlockPos, Direction>)Pair.of(relativePos, direction.getOpposite()));
        }
    }

    public void removePumpFromPipes() {
        this.fluidNetwork.forEach((pos, node) -> {
            PipeTileEntity tileEntity = (PipeTileEntity)((Object)((Object)node.tileEntity.get()));
            if (tileEntity != null) {
                tileEntity.removePump(this.worldPosition);
                FluidNetworkHandler.instance().addPipeForUpdate(tileEntity);
            }
        });
    }

    public List<IFluidHandler> getFluidInteractionHandlersOnNetwork(Level world) {
        ArrayList<IFluidHandler> handlers = new ArrayList<IFluidHandler>();
        this.fluidHandlers.forEach(pair -> {
            IFluidHandler handler;
            BlockEntity tileEntity;
            if (world.isLoaded((BlockPos)pair.getLeft()) && (tileEntity = world.getBlockEntity((BlockPos)pair.getLeft())) != null && (handler = (IFluidHandler)world.getCapability(Capabilities.FluidHandler.BLOCK, (BlockPos)pair.getLeft(), ((Direction)pair.getRight()))) != null) {
                handlers.add(handler);
            }
        });
        return handlers;
    }

    public Optional<IFluidHandler> getSourceFluidInteractionHandler(Level world) {
        Direction direction = (Direction)this.getBlockState().getValue((Property)FluidPumpBlock.DIRECTION);
        IFluidHandler handler = (IFluidHandler)world.getCapability(Capabilities.FluidHandler.BLOCK, this.worldPosition.relative(direction.getOpposite()), direction);
        return Optional.ofNullable(handler);
    }

    public void cyclePowerMode() {
        this.powerMode = PowerMode.values()[(this.powerMode.ordinal() + 1) % PowerMode.values().length];
        if (this.level != null && !this.level.isClientSide()) {
            CompoundTag compound = new CompoundTag();
            this.saveAdditional(compound, (HolderLookup.Provider)this.level.registryAccess());
            TileEntityUtil.sendUpdatePacket((BlockEntity)this, compound);
            BlockState state = this.getBlockState();
            state = ((FluidPumpBlock)state.getBlock()).getDisabledState(state, this.level, this.worldPosition);
            this.level.setBlock(this.worldPosition, state, 10);
        }
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        if (compound.contains("PowerMode", 3)) {
            this.powerMode = PowerMode.fromOrdinal(compound.getInt("PowerMode"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        compound.putInt("PowerMode", this.powerMode.ordinal());
    }

    private void writePowerMode(CompoundTag compound) {
        compound.putInt("PowerMode", this.powerMode.ordinal());
    }

    public static enum PowerMode {
        ALWAYS_ACTIVE("always", input -> true),
        REQUIRES_SIGNAL_ON("on", input -> Objects.requireNonNull(((PumpTileEntity)input).level).hasNeighborSignal(((PumpTileEntity)input).worldPosition)),
        REQUIRES_SIGNAL_OFF("off", input -> !Objects.requireNonNull(((PumpTileEntity)input).level).hasNeighborSignal(((PumpTileEntity)input).worldPosition));

        private static final String LANG_KEY_CHAT_PREFIX = "vehicle.chat.pump.power";
        private String key;
        private Function<PumpTileEntity, Boolean> function;

        private PowerMode(String key, Function<PumpTileEntity, Boolean> function) {
            this.key = String.join((CharSequence)".", LANG_KEY_CHAT_PREFIX, key);
            this.function = function;
        }

        public boolean test(PumpTileEntity pump) {
            return this.function.apply(pump);
        }

        public String getKey() {
            return this.key;
        }

        @Nullable
        public static PowerMode fromOrdinal(int ordinal) {
            if (ordinal < 0 || ordinal >= PowerMode.values().length) {
                return null;
            }
            return PowerMode.values()[ordinal];
        }
    }

    private static class PipeNode {
        private WeakReference<PipeTileEntity> tileEntity;

        private PipeNode() {
        }
    }
}



