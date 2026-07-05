package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;


public class MessageAltitude implements IMessage<MessageAltitude>
{
    public static final CustomPacketPayload.Type<MessageAltitude> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "altitude"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageAltitude> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageAltitude().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

	private HelicopterEntity.AltitudeChange altitudeChange;

	public MessageAltitude() {}

	public MessageAltitude(HelicopterEntity.AltitudeChange altitudeChange)
	{
		this.altitudeChange = altitudeChange;
	}

	@Override
	public void encode(MessageAltitude message, RegistryFriendlyByteBuf buffer)
	{
		buffer.writeEnum(message.altitudeChange);
	}

	@Override
	public MessageAltitude decode(RegistryFriendlyByteBuf buffer)
	{
		return new MessageAltitude(buffer.readEnum(HelicopterEntity.AltitudeChange.class));
	}

	@Override
	public void handle(MessageAltitude message, IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			ServerPlayer player = ((ServerPlayer) context.player());
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof HelicopterEntity)
				{
					((HelicopterEntity) riding).setAltitudeChange(message.altitudeChange);
				}
			}
		});
	}
}
