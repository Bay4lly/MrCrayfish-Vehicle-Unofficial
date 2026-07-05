package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class MessageThrowVehicle implements IMessage<MessageThrowVehicle>
{
    public static final CustomPacketPayload.Type<MessageThrowVehicle> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "throw_vehicle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageThrowVehicle> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageThrowVehicle().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    @Override
    public void encode(MessageThrowVehicle message, RegistryFriendlyByteBuf buffer) {}

    @Override
    public MessageThrowVehicle decode(RegistryFriendlyByteBuf buffer)
    {
        return new MessageThrowVehicle();
    }

    @Override
    public void handle(MessageThrowVehicle message, IPayloadContext context)
    {
        context.enqueueWork(() ->
        {
            ServerPlayer player = ((ServerPlayer) context.player());
            if(player != null && player.isCrouching())
            {
                //Spawns the vehicle and plays the placing sound
                if(!HeldVehicleDataHandler.isHoldingVehicle(player))
                    return;

                CompoundTag heldTag = HeldVehicleDataHandler.getHeldVehicle(player);
                Optional<EntityType<?>> optional = EntityType.byString(heldTag.getString("id"));
                if(!optional.isPresent())
                    return;

                EntityType<?> entityType = optional.get();
                Entity entity = entityType.create(player.level());
                if(entity instanceof VehicleEntity)
                {
                    entity.load(heldTag);

                    //Updates the player capability
                    HeldVehicleDataHandler.setHeldVehicle(player, new CompoundTag());

                    //Sets the positions and spawns the entity
                    float rotation = (player.getYHeadRot() + 90F) % 360.0F;
                    Vec3 heldOffset = ((VehicleEntity) entity).getProperties().getHeldOffset().yRot((float) Math.toRadians(-player.getYHeadRot()));

                    //Gets the clicked vec if it was a right click block event
                    Vec3 lookVec = player.getLookAngle();
                    double posX = player.getX();
                    double posY = player.getY() + player.getEyeHeight();
                    double posZ = player.getZ();
                    entity.absMoveTo(posX + heldOffset.x * 0.0625D, posY + heldOffset.y * 0.0625D, posZ + heldOffset.z * 0.0625D, rotation, 0F);

                    Vec3 motion = entity.getDeltaMovement();
                    entity.setDeltaMovement(motion.x() + lookVec.x, motion.y() + lookVec.y, motion.z() + lookVec.z);
                    entity.fallDistance = 0.0F;

                    player.level().addFreshEntity(entity);
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ENTITY_VEHICLE_PICK_UP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        });
    }
}
