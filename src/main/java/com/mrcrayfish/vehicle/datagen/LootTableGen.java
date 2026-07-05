package com.mrcrayfish.vehicle.datagen;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.world.storage.loot.functions.CopyFluidTanks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class LootTableGen extends BlockLootSubProvider
{
    public LootTableGen(net.minecraft.core.HolderLookup.Provider provider)
    {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, provider);
    }

    @Override
    protected void generate()
    {
        this.add(ModBlocks.FLUID_EXTRACTOR.get(), this::createFluidTankDrop);
        this.add(ModBlocks.FLUID_MIXER.get(), this::createFluidTankDrop);
        this.add(ModBlocks.FUEL_DRUM.get(), this::createFluidTankDrop);
        this.add(ModBlocks.INDUSTRIAL_FUEL_DRUM.get(), this::createFluidTankDrop);
        this.dropSelf(ModBlocks.FLUID_PIPE.get());
        this.dropSelf(ModBlocks.FLUID_PUMP.get());
        this.dropSelf(ModBlocks.GAS_PUMP.get());
        this.dropSelf(ModBlocks.TRAFFIC_CONE.get());
        this.dropSelf(ModBlocks.WORKSTATION.get());
        this.dropSelf(ModBlocks.WORKSTATION.get());
        this.dropSelf(ModBlocks.JACK.get());
        this.dropSelf(ModBlocks.JACK_HEAD.get());
        this.add(ModBlocks.VEHICLE_CRATE.get(), this::createVehicleCrateDrop);
    }

    @Override
    protected Iterable<Block> getKnownBlocks()
    {
        return BuiltInRegistries.BLOCK.stream().filter(block -> BuiltInRegistries.BLOCK.getKey(block) != null && Reference.MOD_ID.equals(BuiltInRegistries.BLOCK.getKey(block).getNamespace())).collect(Collectors.toSet()); // FIXME
    }

    protected LootTable.Builder createFluidTankDrop(Block block)
    {
        return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(block).apply(CopyFluidTanks.copyFluidTanks()))));
    }

    protected LootTable.Builder createVehicleCrateDrop(Block block)
    {
        return this.createSingleItemTable(block);
    }
}
