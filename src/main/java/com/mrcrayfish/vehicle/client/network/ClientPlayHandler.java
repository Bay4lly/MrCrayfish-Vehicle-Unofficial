package com.mrcrayfish.vehicle.client.network;

import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.client.handler.HeldVehicleHandler;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.network.message.MessageEntityFluid;
import com.mrcrayfish.vehicle.network.message.MessageSyncHeldVehicle;
import com.mrcrayfish.vehicle.network.message.MessageSyncInventory;
import com.mrcrayfish.vehicle.network.message.MessageSyncPlayerSeat;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * Author: MrCrayfish
 */
public class ClientPlayHandler
{

    public static void handleSyncInventory(MessageSyncInventory message)
    {
        Level world = Minecraft.getInstance().level;
        if(world == null)
            return;

        Entity entity = world.getEntity(message.getEntityId());
        if(!(entity instanceof IStorage))
            return;

        ((IStorage) entity).getInventory().fromTag(message.getCompound().getList("Inventory", Tag.TAG_COMPOUND));
    }

    public static void handleEntityFluid(MessageEntityFluid message)
    {
        Level world = Minecraft.getInstance().level;
        if(world == null)
            return;

        Entity entity = world.getEntity(message.getEntityId());
        if(entity == null)
            return;

        LazyOptional<IFluidHandler> optional = entity.getCapability(ForgeCapabilities.FLUID_HANDLER);
        optional.ifPresent(handler ->
        {
            if(handler instanceof FluidTank)
            {
                FluidTank tank = (FluidTank) handler;
                tank.setFluid(message.getStack());
            }
        });
    }

    public static void handleSyncPlayerSeat(MessageSyncPlayerSeat message)
    {
        Player player = Minecraft.getInstance().player;
        if(player != null)
        {
            Entity entity = player.getCommandSenderWorld().getEntity(message.getEntityId());
            if(entity instanceof VehicleEntity)
            {
                VehicleEntity vehicle = (VehicleEntity) entity;
                vehicle.getSeatTracker().setSeatIndex(message.getSeatIndex(), message.getUuid());
            }
        }
    }

    public static void handleSyncHeldVehicle(MessageSyncHeldVehicle message)
    {
        Level world = Minecraft.getInstance().level;
        if(world != null)
        {
            Entity entity = world.getEntity(message.getEntityId());
            Player player = null;
            if(entity instanceof Player)
            {
                player = (Player) entity;
            }
            else
            {
                Player local = Minecraft.getInstance().player;
                if(local != null && local.getId() == message.getEntityId())
                {
                    player = local;
                }
            }

            if(player != null)
            {
                VehicleMod.LOGGER.debug("[HeldVehicle] Client received sync for player {} -> tagEmpty={}", player.getUUID(), message.getVehicleTag() == null || message.getVehicleTag().isEmpty());
                HeldVehicleDataHandler.setHeldVehicle(player, message.getVehicleTag());
                boolean holding = HeldVehicleDataHandler.isHoldingVehicle(player);
                if(holding)
                {
                    if(!HeldVehicleHandler.idToCounter.containsKey(player.getUUID()))
                    {
                        HeldVehicleHandler.idToCounter.put(player.getUUID(), new HeldVehicleHandler.AnimationCounter(40));
                    }
                }
                else
                {
                    HeldVehicleHandler.idToCounter.remove(player.getUUID());
                }
            }
        }
    }
}
