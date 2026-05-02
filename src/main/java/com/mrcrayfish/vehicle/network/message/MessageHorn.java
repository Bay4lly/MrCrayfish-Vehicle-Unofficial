package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class MessageHorn implements IMessage<MessageHorn>
{
	private boolean horn;

	public MessageHorn() {}

	public MessageHorn(boolean horn)
	{
		this.horn = horn;
	}

	@Override
	public void encode(MessageHorn message, FriendlyByteBuf buffer)
	{
		buffer.writeBoolean(message.horn);
	}

	@Override
	public MessageHorn decode(FriendlyByteBuf buffer)
	{
		return new MessageHorn(buffer.readBoolean());
	}

	@Override
	public void handle(MessageHorn message, Supplier<Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayer player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PoweredVehicleEntity)
				{
					((PoweredVehicleEntity) riding).setHorn(message.horn);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
