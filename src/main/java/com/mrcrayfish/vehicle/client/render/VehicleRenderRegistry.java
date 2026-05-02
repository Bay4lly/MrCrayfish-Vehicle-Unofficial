package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.VehicleProperties;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
// FIXME remove
public final class VehicleRenderRegistry
{
    private static final Map<EntityType<?>, AbstractVehicleRenderer<?>> RENDERER_MAP = new HashMap<>();
    private static final Map<EntityType<?>, Function<Supplier<VehicleProperties>, ?>> RENDERER_FUNCTION_MAP = new HashMap<>();

    public static synchronized void registerVehicleRendererFunction(EntityType<?> type, Function<Supplier<VehicleProperties>, ?> rendererFunction, AbstractVehicleRenderer<?> defaultRenderer)
    {
        RENDERER_FUNCTION_MAP.put(type, rendererFunction);
        RENDERER_MAP.put(type, defaultRenderer);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static AbstractVehicleRenderer<?> getRendererFunction(EntityType<?> type)
    {
        Function<Supplier<VehicleProperties>, AbstractVehicleRenderer<?>> rendererFunction = (Function<Supplier<VehicleProperties>, AbstractVehicleRenderer<?>>) RENDERER_FUNCTION_MAP.get(type);
        return rendererFunction != null ? rendererFunction.apply(() -> VehicleProperties.get(type)) : null;
    }

    @Nullable
    public static AbstractVehicleRenderer<?> getRenderer(EntityType<?> type)
    {
        return RENDERER_MAP.get(type);
    }
}
