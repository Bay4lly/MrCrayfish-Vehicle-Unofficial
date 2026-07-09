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
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.inventory.IAttachableChest;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.network.message.IMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageOpenStorage
implements IMessage<MessageOpenStorage> {
    public static final CustomPacketPayload.Type<MessageOpenStorage> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"open_storage"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageOpenStorage> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageOpenStorage)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageOpenStorage().decode((RegistryFriendlyByteBuf)buf));
    private int entityId;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageOpenStorage() {
    }

    public MessageOpenStorage(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageOpenStorage message, RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageOpenStorage decode(RegistryFriendlyByteBuf buffer) {
        return new MessageOpenStorage(buffer.readInt());
    }

    @Override
    public void handle(MessageOpenStorage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level world;
            Entity targetEntity;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && (targetEntity = (world = player.level()).getEntity(message.entityId)) instanceof IStorage) {
                IStorage storage = (IStorage)targetEntity;
                float reachDistance = (float)player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).getValue();
                if (player.distanceTo(targetEntity) < reachDistance) {
                    if (targetEntity instanceof IAttachableChest) {
                        IAttachableChest attachableChest = (IAttachableChest)targetEntity;
                        if (attachableChest.hasChest()) {
                            ItemStack stack = player.getInventory().getSelected();
                            if (stack.getItem() == ModItems.WRENCH.get()) {
                                ((IAttachableChest)targetEntity).removeChest();
                            } else {
                                player.openMenu(storage.getStorageContainerProvider(), buffer -> buffer.writeVarInt(message.entityId));
                            }
                        }
                    } else {
                        player.openMenu(storage.getStorageContainerProvider(), buffer -> buffer.writeVarInt(message.entityId));
                    }
                }
            }
        });
    }
}

