package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.SeatTracker;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class MessageCycleSeats implements IMessage<MessageCycleSeats>
{
    public static final CustomPacketPayload.Type<MessageCycleSeats> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "cycle_seats"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageCycleSeats> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageCycleSeats().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public MessageCycleSeats() {}

    @Override
    public void encode(MessageCycleSeats message, RegistryFriendlyByteBuf buffer) {}

    @Override
    public MessageCycleSeats decode(RegistryFriendlyByteBuf buffer)
    {
        return new MessageCycleSeats();
    }

    @Override
    public void handle(MessageCycleSeats message, IPayloadContext context)
    {
        if(context.flow().isServerbound())
        {
            context.enqueueWork(() ->
            {
                ServerPlayer player = ((ServerPlayer) context.player());
                if(player != null && player.getVehicle() instanceof VehicleEntity)
                {
                    VehicleEntity vehicle = (VehicleEntity) player.getVehicle();
                    List<Seat> seats = vehicle.getProperties().getSeats();

                    /* No need to cycle if already full of passengers */
                    if(vehicle.getPassengers().size() >= seats.size())
                        return;

                    SeatTracker tracker = vehicle.getSeatTracker();
                    int seatIndex = tracker.getSeatIndex(player.getUUID());
                    for(int i = 0; i < seats.size() - 1; i++)
                    {
                        int nextIndex = (seatIndex + (i + 1)) % seats.size();
                        if(tracker.isSeatAvailable(nextIndex))
                        {
                            tracker.setSeatIndex(nextIndex, player.getUUID());
                            return;
                        }
                    }
                }
            });
        }
    }
}
