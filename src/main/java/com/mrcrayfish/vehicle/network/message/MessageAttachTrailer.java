package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageAttachTrailer implements IMessage<MessageAttachTrailer>
{
    private int trailerId;
    private int entityId;

    public MessageAttachTrailer() {}

    public MessageAttachTrailer(int trailerId, int entityId)
    {
        this.trailerId = trailerId;
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageAttachTrailer message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.trailerId);
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageAttachTrailer decode(FriendlyByteBuf buffer)
    {
        return new MessageAttachTrailer(buffer.readInt(), buffer.readInt());
    }

    @Override
    public void handle(MessageAttachTrailer message, Supplier<Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayer player = supplier.get().getSender();
            if(player != null)
            {
                VehicleMod.LOGGER.debug("[AttachTrailer] server received attach request trailerId={} for playerId={}", message.trailerId, message.entityId);
                Level world = player.level();
                Entity trailerEntity = world.getEntity(message.trailerId);
                if(trailerEntity instanceof TrailerEntity)
                {
                    TrailerEntity trailer = (TrailerEntity) trailerEntity;
                    Entity entity = world.getEntity(message.entityId);
                    if(entity instanceof Player && entity.getVehicle() == null && ModDataKeys.TRAILER.getValue((Player) entity) == -1)
                    {
                        VehicleMod.LOGGER.debug("[AttachTrailer] attaching trailer {} to player {}", message.trailerId, message.entityId);
                        trailer.setPullingOrMaybeTrailer(entity);
                        ModDataKeys.TRAILER.setValue((Player) entity, message.trailerId);
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
