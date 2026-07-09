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

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.network.message.IMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageAltitude
implements IMessage<MessageAltitude> {
    public static final CustomPacketPayload.Type<MessageAltitude> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"altitude"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageAltitude> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageAltitude)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageAltitude().decode((RegistryFriendlyByteBuf)buf));
    private HelicopterEntity.AltitudeChange altitudeChange;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageAltitude() {
    }

    public MessageAltitude(HelicopterEntity.AltitudeChange altitudeChange) {
        this.altitudeChange = altitudeChange;
    }

    @Override
    public void encode(MessageAltitude message, RegistryFriendlyByteBuf buffer) {
        buffer.writeEnum((Enum)message.altitudeChange);
    }

    @Override
    public MessageAltitude decode(RegistryFriendlyByteBuf buffer) {
        return new MessageAltitude((HelicopterEntity.AltitudeChange)buffer.readEnum(HelicopterEntity.AltitudeChange.class));
    }

    @Override
    public void handle(MessageAltitude message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity riding;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && (riding = player.getVehicle()) instanceof HelicopterEntity) {
                ((HelicopterEntity)riding).setAltitudeChange(message.altitudeChange);
            }
        });
    }
}

