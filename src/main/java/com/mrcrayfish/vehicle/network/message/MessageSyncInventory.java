package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.client.network.ClientPlayHandler;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSyncInventory implements IMessage<MessageSyncInventory>
{
    private int entityId;
    private CompoundTag compound;

    public MessageSyncInventory() {}

    public MessageSyncInventory(int entityId, StorageInventory storageInventory)
    {
        this.entityId = entityId;
        CompoundTag tag = new CompoundTag();
        tag.put("Inventory", storageInventory.createTag());
        this.compound = tag;
    }

    private MessageSyncInventory(int entityId, CompoundTag compound)
    {
        this.entityId = entityId;
        this.compound = compound;
    }

    @Override
    public void encode(MessageSyncInventory message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
        buffer.writeNbt(message.compound);
    }

    @Override
    public MessageSyncInventory decode(FriendlyByteBuf buffer)
    {
        return new MessageSyncInventory(buffer.readInt(), buffer.readNbt());
    }

    @Override
    public void handle(MessageSyncInventory message, Supplier<Context> supplier)
    {
        IMessage.enqueueTask(supplier, () -> ClientPlayHandler.handleSyncInventory(message));
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public CompoundTag getCompound()
    {
        return this.compound;
    }
}
