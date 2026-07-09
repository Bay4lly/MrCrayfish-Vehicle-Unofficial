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

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
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

public class MessageFuelVehicle
implements IMessage<MessageFuelVehicle> {
    public static final CustomPacketPayload.Type<MessageFuelVehicle> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"fuel_vehicle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageFuelVehicle> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageFuelVehicle)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageFuelVehicle().decode((RegistryFriendlyByteBuf)buf));
    protected int entityId;
    private InteractionHand hand;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageFuelVehicle() {
    }

    public MessageFuelVehicle(int entityId, InteractionHand hand) {
        this.entityId = entityId;
        this.hand = hand;
    }

    @Override
    public void encode(MessageFuelVehicle message, RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(message.entityId);
        buffer.writeEnum((Enum)message.hand);
    }

    @Override
    public MessageFuelVehicle decode(RegistryFriendlyByteBuf buffer) {
        return new MessageFuelVehicle(buffer.readInt(), (InteractionHand)buffer.readEnum(InteractionHand.class));
    }

    @Override
    public void handle(MessageFuelVehicle message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity targetEntity;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && (targetEntity = player.level().getEntity(message.entityId)) instanceof PoweredVehicleEntity) {
                ((PoweredVehicleEntity)targetEntity).fuelVehicle((Player)player, message.hand);
            }
        });
    }
}

