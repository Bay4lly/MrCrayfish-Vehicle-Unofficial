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
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.network.message.IMessage;
import com.mrcrayfish.vehicle.util.CommonUtils;
import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageInteractKey
implements IMessage<MessageInteractKey> {
    public static final CustomPacketPayload.Type<MessageInteractKey> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"interact_key"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageInteractKey> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageInteractKey)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageInteractKey().decode((RegistryFriendlyByteBuf)buf));
    private int entityId;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageInteractKey() {
    }

    public MessageInteractKey(Entity targetEntity) {
        this.entityId = targetEntity.getId();
    }

    private MessageInteractKey(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageInteractKey message, RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageInteractKey decode(RegistryFriendlyByteBuf buffer) {
        return new MessageInteractKey(buffer.readInt());
    }

    @Override
    public void handle(MessageInteractKey message, IPayloadContext context) {
        context.enqueueWork(() -> {
            PoweredVehicleEntity poweredVehicle;
            Entity targetEntity;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && (targetEntity = player.level().getEntity(message.entityId)) instanceof PoweredVehicleEntity && (poweredVehicle = (PoweredVehicleEntity)targetEntity).isKeyNeeded()) {
                ItemStack stack = player.getMainHandItem();
                if (!stack.isEmpty() && stack.getItem() == ModItems.WRENCH.get()) {
                    if (poweredVehicle.isOwner((Player)player)) {
                        poweredVehicle.ejectKey();
                        poweredVehicle.setKeyNeeded(false);
                        CommonUtils.sendInfoMessage((Player)player, "vehicle.status.key_removed");
                    } else {
                        CommonUtils.sendInfoMessage((Player)player, "vehicle.status.invalid_owner");
                    }
                    return;
                }
                if (poweredVehicle.getKeyStack().isEmpty()) {
                    if (!stack.isEmpty() && stack.getItem() == ModItems.KEY.get()) {
                        UUID keyUuid = CommonUtils.getOrCreateStackTag(stack).getUUID("VehicleId");
                        if (poweredVehicle.getUUID().equals(keyUuid)) {
                            poweredVehicle.setKeyStack(stack.copy());
                            player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                        } else {
                            CommonUtils.sendInfoMessage((Player)player, "vehicle.status.key_invalid");
                        }
                    }
                } else {
                    poweredVehicle.ejectKey();
                }
            }
        });
    }
}

