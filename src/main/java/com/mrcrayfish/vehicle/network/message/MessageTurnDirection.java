package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class MessageTurnDirection implements IMessage<MessageTurnDirection>
{
	private PoweredVehicleEntity.TurnDirection direction;

	public MessageTurnDirection() {}

	public MessageTurnDirection(PoweredVehicleEntity.TurnDirection direction)
	{
		this.direction = direction;
	}

	@Override
	public void encode(MessageTurnDirection message, FriendlyByteBuf buffer)
	{
		buffer.writeEnum(message.direction);
	}

	@Override
	public MessageTurnDirection decode(FriendlyByteBuf buffer)
	{
		return new MessageTurnDirection(buffer.readEnum(PoweredVehicleEntity.TurnDirection.class));
	}

	@Override
	public void handle(MessageTurnDirection message, Supplier<Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayer player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PoweredVehicleEntity)
				{
					((PoweredVehicleEntity) riding).setTurnDirection(message.direction);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
