package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class MessageDrift implements IMessage<MessageDrift>
{
	private boolean drifting;

	public MessageDrift() {}

	public MessageDrift(boolean drifting)
	{
		this.drifting = drifting;
	}

	@Override
	public void encode(MessageDrift message, FriendlyByteBuf buffer)
	{
		buffer.writeBoolean(message.drifting);
	}

	@Override
	public MessageDrift decode(FriendlyByteBuf buffer)
	{
		return new MessageDrift(buffer.readBoolean());
	}

	@Override
	public void handle(MessageDrift message, Supplier<Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayer player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof LandVehicleEntity)
				{
					((LandVehicleEntity) riding).setDrifting(message.drifting);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
