/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.neoforged.neoforge.network.PacketDistributor
 *  net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
 *  net.neoforged.neoforge.network.registration.PayloadRegistrar
 */
package com.mrcrayfish.vehicle.network;

import com.mrcrayfish.vehicle.network.message.IMessage;
import com.mrcrayfish.vehicle.network.message.MessageAccelerating;
import com.mrcrayfish.vehicle.network.message.MessageAltitude;
import com.mrcrayfish.vehicle.network.message.MessageAttachChest;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import com.mrcrayfish.vehicle.network.message.MessageCraftVehicle;
import com.mrcrayfish.vehicle.network.message.MessageCycleSeats;
import com.mrcrayfish.vehicle.network.message.MessageDrift;
import com.mrcrayfish.vehicle.network.message.MessageEntityFluid;
import com.mrcrayfish.vehicle.network.message.MessageFlaps;
import com.mrcrayfish.vehicle.network.message.MessageFuelItem;
import com.mrcrayfish.vehicle.network.message.MessageFuelVehicle;
import com.mrcrayfish.vehicle.network.message.MessageHitchTrailer;
import com.mrcrayfish.vehicle.network.message.MessageHorn;
import com.mrcrayfish.vehicle.network.message.MessageInteractKey;
import com.mrcrayfish.vehicle.network.message.MessageOpenStorage;
import com.mrcrayfish.vehicle.network.message.MessagePickupVehicle;
import com.mrcrayfish.vehicle.network.message.MessagePower;
import com.mrcrayfish.vehicle.network.message.MessageSyncHeldVehicle;
import com.mrcrayfish.vehicle.network.message.MessageSyncInventory;
import com.mrcrayfish.vehicle.network.message.MessageSyncPlayerSeat;
import com.mrcrayfish.vehicle.network.message.MessageThrowVehicle;
import com.mrcrayfish.vehicle.network.message.MessageTravelProperties;
import com.mrcrayfish.vehicle.network.message.MessageTurnAngle;
import com.mrcrayfish.vehicle.network.message.MessageTurnDirection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {
    public static final String PROTOCOL_VERSION = "1";

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        PacketHandler.register(registrar, MessageTurnDirection.TYPE, MessageTurnDirection.STREAM_CODEC);
        PacketHandler.register(registrar, MessageTurnAngle.TYPE, MessageTurnAngle.STREAM_CODEC);
        PacketHandler.register(registrar, MessageAccelerating.TYPE, MessageAccelerating.STREAM_CODEC);
        PacketHandler.register(registrar, MessageDrift.TYPE, MessageDrift.STREAM_CODEC);
        PacketHandler.register(registrar, MessageHorn.TYPE, MessageHorn.STREAM_CODEC);
        PacketHandler.register(registrar, MessageThrowVehicle.TYPE, MessageThrowVehicle.STREAM_CODEC);
        PacketHandler.register(registrar, MessagePickupVehicle.TYPE, MessagePickupVehicle.STREAM_CODEC);
        PacketHandler.register(registrar, MessageFlaps.TYPE, MessageFlaps.STREAM_CODEC);
        PacketHandler.register(registrar, MessageAttachChest.TYPE, MessageAttachChest.STREAM_CODEC);
        PacketHandler.register(registrar, MessageAttachTrailer.TYPE, MessageAttachTrailer.STREAM_CODEC);
        PacketHandler.register(registrar, MessageFuelVehicle.TYPE, MessageFuelVehicle.STREAM_CODEC);
        PacketHandler.register(registrar, MessageFuelItem.TYPE, MessageFuelItem.STREAM_CODEC);
        PacketHandler.register(registrar, MessageInteractKey.TYPE, MessageInteractKey.STREAM_CODEC);
        PacketHandler.register(registrar, MessageAltitude.TYPE, MessageAltitude.STREAM_CODEC);
        PacketHandler.register(registrar, MessageCraftVehicle.TYPE, MessageCraftVehicle.STREAM_CODEC);
        PacketHandler.register(registrar, MessageHitchTrailer.TYPE, MessageHitchTrailer.STREAM_CODEC);
        PacketHandler.register(registrar, MessageSyncInventory.TYPE, MessageSyncInventory.STREAM_CODEC);
        PacketHandler.register(registrar, MessageOpenStorage.TYPE, MessageOpenStorage.STREAM_CODEC);
        PacketHandler.register(registrar, MessageTravelProperties.TYPE, MessageTravelProperties.STREAM_CODEC);
        PacketHandler.register(registrar, MessagePower.TYPE, MessagePower.STREAM_CODEC);
        PacketHandler.register(registrar, MessageEntityFluid.TYPE, MessageEntityFluid.STREAM_CODEC);
        PacketHandler.register(registrar, MessageSyncPlayerSeat.TYPE, MessageSyncPlayerSeat.STREAM_CODEC);
        PacketHandler.register(registrar, MessageCycleSeats.TYPE, MessageCycleSeats.STREAM_CODEC);
        PacketHandler.register(registrar, MessageSyncHeldVehicle.TYPE, MessageSyncHeldVehicle.STREAM_CODEC);
    }

    private static <T extends IMessage<T>> void register(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playBidirectional(type, codec, (payload, context) -> payload.handle(payload, context));
    }

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.sendToServer((CustomPacketPayload)message, (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload message) {
        PacketDistributor.sendToPlayer((ServerPlayer)player, (CustomPacketPayload)message, (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    public static void sendToTrackingEntity(Entity entity, CustomPacketPayload message) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, (CustomPacketPayload)message, (CustomPacketPayload[])new CustomPacketPayload[0]);
    }
}

