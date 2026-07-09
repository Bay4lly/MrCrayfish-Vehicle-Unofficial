/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.data.loot.BlockLootSubProvider
 *  net.minecraft.world.flag.FeatureFlags
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.storage.loot.LootPool
 *  net.minecraft.world.level.storage.loot.LootPool$Builder
 *  net.minecraft.world.level.storage.loot.LootTable
 *  net.minecraft.world.level.storage.loot.LootTable$Builder
 *  net.minecraft.world.level.storage.loot.entries.LootItem
 *  net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer$Builder
 *  net.minecraft.world.level.storage.loot.functions.LootItemFunction$Builder
 *  net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder
 *  net.minecraft.world.level.storage.loot.providers.number.ConstantValue
 *  net.minecraft.world.level.storage.loot.providers.number.NumberProvider
 */
package com.mrcrayfish.vehicle.datagen;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.world.storage.loot.functions.CopyFluidTanks;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class LootTableGen
extends BlockLootSubProvider {
    public LootTableGen(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, provider);
    }

    protected void generate() {
        this.add((Block)ModBlocks.FLUID_EXTRACTOR.get(), this::createFluidTankDrop);
        this.add((Block)ModBlocks.FLUID_MIXER.get(), this::createFluidTankDrop);
        this.add((Block)ModBlocks.FUEL_DRUM.get(), this::createFluidTankDrop);
        this.add((Block)ModBlocks.INDUSTRIAL_FUEL_DRUM.get(), this::createFluidTankDrop);
        this.dropSelf((Block)ModBlocks.FLUID_PIPE.get());
        this.dropSelf((Block)ModBlocks.FLUID_PUMP.get());
        this.dropSelf((Block)ModBlocks.GAS_PUMP.get());
        this.dropSelf((Block)ModBlocks.TRAFFIC_CONE.get());
        this.dropSelf((Block)ModBlocks.WORKSTATION.get());
        this.dropSelf((Block)ModBlocks.WORKSTATION.get());
        this.dropSelf((Block)ModBlocks.JACK.get());
        this.dropSelf((Block)ModBlocks.JACK_HEAD.get());
        this.add((Block)ModBlocks.VEHICLE_CRATE.get(), this::createVehicleCrateDrop);
    }

    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream().filter(block -> BuiltInRegistries.BLOCK.getKey(block) != null && "vehicle".equals(BuiltInRegistries.BLOCK.getKey(block).getNamespace())).collect(Collectors.toSet());
    }

    protected LootTable.Builder createFluidTankDrop(Block block) {
        return LootTable.lootTable().withPool((LootPool.Builder)this.applyExplosionCondition((ItemLike)block, (ConditionUserBuilder)LootPool.lootPool().setRolls((NumberProvider)ConstantValue.exactly((float)1.0f)).add((LootPoolEntryContainer.Builder)LootItem.lootTableItem((ItemLike)block).apply((LootItemFunction.Builder)CopyFluidTanks.copyFluidTanks()))));
    }

    protected LootTable.Builder createVehicleCrateDrop(Block block) {
        return this.createSingleItemTable((ItemLike)block);
    }
}

