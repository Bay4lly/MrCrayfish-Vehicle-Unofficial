package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;


public class MessageFuelVehicle implements IMessage<MessageFuelVehicle>
{
    public static final CustomPacketPayload.Type<MessageFuelVehicle> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "fuel_vehicle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageFuelVehicle> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageFuelVehicle().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    protected int entityId;
    private InteractionHand hand;

    public MessageFuelVehicle()
    {
    }

    public MessageFuelVehicle(int entityId, InteractionHand hand)
    {
        this.entityId = entityId;
        this.hand = hand;
    }

    @Override
    public void encode(MessageFuelVehicle message, RegistryFriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
        buffer.writeEnum(message.hand);
    }

    @Override
    public MessageFuelVehicle decode(RegistryFriendlyByteBuf buffer)
    {
        return new MessageFuelVehicle(buffer.readInt(), buffer.readEnum(InteractionHand.class));
    }

    @Override
    public void handle(MessageFuelVehicle message, IPayloadContext context)
    {
        context.enqueueWork(() -> {
            ServerPlayer player = ((ServerPlayer) context.player());
            if(player != null)
            {
                Entity targetEntity = player.level().getEntity(message.entityId);
                if(targetEntity instanceof PoweredVehicleEntity)
                {
                    ((PoweredVehicleEntity) targetEntity).fuelVehicle(player, message.hand);
                }
            }
        });
    }
}