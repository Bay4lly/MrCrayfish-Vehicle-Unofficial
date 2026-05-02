package com.mrcrayfish.vehicle.client.init;

import com.mrcrayfish.vehicle.client.render.JackRenderer;
import com.mrcrayfish.vehicle.client.render.tileentity.*;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers
{
    @SubscribeEvent
    public static void registerSomeRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModTileEntities.FLUID_EXTRACTOR.get(), FluidExtractorRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntities.FUEL_DRUM.get(), FuelDrumRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntities.INDUSTRIAL_FUEL_DRUM.get(), FuelDrumRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntities.VEHICLE_CRATE.get(), VehicleCrateRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntities.JACK.get(), JackBlockRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntities.GAS_PUMP.get(), GasPumpRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntities.GAS_PUMP_TANK.get(), GasPumpTankRenderer::new);
        event.registerBlockEntityRenderer(ModTileEntities.FLUID_PUMP.get(), FluidPumpRenderer::new);

        event.registerEntityRenderer(ModEntities.JACK.get(), JackRenderer::new);
    }
}
