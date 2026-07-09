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
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.SoundType
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.inventory.IAttachableChest;
import com.mrcrayfish.vehicle.network.message.IMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageAttachChest
implements IMessage<MessageAttachChest> {
    public static final CustomPacketPayload.Type<MessageAttachChest> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"attach_chest"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageAttachChest> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageAttachChest)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageAttachChest().decode((RegistryFriendlyByteBuf)buf));
    private int entityId;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageAttachChest() {
    }

    public MessageAttachChest(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageAttachChest message, RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageAttachChest decode(RegistryFriendlyByteBuf buffer) {
        return new MessageAttachChest(buffer.readInt());
    }

    @Override
    public void handle(MessageAttachChest message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level world;
            Entity targetEntity;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null && (targetEntity = (world = player.level()).getEntity(message.entityId)) instanceof IAttachableChest) {
                ItemStack stack;
                IAttachableChest attachableChest;
                float reachDistance = (float)player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).getValue();
                if (player.distanceTo(targetEntity) < reachDistance && !(attachableChest = (IAttachableChest)targetEntity).hasChest() && !(stack = player.getInventory().getSelected()).isEmpty() && stack.getItem() == Items.CHEST) {
                    attachableChest.attachChest(stack);
                    world.playSound(null, targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), SoundType.WOOD.getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }
        });
    }
}

