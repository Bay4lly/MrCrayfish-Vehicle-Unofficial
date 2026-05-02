package com.mrcrayfish.vehicle.client.audio;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.ref.WeakReference;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class MovingSoundVehicleRiding extends AbstractTickableSoundInstance
{
    private final WeakReference<Player> playerRef;
    private final WeakReference<PoweredVehicleEntity> vehicleRef;

    public MovingSoundVehicleRiding(Player player, PoweredVehicleEntity vehicle)
    {
        super(vehicle.getEngineSound(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.playerRef = new WeakReference<>(player);
        this.vehicleRef = new WeakReference<>(vehicle);
        this.attenuation = Attenuation.NONE;
        this.relative = true;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.001F;
    }

    @Override
    public void tick()
    {
        PoweredVehicleEntity vehicle = this.vehicleRef.get();
        Player player = this.playerRef.get();
        if(vehicle == null || player == null)
        {
            this.stop();
            return;
        }
        if(!vehicle.isAlive() || !vehicle.equals(player.getVehicle()) || !player.equals(Minecraft.getInstance().player) || vehicle.getPassengers().size() == 0)
        {
            this.stop();
            return;
        }
        this.volume = vehicle.getControllingPassenger() != null && vehicle.isEnginePowered() ? 1.0F : 0.0F;
        this.pitch = vehicle.getMinEnginePitch() + (vehicle.getMaxEnginePitch() - vehicle.getMinEnginePitch()) * Math.abs(vehicle.getActualSpeed());
    }
}
