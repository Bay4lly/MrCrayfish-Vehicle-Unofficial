package com.mrcrayfish.vehicle.common.entity;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageSyncHeldVehicle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class HeldVehicleDataHandler
{
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Reference.MOD_ID);

    public static final Supplier<AttachmentType<HeldVehicle>> HELD_VEHICLE =
        ATTACHMENT_TYPES.register("held_vehicle", () ->
            AttachmentType.builder(HeldVehicle::new).build()
    );

    public static void registerBus(IEventBus modBus)
    {
        ATTACHMENT_TYPES.register(modBus);
    }

    public static void register()
    {
        NeoForge.EVENT_BUS.register(new HeldVehicleDataHandler());
    }

    public static boolean isHoldingVehicle(Player player)
    {
        return !player.getData(HELD_VEHICLE).getVehicleTag().isEmpty();
    }

    public static CompoundTag getHeldVehicle(Player player)
    {
        return player.getData(HELD_VEHICLE).getVehicleTag();
    }

    public static void setHeldVehicle(Player player, CompoundTag vehicleTag)
    {
        player.getData(HELD_VEHICLE).setVehicleTag(vehicleTag);
        if(!player.level().isClientSide)
        {
            VehicleMod.LOGGER.debug("[HeldVehicle] setHeldVehicle server-side for player {} -> tagEmpty={}", player.getUUID(), vehicleTag == null || vehicleTag.isEmpty());
            if(player instanceof ServerPlayer serverPlayer)
            {
                PacketHandler.sendToPlayer(serverPlayer, new MessageSyncHeldVehicle(player.getId(), vehicleTag));
                PacketHandler.sendToTrackingEntity(player, new MessageSyncHeldVehicle(player.getId(), vehicleTag));
            }
        }
        else
        {
            VehicleMod.LOGGER.debug("[HeldVehicle] setHeldVehicle client-side for player {} -> tagEmpty={}", player.getUUID(), vehicleTag == null || vehicleTag.isEmpty());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event)
    {
        if(event.isWasDeath())
            return;

        CompoundTag vehicleTag = getHeldVehicle(event.getOriginal());
        if(!vehicleTag.isEmpty())
        {
            setHeldVehicle(event.getEntity(), vehicleTag);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof Player player)
        {
            CompoundTag vehicleTag = getHeldVehicle(player);
            PacketHandler.sendToPlayer((ServerPlayer) event.getEntity(), new MessageSyncHeldVehicle(player.getId(), vehicleTag));
        }
    }

    @SubscribeEvent
    public void onPlayerJoinLevel(EntityJoinLevelEvent event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof Player player && !event.getLevel().isClientSide)
        {
            CompoundTag vehicleTag = getHeldVehicle(player);
            PacketHandler.sendToPlayer((ServerPlayer) player, new MessageSyncHeldVehicle(player.getId(), vehicleTag));
        }
    }

    public static class HeldVehicle
    {
        private CompoundTag compound = new CompoundTag();

        public void setVehicleTag(CompoundTag tagCompound)
        {
            this.compound = tagCompound;
        }

        public CompoundTag getVehicleTag()
        {
            return compound;
        }
    }
}
