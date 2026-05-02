package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class MessageTravelProperties implements IMessage<MessageTravelProperties>
{
	private float travelSpeed;
	private float travelDirection;

	public MessageTravelProperties() {}

	public MessageTravelProperties(float travelSpeed, float travelDirection)
	{
		this.travelSpeed = travelSpeed;
		this.travelDirection = travelDirection;
	}

	@Override
	public void encode(MessageTravelProperties message, FriendlyByteBuf buffer)
	{
		buffer.writeFloat(message.travelSpeed);
		buffer.writeFloat(message.travelDirection);
	}

	@Override
	public MessageTravelProperties decode(FriendlyByteBuf buffer)
	{
		return new MessageTravelProperties(buffer.readFloat(), buffer.readFloat());
	}

	@Override
	public void handle(MessageTravelProperties message, Supplier<Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayer player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof HelicopterEntity)
				{
					HelicopterEntity helicopter = (HelicopterEntity) riding;
					helicopter.setTravelSpeed(message.travelSpeed);
					helicopter.setTravelDirection(message.travelDirection);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
