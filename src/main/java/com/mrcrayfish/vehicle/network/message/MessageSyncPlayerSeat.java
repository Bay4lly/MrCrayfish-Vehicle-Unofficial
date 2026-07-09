/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageSyncPlayerSeat
implements IMessage<MessageSyncPlayerSeat> {
    public static final CustomPacketPayload.Type<MessageSyncPlayerSeat> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"sync_player_seat"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncPlayerSeat> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageSyncPlayerSeat)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageSyncPlayerSeat().decode((RegistryFriendlyByteBuf)buf));
    private int entityId;
    private int seatIndex;
    private UUID uuid;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageSyncPlayerSeat() {
    }

    public MessageSyncPlayerSeat(int entityId, int seatIndex, UUID uuid) {
        this.entityId = entityId;
        this.seatIndex = seatIndex;
        this.uuid = uuid;
    }

    @Override
    public void encode(MessageSyncPlayerSeat message, RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(message.entityId);
        buffer.writeVarInt(message.seatIndex);
        buffer.writeUUID(message.uuid);
    }

    @Override
    public MessageSyncPlayerSeat decode(RegistryFriendlyByteBuf buffer) {
        return new MessageSyncPlayerSeat(buffer.readVarInt(), buffer.readVarInt(), buffer.readUUID());
    }

    @Override
    public void handle(MessageSyncPlayerSeat message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            IMessage.enqueueTask(context, () -> ClientPlayHandler.handleSyncPlayerSeat(message));
        }
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getSeatIndex() {
        return this.seatIndex;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}

