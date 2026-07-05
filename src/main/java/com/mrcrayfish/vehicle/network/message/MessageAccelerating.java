package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;


public class MessageAccelerating implements IMessage<MessageAccelerating>
{
    public static final CustomPacketPayload.Type<MessageAccelerating> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "accelerating"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageAccelerating> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageAccelerating().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

	private PoweredVehicleEntity.AccelerationDirection acceleration;

	public MessageAccelerating() {}

	public MessageAccelerating(PoweredVehicleEntity.AccelerationDirection acceleration)
	{
		this.acceleration = acceleration;
	}

	@Override
	public void encode(MessageAccelerating message, RegistryFriendlyByteBuf buffer)
	{
		buffer.writeEnum(message.acceleration);
	}

	@Override
	public MessageAccelerating decode(RegistryFriendlyByteBuf buffer)
	{
		return new MessageAccelerating(buffer.readEnum(PoweredVehicleEntity.AccelerationDirection.class));
	}

	@Override
	public void handle(MessageAccelerating message, IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			ServerPlayer player = ((ServerPlayer) context.player());
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PoweredVehicleEntity)
				{
					((PoweredVehicleEntity) riding).setAcceleration(message.acceleration);
				}
			}
		});
	}
}
