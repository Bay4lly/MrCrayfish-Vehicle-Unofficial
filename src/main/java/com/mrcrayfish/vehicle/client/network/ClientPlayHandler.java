/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 */
package com.mrcrayfish.vehicle.client.network;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.client.handler.HeldVehicleHandler;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.trailer.FluidTrailerEntity;
import com.mrcrayfish.vehicle.network.message.MessageEntityFluid;
import com.mrcrayfish.vehicle.network.message.MessageSyncHeldVehicle;
import com.mrcrayfish.vehicle.network.message.MessageSyncInventory;
import com.mrcrayfish.vehicle.network.message.MessageSyncPlayerSeat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class ClientPlayHandler {
    public static void handleSyncInventory(MessageSyncInventory message) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }
        Entity entity = world.getEntity(message.getEntityId());
        if (!(entity instanceof IStorage)) {
            return;
        }
        ((IStorage)entity).getInventory().fromTag(message.getCompound().getList("Inventory", 10), (HolderLookup.Provider)world.registryAccess());
    }

    public static void handleEntityFluid(MessageEntityFluid message) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }
        Entity entity = world.getEntity(message.getEntityId());
        if (entity == null) {
            return;
        }
        if (entity instanceof FluidTrailerEntity) {
            FluidTrailerEntity fluidTrailer = (FluidTrailerEntity)entity;
            fluidTrailer.fluidHandler().setFluid(message.getStack());
        }
    }

    public static void handleSyncPlayerSeat(MessageSyncPlayerSeat message) {
        Entity entity;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && (entity = player.getCommandSenderWorld().getEntity(message.getEntityId())) instanceof VehicleEntity) {
            VehicleEntity vehicle = (VehicleEntity)entity;
            vehicle.getSeatTracker().setSeatIndex(message.getSeatIndex(), message.getUuid());
        }
    }

    public static void handleSyncHeldVehicle(MessageSyncHeldVehicle message) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            Entity entity = world.getEntity(message.getEntityId());
            Player player = null;
            if (entity instanceof Player) {
                player = (Player)entity;
            } else {
                LocalPlayer local = Minecraft.getInstance().player;
                if (local != null && local.getId() == message.getEntityId()) {
                    player = local;
                }
            }
            if (player != null) {
                VehicleMod.LOGGER.debug("[HeldVehicle] Client received sync for player {} -> tagEmpty={}", (Object)player.getUUID(), (Object)(message.getVehicleTag() == null || message.getVehicleTag().isEmpty() ? 1 : 0));
                HeldVehicleDataHandler.setHeldVehicle(player, message.getVehicleTag());
                boolean holding = HeldVehicleDataHandler.isHoldingVehicle(player);
                if (holding) {
                    if (!HeldVehicleHandler.idToCounter.containsKey(player.getUUID())) {
                        HeldVehicleHandler.idToCounter.put(player.getUUID(), new HeldVehicleHandler.AnimationCounter(40));
                    }
                } else {
                    HeldVehicleHandler.idToCounter.remove(player.getUUID());
                }
            }
        }
    }
}

