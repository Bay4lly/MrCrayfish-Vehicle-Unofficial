/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.mrcrayfish.vehicle.common;

import com.mrcrayfish.vehicle.entity.IEngineTier;
import com.mrcrayfish.vehicle.entity.IEngineType;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.item.EngineItem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.tuple.Pair;

public class VehicleRegistry {
    private static final Set<DeferredHolder<EntityType<?>, EntityType<? extends VehicleEntity>>> REGISTERED_VEHICLES = new HashSet();
    private static final Map<ResourceLocation, IEngineType> ID_TO_ENGINE_TYPE = new HashMap<ResourceLocation, IEngineType>();
    private static final Map<Pair<IEngineType, IEngineTier>, EngineItem> PAIR_TO_ENGINE_ITEM = new HashMap<Pair<IEngineType, IEngineTier>, EngineItem>();

    public static synchronized void registerVehicleType(DeferredHolder<EntityType<?>, EntityType<? extends VehicleEntity>> entityType) {
        REGISTERED_VEHICLES.add(entityType);
    }

    public static Set<DeferredHolder<EntityType<?>, EntityType<? extends VehicleEntity>>> getRegisteredVehicleTypes() {
        return REGISTERED_VEHICLES;
    }

    public static synchronized void registerEngine(IEngineType type, IEngineTier tier, EngineItem item) {
        Pair pair = Pair.of(type, tier);
        if (PAIR_TO_ENGINE_ITEM.containsKey(pair)) {
            throw new IllegalArgumentException("The given engine type/tier pair is already registered!");
        }
        PAIR_TO_ENGINE_ITEM.put((Pair<IEngineType, IEngineTier>)pair, item);
        ID_TO_ENGINE_TYPE.put(type.getId(), type);
    }

    @Nullable
    public static IEngineType getEngineTypeFromId(ResourceLocation id) {
        return ID_TO_ENGINE_TYPE.get(id);
    }

    @Nullable
    public static EngineItem getEngineItem(IEngineType type, IEngineTier tier) {
        return PAIR_TO_ENGINE_ITEM.get(Pair.of(type, tier));
    }
}

