/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.client.network.ClientPlayHandler;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.network.message.IMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageSyncInventory
implements IMessage<MessageSyncInventory> {
    public static final CustomPacketPayload.Type<MessageSyncInventory> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"sync_inventory"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncInventory> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageSyncInventory)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageSyncInventory().decode((RegistryFriendlyByteBuf)buf));
    private int entityId;
    private CompoundTag compound;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageSyncInventory() {
    }

    public MessageSyncInventory(int entityId, StorageInventory storageInventory) {
        this.entityId = entityId;
        CompoundTag tag = new CompoundTag();
        tag.put("Inventory", (Tag)storageInventory.createTag());
        this.compound = tag;
    }

    private MessageSyncInventory(int entityId, CompoundTag compound) {
        this.entityId = entityId;
        this.compound = compound;
    }

    @Override
    public void encode(MessageSyncInventory message, RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(message.entityId);
        buffer.writeNbt((Tag)message.compound);
    }

    @Override
    public MessageSyncInventory decode(RegistryFriendlyByteBuf buffer) {
        return new MessageSyncInventory(buffer.readInt(), buffer.readNbt());
    }

    @Override
    public void handle(MessageSyncInventory message, IPayloadContext context) {
        IMessage.enqueueTask(context, () -> ClientPlayHandler.handleSyncInventory(message));
    }

    public int getEntityId() {
        return this.entityId;
    }

    public CompoundTag getCompound() {
        return this.compound;
    }
}

