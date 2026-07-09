/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.Entity
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.client.event.RenderGuiLayerEvent$Post
 *  net.neoforged.neoforge.client.gui.VanillaGuiLayers
 */
package com.mrcrayfish.vehicle.client.handler;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import java.awt.Color;
import java.text.DecimalFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class OverlayHandler {
    @SubscribeEvent
    public void onRenderTick(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals((Object)VanillaGuiLayers.HOTBAR)) {
            return;
        }
        if (!((Boolean)Config.CLIENT.enabledSpeedometer.get()).booleanValue()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        Entity entity = player.getVehicle();
        if (!(entity instanceof PoweredVehicleEntity)) {
            return;
        }
        PoweredVehicleEntity vehicle = (PoweredVehicleEntity)entity;
        String speed = new DecimalFormat("0.0").format(vehicle.getKilometersPreHour());
        event.getGuiGraphics().drawString(mc.font, String.valueOf(ChatFormatting.BOLD) + "BPS: " + String.valueOf(ChatFormatting.YELLOW) + speed, 10, 10, Color.WHITE.getRGB());
        if (vehicle.requiresFuel()) {
            DecimalFormat format = new DecimalFormat("0.0");
            String fuel = format.format(vehicle.getCurrentFuel()) + "/" + format.format(vehicle.getFuelCapacity());
            event.getGuiGraphics().drawString(mc.font, String.valueOf(ChatFormatting.BOLD) + "Fuel: " + String.valueOf(ChatFormatting.YELLOW) + fuel, 10, 25, Color.WHITE.getRGB());
        }
    }
}

