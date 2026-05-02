package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class MessageAltitude implements IMessage<MessageAltitude>
{
	private HelicopterEntity.AltitudeChange altitudeChange;

	public MessageAltitude() {}

	public MessageAltitude(HelicopterEntity.AltitudeChange altitudeChange)
	{
		this.altitudeChange = altitudeChange;
	}

	@Override
	public void encode(MessageAltitude message, FriendlyByteBuf buffer)
	{
		buffer.writeEnum(message.altitudeChange);
	}

	@Override
	public MessageAltitude decode(FriendlyByteBuf buffer)
	{
		return new MessageAltitude(buffer.readEnum(HelicopterEntity.AltitudeChange.class));
	}

	@Override
	public void handle(MessageAltitude message, Supplier<Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayer player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof HelicopterEntity)
				{
					((HelicopterEntity) riding).setAltitudeChange(message.altitudeChange);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
