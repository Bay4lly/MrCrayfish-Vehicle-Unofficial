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


public class MessageHorn implements IMessage<MessageHorn>
{
    public static final CustomPacketPayload.Type<MessageHorn> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "horn"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageHorn> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageHorn().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

	private boolean horn;

	public MessageHorn() {}

	public MessageHorn(boolean horn)
	{
		this.horn = horn;
	}

	@Override
	public void encode(MessageHorn message, RegistryFriendlyByteBuf buffer)
	{
		buffer.writeBoolean(message.horn);
	}

	@Override
	public MessageHorn decode(RegistryFriendlyByteBuf buffer)
	{
		return new MessageHorn(buffer.readBoolean());
	}

	@Override
	public void handle(MessageHorn message, IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			ServerPlayer player = ((ServerPlayer) context.player());
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PoweredVehicleEntity)
				{
					((PoweredVehicleEntity) riding).setHorn(message.horn);
				}
			}
		});
	}
}
