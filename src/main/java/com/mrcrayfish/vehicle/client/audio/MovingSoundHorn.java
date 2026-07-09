/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.sounds.SoundSource
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import java.lang.ref.WeakReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public class MovingSoundHorn
extends AbstractTickableSoundInstance {
    private final WeakReference<PoweredVehicleEntity> vehicleRef;

    public MovingSoundHorn(PoweredVehicleEntity vehicle) {
        super(vehicle.getHornSound(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.vehicleRef = new WeakReference<PoweredVehicleEntity>(vehicle);
        this.looping = true;
        this.delay = 0;
        this.volume = 0.001f;
        this.pitch = 0.85f;
    }

    public void tick() {
        PoweredVehicleEntity vehicle = (PoweredVehicleEntity)this.vehicleRef.get();
        if (vehicle == null || Minecraft.getInstance().player == null) {
            this.stop();
            return;
        }
        float f = this.volume = vehicle.getHorn() ? 1.0f : 0.0f;
        if (vehicle.isAlive() && vehicle.getPassengers().size() > 0) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            this.x = (float)(vehicle.getX() + (localPlayer.getX() - vehicle.getX()) * 0.65);
            this.y = (float)(vehicle.getY() + (localPlayer.getY() - vehicle.getY()) * 0.65);
            this.z = (float)(vehicle.getZ() + (localPlayer.getZ() - vehicle.getZ()) * 0.65);
        } else {
            this.stop();
        }
    }
}

