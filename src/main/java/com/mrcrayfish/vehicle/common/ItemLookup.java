/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.mrcrayfish.vehicle.common;

import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.WheelType;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.item.WheelItem;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.tuple.Pair;

public class ItemLookup {
    private static final Map<WheelType, Item> WHEEL_LOOKUP = new HashMap<WheelType, Item>();
    private static final Map<Pair<EngineType, EngineTier>, Item> ENGINE_LOOKUP = new HashMap<Pair<EngineType, EngineTier>, Item>();
    private static boolean init;

    public static void init() {
        if (init) {
            return;
        }
        WHEEL_LOOKUP.put(WheelType.STANDARD, (Item)ModItems.STANDARD_WHEEL.get());
        WHEEL_LOOKUP.put(WheelType.SPORTS, (Item)ModItems.SPORTS_WHEEL.get());
        WHEEL_LOOKUP.put(WheelType.RACING, (Item)ModItems.RACING_WHEEL.get());
        WHEEL_LOOKUP.put(WheelType.OFF_ROAD, (Item)ModItems.OFF_ROAD_WHEEL.get());
        WHEEL_LOOKUP.put(WheelType.SNOW, (Item)ModItems.SNOW_WHEEL.get());
        WHEEL_LOOKUP.put(WheelType.ALL_TERRAIN, (Item)ModItems.ALL_TERRAIN_WHEEL.get());
        WHEEL_LOOKUP.put(WheelType.PLASTIC, (Item)ModItems.PLASTIC_WHEEL.get());
        ENGINE_LOOKUP.put((Pair<EngineType, EngineTier>)Pair.of(EngineType.SMALL_MOTOR, EngineTier.IRON), (Item)ModItems.IRON_SMALL_ENGINE.get());
        ENGINE_LOOKUP.put((Pair<EngineType, EngineTier>)Pair.of(EngineType.SMALL_MOTOR, EngineTier.GOLD), (Item)ModItems.GOLD_SMALL_ENGINE.get());
        ENGINE_LOOKUP.put((Pair<EngineType, EngineTier>)Pair.of(EngineType.SMALL_MOTOR, EngineTier.DIAMOND), (Item)ModItems.DIAMOND_SMALL_ENGINE.get());
        ENGINE_LOOKUP.put((Pair<EngineType, EngineTier>)Pair.of(EngineType.LARGE_MOTOR, EngineTier.IRON), (Item)ModItems.IRON_LARGE_ENGINE.get());
        ENGINE_LOOKUP.put((Pair<EngineType, EngineTier>)Pair.of(EngineType.LARGE_MOTOR, EngineTier.GOLD), (Item)ModItems.GOLD_LARGE_ENGINE.get());
        ENGINE_LOOKUP.put((Pair<EngineType, EngineTier>)Pair.of(EngineType.LARGE_MOTOR, EngineTier.DIAMOND), (Item)ModItems.DIAMOND_LARGE_ENGINE.get());
        ENGINE_LOOKUP.put((Pair<EngineType, EngineTier>)Pair.of(EngineType.ELECTRIC_MOTOR, EngineTier.IRON), (Item)ModItems.IRON_ELECTRIC_ENGINE.get());
        ENGINE_LOOKUP.put((Pair<EngineType, EngineTier>)Pair.of(EngineType.ELECTRIC_MOTOR, EngineTier.GOLD), (Item)ModItems.GOLD_ELECTRIC_ENGINE.get());
        ENGINE_LOOKUP.put((Pair<EngineType, EngineTier>)Pair.of(EngineType.ELECTRIC_MOTOR, EngineTier.DIAMOND), (Item)ModItems.DIAMOND_ELECTRIC_ENGINE.get());
        init = true;
    }

    public static ItemStack getWheel(WheelType type, int color) {
        ItemStack wheel = new ItemStack((ItemLike)WHEEL_LOOKUP.getOrDefault(type, Items.AIR));
        if (wheel.getItem() instanceof WheelItem) {
            WheelItem wheelItem = (WheelItem)wheel.getItem();
            if (color != -1) {
                wheelItem.setColor(wheel, color);
            }
            return wheel;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getEngine(EngineType type, EngineTier tier) {
        return new ItemStack((ItemLike)ENGINE_LOOKUP.getOrDefault(Pair.of(type, tier), Items.AIR));
    }

    public static ItemStack getEngine(PoweredVehicleEntity entity) {
        if (entity.hasEngine()) {
            return new ItemStack((ItemLike)ENGINE_LOOKUP.getOrDefault(Pair.of((Object)entity.getProperties().getEngineType(), entity.getEngineTier()), Items.AIR));
        }
        return ItemStack.EMPTY;
    }
}

