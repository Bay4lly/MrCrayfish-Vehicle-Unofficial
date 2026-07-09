/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.ModList
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.common.EventBusSubscriber$Bus
 *  net.neoforged.neoforge.client.event.EntityRenderersEvent$RegisterRenderers
 */
package com.mrcrayfish.vehicle.client.init;

import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.EntityVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.client.render.vehicle.ATVRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.AluminumBoatRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.BathModel;
import com.mrcrayfish.vehicle.client.render.vehicle.BumperCarModel;
import com.mrcrayfish.vehicle.client.render.vehicle.CompactHelicopterRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.DirtBikeRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.DuneBuggyRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.FertilizerTrailerRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.FluidTrailerRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.GoKartRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.GolfCartRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.JetSkiRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.LawnMowerRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.MiniBikeRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.MiniBusRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.MopedRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.OffRoaderRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.QuadBikeRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.SeederTrailerRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.ShoppingCartRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.SmartCarRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.SofaCarRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.SofaHelicopterRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.SpeedBoatRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.SportsPlaneRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.StorageTrailerRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.TractorRenderer;
import com.mrcrayfish.vehicle.client.render.vehicle.VehicleTrailerRenderer;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModEntities;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(bus=EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public class ModBlockEntityRenderers {
    @SubscribeEvent
    public static void registerSomeRenderers(EntityRenderersEvent.RegisterRenderers event) {
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.ATV.get(), ATVRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.DUNE_BUGGY.get(), DuneBuggyRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.GO_KART.get(), GoKartRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.SHOPPING_CART.get(), ShoppingCartRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.MINI_BIKE.get(), MiniBikeRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.BUMPER_CAR.get(), BumperCarModel::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.JET_SKI.get(), JetSkiRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.SPEED_BOAT.get(), SpeedBoatRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.ALUMINUM_BOAT.get(), AluminumBoatRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.SMART_CAR.get(), SmartCarRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.LAWN_MOWER.get(), LawnMowerRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.MOPED.get(), MopedRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.SPORTS_PLANE.get(), SportsPlaneRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.GOLF_CART.get(), GolfCartRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.OFF_ROADER.get(), OffRoaderRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.TRACTOR.get(), TractorRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.MINI_BUS.get(), MiniBusRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.DIRT_BIKE.get(), DirtBikeRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.QUAD_BIKE.get(), QuadBikeRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.COMPACT_HELICOPTER.get(), CompactHelicopterRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.VEHICLE_TRAILER.get(), VehicleTrailerRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.STORAGE_TRAILER.get(), StorageTrailerRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.FLUID_TRAILER.get(), FluidTrailerRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.SEEDER.get(), SeederTrailerRenderer::new);
        ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.FERTILIZER.get(), FertilizerTrailerRenderer::new);
        if (ModList.get().isLoaded("cfm")) {
            ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.SOFA.get(), SofaCarRenderer::new);
            ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.BATH.get(), BathModel::new);
            ModBlockEntityRenderers.registerVehicleRenderer(event, (EntityType)ModEntities.SOFACOPTER.get(), SofaHelicopterRenderer::new);
        }
    }

    private static <T extends VehicleEntity> void registerVehicleRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<T> type, Function<Supplier<VehicleProperties>, AbstractVehicleRenderer<T>> rendererFunction) {
        AbstractVehicleRenderer renderer = rendererFunction.apply(() -> VehicleProperties.get(type));
        event.registerEntityRenderer(type, manager -> new EntityVehicleRenderer(manager, renderer));
        VehicleRenderRegistry.registerVehicleRendererFunction(type, rendererFunction, renderer);
        EntityRayTracer.IRayTraceTransforms transforms = renderer.getRayTraceTransforms();
        if (transforms != null) {
            EntityRayTracer.instance().registerTransforms(type, transforms);
        }
    }
}

