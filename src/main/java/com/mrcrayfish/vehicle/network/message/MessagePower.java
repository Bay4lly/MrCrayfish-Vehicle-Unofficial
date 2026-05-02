package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class MessagePower implements IMessage<MessagePower>
{
	private float power;

	public MessagePower() {}

	public MessagePower(float power)
	{
		this.power = power;
	}

	@Override
	public void encode(MessagePower message, FriendlyByteBuf buffer)
	{
		buffer.writeFloat(message.power);
	}

	@Override
	public MessagePower decode(FriendlyByteBuf buffer)
	{
		return new MessagePower(buffer.readFloat());
	}

	@Override
	public void handle(MessagePower message, Supplier<Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayer player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PoweredVehicleEntity)
				{
					((PoweredVehicleEntity) riding).setPower(message.power);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
