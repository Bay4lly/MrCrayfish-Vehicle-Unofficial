package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageAccelerating implements IMessage<MessageAccelerating>
{
	private PoweredVehicleEntity.AccelerationDirection acceleration;

	public MessageAccelerating() {}

	public MessageAccelerating(PoweredVehicleEntity.AccelerationDirection acceleration)
	{
		this.acceleration = acceleration;
	}

	@Override
	public void encode(MessageAccelerating message, FriendlyByteBuf buffer)
	{
		buffer.writeEnum(message.acceleration);
	}

	@Override
	public MessageAccelerating decode(FriendlyByteBuf buffer)
	{
		return new MessageAccelerating(buffer.readEnum(PoweredVehicleEntity.AccelerationDirection.class));
	}

	@Override
	public void handle(MessageAccelerating message, Supplier<NetworkEvent.Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayer player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PoweredVehicleEntity)
				{
					((PoweredVehicleEntity) riding).setAcceleration(message.acceleration);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
