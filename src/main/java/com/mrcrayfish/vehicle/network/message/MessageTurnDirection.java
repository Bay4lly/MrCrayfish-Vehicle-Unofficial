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


public class MessageTurnDirection implements IMessage<MessageTurnDirection>
{
    public static final CustomPacketPayload.Type<MessageTurnDirection> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "turn_direction"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageTurnDirection> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageTurnDirection().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

	private PoweredVehicleEntity.TurnDirection direction;

	public MessageTurnDirection() {}

	public MessageTurnDirection(PoweredVehicleEntity.TurnDirection direction)
	{
		this.direction = direction;
	}

	@Override
	public void encode(MessageTurnDirection message, RegistryFriendlyByteBuf buffer)
	{
		buffer.writeEnum(message.direction);
	}

	@Override
	public MessageTurnDirection decode(RegistryFriendlyByteBuf buffer)
	{
		return new MessageTurnDirection(buffer.readEnum(PoweredVehicleEntity.TurnDirection.class));
	}

	@Override
	public void handle(MessageTurnDirection message, IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			ServerPlayer player = ((ServerPlayer) context.player());
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PoweredVehicleEntity)
				{
					((PoweredVehicleEntity) riding).setTurnDirection(message.direction);
				}
			}
		});
	}
}
