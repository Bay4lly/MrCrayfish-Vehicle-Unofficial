/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.mrcrayfish.vehicle.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create((Registry)BuiltInRegistries.SOUND_EVENT, (String)"vehicle");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_BOOST_PAD_BOOST = ModSounds.register("block.boost_pad.boost");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_GAS_PUMP_NOZZLE_PICK_UP = ModSounds.register("block.gas_pump.nozzle.pick_up");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_GAS_PUMP_NOZZLE_PUT_DOWN = ModSounds.register("block.gas_pump.nozzle.put_down");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_JACK_HEAD_DOWN = ModSounds.register("block.jack.head_down");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_JACK_HEAD_UP = ModSounds.register("block.jack.head_up");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_JACK_AIR_WRENCH_GUN = ModSounds.register("block.jack.air_wrench_gun");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_VEHICLE_CRATE_PANEL_LAND = ModSounds.register("block.vehicle_crate.panel_land");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_ATV_ENGINE = ModSounds.register("entity.atv.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_BUMPER_CAR_ENGINE = ModSounds.register("entity.bumper_car.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_BUMPER_CAR_BONK = ModSounds.register("entity.bumper_car.bonk");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_DIRT_BIKE_ENGINE = ModSounds.register("entity.dirt_bike.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_GO_KART_ENGINE = ModSounds.register("entity.go_kart.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_MINI_BUS_ENGINE = ModSounds.register("entity.mini_bus.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_MOPED_ENGINE = ModSounds.register("entity.moped.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_QUAD_BIKE_ENGINE = ModSounds.register("entity.quad_bike.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_SPEED_BOAT_ENGINE = ModSounds.register("entity.speed_boat.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_SPORTS_PLANE_ENGINE = ModSounds.register("entity.sports_plane.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_TRACTOR_ENGINE = ModSounds.register("entity.tractor.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_DESTROYED = ModSounds.register("entity.vehicle.destroyed");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_HELICOPTER_ROTOR = ModSounds.register("entity.vehicle.helicopter_rotor");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_HORN = ModSounds.register("entity.vehicle.horn");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_IMPACT = ModSounds.register("entity.vehicle.impact");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_PICK_UP = ModSounds.register("entity.vehicle.pick_up");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_THUD = ModSounds.register("entity.vehicle.thud");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_FUEL_PORT_LARGE_OPEN = ModSounds.register("entity.vehicle.fuel_port.large.open");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_FUEL_PORT_LARGE_CLOSE = ModSounds.register("entity.vehicle.fuel_port.large.close");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_FUEL_PORT_SMALL_OPEN = ModSounds.register("entity.vehicle.fuel_port.small.open");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_FUEL_PORT_SMALL_CLOSE = ModSounds.register("entity.vehicle.fuel_port.small.close");
    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_JERRY_CAN_LIQUID_GLUG = ModSounds.register("item.jerry_can.liquid_glug");
    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_SPRAY_CAN_SHAKE = ModSounds.register("item.spray_can.shake");
    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_SPRAY_CAN_SPRAY = ModSounds.register("item.spray_can.spray");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String id) {
        return REGISTER.register(id, () -> SoundEvent.createVariableRangeEvent((ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)id)));
    }
}

