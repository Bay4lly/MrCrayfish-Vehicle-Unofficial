package com.mrcrayfish.vehicle.network;

import com.mrcrayfish.vehicle.network.message.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Author: MrCrayfish
 * <p>
 * Ported to the NeoForge 1.21.1 custom payload network system.
 */
public class PacketHandler
{
    public static final String PROTOCOL_VERSION = "1";

    public static void register(RegisterPayloadHandlersEvent event)
    {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        register(registrar, MessageTurnDirection.TYPE, MessageTurnDirection.STREAM_CODEC);
        register(registrar, MessageTurnAngle.TYPE, MessageTurnAngle.STREAM_CODEC);
        register(registrar, MessageAccelerating.TYPE, MessageAccelerating.STREAM_CODEC);
        register(registrar, MessageDrift.TYPE, MessageDrift.STREAM_CODEC);
        register(registrar, MessageHorn.TYPE, MessageHorn.STREAM_CODEC);
        register(registrar, MessageThrowVehicle.TYPE, MessageThrowVehicle.STREAM_CODEC);
        register(registrar, MessagePickupVehicle.TYPE, MessagePickupVehicle.STREAM_CODEC);
        register(registrar, MessageFlaps.TYPE, MessageFlaps.STREAM_CODEC);
        register(registrar, MessageAttachChest.TYPE, MessageAttachChest.STREAM_CODEC);
        register(registrar, MessageAttachTrailer.TYPE, MessageAttachTrailer.STREAM_CODEC);
        register(registrar, MessageFuelVehicle.TYPE, MessageFuelVehicle.STREAM_CODEC);
        register(registrar, MessageFuelItem.TYPE, MessageFuelItem.STREAM_CODEC);
        register(registrar, MessageInteractKey.TYPE, MessageInteractKey.STREAM_CODEC);
        register(registrar, MessageAltitude.TYPE, MessageAltitude.STREAM_CODEC);
        register(registrar, MessageCraftVehicle.TYPE, MessageCraftVehicle.STREAM_CODEC);
        register(registrar, MessageHitchTrailer.TYPE, MessageHitchTrailer.STREAM_CODEC);
        register(registrar, MessageSyncInventory.TYPE, MessageSyncInventory.STREAM_CODEC);
        register(registrar, MessageOpenStorage.TYPE, MessageOpenStorage.STREAM_CODEC);
        register(registrar, MessageTravelProperties.TYPE, MessageTravelProperties.STREAM_CODEC);
        register(registrar, MessagePower.TYPE, MessagePower.STREAM_CODEC);
        register(registrar, MessageEntityFluid.TYPE, MessageEntityFluid.STREAM_CODEC);
        register(registrar, MessageSyncPlayerSeat.TYPE, MessageSyncPlayerSeat.STREAM_CODEC);
        register(registrar, MessageCycleSeats.TYPE, MessageCycleSeats.STREAM_CODEC);
        register(registrar, MessageSyncHeldVehicle.TYPE, MessageSyncHeldVehicle.STREAM_CODEC);
    }

    private static <T extends IMessage<T>> void register(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec)
    {
        registrar.playBidirectional(type, codec, (payload, context) -> payload.handle(payload, context));
    }

    /* Sending helpers that replace the old SimpleChannel API. */

    public static void sendToServer(CustomPacketPayload message)
    {
        PacketDistributor.sendToServer(message);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload message)
    {
        PacketDistributor.sendToPlayer(player, message);
    }

    public static void sendToTrackingEntity(Entity entity, CustomPacketPayload message)
    {
        PacketDistributor.sendToPlayersTrackingEntity(entity, message);
    }
}
