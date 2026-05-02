package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.util.TileEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidHandlerBlockEntity;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class TileFluidHandlerSynced extends FluidHandlerBlockEntity
{
    public TileFluidHandlerSynced(@Nonnull BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, int capacity)
    {
        super(tileEntityTypeIn, pos, state);
        this.tank = new FluidTank(capacity)
        {
            @Override
            protected void onContentsChanged()
            {
                TileFluidHandlerSynced.this.syncFluidToClient();
            }
        };
    }

    public TileFluidHandlerSynced(@Nonnull BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, int capacity, Predicate<FluidStack> validator)
    {
        super(tileEntityTypeIn, pos, state);
        this.tank = new FluidTank(capacity, validator)
        {
            @Override
            protected void onContentsChanged()
            {
                TileFluidHandlerSynced.this.syncFluidToClient();
            }
        };
    }

    public void syncFluidToClient()
    {
        if(this.level != null && !this.level.isClientSide)
        {
            CompoundTag compound = new CompoundTag();
            super.saveAdditional(compound);
            TileEntityUtil.sendUpdatePacket(this, compound);
        }
    }

    public void syncFluidToPlayer(ServerPlayer player)
    {
        if(this.level != null && !this.level.isClientSide)
        {
            CompoundTag compound = new CompoundTag();
            super.saveAdditional(compound);
            TileEntityUtil.sendUpdatePacket(this, compound);
        }
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.saveWithId();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        CompoundTag nbt = pkt.getTag();
        if(nbt != null)
        {
            this.load(nbt);
        }
    }

    public FluidTank getFluidTank()
    {
        return this.tank;
    }
}