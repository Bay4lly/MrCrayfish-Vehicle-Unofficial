package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.client.network.ClientPlayHandler;
import net.minecraft.nbt.CompoundTag;


/**
 * Author: MrCrayfish
 */
public class MessageSyncHeldVehicle implements IMessage<MessageSyncHeldVehicle>
{
    public static final CustomPacketPayload.Type<MessageSyncHeldVehicle> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "sync_held_vehicle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncHeldVehicle> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageSyncHeldVehicle().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private int entityId;
    private CompoundTag vehicleTag;

    public MessageSyncHeldVehicle() {}

    public MessageSyncHeldVehicle(int entityId, CompoundTag vehicleTag)
    {
        this.entityId = entityId;
        this.vehicleTag = vehicleTag;
    }

    @Override
    public void encode(MessageSyncHeldVehicle message, RegistryFriendlyByteBuf buffer)
    {
        buffer.writeVarInt(message.entityId);
        buffer.writeNbt(message.vehicleTag);
    }

    @Override
    public MessageSyncHeldVehicle decode(RegistryFriendlyByteBuf buffer)
    {
        return new MessageSyncHeldVehicle(buffer.readVarInt(), buffer.readNbt());
    }

    @Override
    public void handle(MessageSyncHeldVehicle message, IPayloadContext context)
    {
        if(context.flow().isClientbound())
        {
            IMessage.enqueueTask(context, () -> ClientPlayHandler.handleSyncHeldVehicle(message));
        }
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public CompoundTag getVehicleTag()
    {
        return this.vehicleTag;
    }
}
