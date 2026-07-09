/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.EntityType
 */
package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityType;

public final class VehicleRenderRegistry {
    private static final Map<EntityType<?>, AbstractVehicleRenderer<?>> RENDERER_MAP = new HashMap();
    private static final Map<EntityType<?>, Function<Supplier<VehicleProperties>, ?>> RENDERER_FUNCTION_MAP = new HashMap();

    public static synchronized void registerVehicleRendererFunction(EntityType<?> type, Function<Supplier<VehicleProperties>, ?> rendererFunction, AbstractVehicleRenderer<?> defaultRenderer) {
        RENDERER_FUNCTION_MAP.put(type, rendererFunction);
        RENDERER_MAP.put(type, defaultRenderer);
    }

    @Nullable
    public static AbstractVehicleRenderer<?> getRendererFunction(EntityType<?> type) {
        Function<Supplier<VehicleProperties>, ?> rendererFunction = RENDERER_FUNCTION_MAP.get(type);
        return rendererFunction != null ? (AbstractVehicleRenderer)rendererFunction.apply(() -> VehicleProperties.get(type)) : null;
    }

    @Nullable
    public static AbstractVehicleRenderer<?> getRenderer(EntityType<?> type) {
        return RENDERER_MAP.get(type);
    }
}

