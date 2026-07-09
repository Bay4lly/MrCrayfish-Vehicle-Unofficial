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

public class MessageTurnAngle
implements IMessage<MessageTurnAngle> {
    public static final CustomPacketPayload.Type<MessageTurnAngle> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"turn_angle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageTurnAngle> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageTurnAngle)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageTurnAngle().decode((RegistryFriendlyByteBuf)buf));
    private float angle;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageTurnAngle() {
    }

    public MessageTurnAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public void encode(MessageTurnAngle message, RegistryFriendlyByteBuf buffer) {
        buffer.writeFloat(message.angle);
    }

    @Override
    public MessageTurnAngle decode(RegistryFriendlyByteBuf buffer) {
        return new MessageTurnAngle(buffer.readFloat());
    }

    @Override
    public void handle(MessageTurnAngle message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity riding;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && (riding = player.getVehicle()) instanceof PoweredVehicleEntity) {
                ((PoweredVehicleEntity)riding).setTargetTurnAngle(message.angle);
            }
        });
    }
}

