package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.common.CommonEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;


/**
 * Author: MrCrayfish
 */
public class MessagePickupVehicle implements IMessage<MessagePickupVehicle>
{
    public static final CustomPacketPayload.Type<MessagePickupVehicle> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "pickup_vehicle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessagePickupVehicle> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessagePickupVehicle().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private int entityId;

    public MessagePickupVehicle()
    {
    }

    public MessagePickupVehicle(Entity targetEntity)
    {
        this.entityId = targetEntity.getId();
    }

    public MessagePickupVehicle(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessagePickupVehicle message, RegistryFriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessagePickupVehicle decode(RegistryFriendlyByteBuf buffer)
    {
        return new MessagePickupVehicle(buffer.readInt());
    }

    @Override
    public void handle(MessagePickupVehicle message, IPayloadContext context)
    {
        context.enqueueWork(() -> {
            ServerPlayer player = ((ServerPlayer) context.player());
            if(player != null && player.isCrouching())
            {
                Entity targetEntity = player.level().getEntity(message.entityId);
                if(targetEntity != null)
                {
                    CommonEvents.pickUpVehicle(player.level(), player, InteractionHand.MAIN_HAND, targetEntity);
                }
            }
        });
    }
}