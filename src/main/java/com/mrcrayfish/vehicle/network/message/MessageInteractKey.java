package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.CommonUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class MessageInteractKey implements IMessage<MessageInteractKey>
{
    public static final CustomPacketPayload.Type<MessageInteractKey> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "interact_key"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageInteractKey> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageInteractKey().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private int entityId;

    public MessageInteractKey()
    {
    }

    public MessageInteractKey(Entity targetEntity)
    {
        this.entityId = targetEntity.getId();
    }

    private MessageInteractKey(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageInteractKey message, RegistryFriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageInteractKey decode(RegistryFriendlyByteBuf buffer)
    {
        return new MessageInteractKey(buffer.readInt());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(MessageInteractKey message, IPayloadContext context)
    {
        context.enqueueWork(() ->
        {
            ServerPlayer player = ((ServerPlayer) context.player());
            if(player != null)
            {
                Entity targetEntity = player.level().getEntity(message.entityId);
                if(targetEntity instanceof PoweredVehicleEntity)
                {
                    PoweredVehicleEntity poweredVehicle = (PoweredVehicleEntity) targetEntity;
                    if(poweredVehicle.isKeyNeeded())
                    {
                        ItemStack stack = player.getMainHandItem();
                        if(!stack.isEmpty() && stack.getItem() == ModItems.WRENCH.get())
                        {
                            if(poweredVehicle.isOwner(player))
                            {
                                poweredVehicle.ejectKey();
                                poweredVehicle.setKeyNeeded(false);
                                CommonUtils.sendInfoMessage(player, "vehicle.status.key_removed");
                            }
                            else
                            {
                                CommonUtils.sendInfoMessage(player, "vehicle.status.invalid_owner");
                            }
                            return;
                        }
                        if(poweredVehicle.getKeyStack().isEmpty())
                        {
                            if(!stack.isEmpty() && stack.getItem() == ModItems.KEY.get())
                            {
                                UUID keyUuid = CommonUtils.getOrCreateStackTag(stack).getUUID("VehicleId");
                                if(poweredVehicle.getUUID().equals(keyUuid))
                                {
                                    poweredVehicle.setKeyStack(stack.copy());
                                    player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                                }
                                else
                                {
                                    CommonUtils.sendInfoMessage(player, "vehicle.status.key_invalid");
                                }
                            }
                        }
                        else
                        {
                            poweredVehicle.ejectKey();
                        }
                    }
                }
            }
        });
    }
}
