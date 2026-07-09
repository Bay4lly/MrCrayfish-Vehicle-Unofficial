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

public class MessageTravelProperties
implements IMessage<MessageTravelProperties> {
    public static final CustomPacketPayload.Type<MessageTravelProperties> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"travel_properties"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageTravelProperties> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageTravelProperties)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageTravelProperties().decode((RegistryFriendlyByteBuf)buf));
    private float travelSpeed;
    private float travelDirection;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageTravelProperties() {
    }

    public MessageTravelProperties(float travelSpeed, float travelDirection) {
        this.travelSpeed = travelSpeed;
        this.travelDirection = travelDirection;
    }

    @Override
    public void encode(MessageTravelProperties message, RegistryFriendlyByteBuf buffer) {
        buffer.writeFloat(message.travelSpeed);
        buffer.writeFloat(message.travelDirection);
    }

    @Override
    public MessageTravelProperties decode(RegistryFriendlyByteBuf buffer) {
        return new MessageTravelProperties(buffer.readFloat(), buffer.readFloat());
    }

    @Override
    public void handle(MessageTravelProperties message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity riding;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && (riding = player.getVehicle()) instanceof HelicopterEntity) {
                HelicopterEntity helicopter = (HelicopterEntity)riding;
                helicopter.setTravelSpeed(message.travelSpeed);
                helicopter.setTravelDirection(message.travelDirection);
            }
        });
    }
}

