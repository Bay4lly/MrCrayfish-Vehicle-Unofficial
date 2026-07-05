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


public class MessageTurnAngle implements IMessage<MessageTurnAngle>
{
    public static final CustomPacketPayload.Type<MessageTurnAngle> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "turn_angle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageTurnAngle> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageTurnAngle().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

	private float angle;

	public MessageTurnAngle() {}

	public MessageTurnAngle(float angle)
	{
		this.angle = angle;
	}

	@Override
	public void encode(MessageTurnAngle message, RegistryFriendlyByteBuf buffer)
	{
		buffer.writeFloat(message.angle);
	}

	@Override
	public MessageTurnAngle decode(RegistryFriendlyByteBuf buffer)
	{
		return new MessageTurnAngle(buffer.readFloat());
	}

	@Override
	public void handle(MessageTurnAngle message, IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			ServerPlayer player = ((ServerPlayer) context.player());
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PoweredVehicleEntity)
				{
					((PoweredVehicleEntity) riding).setTargetTurnAngle(message.angle);
				}
			}
		});
	}
}
