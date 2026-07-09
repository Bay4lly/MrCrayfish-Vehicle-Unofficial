/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.client.event.InputEvent$Key
 */
package com.mrcrayfish.vehicle.client.handler;

import com.mrcrayfish.vehicle.client.init.KeyBinds;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageCycleSeats;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;

public class InputHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.screen != null) {
            return;
        }
        if (event.getAction() != 1) {
            return;
        }
        if (KeyBinds.KEY_CYCLE_SEATS.isDown() && minecraft.player.getVehicle() instanceof VehicleEntity) {
            PacketHandler.sendToServer(new MessageCycleSeats());
        }
    }
}

