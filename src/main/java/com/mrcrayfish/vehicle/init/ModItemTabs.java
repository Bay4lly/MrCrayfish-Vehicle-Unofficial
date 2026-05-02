package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModItemTabs
{
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Reference.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = REGISTER.register("main_tab", () -> CreativeModeTab.builder()
            .title(Component.literal("Vehicles"))
            .icon(() -> ModItems.IRON_SMALL_ENGINE.get().getDefaultInstance())
            .displayItems((params, output) -> {
                output.accept(ModItems.PANEL.get());
                output.accept(ModItems.STANDARD_WHEEL.get());
                output.accept(ModItems.SPORTS_WHEEL.get());
                output.accept(ModItems.RACING_WHEEL.get());
                output.accept(ModItems.OFF_ROAD_WHEEL.get());
                output.accept(ModItems.SNOW_WHEEL.get());
                output.accept(ModItems.ALL_TERRAIN_WHEEL.get());
                output.accept(ModItems.PLASTIC_WHEEL.get());
                output.accept(ModItems.IRON_SMALL_ENGINE.get());
                output.accept(ModItems.GOLD_SMALL_ENGINE.get());
                output.accept(ModItems.DIAMOND_SMALL_ENGINE.get());
                output.accept(ModItems.NETHERITE_SMALL_ENGINE.get());
                output.accept(ModItems.IRON_LARGE_ENGINE.get());
                output.accept(ModItems.GOLD_LARGE_ENGINE.get());
                output.accept(ModItems.DIAMOND_LARGE_ENGINE.get());
                output.accept(ModItems.NETHERITE_LARGE_ENGINE.get());
                output.accept(ModItems.IRON_ELECTRIC_ENGINE.get());
                output.accept(ModItems.GOLD_ELECTRIC_ENGINE.get());
                output.accept(ModItems.DIAMOND_ELECTRIC_ENGINE.get());
                output.accept(ModItems.NETHERITE_ELECTRIC_ENGINE.get());
                ItemStack sprayCan = ModItems.SPRAY_CAN.get().getDefaultInstance();
                ModItems.SPRAY_CAN.get().refill(sprayCan);
                output.accept(sprayCan);
                output.accept(ModItems.JERRY_CAN.get());
                output.accept(ModItems.INDUSTRIAL_JERRY_CAN.get());
                output.accept(ModItems.WRENCH.get());
                output.accept(ModItems.HAMMER.get());
                output.accept(ModItems.KEY.get());
                output.accept(ModItems.FUELIUM_BUCKET.get());
                output.accept(ModItems.ENDER_SAP_BUCKET.get());
                output.accept(ModItems.BLAZE_JUICE_BUCKET.get());

                output.accept(ModBlocks.TRAFFIC_CONE.get());
                output.accept(ModBlocks.FLUID_EXTRACTOR.get());
                output.accept(ModBlocks.FLUID_MIXER.get());
                output.accept(ModBlocks.FLUID_PIPE.get());
                output.accept(ModBlocks.FLUID_PUMP.get());
                output.accept(ModBlocks.GAS_PUMP.get());
                output.accept(ModBlocks.FUEL_DRUM.get());
                output.accept(ModBlocks.INDUSTRIAL_FUEL_DRUM.get());
                output.accept(ModBlocks.WORKSTATION.get());
                output.accept(ModBlocks.JACK.get());

                VehicleCrateBlock.REGISTERED_CRATES.forEach(resourceLocation ->
                {
                    CompoundTag blockEntityTag = new CompoundTag();
                    blockEntityTag.putString("Vehicle", resourceLocation.toString());
                    blockEntityTag.putBoolean("Creative", true);
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.put("BlockEntityTag", blockEntityTag);
                    ItemStack stack = new ItemStack(ModBlocks.VEHICLE_CRATE.get());
                    stack.setTag(itemTag);
                    output.accept(stack);
                });
            })
            .build()
    );
}
