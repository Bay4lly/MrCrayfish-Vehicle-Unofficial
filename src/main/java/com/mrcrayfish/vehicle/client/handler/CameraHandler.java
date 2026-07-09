/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.Entity
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.client.event.ClientTickEvent$Post
 *  net.neoforged.neoforge.client.event.ComputeFovModifierEvent
 *  net.neoforged.neoforge.client.event.InputEvent$Key
 *  net.neoforged.neoforge.event.entity.EntityMountEvent
 */
package com.mrcrayfish.vehicle.client.handler;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;

public class CameraHandler {
    private CameraType originalCameraType = null;

    @SubscribeEvent
    public void onEntityMount(EntityMountEvent event) {
        if (!((Boolean)Config.CLIENT.autoPerspective.get()).booleanValue()) {
            return;
        }
        if (!event.getLevel().isClientSide()) {
            return;
        }
        if (!event.getEntityMounting().equals((Object)Minecraft.getInstance().player)) {
            return;
        }
        if (event.isMounting()) {
            Entity entity = event.getEntityBeingMounted();
            if (!(entity instanceof VehicleEntity)) {
                return;
            }
            this.originalCameraType = Minecraft.getInstance().options.getCameraType();
            Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
        } else if (this.originalCameraType != null) {
            Minecraft.getInstance().options.setCameraType(this.originalCameraType);
            this.originalCameraType = null;
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        if (!((Boolean)Config.CLIENT.autoPerspective.get()).booleanValue()) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        Entity entity = player.getVehicle();
        if (!(entity instanceof VehicleEntity)) {
            return;
        }
        if (!Minecraft.getInstance().options.keyTogglePerspective.isDown()) {
            return;
        }
        this.originalCameraType = null;
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (player.getVehicle() != null) {
            return;
        }
        this.originalCameraType = null;
    }

    @SubscribeEvent
    public void onFovUpdate(ComputeFovModifierEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        Entity ridingEntity = player.getVehicle();
        if (ridingEntity instanceof VehicleEntity) {
            event.setNewFovModifier(1.0f);
        }
    }
}

