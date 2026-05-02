package com.mrcrayfish.vehicle.client.handler;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Author: MrCrayfish
 */
// FIXME maybe replace with overlay?
public class OverlayHandler
{
    @SubscribeEvent
    public void onRenderTick(RenderGuiOverlayEvent.Post event) // FIXME RenderGuiOverlayEvent vs RenderGuiEvent
    {
        if(!Config.CLIENT.enabledSpeedometer.get())
            return;

        // FIXME
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null)
            return;

        Entity entity = player.getVehicle();
        if(!(entity instanceof PoweredVehicleEntity))
            return;

        PoweredVehicleEntity vehicle = (PoweredVehicleEntity) entity;
        String speed = new DecimalFormat("0.0").format(vehicle.getKilometersPreHour());
        event.getGuiGraphics().drawString(mc.font, ChatFormatting.BOLD + "BPS: " + ChatFormatting.YELLOW + speed, 10, 10, Color.WHITE.getRGB());

        if(vehicle.requiresFuel())
        {
            DecimalFormat format = new DecimalFormat("0.0");
            String fuel = format.format(vehicle.getCurrentFuel()) + "/" + format.format(vehicle.getFuelCapacity());
            event.getGuiGraphics().drawString(mc.font, ChatFormatting.BOLD + "Fuel: " + ChatFormatting.YELLOW + fuel, 10, 25, Color.WHITE.getRGB());
        }
    }
}
