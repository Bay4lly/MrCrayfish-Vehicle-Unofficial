package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageThrowVehicle implements IMessage<MessageThrowVehicle>
{
    @Override
    public void encode(MessageThrowVehicle message, FriendlyByteBuf buffer) {}

    @Override
    public MessageThrowVehicle decode(FriendlyByteBuf buffer)
    {
        return new MessageThrowVehicle();
    }

    @Override
    public void handle(MessageThrowVehicle message, Supplier<Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayer player = supplier.get().getSender();
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
        supplier.get().setPacketHandled(true);
    }
}
