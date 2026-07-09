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
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.network.message.IMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessagePickupVehicle
implements IMessage<MessagePickupVehicle> {
    public static final CustomPacketPayload.Type<MessagePickupVehicle> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"pickup_vehicle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessagePickupVehicle> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessagePickupVehicle)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessagePickupVehicle().decode((RegistryFriendlyByteBuf)buf));
    private int entityId;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessagePickupVehicle() {
    }

    public MessagePickupVehicle(Entity targetEntity) {
        this.entityId = targetEntity.getId();
    }

    public MessagePickupVehicle(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessagePickupVehicle message, RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessagePickupVehicle decode(RegistryFriendlyByteBuf buffer) {
        return new MessagePickupVehicle(buffer.readInt());
    }

    @Override
    public void handle(MessagePickupVehicle message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity targetEntity;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && player.isCrouching() && (targetEntity = player.level().getEntity(message.entityId)) != null) {
                CommonEvents.pickUpVehicle(player.level(), (Player)player, InteractionHand.MAIN_HAND, targetEntity);
            }
        });
    }
}

