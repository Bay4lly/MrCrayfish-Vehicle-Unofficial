/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.common.ModConfigSpec
 *  net.neoforged.neoforge.common.ModConfigSpec$BooleanValue
 *  net.neoforged.neoforge.common.ModConfigSpec$Builder
 *  net.neoforged.neoforge.common.ModConfigSpec$ConfigValue
 *  net.neoforged.neoforge.common.ModConfigSpec$DoubleValue
 *  net.neoforged.neoforge.common.ModConfigSpec$IntValue
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.mrcrayfish.vehicle;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    static final ModConfigSpec clientSpec;
    public static final Client CLIENT;
    static final ModConfigSpec serverSpec;
    public static final Server SERVER;

    static {
        Pair clientSpecPair = new ModConfigSpec.Builder().configure(Client::new);
        clientSpec = (ModConfigSpec)clientSpecPair.getRight();
        CLIENT = (Client)clientSpecPair.getLeft();
        Pair commonSpecPair = new ModConfigSpec.Builder().configure(Server::new);
        serverSpec = (ModConfigSpec)commonSpecPair.getRight();
        SERVER = (Server)commonSpecPair.getLeft();
    }

    public static class Client {
        public final ModConfigSpec.BooleanValue renderOutlines;
        public final ModConfigSpec.BooleanValue renderSteeringDebug;
        public final ModConfigSpec.BooleanValue reloadRayTracerEachTick;
        public final ModConfigSpec.BooleanValue enabledLeftClick;
        public final ModConfigSpec.BooleanValue enabledSpeedometer;
        public final ModConfigSpec.BooleanValue autoPerspective;
        public final ModConfigSpec.BooleanValue workstationAnimation;
        public final ModConfigSpec.BooleanValue useTriggers;
        public final ModConfigSpec.BooleanValue rotateCameraWithVehicle;
        public final ModConfigSpec.BooleanValue reloadVehiclePropertiesEachTick;
        public final ModConfigSpec.IntValue hoseSegments;

        Client(ModConfigSpec.Builder builder) {
            builder.comment("Client configuration settings").push("client");
            builder.comment("Configuration options for debugging vehicles").push("debug");
            this.renderOutlines = builder.comment("If true, renders an outline of all the elements on a vehicle's model. Useful for debugging interactions.").translation("vehicle.config.client.debug.render_outlines").define("renderOutlines", false);
            this.renderSteeringDebug = builder.comment("If true, renders lines to help visualise steering direction and target position.").translation("vehicle.config.client.debug.render_steering_debug").define("renderSteeringDebug", false);
            this.reloadRayTracerEachTick = builder.comment("If true, the raytracer will be reloaded each tick.").translation("vehicle.config.client.debug.raytracer.continuous_reload").define("reloadRaytracerEachTick", false);
            this.reloadVehiclePropertiesEachTick = builder.comment("If true, the vehicle properties will be reloaded each tick.").translation("vehicle.config.client.debug.properties.continuous_reload").define("reloadVehiclePropertiesEachTick", false);
            builder.pop();
            builder.comment("Configuration options for vehicle interaction").push("interaction");
            this.enabledLeftClick = builder.comment("If true, raytraces will be performed on nearby vehicles when left-clicking the mouse, rather than just right-clicking it. This allows one to be damaged/broken when clicking anywhere on it, rather than just on its bounding box.").translation("vehicle.config.client.interaction.left_click").define("enabledLeftClick", true);
            builder.pop();
            builder.comment("Configuration for display related options").push("display");
            this.enabledSpeedometer = builder.comment("If true, displays a speedometer on the HUD when driving a vehicle").translation("vehicle.config.client.display.speedometer").define("enabledSpeedometer", true);
            this.autoPerspective = builder.comment("If true, automatically switches to third person when mounting vehicles").translation("vehicle.config.client.display.auto_perspective").define("autoPerspective", true);
            this.workstationAnimation = builder.comment("If true, an animation is performed while cycling vehicles in the workstation").translation("vehicle.config.client.display.workstation_animation").define("workstationAnimation", true);
            this.rotateCameraWithVehicle = builder.comment("If true, automatically rotates the camera when turning in a vehicle").translation("vehicle.config.client.display.rotate_camera").define("rotateCameraWithVehicle", true);
            this.hoseSegments = builder.comment("The amount of segments to use to render the hose on a gas pump. The lower the value, the better the performance but renders a less realistically looking hose").translation("vehicle.config.client.display.hose_segments").defineInRange("hoseSegments", 10, 1, 100);
            builder.pop();
            builder.comment("Configuration options for controller support (Must have Controllable install)").push("controller");
            builder.pop();
            builder.comment("Configuration options for controller support (Must have Controllable install)").push("controller");
            this.useTriggers = builder.comment("If true, will use the triggers on controller to control the acceleration of the vehicle.").translation("vehicle.config.client.controller.use_triggers").define("useTriggers", false);
            builder.pop();
            builder.pop();
        }
    }

    public static class Server {
        public final ModConfigSpec.BooleanValue fuelEnabled;
        public final ModConfigSpec.BooleanValue vehicleDamage;
        public final ModConfigSpec.DoubleValue trailerDetachThreshold;
        public final ModConfigSpec.IntValue trailerSyncCooldown;
        public final ModConfigSpec.IntValue trailerInventorySyncCooldown;
        public final ModConfigSpec.BooleanValue pickUpVehicles;
        public final ModConfigSpec.DoubleValue maxHoseDistance;
        public final ModConfigSpec.IntValue pumpTransferAmount;
        public final ModConfigSpec.IntValue gasPumpCapacity;
        public final ModConfigSpec.IntValue pumpCapacity;
        public final ModConfigSpec.IntValue extractorCapacity;
        public final ModConfigSpec.IntValue extractorExtractTime;
        public final ModConfigSpec.IntValue mixerInputCapacity;
        public final ModConfigSpec.IntValue mixerOutputCapacity;
        public final ModConfigSpec.IntValue mixerMixTime;
        public final ModConfigSpec.IntValue fuelDrumCapacity;
        public final ModConfigSpec.IntValue industrialFuelDrumCapacity;
        public final ModConfigSpec.DoubleValue fuelConsumptionFactor;
        public final ModConfigSpec.ConfigValue<List<? extends String>> disabledVehicles;
        public final ModConfigSpec.ConfigValue<List<? extends String>> validFuels;
        public final ModConfigSpec.IntValue jerryCanCapacity;
        public final ModConfigSpec.IntValue industrialJerryCanCapacity;
        public final ModConfigSpec.IntValue jerryCanFillRate;
        public final ModConfigSpec.IntValue sprayCanCapacity;

        Server(ModConfigSpec.Builder builder) {
            builder.comment("Server configuration settings").push("common");
            builder.comment("General configuration options").push("general");
            this.fuelEnabled = builder.comment("If true, vehicles will require fuel for them to be driven.").translation("vehicle.config.server.fuel_enabled").define("fuelEnabled", true);
            this.vehicleDamage = builder.comment("If true, vehicles will take damage.").translation("vehicle.config.server.vehicle_damage").define("vehicleDamage", true);
            this.pickUpVehicles = builder.comment("Allows players to pick up vehicles by crouching and right clicking").translation("vehicle.config.server.pick_up_vehicles").define("pickUpVehicles", true);
            this.fuelConsumptionFactor = builder.comment("Change the amount of fuel vehicles consumes by multiplying the consumption rate by this factor").translation("vehicle.config.server.fuel_consumption_modifier").defineInRange("fuelConsumptionModifier", 1.0, 0.0, Double.MAX_VALUE);
            this.disabledVehicles = builder.comment("A list of vehicles that are prevented from being crafted in the workstation").defineList("disabledVehicles", Collections.emptyList(), o -> true);
            this.validFuels = builder.comment("A list of fluids that can be used as fuel for vehicles").defineList("validFuels", Arrays.asList("vehicle:fuelium", "immersiveengineering:biodiesel", "immersivepetroleum:diesel"), o -> true);
            builder.pop();
            builder.comment("Configuration options for trailers").push("trailer");
            this.trailerDetachThreshold = builder.comment("The distance threshold before the trailer detaches from a vehicle").translation("vehicle.config.server.trailer_detach_threshold").defineInRange("trailerDetachThreshold", 6.0, 1.0, 10.0);
            this.trailerSyncCooldown = builder.comment("The amount of ticks to wait before syncing data to clients about the trailer connection. This is important for smooth trailer movement on client side.").translation("vehicle.config.server.trailer_sync_cooldown").defineInRange("trailerSyncCooldown", 100, 1, Integer.MAX_VALUE);
            this.trailerInventorySyncCooldown = builder.comment("The amount of ticks to wait before syncing trailer inventory to tracking clients. If the value is set to 0 or less, the inventory will not sync and will save on network usage.").translation("vehicle.config.server.trailer_inventory_sync_cooldown").defineInRange("trailerInventorySyncCooldown", 20, 1, Integer.MAX_VALUE);
            builder.pop();
            builder.comment("Configuration options for blocks").push("blocks");
            builder.comment("Configuration options for Gas Pumps").push("gas_pump");
            this.maxHoseDistance = builder.comment("The maximum distance before the hose from the gas pump or fluid hose breaks").translation("vehicle.config.server.max_hose_distance").defineInRange("maxHoseDistance", 10.0, 1.0, 100.0);
            this.gasPumpCapacity = builder.comment("The fluid capacity of the gas pump in millibuckets").translation("vehicle.config.server.gas_pump_capacity").defineInRange("gasPumpCapacity", 50000, 1, Integer.MAX_VALUE);
            builder.pop();
            builder.comment("Configuration options for fluid pumps").push("fluid_pump");
            this.pumpTransferAmount = builder.comment("The amount of fluid a pump will transfer each tick").translation("vehicle.config.server.pump_transfer_amount").defineInRange("pumpTransferAmount", 50, 1, Integer.MAX_VALUE);
            this.pumpCapacity = builder.comment("The fluid capacity of the fluid pump in millibuckets").translation("vehicle.config.server.fluid_pump_capacity").defineInRange("pumpCapacity", 500, 1, Integer.MAX_VALUE);
            builder.pop();
            builder.comment("Configuration options for fluid extractors").push("fluid_extractor");
            this.extractorExtractTime = builder.comment("The amount of ticks before fluid is extracted from an item").translation("vehicle.config.server.fluid_extractor_time").defineInRange("extractorExtractTime", 600, 1, Integer.MAX_VALUE);
            this.extractorCapacity = builder.comment("The fluid capacity of the fluid extractor in millibuckets").translation("vehicle.config.server.fluid_extractor_capacity").defineInRange("extractorCapacity", 5000, 1, Integer.MAX_VALUE);
            builder.pop();
            builder.comment("Configuration options for fluid mixers").push("fluid_mixer");
            this.mixerMixTime = builder.comment("The amount of ticks to mix fluids together").translation("vehicle.config.server.fluid_mixer_time").defineInRange("mixerMixTime", 100, 1, Integer.MAX_VALUE);
            this.mixerInputCapacity = builder.comment("The input fluid capacity of the fluid mixer in millibuckets").translation("vehicle.config.server.fluid_mixer_input_capacity").defineInRange("mixerInputCapacity", 5000, 1, Integer.MAX_VALUE);
            this.mixerOutputCapacity = builder.comment("The output fluid capacity of the fluid mixer in millibuckets").translation("vehicle.config.server.fluid_mixer_output_capacity").defineInRange("mixerOutputCapacity", 10000, 1, Integer.MAX_VALUE);
            builder.pop();
            builder.comment("Configuration options for fuel drums").push("fuel_drum");
            this.fuelDrumCapacity = builder.comment("The fluid capacity of the fuel drum in millibuckets").translation("vehicle.config.server.fuel_drum_capacity").defineInRange("fuelDrumCapacity", 40000, 1, Integer.MAX_VALUE);
            this.industrialFuelDrumCapacity = builder.comment("The fluid capacity of the industrial fuel drum in millibuckets").translation("vehicle.config.server.industrial_fuel_drum_capacity").defineInRange("industrialFuelDrumCapacity", 75000, 1, Integer.MAX_VALUE);
            builder.pop();
            builder.pop();
            builder.comment("Configuration options for items").push("items");
            builder.comment("Configuration options for jerry cans").push("jerry_can");
            this.jerryCanCapacity = builder.comment("The fluid capacity of the jerry can in millibuckets").translation("vehicle.config.server.jerry_can_capacity").defineInRange("jerryCanCapacity", 5000, 1, Integer.MAX_VALUE);
            this.industrialJerryCanCapacity = builder.comment("The fluid capacity of the industrial jerry can in millibuckets").translation("vehicle.config.server.industrial_jerry_can_capacity").defineInRange("industrialJerryCanCapacity", 15000, 1, Integer.MAX_VALUE);
            this.jerryCanFillRate = builder.comment("The amount of fluid transferred when pouring or filling a jerry can").translation("vehicle.config.server.jerry_can_fill_rate").defineInRange("fillRate", 500, 1, Integer.MAX_VALUE);
            builder.pop();
            builder.comment("Configuration options for spray cans").push("spray_can");
            this.sprayCanCapacity = builder.comment("The amount of sprays before a spray can becomes empty").translation("vehicle.config.server.spray_can_capacity").defineInRange("sprayCanCapacity", 20, 1, Integer.MAX_VALUE);
            builder.pop();
            builder.pop();
            builder.pop();
        }
    }
}

