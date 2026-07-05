package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.common.inventory.IAttachableChest;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.NeoForgeMod;


/**
 * Author: MrCrayfish
 */
public class MessageAttachChest implements IMessage<MessageAttachChest>
{
    public static final CustomPacketPayload.Type<MessageAttachChest> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "attach_chest"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageAttachChest> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageAttachChest().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private int entityId;

    public MessageAttachChest() {}

    public MessageAttachChest(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageAttachChest message, RegistryFriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageAttachChest decode(RegistryFriendlyByteBuf buffer)
    {
        return new MessageAttachChest(buffer.readInt());
    }

    @Override
    public void handle(MessageAttachChest message, IPayloadContext context)
    {
        context.enqueueWork(() ->
        {
            ServerPlayer player = ((ServerPlayer) context.player());
            if(player != null)
            {
                Level world = player.level();
                Entity targetEntity = world.getEntity(message.entityId);
                if(targetEntity instanceof IAttachableChest)
                {
                    float reachDistance = (float) player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ENTITY_INTERACTION_RANGE).getValue(); // FIXME
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
    }
}
