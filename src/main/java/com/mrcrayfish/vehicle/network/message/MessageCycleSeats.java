package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.SeatTracker;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageCycleSeats implements IMessage<MessageCycleSeats>
{
    public MessageCycleSeats() {}

    @Override
    public void encode(MessageCycleSeats message, FriendlyByteBuf buffer) {}

    @Override
    public MessageCycleSeats decode(FriendlyByteBuf buffer)
    {
        return new MessageCycleSeats();
    }

    @Override
    public void handle(MessageCycleSeats message, Supplier<Context> supplier)
    {
        if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
        {
            supplier.get().enqueueWork(() ->
            {
                ServerPlayer player = supplier.get().getSender();
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
            supplier.get().setPacketHandled(true);
        }
    }
}
