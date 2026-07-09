/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  javax.annotation.Nonnull
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.PackOutput$PathProvider
 *  net.minecraft.data.PackOutput$Target
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mrcrayfish.vehicle.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class VehiclePropertiesProvider
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(VehicleProperties.class, (Object)new VehicleProperties.Serializer()).create();
    private final PackOutput.PathProvider pathProvider;
    private final Map<ResourceLocation, VehicleProperties> vehiclePropertiesMap = new HashMap<ResourceLocation, VehicleProperties>();

    protected VehiclePropertiesProvider(PackOutput generator) {
        this.pathProvider = generator.createPathProvider(PackOutput.Target.RESOURCE_PACK, "vehicles");
    }

    protected final void add(EntityType<? extends VehicleEntity> type, VehicleProperties.Builder builder) {
        this.add(BuiltInRegistries.ENTITY_TYPE.getKey(type), builder);
    }

    protected final void add(ResourceLocation id, VehicleProperties.Builder builder) {
        this.vehiclePropertiesMap.put(id, builder.build(false));
    }

    protected abstract void registerProperties();

    public CompletableFuture<?> run(CachedOutput cache) {
        this.vehiclePropertiesMap.clear();
        this.registerProperties();
        ArrayList futures = new ArrayList();
        this.vehiclePropertiesMap.forEach((id, properties) -> futures.add(DataProvider.saveStable((CachedOutput)cache, (JsonElement)GSON.toJsonTree(properties), (Path)this.pathProvider.json(id))));
        return CompletableFuture.allOf((CompletableFuture[])futures.toArray(CompletableFuture[]::new));
    }

    @Nonnull
    public String getName() {
        return "VehicleProperties";
    }
}

