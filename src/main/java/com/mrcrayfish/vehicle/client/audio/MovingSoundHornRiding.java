/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.player.Player
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import java.lang.ref.WeakReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public class MovingSoundHornRiding
extends AbstractTickableSoundInstance {
    private final WeakReference<Player> playerRef;
    private final WeakReference<PoweredVehicleEntity> vehicleRef;

    public MovingSoundHornRiding(Player player, PoweredVehicleEntity vehicle) {
        super(vehicle.getHornSound(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.playerRef = new WeakReference<Player>(player);
        this.vehicleRef = new WeakReference<PoweredVehicleEntity>(vehicle);
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.001f;
        this.pitch = 0.85f;
    }

    public void tick() {
        PoweredVehicleEntity vehicle = (PoweredVehicleEntity)this.vehicleRef.get();
        Player player = (Player)this.playerRef.get();
        if (vehicle == null || player == null) {
            this.stop();
            return;
        }
        if (!vehicle.isAlive() || player.getVehicle() == null || player.getVehicle() != vehicle || !player.equals((Object)Minecraft.getInstance().player) || vehicle.getPassengers().size() == 0) {
            this.stop();
            return;
        }
        this.volume = vehicle.getHorn() ? 1.0f : 0.0f;
    }
}

