/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.client.network.ClientPlayHandler;
import com.mrcrayfish.vehicle.network.message.IMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageSyncHeldVehicle
implements IMessage<MessageSyncHeldVehicle> {
    public static final CustomPacketPayload.Type<MessageSyncHeldVehicle> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"sync_held_vehicle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncHeldVehicle> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageSyncHeldVehicle)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageSyncHeldVehicle().decode((RegistryFriendlyByteBuf)buf));
    private int entityId;
    private CompoundTag vehicleTag;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageSyncHeldVehicle() {
    }

    public MessageSyncHeldVehicle(int entityId, CompoundTag vehicleTag) {
        this.entityId = entityId;
        this.vehicleTag = vehicleTag;
    }

    @Override
    public void encode(MessageSyncHeldVehicle message, RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(message.entityId);
        buffer.writeNbt((Tag)message.vehicleTag);
    }

    @Override
    public MessageSyncHeldVehicle decode(RegistryFriendlyByteBuf buffer) {
        return new MessageSyncHeldVehicle(buffer.readVarInt(), buffer.readNbt());
    }

    @Override
    public void handle(MessageSyncHeldVehicle message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            IMessage.enqueueTask(context, () -> ClientPlayHandler.handleSyncHeldVehicle(message));
        }
    }

    public int getEntityId() {
        return this.entityId;
    }

    public CompoundTag getVehicleTag() {
        return this.vehicleTag;
    }
}

