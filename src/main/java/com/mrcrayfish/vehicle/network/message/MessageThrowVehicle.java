/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.network.message.IMessage;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageThrowVehicle
implements IMessage<MessageThrowVehicle> {
    public static final CustomPacketPayload.Type<MessageThrowVehicle> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"throw_vehicle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageThrowVehicle> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageThrowVehicle)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageThrowVehicle().decode((RegistryFriendlyByteBuf)buf));

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void encode(MessageThrowVehicle message, RegistryFriendlyByteBuf buffer) {
    }

    @Override
    public MessageThrowVehicle decode(RegistryFriendlyByteBuf buffer) {
        return new MessageThrowVehicle();
    }

    @Override
    public void handle(MessageThrowVehicle message, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && player.isCrouching()) {
                if (!HeldVehicleDataHandler.isHoldingVehicle((Player)player)) {
                    return;
                }
                CompoundTag heldTag = HeldVehicleDataHandler.getHeldVehicle((Player)player);
                Optional optional = EntityType.byString((String)heldTag.getString("id"));
                if (!optional.isPresent()) {
                    return;
                }
                EntityType entityType = (EntityType)optional.get();
                Entity entity = entityType.create(player.level());
                if (entity instanceof VehicleEntity) {
                    entity.load(heldTag);
                    HeldVehicleDataHandler.setHeldVehicle((Player)player, new CompoundTag());
                    float rotation = (player.getYHeadRot() + 90.0f) % 360.0f;
                    Vec3 heldOffset = ((VehicleEntity)entity).getProperties().getHeldOffset().yRot((float)Math.toRadians(-player.getYHeadRot()));
                    Vec3 lookVec = player.getLookAngle();
                    double posX = player.getX();
                    double posY = player.getY() + (double)player.getEyeHeight();
                    double posZ = player.getZ();
                    entity.absMoveTo(posX + heldOffset.x * 0.0625, posY + heldOffset.y * 0.0625, posZ + heldOffset.z * 0.0625, rotation, 0.0f);
                    Vec3 motion = entity.getDeltaMovement();
                    entity.setDeltaMovement(motion.x() + lookVec.x, motion.y() + lookVec.y, motion.z() + lookVec.z);
                    entity.fallDistance = 0.0f;
                    player.level().addFreshEntity(entity);
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), (SoundEvent)ModSounds.ENTITY_VEHICLE_PICK_UP.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                }
            }
        });
    }
}

