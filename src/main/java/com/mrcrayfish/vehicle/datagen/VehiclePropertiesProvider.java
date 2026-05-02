package com.mrcrayfish.vehicle.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public abstract class VehiclePropertiesProvider implements DataProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(VehicleProperties.class, new VehicleProperties.Serializer()).create();

    private final PackOutput.PathProvider pathProvider;
    private final Map<ResourceLocation, VehicleProperties> vehiclePropertiesMap = new HashMap<>();

    protected VehiclePropertiesProvider(PackOutput generator)
    {
        this.pathProvider = generator.createPathProvider(Target.RESOURCE_PACK, "vehicles");
    }

    protected final void add(EntityType<? extends VehicleEntity> type, VehicleProperties.Builder builder)
    {
        this.add(ForgeRegistries.ENTITY_TYPES.getKey(type), builder); // FIXME
    }

    protected final void add(ResourceLocation id, VehicleProperties.Builder builder)
    {
        this.vehiclePropertiesMap.put(id, builder.build(false));
    }

    protected abstract void registerProperties();

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        this.vehiclePropertiesMap.clear();
        this.registerProperties();

        List<CompletableFuture<?>> futures = new ArrayList<>();

        this.vehiclePropertiesMap.forEach((id, properties) -> futures.add(DataProvider.saveStable(cache, GSON.toJsonTree(properties), pathProvider.json(id))));

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Nonnull
    @Override
    public String getName()
    {
        return "VehicleProperties";
    }
}
