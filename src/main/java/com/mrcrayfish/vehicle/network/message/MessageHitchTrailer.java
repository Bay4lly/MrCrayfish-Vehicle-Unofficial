package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageHitchTrailer implements IMessage<MessageHitchTrailer>
{
    private boolean hitch;
    private int vehicleId = -1;

    public MessageHitchTrailer() {}

    public MessageHitchTrailer(boolean hitch)
    {
        this.hitch = hitch;
    }

    public MessageHitchTrailer(int vehicleId, boolean hitch)
    {
        this.vehicleId = vehicleId;
        this.hitch = hitch;
    }

    @Override
    public void encode(MessageHitchTrailer message, FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(message.vehicleId);
        buffer.writeBoolean(message.hitch);
    }

    @Override
    public MessageHitchTrailer decode(FriendlyByteBuf buffer)
    {
        return new MessageHitchTrailer(buffer.readVarInt(), buffer.readBoolean());
    }

    @Override
    public void handle(MessageHitchTrailer message, Supplier<Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayer player = supplier.get().getSender();
                if(player != null)
            {
                    Level world = player.level();
                    Entity ent = world.getEntity(message.vehicleId);
                    if(!(ent instanceof VehicleEntity))
                        return;

                    VehicleEntity vehicle = (VehicleEntity) ent;

                    // Ensure the player is actually occupying or controlling this vehicle on the server
                    if(player.getVehicle() != vehicle && vehicle.getSeatTracker().getSeatIndex(player.getUUID()) == -1)
                    {
                        return;
                    }
                if(!vehicle.canTowTrailer())
                    return;

                if(!message.hitch)
                {
                    if(vehicle.getTrailer() != null)
                    {
                        vehicle.setTrailerAndPulling(null);
                        player.level().playSound(null, vehicle.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
                else
                {
                    VehicleProperties properties = vehicle.getProperties();
                    Vec3 vehicleVec = vehicle.position();
                    Vec3 towBarVec = properties.getTowBarPosition();
                    towBarVec = new Vec3(towBarVec.x * 0.0625, towBarVec.y * 0.0625, towBarVec.z * 0.0625 + properties.getBodyPosition().getZ());
                    if(vehicle instanceof LandVehicleEntity)
                    {
                        LandVehicleEntity landVehicle = (LandVehicleEntity) vehicle;
                        vehicleVec = vehicleVec.add(towBarVec.yRot((float) Math.toRadians(-vehicle.getYRot() + landVehicle.additionalYaw)));
                    }
                    else
                    {
                        vehicleVec = vehicleVec.add(towBarVec.yRot((float) Math.toRadians(-vehicle.getYRot())));
                    }

                    // Create a small box around the tow bar and search for unattached trailers.
                    // Inflate slightly more to be more forgiving for server/client desync and player aim.
                    AABB towBarBox = new AABB(vehicleVec.x, vehicleVec.y, vehicleVec.z, vehicleVec.x, vehicleVec.y, vehicleVec.z).inflate(0.5);
                    List<TrailerEntity> trailers = player.level().getEntitiesOfClass(TrailerEntity.class, vehicle.getBoundingBox().inflate(5), input -> input.getPullingEntity() == null);
                    for(TrailerEntity trailer : trailers)
                    {
                        if(trailer.getPullingEntity() != null)
                            continue;

                        Vec3 trailerVec = trailer.position();
                        Vec3 hitchVec = new Vec3(0, 0, -trailer.getHitchOffset() / 16.0);
                        trailerVec = trailerVec.add(hitchVec.yRot((float) Math.toRadians(-trailer.getYRot())));
                        AABB hitchBox = new AABB(trailerVec.x, trailerVec.y, trailerVec.z, trailerVec.x, trailerVec.y, trailerVec.z).inflate(0.25);
                        if(towBarBox.intersects(hitchBox))
                        {
                            vehicle.setTrailerAndPulling(trailer);
                            player.level().playSound(null, vehicle.blockPosition(), SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 1.0F, 1.5F);
                            return;
                        }
                    }

                    // Fallback: if no exact intersection found, attach the closest unattached trailer to the tow bar
                    // within a reasonable threshold (0.75). This helps when server/client positions differ slightly.
                    double bestDist = Double.MAX_VALUE;
                    TrailerEntity bestTrailer = null;
                    for(TrailerEntity trailer : trailers)
                    {
                        if(trailer.getPullingEntity() != null) continue;
                        Vec3 trailerVec = trailer.position();
                        Vec3 hitchVec = new Vec3(0, 0, -trailer.getHitchOffset() / 16.0);
                        trailerVec = trailerVec.add(hitchVec.yRot((float) Math.toRadians(-trailer.getYRot())));
                        double dx = trailerVec.x - vehicleVec.x;
                        double dy = trailerVec.y - vehicleVec.y;
                        double dz = trailerVec.z - vehicleVec.z;
                        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        if(dist < bestDist)
                        {
                            bestDist = dist;
                            bestTrailer = trailer;
                        }
                    }
                    if(bestTrailer != null && bestDist <= 0.75)
                    {
                        vehicle.setTrailerAndPulling(bestTrailer);
                        player.level().playSound(null, vehicle.blockPosition(), SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 1.0F, 1.5F);
                        return;
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
