/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.common.EventBusSubscriber$Bus
 *  net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
 *  net.neoforged.neoforge.client.settings.IKeyConflictContext
 *  net.neoforged.neoforge.client.settings.KeyConflictContext
 */
package com.mrcrayfish.vehicle.client.init;

import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

@EventBusSubscriber(bus=EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public class KeyBinds {
    public static final IKeyConflictContext RIDING_VEHICLE = new IKeyConflictContext(){

        public boolean isActive() {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.getVehicle() instanceof VehicleEntity) {
                return KeyConflictContext.IN_GAME.isActive();
            }
            return false;
        }

        public boolean conflicts(IKeyConflictContext other) {
            return other == this;
        }
    };
    public static final KeyMapping KEY_HORN = new KeyMapping("key.vehicle.horn", 72, "key.categories.vehicle");
    public static final KeyMapping KEY_CYCLE_SEATS = new KeyMapping("key.vehicle.cycle_seats", 67, "key.categories.vehicle");

    @SubscribeEvent
    public static void registerKeyBinds(RegisterKeyMappingsEvent event) {
        event.register(KEY_HORN);
        event.register(KEY_CYCLE_SEATS);
    }

    static {
        KEY_HORN.setKeyConflictContext(RIDING_VEHICLE);
        KEY_CYCLE_SEATS.setKeyConflictContext(RIDING_VEHICLE);
    }
}

