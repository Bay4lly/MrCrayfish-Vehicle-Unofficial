package com.mrcrayfish.vehicle.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class TileEntityUtil
{
    public static void sendUpdatePacket(BlockEntity tileEntity)
    {
        Packet<?> packet = tileEntity.getUpdatePacket();
        if(packet != null)
        {
            sendUpdatePacket(tileEntity.getLevel(), tileEntity.getBlockPos(), packet);
        }
    }

    public static void sendUpdatePacket(BlockEntity tileEntity, CompoundTag compound)
    {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(tileEntity, (be, provider) -> compound);
        sendUpdatePacket(tileEntity.getLevel(), tileEntity.getBlockPos(), packet);
    }

    public static void sendUpdatePacket(BlockEntity tileEntity, ServerPlayer player)
    {
        HolderLookup.Provider provider = tileEntity.getLevel() != null ? tileEntity.getLevel().registryAccess() : net.minecraft.core.RegistryAccess.EMPTY;
        sendUpdatePacket(tileEntity, tileEntity.getUpdateTag(provider), player);
    }

    public static void sendUpdatePacket(BlockEntity tileEntity, CompoundTag compound, ServerPlayer player)
    {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(tileEntity, (be, provider) -> compound);
        player.connection.send(packet);
    }

    private static void sendUpdatePacket(Level world, BlockPos pos, Packet<?> packet)
    {
        if(world instanceof ServerLevel)
        {
            ServerLevel server = (ServerLevel) world;
            List<ServerPlayer> players = server.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false);
            players.forEach(player -> player.connection.send(packet));
        }
    }
}
