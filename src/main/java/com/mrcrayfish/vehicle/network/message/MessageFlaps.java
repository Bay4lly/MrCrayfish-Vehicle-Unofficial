package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PlaneEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class MessageFlaps implements IMessage<MessageFlaps>
{
	private PlaneEntity.FlapDirection flapDirection;

	public MessageFlaps() {}

	public MessageFlaps(PlaneEntity.FlapDirection flapDirection)
	{
		this.flapDirection = flapDirection;
	}

	@Override
	public void encode(MessageFlaps message, FriendlyByteBuf buffer)
	{
		buffer.writeEnum(message.flapDirection);
	}

	@Override
	public MessageFlaps decode(FriendlyByteBuf buffer)
	{
		return new MessageFlaps(buffer.readEnum(PlaneEntity.FlapDirection.class));
	}

	@Override
	public void handle(MessageFlaps message, Supplier<Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayer player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PlaneEntity)
				{
					((PlaneEntity) riding).setFlapDirection(message.flapDirection);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
