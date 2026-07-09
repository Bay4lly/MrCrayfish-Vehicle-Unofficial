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
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.network.message.IMessage;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageHitchTrailer
implements IMessage<MessageHitchTrailer> {
    public static final CustomPacketPayload.Type<MessageHitchTrailer> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"hitch_trailer"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageHitchTrailer> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageHitchTrailer)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageHitchTrailer().decode((RegistryFriendlyByteBuf)buf));
    private boolean hitch;
    private int vehicleId = -1;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageHitchTrailer() {
    }

    public MessageHitchTrailer(boolean hitch) {
        this.hitch = hitch;
    }

    public MessageHitchTrailer(int vehicleId, boolean hitch) {
        this.vehicleId = vehicleId;
        this.hitch = hitch;
    }

    @Override
    public void encode(MessageHitchTrailer message, RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(message.vehicleId);
        buffer.writeBoolean(message.hitch);
    }

    @Override
    public MessageHitchTrailer decode(RegistryFriendlyByteBuf buffer) {
        return new MessageHitchTrailer(buffer.readVarInt(), buffer.readBoolean());
    }

    @Override
    public void handle(MessageHitchTrailer message, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null) {
                Level world = player.level();
                Entity ent = world.getEntity(message.vehicleId);
                if (!(ent instanceof VehicleEntity)) {
                    return;
                }
                VehicleEntity vehicle = (VehicleEntity)ent;
                if (player.getVehicle() != vehicle && vehicle.getSeatTracker().getSeatIndex(player.getUUID()) == -1) {
                    return;
                }
                if (!vehicle.canTowTrailer()) {
                    return;
                }
                if (!message.hitch) {
                    if (vehicle.getTrailer() != null) {
                        vehicle.setTrailerAndPulling(null);
                        player.level().playSound(null, vehicle.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 1.0f);
                    }
                } else {
                    VehicleProperties properties = vehicle.getProperties();
                    Vec3 vehicleVec = vehicle.position();
                    Vec3 towBarVec = properties.getTowBarPosition();
                    towBarVec = new Vec3(towBarVec.x * 0.0625, towBarVec.y * 0.0625, towBarVec.z * 0.0625 + properties.getBodyPosition().getZ());
                    if (vehicle instanceof LandVehicleEntity) {
                        LandVehicleEntity landVehicle = (LandVehicleEntity)vehicle;
                        vehicleVec = vehicleVec.add(towBarVec.yRot((float)Math.toRadians(-vehicle.getYRot() + landVehicle.additionalYaw)));
                    } else {
                        vehicleVec = vehicleVec.add(towBarVec.yRot((float)Math.toRadians(-vehicle.getYRot())));
                    }
                    AABB towBarBox = new AABB(vehicleVec.x, vehicleVec.y, vehicleVec.z, vehicleVec.x, vehicleVec.y, vehicleVec.z).inflate(0.5);
                    List trailers = player.level().getEntitiesOfClass(TrailerEntity.class, vehicle.getBoundingBox().inflate(5.0), input -> input.getPullingEntity() == null);
                    for (Object t : trailers) {
                        TrailerEntity trailer = (TrailerEntity)t;
                        if (trailer.getPullingEntity() != null) continue;
                        Vec3 trailerVec = trailer.position();
                        Vec3 hitchVec = new Vec3(0.0, 0.0, -trailer.getHitchOffset() / 16.0);
                        trailerVec = trailerVec.add(hitchVec.yRot((float)Math.toRadians(-trailer.getYRot())));
                        AABB hitchBox = new AABB(trailerVec.x, trailerVec.y, trailerVec.z, trailerVec.x, trailerVec.y, trailerVec.z).inflate(0.25);
                        if (!towBarBox.intersects(hitchBox)) continue;
                        vehicle.setTrailerAndPulling(trailer);
                        player.level().playSound(null, vehicle.blockPosition(), SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 1.0f, 1.5f);
                        return;
                    }
                    double bestDist = Double.MAX_VALUE;
                    TrailerEntity bestTrailer = null;
                    for (Object t : trailers) {
                        TrailerEntity trailer = (TrailerEntity)t;
                        if (trailer.getPullingEntity() != null) continue;
                        Vec3 trailerVec = trailer.position();
                        Vec3 hitchVec = new Vec3(0.0, 0.0, -trailer.getHitchOffset() / 16.0);
                        trailerVec = trailerVec.add(hitchVec.yRot((float)Math.toRadians(-trailer.getYRot())));
                        double dx = trailerVec.x - vehicleVec.x;
                        double dy = trailerVec.y - vehicleVec.y;
                        double dz = trailerVec.z - vehicleVec.z;
                        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        if (!(dist < bestDist)) continue;
                        bestDist = dist;
                        bestTrailer = trailer;
                    }
                    if (bestTrailer != null && bestDist <= 0.75) {
                        vehicle.setTrailerAndPulling(bestTrailer);
                        player.level().playSound(null, vehicle.blockPosition(), SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 1.0f, 1.5f);
                        return;
                    }
                }
            }
        });
    }
}

