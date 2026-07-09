/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.attachment.AttachmentType
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$Clone
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$StartTracking
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  net.neoforged.neoforge.registries.NeoForgeRegistries
 */
package com.mrcrayfish.vehicle.common.entity;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageSyncHeldVehicle;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class HeldVehicleDataHandler {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create((Registry)NeoForgeRegistries.ATTACHMENT_TYPES, (String)"vehicle");
    public static final Supplier<AttachmentType<HeldVehicle>> HELD_VEHICLE = ATTACHMENT_TYPES.register("held_vehicle", () -> AttachmentType.builder(HeldVehicle::new).build());

    public static void registerBus(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
    }

    public static void register() {
        NeoForge.EVENT_BUS.register((Object)new HeldVehicleDataHandler());
    }

    public static boolean isHoldingVehicle(Player player) {
        return !((HeldVehicle)player.getData(HELD_VEHICLE)).getVehicleTag().isEmpty();
    }

    public static CompoundTag getHeldVehicle(Player player) {
        return ((HeldVehicle)player.getData(HELD_VEHICLE)).getVehicleTag();
    }

    public static void setHeldVehicle(Player player, CompoundTag vehicleTag) {
        ((HeldVehicle)player.getData(HELD_VEHICLE)).setVehicleTag(vehicleTag);
        if (!player.level().isClientSide) {
            VehicleMod.LOGGER.debug("[HeldVehicle] setHeldVehicle server-side for player {} -> tagEmpty={}", (Object)player.getUUID(), (Object)(vehicleTag == null || vehicleTag.isEmpty() ? 1 : 0));
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                PacketHandler.sendToPlayer(serverPlayer, new MessageSyncHeldVehicle(player.getId(), vehicleTag));
                PacketHandler.sendToTrackingEntity(player, new MessageSyncHeldVehicle(player.getId(), vehicleTag));
            }
        } else {
            VehicleMod.LOGGER.debug("[HeldVehicle] setHeldVehicle client-side for player {} -> tagEmpty={}", (Object)player.getUUID(), (Object)(vehicleTag == null || vehicleTag.isEmpty() ? 1 : 0));
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            return;
        }
        CompoundTag vehicleTag = HeldVehicleDataHandler.getHeldVehicle(event.getOriginal());
        if (!vehicleTag.isEmpty()) {
            HeldVehicleDataHandler.setHeldVehicle(event.getEntity(), vehicleTag);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        Entity entity = event.getTarget();
        if (entity instanceof Player) {
            Player player = (Player)entity;
            CompoundTag vehicleTag = HeldVehicleDataHandler.getHeldVehicle(player);
            PacketHandler.sendToPlayer((ServerPlayer)event.getEntity(), new MessageSyncHeldVehicle(player.getId(), vehicleTag));
        }
    }

    @SubscribeEvent
    public void onPlayerJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (!event.getLevel().isClientSide) {
                CompoundTag vehicleTag = HeldVehicleDataHandler.getHeldVehicle(player);
                PacketHandler.sendToPlayer((ServerPlayer)player, new MessageSyncHeldVehicle(player.getId(), vehicleTag));
            }
        }
    }

    public static class HeldVehicle {
        private CompoundTag compound = new CompoundTag();

        public void setVehicleTag(CompoundTag tagCompound) {
            this.compound = tagCompound;
        }

        public CompoundTag getVehicleTag() {
            return this.compound;
        }
    }
}

