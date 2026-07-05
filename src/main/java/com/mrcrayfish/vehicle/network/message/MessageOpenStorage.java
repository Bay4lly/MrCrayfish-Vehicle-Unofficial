package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.common.inventory.IAttachableChest;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;


/**
 * Author: MrCrayfish
 */
public class MessageOpenStorage implements IMessage<MessageOpenStorage>
{
    public static final CustomPacketPayload.Type<MessageOpenStorage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "open_storage"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageOpenStorage> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageOpenStorage().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private int entityId;

    public MessageOpenStorage() {}

    public MessageOpenStorage(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageOpenStorage message, RegistryFriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageOpenStorage decode(RegistryFriendlyByteBuf buffer)
    {
        return new MessageOpenStorage(buffer.readInt());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(MessageOpenStorage message, IPayloadContext context)
    {
        context.enqueueWork(() ->
        {
            ServerPlayer player = ((ServerPlayer) context.player());
            if(player != null)
            {
                Level world = player.level();
                Entity targetEntity = world.getEntity(message.entityId);
                if(targetEntity instanceof IStorage)
                {
                    IStorage storage = (IStorage) targetEntity;
                    float reachDistance = (float) player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ENTITY_INTERACTION_RANGE).getValue(); // FIXME
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
                                    player.openMenu(storage.getStorageContainerProvider(), buffer -> buffer.writeVarInt(message.entityId));
                                }
                            }
                        }
                        else
                        {
                            player.openMenu(storage.getStorageContainerProvider(), buffer -> buffer.writeVarInt(message.entityId));
                        }
                    }
                }
            }
        });
    }
}
