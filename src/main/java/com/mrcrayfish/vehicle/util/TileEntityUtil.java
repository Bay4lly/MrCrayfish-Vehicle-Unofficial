package com.mrcrayfish.vehicle.util;

import net.minecraft.core.BlockPos;
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
    /**
     * Sends an update packet to clients tracking a tile entity.
     *
     * @param tileEntity the tile entity to update
     */
    public static void sendUpdatePacket(BlockEntity tileEntity)
    {
        Packet<?> packet = tileEntity.getUpdatePacket();
        if(packet != null)
        {
            sendUpdatePacket(tileEntity.getLevel(), tileEntity.getBlockPos(), packet);
        }
    }

    /**
     * Sends an update packet to clients tracking a tile entity with a specific CompoundTag
     *
     * @param tileEntity the tile entity to update
     */
    public static void sendUpdatePacket(BlockEntity tileEntity, CompoundTag compound)
    {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(tileEntity, unused -> compound);
        sendUpdatePacket(tileEntity.getLevel(), tileEntity.getBlockPos(), packet);
    }

    /**
     * Sends an update packet but only to a specific player. This helps reduce overhead on the network
     * when you only want to update a tile entity for a single player rather than everyone who is
     * tracking the tile entity.
     *
     * @param tileEntity the tile entity to update
     * @param player the player to send the update to
     */
    public static void sendUpdatePacket(BlockEntity tileEntity, ServerPlayer player)
    {
        sendUpdatePacket(tileEntity, tileEntity.getUpdateTag(), player);
    }

    /**
     * Sends an update packet with a custom nbt compound but only to a specific player. This helps
     * reduce overhead on the network when you only want to update a tile entity for a single player
     * rather than everyone who is tracking the tile entity.
     *
     * @param tileEntity the tile entity to update
     * @param compound the update tag to send
     * @param player the player to send the update to
     */
    public static void sendUpdatePacket(BlockEntity tileEntity, CompoundTag compound, ServerPlayer player)
    {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(tileEntity, unused -> compound);
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
