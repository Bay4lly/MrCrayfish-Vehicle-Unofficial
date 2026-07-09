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

public class MessagePower
implements IMessage<MessagePower> {
    public static final CustomPacketPayload.Type<MessagePower> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"power"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessagePower> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessagePower)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessagePower().decode((RegistryFriendlyByteBuf)buf));
    private float power;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessagePower() {
    }

    public MessagePower(float power) {
        this.power = power;
    }

    @Override
    public void encode(MessagePower message, RegistryFriendlyByteBuf buffer) {
        buffer.writeFloat(message.power);
    }

    @Override
    public MessagePower decode(RegistryFriendlyByteBuf buffer) {
        return new MessagePower(buffer.readFloat());
    }

    @Override
    public void handle(MessagePower message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity riding;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && (riding = player.getVehicle()) instanceof PoweredVehicleEntity) {
                ((PoweredVehicleEntity)riding).setPower(message.power);
            }
        });
    }
}

