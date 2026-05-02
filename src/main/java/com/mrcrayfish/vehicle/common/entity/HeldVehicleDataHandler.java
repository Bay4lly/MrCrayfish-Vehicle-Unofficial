package com.mrcrayfish.vehicle.common.entity;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageSyncHeldVehicle;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class HeldVehicleDataHandler
{
    public static void register()
    {
        MinecraftForge.EVENT_BUS.register(new HeldVehicleDataHandler());
    }

    public static boolean isHoldingVehicle(Player player)
    {
        HeldVehicle handler = getInteractionHandler(player);
        if(handler != null)
        {
            return !handler.getVehicleTag().isEmpty();
        }
        return false;
    }

    public static CompoundTag getHeldVehicle(Player player)
    {
        HeldVehicle handler = getInteractionHandler(player);
        if(handler != null)
        {
            return handler.getVehicleTag();
        }
        return new CompoundTag();
    }

    public static void setHeldVehicle(Player player, CompoundTag vehicleTag)
    {
        HeldVehicle handler = getInteractionHandler(player);
        if(handler != null)
        {
            handler.setVehicleTag(vehicleTag);
        }
        if(!player.level().isClientSide)
        {
            VehicleMod.LOGGER.debug("[HeldVehicle] setHeldVehicle server-side for player {} -> tagEmpty={}", player.getUUID(), vehicleTag == null || vehicleTag.isEmpty());
            if(player instanceof ServerPlayer)
            {
                // Send directly to the player
                PacketHandler.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new MessageSyncHeldVehicle(player.getId(), vehicleTag));
                // Also send to all tracking clients for extra robustness (covers some integration edge-cases)
                PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new MessageSyncHeldVehicle(player.getId(), vehicleTag));
            }
        }
        else
        {
            VehicleMod.LOGGER.debug("[HeldVehicle] setHeldVehicle client-side for player {} -> tagEmpty={}", player.getUUID(), vehicleTag == null || vehicleTag.isEmpty());
        }
    }

    @Nullable
    public static HeldVehicle getInteractionHandler(Player player)
    {
        return player.getCapability(Cap.TYPE, Direction.DOWN).orElse(null);
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof Player)
        {
            event.addCapability(new ResourceLocation(Reference.MOD_ID, "held_vehicle"), new Cap(new HeldVehicle()));
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
        if(event.getTarget() instanceof Player)
        {
            Player player = (Player) event.getTarget();
            CompoundTag vehicleTag = getHeldVehicle(player);
            PacketHandler.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new MessageSyncHeldVehicle(player.getId(), vehicleTag));
        }
    }

    @SubscribeEvent
    public void onPlayerJoinLevel(EntityJoinLevelEvent event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof Player && !event.getLevel().isClientSide)
        {
            Player player = (Player) entity;
            CompoundTag vehicleTag = getHeldVehicle(player);
            PacketHandler.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new MessageSyncHeldVehicle(player.getId(), vehicleTag));
        }
    }

    @AutoRegisterCapability
    public static class HeldVehicle implements INBTSerializable<CompoundTag>
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

        @Override
        public CompoundTag serializeNBT() {
            return compound;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            compound = nbt;
        }
    }

    public static class Cap implements ICapabilitySerializable<CompoundTag>
    {
        public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "held_vehicle");
        public static final Capability<HeldVehicle> TYPE = CapabilityManager.get(new CapabilityToken<>() {});

        private final HeldVehicle heldVehicle;
        private final LazyOptional<HeldVehicle> cachedOptionalValue;

        public Cap(HeldVehicle heldVehicle) {
            this.heldVehicle = heldVehicle;
            cachedOptionalValue = LazyOptional.of(() -> this.heldVehicle);
        }

        @Override
        public CompoundTag serializeNBT() {
            return heldVehicle.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            heldVehicle.deserializeNBT(nbt);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return TYPE.orEmpty(cap, cachedOptionalValue);
        }
    }
}
