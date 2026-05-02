package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class MessageTurnAngle implements IMessage<MessageTurnAngle>
{
	private float angle;

	public MessageTurnAngle() {}

	public MessageTurnAngle(float angle)
	{
		this.angle = angle;
	}

	@Override
	public void encode(MessageTurnAngle message, FriendlyByteBuf buffer)
	{
		buffer.writeFloat(message.angle);
	}

	@Override
	public MessageTurnAngle decode(FriendlyByteBuf buffer)
	{
		return new MessageTurnAngle(buffer.readFloat());
	}

	@Override
	public void handle(MessageTurnAngle message, Supplier<Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayer player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PoweredVehicleEntity)
				{
					((PoweredVehicleEntity) riding).setTargetTurnAngle(message.angle);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
