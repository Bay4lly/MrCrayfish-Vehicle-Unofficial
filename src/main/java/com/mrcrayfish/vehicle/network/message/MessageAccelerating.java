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

public class MessageAccelerating
implements IMessage<MessageAccelerating> {
    public static final CustomPacketPayload.Type<MessageAccelerating> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"accelerating"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageAccelerating> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageAccelerating)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageAccelerating().decode((RegistryFriendlyByteBuf)buf));
    private PoweredVehicleEntity.AccelerationDirection acceleration;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageAccelerating() {
    }

    public MessageAccelerating(PoweredVehicleEntity.AccelerationDirection acceleration) {
        this.acceleration = acceleration;
    }

    @Override
    public void encode(MessageAccelerating message, RegistryFriendlyByteBuf buffer) {
        buffer.writeEnum((Enum)message.acceleration);
    }

    @Override
    public MessageAccelerating decode(RegistryFriendlyByteBuf buffer) {
        return new MessageAccelerating((PoweredVehicleEntity.AccelerationDirection)buffer.readEnum(PoweredVehicleEntity.AccelerationDirection.class));
    }

    @Override
    public void handle(MessageAccelerating message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity riding;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && (riding = player.getVehicle()) instanceof PoweredVehicleEntity) {
                ((PoweredVehicleEntity)riding).setAcceleration(message.acceleration);
            }
        });
    }
}

