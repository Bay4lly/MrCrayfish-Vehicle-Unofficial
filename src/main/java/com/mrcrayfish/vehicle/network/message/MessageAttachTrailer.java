package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import net.minecraft.server.level.ServerPlayer;
import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;


/**
 * Author: MrCrayfish
 */
public class MessageAttachTrailer implements IMessage<MessageAttachTrailer>
{
    public static final CustomPacketPayload.Type<MessageAttachTrailer> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "attach_trailer"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageAttachTrailer> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageAttachTrailer().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private int trailerId;
    private int entityId;

    public MessageAttachTrailer() {}

    public MessageAttachTrailer(int trailerId, int entityId)
    {
        this.trailerId = trailerId;
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageAttachTrailer message, RegistryFriendlyByteBuf buffer)
    {
        buffer.writeInt(message.trailerId);
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageAttachTrailer decode(RegistryFriendlyByteBuf buffer)
    {
        return new MessageAttachTrailer(buffer.readInt(), buffer.readInt());
    }

    @Override
    public void handle(MessageAttachTrailer message, IPayloadContext context)
    {
        context.enqueueWork(() ->
        {
            ServerPlayer player = ((ServerPlayer) context.player());
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
    }
}
