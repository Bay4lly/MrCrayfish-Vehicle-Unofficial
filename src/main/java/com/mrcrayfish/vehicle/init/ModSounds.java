package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Author: MrCrayfish
 */
public class ModSounds
{
    public static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Reference.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_BOOST_PAD_BOOST = register("block.boost_pad.boost");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_GAS_PUMP_NOZZLE_PICK_UP = register("block.gas_pump.nozzle.pick_up");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_GAS_PUMP_NOZZLE_PUT_DOWN = register("block.gas_pump.nozzle.put_down");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_JACK_HEAD_DOWN = register("block.jack.head_down");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_JACK_HEAD_UP = register("block.jack.head_up");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_JACK_AIR_WRENCH_GUN = register("block.jack.air_wrench_gun");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_VEHICLE_CRATE_PANEL_LAND = register("block.vehicle_crate.panel_land");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_ATV_ENGINE = register("entity.atv.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_BUMPER_CAR_ENGINE = register("entity.bumper_car.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_BUMPER_CAR_BONK = register("entity.bumper_car.bonk");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_DIRT_BIKE_ENGINE = register("entity.dirt_bike.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_GO_KART_ENGINE = register("entity.go_kart.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_MINI_BUS_ENGINE = register("entity.mini_bus.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_MOPED_ENGINE = register("entity.moped.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_QUAD_BIKE_ENGINE = register("entity.quad_bike.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_SPEED_BOAT_ENGINE = register("entity.speed_boat.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_SPORTS_PLANE_ENGINE = register("entity.sports_plane.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_TRACTOR_ENGINE = register("entity.tractor.engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_DESTROYED = register("entity.vehicle.destroyed");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_HELICOPTER_ROTOR = register("entity.vehicle.helicopter_rotor");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_HORN = register("entity.vehicle.horn");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_IMPACT = register("entity.vehicle.impact");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_PICK_UP = register("entity.vehicle.pick_up");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_THUD = register("entity.vehicle.thud");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_FUEL_PORT_LARGE_OPEN = register("entity.vehicle.fuel_port.large.open");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_FUEL_PORT_LARGE_CLOSE = register("entity.vehicle.fuel_port.large.close");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_FUEL_PORT_SMALL_OPEN = register("entity.vehicle.fuel_port.small.open");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VEHICLE_FUEL_PORT_SMALL_CLOSE = register("entity.vehicle.fuel_port.small.close");
    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_JERRY_CAN_LIQUID_GLUG = register("item.jerry_can.liquid_glug");
    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_SPRAY_CAN_SHAKE = register("item.spray_can.shake");
    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_SPRAY_CAN_SPRAY = register("item.spray_can.spray");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String id)
    {
        return ModSounds.REGISTER.register(id, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, id)));
    }
}