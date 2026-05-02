package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.inventory.IAttachableChest;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageAttachChest implements IMessage<MessageAttachChest>
{
    private int entityId;

    public MessageAttachChest() {}

    public MessageAttachChest(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageAttachChest message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageAttachChest decode(FriendlyByteBuf buffer)
    {
        return new MessageAttachChest(buffer.readInt());
    }

    @Override
    public void handle(MessageAttachChest message, Supplier<Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayer player = supplier.get().getSender();
            if(player != null)
            {
                Level world = player.level();
                Entity targetEntity = world.getEntity(message.entityId);
                if(targetEntity instanceof IAttachableChest)
                {
                    float reachDistance = (float) player.getAttribute(ForgeMod.ENTITY_REACH.get()).getValue(); // FIXME
                    if(player.distanceTo(targetEntity) < reachDistance)
                    {
                        IAttachableChest attachableChest = (IAttachableChest) targetEntity;
                        if(!attachableChest.hasChest())
                        {
                            ItemStack stack = player.getInventory().getSelected();
                            if(!stack.isEmpty() && stack.getItem() == Items.CHEST)
                            {
                                attachableChest.attachChest(stack);
                                world.playSound(null, targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), SoundType.WOOD.getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                            }
                        }
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
