package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;


public class MessageDrift implements IMessage<MessageDrift>
{
    public static final CustomPacketPayload.Type<MessageDrift> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "drift"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageDrift> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageDrift().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

	private boolean drifting;

	public MessageDrift() {}

	public MessageDrift(boolean drifting)
	{
		this.drifting = drifting;
	}

	@Override
	public void encode(MessageDrift message, RegistryFriendlyByteBuf buffer)
	{
		buffer.writeBoolean(message.drifting);
	}

	@Override
	public MessageDrift decode(RegistryFriendlyByteBuf buffer)
	{
		return new MessageDrift(buffer.readBoolean());
	}

	@Override
	public void handle(MessageDrift message, IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			ServerPlayer player = ((ServerPlayer) context.player());
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof LandVehicleEntity)
				{
					((LandVehicleEntity) riding).setDrifting(message.drifting);
				}
			}
		});
	}
}
