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


public class MessagePower implements IMessage<MessagePower>
{
    public static final CustomPacketPayload.Type<MessagePower> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "power"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessagePower> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessagePower().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

	private float power;

	public MessagePower() {}

	public MessagePower(float power)
	{
		this.power = power;
	}

	@Override
	public void encode(MessagePower message, RegistryFriendlyByteBuf buffer)
	{
		buffer.writeFloat(message.power);
	}

	@Override
	public MessagePower decode(RegistryFriendlyByteBuf buffer)
	{
		return new MessagePower(buffer.readFloat());
	}

	@Override
	public void handle(MessagePower message, IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			ServerPlayer player = ((ServerPlayer) context.player());
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PoweredVehicleEntity)
				{
					((PoweredVehicleEntity) riding).setPower(message.power);
				}
			}
		});
	}
}
