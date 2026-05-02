package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.CommonEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessagePickupVehicle implements IMessage<MessagePickupVehicle>
{
    private int entityId;

    public MessagePickupVehicle()
    {
    }

    public MessagePickupVehicle(Entity targetEntity)
    {
        this.entityId = targetEntity.getId();
    }

    public MessagePickupVehicle(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessagePickupVehicle message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessagePickupVehicle decode(FriendlyByteBuf buffer)
    {
        return new MessagePickupVehicle(buffer.readInt());
    }

    @Override
    public void handle(MessagePickupVehicle message, Supplier<Context> supplier)
    {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if(player != null && player.isCrouching())
            {
                Entity targetEntity = player.level().getEntity(message.entityId);
                if(targetEntity != null)
                {
                    CommonEvents.pickUpVehicle(player.level(), player, InteractionHand.MAIN_HAND, targetEntity);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}