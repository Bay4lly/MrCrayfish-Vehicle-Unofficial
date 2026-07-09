/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.SeatTracker;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.network.message.IMessage;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageCycleSeats
implements IMessage<MessageCycleSeats> {
    public static final CustomPacketPayload.Type<MessageCycleSeats> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"cycle_seats"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageCycleSeats> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageCycleSeats)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageCycleSeats().decode((RegistryFriendlyByteBuf)buf));

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void encode(MessageCycleSeats message, RegistryFriendlyByteBuf buffer) {
    }

    @Override
    public MessageCycleSeats decode(RegistryFriendlyByteBuf buffer) {
        return new MessageCycleSeats();
    }

    @Override
    public void handle(MessageCycleSeats message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer)context.player();
                if (player != null && player.getVehicle() instanceof VehicleEntity) {
                    VehicleEntity vehicle = (VehicleEntity)player.getVehicle();
                    List<Seat> seats = vehicle.getProperties().getSeats();
                    if (vehicle.getPassengers().size() >= seats.size()) {
                        return;
                    }
                    SeatTracker tracker = vehicle.getSeatTracker();
                    int seatIndex = tracker.getSeatIndex(player.getUUID());
                    for (int i = 0; i < seats.size() - 1; ++i) {
                        int nextIndex = (seatIndex + (i + 1)) % seats.size();
                        if (!tracker.isSeatAvailable(nextIndex)) continue;
                        tracker.setSeatIndex(nextIndex, player.getUUID());
                        return;
                    }
                }
            });
        }
    }
}

