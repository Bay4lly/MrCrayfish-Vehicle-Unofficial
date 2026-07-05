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


public class MessageTravelProperties implements IMessage<MessageTravelProperties>
{
    public static final CustomPacketPayload.Type<MessageTravelProperties> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "travel_properties"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageTravelProperties> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageTravelProperties().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

	private float travelSpeed;
	private float travelDirection;

	public MessageTravelProperties() {}

	public MessageTravelProperties(float travelSpeed, float travelDirection)
	{
		this.travelSpeed = travelSpeed;
		this.travelDirection = travelDirection;
	}

	@Override
	public void encode(MessageTravelProperties message, RegistryFriendlyByteBuf buffer)
	{
		buffer.writeFloat(message.travelSpeed);
		buffer.writeFloat(message.travelDirection);
	}

	@Override
	public MessageTravelProperties decode(RegistryFriendlyByteBuf buffer)
	{
		return new MessageTravelProperties(buffer.readFloat(), buffer.readFloat());
	}

	@Override
	public void handle(MessageTravelProperties message, IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			ServerPlayer player = ((ServerPlayer) context.player());
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof HelicopterEntity)
				{
					HelicopterEntity helicopter = (HelicopterEntity) riding;
					helicopter.setTravelSpeed(message.travelSpeed);
					helicopter.setTravelDirection(message.travelDirection);
				}
			}
		});
	}
}
