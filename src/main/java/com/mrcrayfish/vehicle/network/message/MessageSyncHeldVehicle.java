package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.client.network.ClientPlayHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSyncHeldVehicle implements IMessage<MessageSyncHeldVehicle>
{
    private int entityId;
    private CompoundTag vehicleTag;

    public MessageSyncHeldVehicle() {}

    public MessageSyncHeldVehicle(int entityId, CompoundTag vehicleTag)
    {
        this.entityId = entityId;
        this.vehicleTag = vehicleTag;
    }

    @Override
    public void encode(MessageSyncHeldVehicle message, FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(message.entityId);
        buffer.writeNbt(message.vehicleTag);
    }

    @Override
    public MessageSyncHeldVehicle decode(FriendlyByteBuf buffer)
    {
        return new MessageSyncHeldVehicle(buffer.readVarInt(), buffer.readNbt());
    }

    @Override
    public void handle(MessageSyncHeldVehicle message, Supplier<Context> supplier)
    {
        if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            IMessage.enqueueTask(supplier, () -> ClientPlayHandler.handleSyncHeldVehicle(message));
        }
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public CompoundTag getVehicleTag()
    {
        return this.vehicleTag;
    }
}
