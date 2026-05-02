package com.mrcrayfish.vehicle.client.init;

import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.EntityVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.client.render.vehicle.*;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.function.Function;
import java.util.function.Supplier;

@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
public class ModBlockEntityRenderers
{
    @SubscribeEvent
    public static void registerSomeRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        // Vehicles
        registerVehicleRenderer(event, ModEntities.ATV.get(), ATVRenderer::new);
        registerVehicleRenderer(event, ModEntities.DUNE_BUGGY.get(), DuneBuggyRenderer::new);
        registerVehicleRenderer(event, ModEntities.GO_KART.get(), GoKartRenderer::new);
        registerVehicleRenderer(event, ModEntities.SHOPPING_CART.get(), ShoppingCartRenderer::new);
        registerVehicleRenderer(event, ModEntities.MINI_BIKE.get(), MiniBikeRenderer::new);
        registerVehicleRenderer(event, ModEntities.BUMPER_CAR.get(), BumperCarModel::new);
        registerVehicleRenderer(event, ModEntities.JET_SKI.get(), JetSkiRenderer::new);
        registerVehicleRenderer(event, ModEntities.SPEED_BOAT.get(), SpeedBoatRenderer::new);
        registerVehicleRenderer(event, ModEntities.ALUMINUM_BOAT.get(), AluminumBoatRenderer::new);
        registerVehicleRenderer(event, ModEntities.SMART_CAR.get(), SmartCarRenderer::new);
        registerVehicleRenderer(event, ModEntities.LAWN_MOWER.get(), LawnMowerRenderer::new);
        registerVehicleRenderer(event, ModEntities.MOPED.get(), MopedRenderer::new);
        registerVehicleRenderer(event, ModEntities.SPORTS_PLANE.get(), SportsPlaneRenderer::new);
        registerVehicleRenderer(event, ModEntities.GOLF_CART.get(), GolfCartRenderer::new);
        registerVehicleRenderer(event, ModEntities.OFF_ROADER.get(), OffRoaderRenderer::new);
        registerVehicleRenderer(event, ModEntities.TRACTOR.get(), TractorRenderer::new);
        registerVehicleRenderer(event, ModEntities.MINI_BUS.get(), MiniBusRenderer::new);
        registerVehicleRenderer(event, ModEntities.DIRT_BIKE.get(), DirtBikeRenderer::new);
        registerVehicleRenderer(event, ModEntities.QUAD_BIKE.get(), QuadBikeRenderer::new);
        registerVehicleRenderer(event, ModEntities.COMPACT_HELICOPTER.get(), CompactHelicopterRenderer::new);

        // Trailers
        registerVehicleRenderer(event, ModEntities.VEHICLE_TRAILER.get(), VehicleTrailerRenderer::new);
        registerVehicleRenderer(event, ModEntities.STORAGE_TRAILER.get(), StorageTrailerRenderer::new);
        registerVehicleRenderer(event, ModEntities.FLUID_TRAILER.get(), FluidTrailerRenderer::new);
        registerVehicleRenderer(event, ModEntities.SEEDER.get(), SeederTrailerRenderer::new);
        registerVehicleRenderer(event, ModEntities.FERTILIZER.get(), FertilizerTrailerRenderer::new);

        // Register Mod Exclusive Vehicles
        if(ModList.get().isLoaded("cfm"))
        {
            registerVehicleRenderer(event, ModEntities.SOFA.get(), SofaCarRenderer::new);
            registerVehicleRenderer(event, ModEntities.BATH.get(), BathModel::new);
            registerVehicleRenderer(event, ModEntities.SOFACOPTER.get(), SofaHelicopterRenderer::new);
        }
    }

    private static <T extends VehicleEntity & EntityRayTracer.IEntityRayTraceable> void registerVehicleRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<T> type, Function<Supplier<VehicleProperties>, AbstractVehicleRenderer<T>> rendererFunction)
    {
        AbstractVehicleRenderer<T> renderer = rendererFunction.apply(() -> VehicleProperties.get(type));
        event.registerEntityRenderer(type, manager -> new EntityVehicleRenderer<>(manager, renderer));
        VehicleRenderRegistry.registerVehicleRendererFunction(type, rendererFunction, renderer);

        EntityRayTracer.IRayTraceTransforms transforms = renderer.getRayTraceTransforms();
        if(transforms != null)
        {
            EntityRayTracer.instance().registerTransforms(type, transforms);
        }
    }
}
