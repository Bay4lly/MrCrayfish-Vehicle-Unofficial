/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$Builder
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.level.Level
 *  net.neoforged.fml.ModList
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.mrcrayfish.vehicle.util;

import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VehicleUtil {
    public static <T extends VehicleEntity> DeferredHolder<EntityType<?>, EntityType<T>> createEntityType(DeferredRegister<EntityType<?>> deferredRegister, String name, BiFunction<EntityType<T>, Level, T> function, float width, float height) {
        return VehicleUtil.createEntityType(deferredRegister, name, function, width, height, true);
    }

    public static <T extends VehicleEntity> DeferredHolder<EntityType<?>, EntityType<T>> createEntityType(DeferredRegister<EntityType<?>> deferredRegister, String name, BiFunction<EntityType<T>, Level, T> function, float width, float height, boolean includeCrate) {
        String modId = deferredRegister.getNamespace();
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath((String)modId, (String)name);
        DeferredHolder type = deferredRegister.register(name, () -> VehicleUtil.buildVehicleType(id, function, width, height));
        VehicleRegistry.registerVehicleType(type);
        if (includeCrate) {
            VehicleCrateBlock.registerVehicle(id);
        }
        return type;
    }

    @Nullable
    public static <T extends VehicleEntity> DeferredHolder<EntityType<?>, EntityType<T>> createModDependentEntityType(DeferredRegister<EntityType<?>> deferredRegister, String modId, String id, BiFunction<EntityType<T>, Level, T> function, float width, float height, boolean registerCrate) {
        if (ModList.get().isLoaded(modId)) {
            return VehicleUtil.createEntityType(deferredRegister, id, function, width, height, registerCrate);
        }
        return null;
    }

    private static <T extends Entity> EntityType<T> buildVehicleType(ResourceLocation id, BiFunction<EntityType<T>, Level, T> function, float width, float height) {
        return EntityType.Builder.of(function::apply, (MobCategory)MobCategory.MISC).sized(width, height).setTrackingRange(16).setUpdateInterval(1).fireImmune().setShouldReceiveVelocityUpdates(true).build(id.toString());
    }
}

