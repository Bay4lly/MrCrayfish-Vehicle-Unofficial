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

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.network.message.IMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageDrift
implements IMessage<MessageDrift> {
    public static final CustomPacketPayload.Type<MessageDrift> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"drift"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageDrift> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageDrift)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageDrift().decode((RegistryFriendlyByteBuf)buf));
    private boolean drifting;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageDrift() {
    }

    public MessageDrift(boolean drifting) {
        this.drifting = drifting;
    }

    @Override
    public void encode(MessageDrift message, RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(message.drifting);
    }

    @Override
    public MessageDrift decode(RegistryFriendlyByteBuf buffer) {
        return new MessageDrift(buffer.readBoolean());
    }

    @Override
    public void handle(MessageDrift message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity riding;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && (riding = player.getVehicle()) instanceof LandVehicleEntity) {
                ((LandVehicleEntity)riding).setDrifting(message.drifting);
            }
        });
    }
}

