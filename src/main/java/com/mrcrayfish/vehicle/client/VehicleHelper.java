/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mrcrayfish.controllable.Controllable
 *  com.mrcrayfish.controllable.client.input.Controller
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.TerrainParticle
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.resources.sounds.TickableSoundInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.client.event.ViewportEvent$RenderFog
 */
package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.input.Controller;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.ClientHandler;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHorn;
import com.mrcrayfish.vehicle.client.audio.MovingSoundHornRiding;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicle;
import com.mrcrayfish.vehicle.client.audio.MovingSoundVehicleRiding;
import com.mrcrayfish.vehicle.client.init.KeyBinds;
import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.entity.PlaneEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class VehicleHelper {
    private static final WeakHashMap<UUID, Map<SoundType, TickableSoundInstance>> SOUND_TRACKER = new WeakHashMap();

    public static void playVehicleSound(Player player, PoweredVehicleEntity vehicle) {
        Minecraft.getInstance().tell(() -> {
            TickableSoundInstance sound;
            Map<SoundType, TickableSoundInstance> soundMap = SOUND_TRACKER.computeIfAbsent(vehicle.getUUID(), uuid -> new HashMap<>());
            if (vehicle.getEngineSound() != null && player.equals((Object)Minecraft.getInstance().player) && ((sound = (TickableSoundInstance)soundMap.get(SoundType.ENGINE_RIDING)) == null || sound.isStopped() || !Minecraft.getInstance().getSoundManager().isActive((SoundInstance)sound))) {
                sound = new MovingSoundVehicleRiding(player, vehicle);
                soundMap.put(SoundType.ENGINE_RIDING, sound);
                Minecraft.getInstance().getSoundManager().play((SoundInstance)sound);
            }
            if (!(vehicle.getEngineSound() == null || player.equals((Object)Minecraft.getInstance().player) || (sound = (TickableSoundInstance)soundMap.get(SoundType.ENGINE)) != null && !sound.isStopped() && Minecraft.getInstance().getSoundManager().isActive((SoundInstance)sound))) {
                sound = new MovingSoundVehicle(vehicle);
                soundMap.put(SoundType.ENGINE, sound);
                Minecraft.getInstance().getSoundManager().play((SoundInstance)new MovingSoundVehicle(vehicle));
            }
            if (!(vehicle.getHornSound() == null || player.equals((Object)Minecraft.getInstance().player) || (sound = (TickableSoundInstance)soundMap.get(SoundType.HORN)) != null && !sound.isStopped() && Minecraft.getInstance().getSoundManager().isActive((SoundInstance)sound))) {
                sound = new MovingSoundHorn(vehicle);
                soundMap.put(SoundType.HORN, sound);
                Minecraft.getInstance().getSoundManager().play((SoundInstance)sound);
            }
            if (vehicle.getHornSound() != null && player.equals((Object)Minecraft.getInstance().player) && ((sound = (TickableSoundInstance)soundMap.get(SoundType.HORN_RIDING)) == null || sound.isStopped() || !Minecraft.getInstance().getSoundManager().isActive((SoundInstance)sound))) {
                sound = new MovingSoundHornRiding(player, vehicle);
                soundMap.put(SoundType.HORN_RIDING, sound);
                Minecraft.getInstance().getSoundManager().play((SoundInstance)sound);
            }
        });
    }

    public static void playSound(SoundEvent soundEvent, BlockPos pos, float volume, float pitch) {
        SimpleSoundInstance sound = new SimpleSoundInstance(soundEvent, SoundSource.BLOCKS, volume, pitch, SoundInstance.createUnseededRandom(), (double)((float)pos.getX() + 0.5f), (double)pos.getY(), (double)((float)pos.getZ() + 0.5f));
        Minecraft.getInstance().submitAsync(() -> VehicleHelper.lambda$playSound$2((SoundInstance)sound));
    }

    public static void playSound(SoundEvent soundEvent, float volume, float pitch) {
        Minecraft.getInstance().submitAsync(() -> Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)soundEvent, (float)volume, (float)pitch)));
    }

    public void onFogDensity(ViewportEvent.RenderFog event) {
    }

    public static PoweredVehicleEntity.AccelerationDirection getAccelerationDirection(LivingEntity entity) {
        boolean reverse;
        boolean forward;
        Controller controller;
        if (ClientHandler.isControllableLoaded() && (controller = Controllable.getController()) != null) {
            if (((Boolean)Config.CLIENT.useTriggers.get()).booleanValue()) {
                if (controller.getRTriggerValue() != 0.0f && controller.getLTriggerValue() == 0.0f) {
                    return PoweredVehicleEntity.AccelerationDirection.FORWARD;
                }
                if (controller.getLTriggerValue() != 0.0f && controller.getRTriggerValue() == 0.0f) {
                    return PoweredVehicleEntity.AccelerationDirection.REVERSE;
                }
            }
            forward = controller.getTrackedButtonStates().getState(0);
            reverse = controller.getTrackedButtonStates().getState(1);
            if (forward && reverse) {
                return PoweredVehicleEntity.AccelerationDirection.CHARGING;
            }
            if (forward) {
                return PoweredVehicleEntity.AccelerationDirection.FORWARD;
            }
            if (reverse) {
                return PoweredVehicleEntity.AccelerationDirection.REVERSE;
            }
        }
        Options settings = Minecraft.getInstance().options;
        forward = settings.keyUp.isDown();
        reverse = settings.keyDown.isDown();
        if (forward && reverse) {
            return PoweredVehicleEntity.AccelerationDirection.CHARGING;
        }
        if (forward) {
            return PoweredVehicleEntity.AccelerationDirection.FORWARD;
        }
        if (reverse) {
            return PoweredVehicleEntity.AccelerationDirection.REVERSE;
        }
        return PoweredVehicleEntity.AccelerationDirection.fromEntity(entity);
    }

    public static PoweredVehicleEntity.TurnDirection getTurnDirection(LivingEntity entity) {
        Controller controller;
        if (ClientHandler.isControllableLoaded() && (controller = Controllable.getController()) != null) {
            if (controller.getLThumbStickXValue() > 0.0f) {
                return PoweredVehicleEntity.TurnDirection.RIGHT;
            }
            if (controller.getLThumbStickXValue() < 0.0f) {
                return PoweredVehicleEntity.TurnDirection.LEFT;
            }
            if (controller.getTrackedButtonStates().getState(16)) {
                return PoweredVehicleEntity.TurnDirection.RIGHT;
            }
            if (controller.getTrackedButtonStates().getState(15)) {
                return PoweredVehicleEntity.TurnDirection.LEFT;
            }
        }
        if (entity.xxa < 0.0f) {
            return PoweredVehicleEntity.TurnDirection.RIGHT;
        }
        if (entity.xxa > 0.0f) {
            return PoweredVehicleEntity.TurnDirection.LEFT;
        }
        return PoweredVehicleEntity.TurnDirection.FORWARD;
    }

    public static float getTargetTurnAngle(PoweredVehicleEntity vehicle, boolean drifting) {
        PoweredVehicleEntity.TurnDirection direction = vehicle.getTurnDirection();
        if (vehicle.getControllingPassenger() != null) {
            float turnNormal;
            Controller controller;
            if (ClientHandler.isControllableLoaded() && (controller = Controllable.getController()) != null && (turnNormal = controller.getLThumbStickXValue()) != 0.0f) {
                float newTurnAngle = vehicle.turnAngle + ((float)vehicle.getMaxTurnAngle() * -turnNormal - vehicle.turnAngle) * 0.15f;
                if (Math.abs(newTurnAngle) > (float)vehicle.getMaxTurnAngle()) {
                    return vehicle.getMaxTurnAngle() * direction.getDir();
                }
                return newTurnAngle;
            }
            if (direction != PoweredVehicleEntity.TurnDirection.FORWARD) {
                float newTurnAngle;
                float amount = (float)(direction.getDir() * vehicle.getTurnSensitivity()) * Math.max(0.65f, 1.0f - Math.abs(vehicle.getSpeed() / 20.0f));
                if (drifting) {
                    amount *= 0.45f;
                }
                if (Math.abs(newTurnAngle = vehicle.turnAngle + amount) > (float)vehicle.getMaxTurnAngle()) {
                    return vehicle.getMaxTurnAngle() * direction.getDir();
                }
                return newTurnAngle;
            }
        }
        if (drifting) {
            return vehicle.turnAngle * 0.95f;
        }
        return vehicle.turnAngle * 0.85f;
    }

    public static boolean isDrifting() {
        Controller controller;
        if (ClientHandler.isControllableLoaded() && (controller = Controllable.getController()) != null && controller.getTrackedButtonStates().getState(10)) {
            return true;
        }
        return Minecraft.getInstance().options.keyJump.isDown();
    }

    public static boolean isHonking() {
        Controller controller;
        if (ClientHandler.isControllableLoaded() && (controller = Controllable.getController()) != null && controller.isButtonPressed(8)) {
            return true;
        }
        return KeyBinds.KEY_HORN.isDown();
    }

    public static PlaneEntity.FlapDirection getFlapDirection() {
        Controller controller;
        boolean flapUp = Minecraft.getInstance().options.keyJump.isDown();
        boolean flapDown = Minecraft.getInstance().options.keySprint.isDown();
        if (ClientHandler.isControllableLoaded() && (controller = Controllable.getController()) != null) {
            flapUp |= controller.getTrackedButtonStates().getState(10);
            flapDown |= controller.getTrackedButtonStates().getState(9);
        }
        return PlaneEntity.FlapDirection.fromInput(flapUp, flapDown);
    }

    public static HelicopterEntity.AltitudeChange getAltitudeChange() {
        Controller controller;
        boolean flapUp = Minecraft.getInstance().options.keyJump.isDown();
        boolean flapDown = Minecraft.getInstance().options.keySprint.isDown();
        if (ClientHandler.isControllableLoaded() && (controller = Controllable.getController()) != null) {
            flapUp |= controller.getTrackedButtonStates().getState(10);
            flapDown |= controller.getTrackedButtonStates().getState(9);
        }
        return HelicopterEntity.AltitudeChange.fromInput(flapUp, flapDown);
    }

    public static float getTravelDirection(HelicopterEntity vehicle) {
        Controller controller;
        if (ClientHandler.isControllableLoaded() && (controller = Controllable.getController()) != null) {
            float xAxis = controller.getLThumbStickXValue();
            float yAxis = controller.getLThumbStickYValue();
            if (xAxis != 0.0f || yAxis != 0.0f) {
                float angle = (float)Math.toDegrees(Math.atan2(-xAxis, yAxis)) + 180.0f;
                return vehicle.getYRot() + angle;
            }
        }
        PoweredVehicleEntity.AccelerationDirection accelerationDirection = vehicle.getAcceleration();
        PoweredVehicleEntity.TurnDirection turnDirection = vehicle.getTurnDirection();
        if (vehicle.getControllingPassenger() != null) {
            if (accelerationDirection == PoweredVehicleEntity.AccelerationDirection.FORWARD) {
                return vehicle.getYRot() + (float)turnDirection.getDir() * -45.0f;
            }
            if (accelerationDirection == PoweredVehicleEntity.AccelerationDirection.REVERSE) {
                return vehicle.getYRot() + 180.0f + (float)turnDirection.getDir() * 45.0f;
            }
            return vehicle.getYRot() + (float)turnDirection.getDir() * -90.0f;
        }
        return vehicle.getYRot();
    }

    public static float getTravelSpeed(HelicopterEntity helicopter) {
        Controller controller;
        if (ClientHandler.isControllableLoaded() && (controller = Controllable.getController()) != null) {
            float xAxis = controller.getLThumbStickXValue();
            float yAxis = controller.getLThumbStickYValue();
            if (xAxis != 0.0f || yAxis != 0.0f) {
                return (float)Math.min(1.0, Math.sqrt(Math.pow(xAxis, 2.0) + Math.pow(yAxis, 2.0)));
            }
        }
        return helicopter.getAcceleration() != PoweredVehicleEntity.AccelerationDirection.NONE || helicopter.getTurnDirection() != PoweredVehicleEntity.TurnDirection.FORWARD ? 1.0f : 0.0f;
    }

    public static float getPower(PoweredVehicleEntity vehicle) {
        Controller controller;
        if (ClientHandler.isControllableLoaded() && ((Boolean)Config.CLIENT.useTriggers.get()).booleanValue() && (controller = Controllable.getController()) != null) {
            PoweredVehicleEntity.AccelerationDirection accelerationDirection = vehicle.getAcceleration();
            if (accelerationDirection == PoweredVehicleEntity.AccelerationDirection.FORWARD) {
                return controller.getRTriggerValue();
            }
            if (accelerationDirection == PoweredVehicleEntity.AccelerationDirection.REVERSE) {
                return controller.getLTriggerValue();
            }
        }
        return 1.0f;
    }

    public static boolean canApplyVehicleYaw(Entity passenger) {
        if (passenger.equals((Object)Minecraft.getInstance().player)) {
            return (Boolean)Config.CLIENT.rotateCameraWithVehicle.get();
        }
        return false;
    }

    public static void spawnWheelParticle(BlockPos pos, BlockState state, double x, double y, double z, Vec3 motion) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel world = mc.level;
        if (world != null) {
            TerrainParticle particle = new TerrainParticle(world, x, y, z, motion.x, motion.y, motion.z, state);
            particle.setPower((float)motion.length());
            mc.particleEngine.add((Particle)particle);
        }
    }

    private static /* synthetic */ void lambda$playSound$2(SoundInstance sound) {
        Minecraft.getInstance().getSoundManager().play(sound);
    }

    private static enum SoundType {
        ENGINE,
        ENGINE_RIDING,
        HORN,
        HORN_RIDING;

    }
}

