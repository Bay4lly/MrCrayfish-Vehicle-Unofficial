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

import com.mrcrayfish.vehicle.entity.PlaneEntity;
import com.mrcrayfish.vehicle.network.message.IMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageFlaps
implements IMessage<MessageFlaps> {
    public static final CustomPacketPayload.Type<MessageFlaps> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"flaps"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageFlaps> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageFlaps)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageFlaps().decode((RegistryFriendlyByteBuf)buf));
    private PlaneEntity.FlapDirection flapDirection;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageFlaps() {
    }

    public MessageFlaps(PlaneEntity.FlapDirection flapDirection) {
        this.flapDirection = flapDirection;
    }

    @Override
    public void encode(MessageFlaps message, RegistryFriendlyByteBuf buffer) {
        buffer.writeEnum((Enum)message.flapDirection);
    }

    @Override
    public MessageFlaps decode(RegistryFriendlyByteBuf buffer) {
        return new MessageFlaps((PlaneEntity.FlapDirection)buffer.readEnum(PlaneEntity.FlapDirection.class));
    }

    @Override
    public void handle(MessageFlaps message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity riding;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && (riding = player.getVehicle()) instanceof PlaneEntity) {
                ((PlaneEntity)riding).setFlapDirection(message.flapDirection);
            }
        });
    }
}

