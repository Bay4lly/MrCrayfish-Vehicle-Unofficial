package com.mrcrayfish.vehicle.client.handler;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Manages changing the point of view of the camera when mounting and dismount vehicles
 *
 * Author: MrCrayfish
 */
public class CameraHandler
{
    private CameraType originalCameraType = null;

    @SubscribeEvent
    public void onEntityMount(EntityMountEvent event)
    {
        if(!Config.CLIENT.autoPerspective.get())
            return;

        if(!event.getLevel().isClientSide())
            return;

        if(!event.getEntityMounting().equals(Minecraft.getInstance().player))
            return;

        if(event.isMounting())
        {
            Entity entity = event.getEntityBeingMounted();
            if(!(entity instanceof VehicleEntity))
                return;

            this.originalCameraType = Minecraft.getInstance().options.getCameraType();
            Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
        }
        else if(this.originalCameraType != null)
        {
            Minecraft.getInstance().options.setCameraType(this.originalCameraType);
            this.originalCameraType = null;
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event)
    {
        if(!Config.CLIENT.autoPerspective.get())
            return;

        Player player = Minecraft.getInstance().player;
        if(player == null)
            return;

        Entity entity = player.getVehicle();
        if(!(entity instanceof VehicleEntity))
            return;

        if(!Minecraft.getInstance().options.keyTogglePerspective.isDown())
            return;

        this.originalCameraType = null;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        Player player = Minecraft.getInstance().player;
        if(event.phase != TickEvent.Phase.END || player == null)
            return;

        if(player.getVehicle() != null)
            return;

        this.originalCameraType = null;
    }

    @SubscribeEvent
    public void onFovUpdate(ComputeFovModifierEvent event)
    {
        Player player = Minecraft.getInstance().player;
        if(player == null)
            return;

        Entity ridingEntity = player.getVehicle();
        if(ridingEntity instanceof VehicleEntity)
        {
            event.setNewFovModifier(1.0F);
        }
    }
}
