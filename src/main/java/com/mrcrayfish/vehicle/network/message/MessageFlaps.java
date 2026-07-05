package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.entity.PlaneEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;


public class MessageFlaps implements IMessage<MessageFlaps>
{
    public static final CustomPacketPayload.Type<MessageFlaps> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "flaps"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageFlaps> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageFlaps().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

	private PlaneEntity.FlapDirection flapDirection;

	public MessageFlaps() {}

	public MessageFlaps(PlaneEntity.FlapDirection flapDirection)
	{
		this.flapDirection = flapDirection;
	}

	@Override
	public void encode(MessageFlaps message, RegistryFriendlyByteBuf buffer)
	{
		buffer.writeEnum(message.flapDirection);
	}

	@Override
	public MessageFlaps decode(RegistryFriendlyByteBuf buffer)
	{
		return new MessageFlaps(buffer.readEnum(PlaneEntity.FlapDirection.class));
	}

	@Override
	public void handle(MessageFlaps message, IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			ServerPlayer player = ((ServerPlayer) context.player());
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PlaneEntity)
				{
					((PlaneEntity) riding).setFlapDirection(message.flapDirection);
				}
			}
		});
	}
}
