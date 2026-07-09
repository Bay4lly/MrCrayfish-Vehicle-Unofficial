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

public class MessageTurnDirection
implements IMessage<MessageTurnDirection> {
    public static final CustomPacketPayload.Type<MessageTurnDirection> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"turn_direction"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageTurnDirection> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageTurnDirection)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageTurnDirection().decode((RegistryFriendlyByteBuf)buf));
    private PoweredVehicleEntity.TurnDirection direction;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageTurnDirection() {
    }

    public MessageTurnDirection(PoweredVehicleEntity.TurnDirection direction) {
        this.direction = direction;
    }

    @Override
    public void encode(MessageTurnDirection message, RegistryFriendlyByteBuf buffer) {
        buffer.writeEnum((Enum)message.direction);
    }

    @Override
    public MessageTurnDirection decode(RegistryFriendlyByteBuf buffer) {
        return new MessageTurnDirection((PoweredVehicleEntity.TurnDirection)buffer.readEnum(PoweredVehicleEntity.TurnDirection.class));
    }

    @Override
    public void handle(MessageTurnDirection message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity riding;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && (riding = player.getVehicle()) instanceof PoweredVehicleEntity) {
                ((PoweredVehicleEntity)riding).setTurnDirection(message.direction);
            }
        });
    }
}

