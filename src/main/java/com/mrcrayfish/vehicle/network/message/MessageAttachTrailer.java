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
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.network.message.IMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageAttachTrailer
implements IMessage<MessageAttachTrailer> {
    public static final CustomPacketPayload.Type<MessageAttachTrailer> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"attach_trailer"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageAttachTrailer> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageAttachTrailer)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageAttachTrailer().decode((RegistryFriendlyByteBuf)buf));
    private int trailerId;
    private int entityId;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageAttachTrailer() {
    }

    public MessageAttachTrailer(int trailerId, int entityId) {
        this.trailerId = trailerId;
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageAttachTrailer message, RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(message.trailerId);
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageAttachTrailer decode(RegistryFriendlyByteBuf buffer) {
        return new MessageAttachTrailer(buffer.readInt(), buffer.readInt());
    }

    @Override
    public void handle(MessageAttachTrailer message, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null) {
                VehicleMod.LOGGER.debug("[AttachTrailer] server received attach request trailerId={} for playerId={}", (Object)message.trailerId, (Object)message.entityId);
                Level world = player.level();
                Entity trailerEntity = world.getEntity(message.trailerId);
                if (trailerEntity instanceof TrailerEntity) {
                    TrailerEntity trailer = (TrailerEntity)trailerEntity;
                    Entity entity = world.getEntity(message.entityId);
                    if (entity instanceof Player && entity.getVehicle() == null && (Integer)ModDataKeys.TRAILER.getValue(((Player)entity)) == -1) {
                        VehicleMod.LOGGER.debug("[AttachTrailer] attaching trailer {} to player {}", (Object)message.trailerId, (Object)message.entityId);
                        trailer.setPullingOrMaybeTrailer(entity);
                        ModDataKeys.TRAILER.setValue(((Player)entity), message.trailerId);
                    }
                }
            }
        });
    }
}

