/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.RegistryAccess$Frozen
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.mrcrayfish.vehicle.util;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TileEntityUtil {
    public static void sendUpdatePacket(BlockEntity tileEntity) {
        Packet packet = tileEntity.getUpdatePacket();
        if (packet != null) {
            TileEntityUtil.sendUpdatePacket(tileEntity.getLevel(), tileEntity.getBlockPos(), packet);
        }
    }

    public static void sendUpdatePacket(BlockEntity tileEntity, CompoundTag compound) {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create((BlockEntity)tileEntity, (be, provider) -> compound);
        TileEntityUtil.sendUpdatePacket(tileEntity.getLevel(), tileEntity.getBlockPos(), packet);
    }

    public static void sendUpdatePacket(BlockEntity tileEntity, ServerPlayer player) {
        net.minecraft.core.RegistryAccess provider = tileEntity.getLevel() != null ? tileEntity.getLevel().registryAccess() : RegistryAccess.EMPTY;
        TileEntityUtil.sendUpdatePacket(tileEntity, tileEntity.getUpdateTag((HolderLookup.Provider)provider), player);
    }

    public static void sendUpdatePacket(BlockEntity tileEntity, CompoundTag compound, ServerPlayer player) {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create((BlockEntity)tileEntity, (be, provider) -> compound);
        player.connection.send((Packet)packet);
    }

    private static void sendUpdatePacket(Level world, BlockPos pos, Packet<?> packet) {
        if (world instanceof ServerLevel) {
            ServerLevel server = (ServerLevel)world;
            List players = server.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false);
            players.forEach(player -> ((net.minecraft.server.level.ServerPlayer)player).connection.send(packet));
        }
    }
}

