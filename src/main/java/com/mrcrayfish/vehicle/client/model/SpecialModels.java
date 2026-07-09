/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.ModelResourceLocation
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.common.EventBusSubscriber$Bus
 *  net.neoforged.neoforge.client.event.ModelEvent$RegisterAdditional
 */
package com.mrcrayfish.vehicle.client.model;

import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid="vehicle", value={Dist.CLIENT}, bus=EventBusSubscriber.Bus.MOD)
public enum SpecialModels implements ISpecialModel
{
    QUAD_BIKE_BODY(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"item/quad_bike_base"), "standalone"), true),
    QUAD_BIKE_HANDLES(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"item/quad_bike_handles"), "standalone"), true),
    ATV_BODY("atv_body"),
    ATV_HANDLES("atv_handles"),
    DUNE_BUGGY_BODY("dune_buggy_body"),
    DUNE_BUGGY_HANDLES("dune_buggy_handles"),
    GO_KART_BODY("go_kart_body"),
    GO_KART_STEERING_WHEEL("go_kart_steering_wheel"),
    SHOPPING_CART_BODY("shopping_cart_body"),
    MINI_BIKE_BODY("mini_bike_body"),
    MINI_BIKE_HANDLES("mini_bike_handles"),
    BUMPER_CAR_BODY("bumper_car_body"),
    JET_SKI_BODY("jet_ski_body"),
    SPEED_BOAT_BODY("speed_boat_body"),
    ALUMINUM_BOAT_BODY("aluminum_boat_body"),
    SMART_CAR_BODY("smart_car_body"),
    LAWN_MOWER_BODY("lawn_mower_body"),
    MOPED_BODY("moped_body"),
    MOPED_MUD_GUARD("moped_mud_guard"),
    MOPED_HANDLES("moped_handles"),
    SPORTS_PLANE("sports_plane_body"),
    SPORTS_PLANE_WING("sports_plane_wing"),
    SPORTS_PLANE_WHEEL_COVER("sports_plane_wheel_cover"),
    SPORTS_PLANE_LEG("sports_plane_leg"),
    SPORTS_PLANE_PROPELLER("sports_plane_propeller"),
    HELICOPTER_BODY(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"item/helicopter_base"), "standalone"), true),
    HELICOPTER_BLADES(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"item/helicopter_blades"), "standalone"), true),
    HELICOPTER_JOYSTICK(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"item/helicopter_joystick"), "standalone"), true),
    HELICOPTER_TAIL_ROTOR(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"item/helicopter_tail_rotor"), "standalone"), true),
    GOLF_CART_BODY("golf_cart_body"),
    OFF_ROADER_BODY("off_roader_body"),
    TRACTOR("tractor_body"),
    MINI_BUS_BODY("mini_bus_body"),
    DIRT_BIKE_BODY("dirt_bike_body"),
    DIRT_BIKE_HANDLES("dirt_bike_handles"),
    VEHICLE_TRAILER("trailer_body"),
    STORAGE_TRAILER("trailer_chest_body"),
    SEEDER_TRAILER("trailer_seeder_body"),
    FERTILIZER_TRAILER("trailer_fertilizer_body"),
    FLUID_TRAILER("trailer_fluid_body"),
    VEHICLE_CRATE_SIDE("vehicle_crate_panel_side"),
    VEHICLE_CRATE_TOP("vehicle_crate_panel_top"),
    JACK_PISTON_HEAD("jack_piston_head"),
    SEED_SPIKER("seed_spiker"),
    NOZZLE("nozzle"),
    TOW_BAR("tow_bar"),
    BIG_TOW_BAR("big_tow_bar"),
    FUEL_DOOR_CLOSED("fuel_door_closed"),
    FUEL_DOOR_OPEN("fuel_door_open"),
    SMALL_FUEL_DOOR_CLOSED("small_fuel_door_closed"),
    SMALL_FUEL_DOOR_OPEN("small_fuel_door_open"),
    KEY_HOLE("key_hole"),
    SOFA_HELICOPTER_ARM("sofa_helicopter_arm"),
    SOFA_HELICOPTER_SKID("sofa_helicopter_skid"),
    RED_SOFA(new ModelResourceLocation(ResourceLocation.parse((String)"cfm:red_sofa"), "inventory"), false),
    RAINBOW_SOFA(new ModelResourceLocation(ResourceLocation.parse((String)"cfm:rainbow_sofa"), "inventory"), false);

    private ModelResourceLocation modelLocation;
    private boolean specialModel;
    @OnlyIn(value=Dist.CLIENT)
    private BakedModel cachedModel;

    private SpecialModels(String modelName) {
        this(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)("vehicle/" + modelName)), "standalone"), true);
    }

    private SpecialModels(ModelResourceLocation resource, boolean specialModel) {
        this.modelLocation = resource;
        this.specialModel = specialModel;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public BakedModel getModel() {
        if (this.cachedModel == null) {
            BakedModel model = Minecraft.getInstance().getModelManager().getModel(this.modelLocation);
            if (model == Minecraft.getInstance().getModelManager().getMissingModel()) {
                return model;
            }
            this.cachedModel = model;
        }
        return this.cachedModel;
    }

    @SubscribeEvent
    @OnlyIn(value=Dist.CLIENT)
    public static void register(ModelEvent.RegisterAdditional event) {
        for (SpecialModels model : SpecialModels.values()) {
            if (!model.specialModel) continue;
            event.register(model.modelLocation);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void clearModelCache() {
        for (SpecialModels model : SpecialModels.values()) {
            model.cachedModel = null;
        }
    }
}

