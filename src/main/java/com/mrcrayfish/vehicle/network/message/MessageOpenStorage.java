package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.inventory.IAttachableChest;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageOpenStorage implements IMessage<MessageOpenStorage>
{
    private int entityId;

    public MessageOpenStorage() {}

    public MessageOpenStorage(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageOpenStorage message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageOpenStorage decode(FriendlyByteBuf buffer)
    {
        return new MessageOpenStorage(buffer.readInt());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(MessageOpenStorage message, Supplier<Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayer player = supplier.get().getSender();
            if(player != null)
            {
                Level world = player.level();
                Entity targetEntity = world.getEntity(message.entityId);
                if(targetEntity instanceof IStorage)
                {
                    IStorage storage = (IStorage) targetEntity;
                    float reachDistance = (float) player.getAttribute(ForgeMod.ENTITY_REACH.get()).getValue(); // FIXME
                    if(player.distanceTo(targetEntity) < reachDistance)
                    {
                        if(targetEntity instanceof IAttachableChest)
                        {
                            IAttachableChest attachableChest = (IAttachableChest) targetEntity;
                            if(attachableChest.hasChest())
                            {
                                ItemStack stack = player.getInventory().getSelected();
                                if(stack.getItem() == ModItems.WRENCH.get())
                                {
                                    ((IAttachableChest) targetEntity).removeChest();
                                }
                                else
                                {
                                    NetworkHooks.openScreen(player, storage.getStorageContainerProvider(), buffer -> buffer.writeVarInt(message.entityId));
                                }
                            }
                        }
                        else
                        {
                            NetworkHooks.openScreen(player, storage.getStorageContainerProvider(), buffer -> buffer.writeVarInt(message.entityId));
                        }
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
