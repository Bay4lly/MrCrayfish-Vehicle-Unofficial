/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.network.message.IMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageHorn
implements IMessage<MessageHorn> {
    public static final CustomPacketPayload.Type<MessageHorn> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"horn"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageHorn> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageHorn)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageHorn().decode((RegistryFriendlyByteBuf)buf));
    private boolean horn;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageHorn() {
    }

    public MessageHorn(boolean horn) {
        this.horn = horn;
    }

    @Override
    public void encode(MessageHorn message, RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(message.horn);
    }

    @Override
    public MessageHorn decode(RegistryFriendlyByteBuf buffer) {
        return new MessageHorn(buffer.readBoolean());
    }

    @Override
    public void handle(MessageHorn message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity riding;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && (riding = player.getVehicle()) instanceof PoweredVehicleEntity) {
                ((PoweredVehicleEntity)riding).setHorn(message.horn);
            }
        });
    }
}

