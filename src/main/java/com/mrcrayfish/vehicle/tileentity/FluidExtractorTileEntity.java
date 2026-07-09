/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.Container
 *  net.minecraft.world.ContainerHelper
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.Nameable
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ContainerData
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 */
package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.FluidMixerBlock;
import com.mrcrayfish.vehicle.crafting.FluidExtractorRecipe;
import com.mrcrayfish.vehicle.init.ModRecipeTypes;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.inventory.container.FluidExtractorContainer;
import com.mrcrayfish.vehicle.tileentity.TileFluidHandlerSynced;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FluidExtractorTileEntity
extends TileFluidHandlerSynced
implements Container,
MenuProvider,
Nameable {
    private NonNullList<ItemStack> inventory = NonNullList.withSize((int)7, ItemStack.EMPTY);
    private static final int SLOT_FUEL_SOURCE = 0;
    public static final int SLOT_FLUID_SOURCE = 1;
    private FluidExtractorRecipe currentRecipe = null;
    private int remainingFuel;
    private int fuelMaxProgress;
    private int extractionProgress;
    private int capacity;
    private boolean extracting;
    private String customName;
    protected final ContainerData fluidExtractorData = new ContainerData(){

        public int get(int index) {
            switch (index) {
                case 0: {
                    return FluidExtractorTileEntity.this.extractionProgress;
                }
                case 1: {
                    return FluidExtractorTileEntity.this.remainingFuel;
                }
                case 2: {
                    return FluidExtractorTileEntity.this.fuelMaxProgress;
                }
                case 3: {
                    return BuiltInRegistries.FLUID.getKey(FluidExtractorTileEntity.this.tank.getFluid().getFluid()).hashCode();
                }
                case 4: {
                    return FluidExtractorTileEntity.this.tank.getFluidAmount();
                }
            }
            return 0;
        }

        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    FluidExtractorTileEntity.this.extractionProgress = value;
                    break;
                }
                case 1: {
                    FluidExtractorTileEntity.this.remainingFuel = value;
                    break;
                }
                case 2: {
                    FluidExtractorTileEntity.this.fuelMaxProgress = value;
                    break;
                }
                case 3: {
                    FluidExtractorTileEntity.this.updateFluid(FluidExtractorTileEntity.this.tank, value);
                    break;
                }
                case 4: {
                    if (FluidExtractorTileEntity.this.tank.isEmpty() && FluidExtractorTileEntity.this.tank.getFluid().getFluid() == Fluids.EMPTY) break;
                    FluidExtractorTileEntity.this.tank.getFluid().setAmount(value);
                }
            }
        }

        public int getCount() {
            return 5;
        }
    };

    public FluidExtractorTileEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)ModTileEntities.FLUID_EXTRACTOR.get(), pos, state, (Integer)Config.SERVER.extractorCapacity.get(), stack -> true);
        this.capacity = (Integer)Config.SERVER.extractorCapacity.get();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FluidExtractorTileEntity blockEntity) {
        if (blockEntity.level != null && !blockEntity.level.isClientSide()) {
            ItemStack source = blockEntity.getItem(1);
            ItemStack fuel = blockEntity.getItem(0);
            if (blockEntity.currentRecipe == null && !source.isEmpty()) {
                blockEntity.currentRecipe = blockEntity.getRecipe().orElse(null);
            } else if (source.isEmpty()) {
                blockEntity.currentRecipe = null;
                blockEntity.extractionProgress = 0;
            }
            blockEntity.updateFuel(source, fuel);
            if (blockEntity.remainingFuel > 0 && blockEntity.canFillWithFluid(source)) {
                blockEntity.setExtracting(true);
                if (blockEntity.extractionProgress++ == (Integer)Config.SERVER.extractorExtractTime.get()) {
                    blockEntity.tank.fill(blockEntity.currentRecipe.getResult().createStack(), IFluidHandler.FluidAction.EXECUTE);
                    blockEntity.extractionProgress = 0;
                    blockEntity.shrinkItem(1);
                    blockEntity.currentRecipe = null;
                }
            } else {
                blockEntity.extractionProgress = 0;
                blockEntity.setExtracting(false);
            }
            if (blockEntity.remainingFuel > 0) {
                --blockEntity.remainingFuel;
                blockEntity.updateFuel(source, fuel);
                if (blockEntity.remainingFuel == 0) {
                    blockEntity.setExtracting(false);
                }
            }
        }
    }

    private void updateFuel(ItemStack source, ItemStack fuel) {
        if (!fuel.isEmpty() && this.remainingFuel == 0 && this.canFillWithFluid(source)) {
            this.remainingFuel = this.fuelMaxProgress = fuel.getBurnTime(RecipeType.SMELTING);
            this.shrinkItem(0);
        }
    }

    private boolean canFillWithFluid(ItemStack stack) {
        return this.currentRecipe != null && this.currentRecipe.getIngredient().getItem() == stack.getItem() && this.tank.getFluidAmount() < this.tank.getCapacity() && (this.tank.isEmpty() || this.tank.getFluid().getFluid() == this.currentRecipe.getResult().getFluid()) && this.tank.getFluidAmount() + this.currentRecipe.getResult().getAmount() <= this.tank.getCapacity();
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean canExtract() {
        ItemStack ingredient = this.getItem(1);
        if (!ingredient.isEmpty()) {
            if (this.currentRecipe == null) {
                this.currentRecipe = this.getRecipe().orElse(null);
            }
        } else {
            this.currentRecipe = null;
        }
        return this.canFillWithFluid(ingredient) && this.remainingFuel >= 0;
    }

    @OnlyIn(value=Dist.CLIENT)
    public FluidExtractorRecipe getCurrentRecipe() {
        return this.currentRecipe;
    }

    public FluidStack getFluidStackTank() {
        return this.tank.getFluid();
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getContainerSize() {
        return 2;
    }

    public boolean isEmpty() {
        for (ItemStack stack : this.inventory) {
            if (stack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public ItemStack getItem(int index) {
        return (ItemStack)this.inventory.get(index);
    }

    public ItemStack removeItem(int index, int count) {
        ItemStack stack = ContainerHelper.removeItem(this.inventory, (int)index, (int)count);
        if (!stack.isEmpty()) {
            this.setChanged();
        }
        return stack;
    }

    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.inventory, (int)index);
    }

    public void setItem(int index, ItemStack stack) {
        this.inventory.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    public boolean stillValid(Player player) {
        return this.level.getBlockEntity(this.worldPosition) == this && player.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == 0) {
            return stack.getBurnTime(RecipeType.SMELTING) > 0;
        }
        if (index == 1) {
            return this.isValidIngredient(stack);
        }
        return false;
    }

    public void clearContent() {
        this.inventory.clear();
    }

    public int getExtractionProgress() {
        return this.fluidExtractorData.get(0);
    }

    public int getRemainingFuel() {
        return this.fluidExtractorData.get(1);
    }

    public int getFuelMaxProgress() {
        return this.fluidExtractorData.get(2);
    }

    public int getFluidLevel() {
        return this.fluidExtractorData.get(4);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        if (compound.contains("ExtractionProgress", 3)) {
            this.extractionProgress = compound.getInt("ExtractionProgress");
        }
        if (compound.contains("RemainingFuel", 3)) {
            this.remainingFuel = compound.getInt("RemainingFuel");
        }
        if (compound.contains("FuelMaxProgress", 3)) {
            this.fuelMaxProgress = compound.getInt("FuelMaxProgress");
        }
        if (compound.contains("Items", 9)) {
            this.inventory = NonNullList.withSize((int)this.getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems((CompoundTag)compound, this.inventory, (HolderLookup.Provider)registries);
        }
        if (compound.contains("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        compound.putInt("ExtractionProgress", this.extractionProgress);
        compound.putInt("RemainingFuel", this.remainingFuel);
        compound.putInt("FuelMaxProgress", this.fuelMaxProgress);
        ContainerHelper.saveAllItems((CompoundTag)compound, this.inventory, (HolderLookup.Provider)registries);
        if (this.hasCustomName()) {
            compound.putString("CustomName", this.customName);
        }
    }

    public Component getName() {
        return this.getDisplayName();
    }

    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public Component getDisplayName() {
        return this.hasCustomName() ? Component.literal((String)this.customName) : Component.translatable((String)"container.fluid_extractor");
    }

    private void shrinkItem(int index) {
        ItemStack stack = this.getItem(index);
        stack.shrink(1);
        if (stack.isEmpty()) {
            this.setItem(index, ItemStack.EMPTY);
        }
    }

    @Nullable
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        return new FluidExtractorContainer(windowId, (Container)playerInventory, this);
    }

    public ContainerData getFluidExtractorData() {
        return this.fluidExtractorData;
    }

    public void updateFluid(FluidTank tank, int fluidHash) {
        Optional<Fluid> optional = BuiltInRegistries.FLUID.stream().filter(fluid -> BuiltInRegistries.FLUID.getKey(fluid).hashCode() == fluidHash).findFirst();
        optional.ifPresent(fluid -> tank.setFluid(new FluidStack(fluid, tank.getFluidAmount())));
    }

    public Optional<FluidExtractorRecipe> getRecipe() {
        return this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.FLUID_EXTRACTOR.get(), new SingleRecipeInput(this.getItem(1)), this.level).map(RecipeHolder::value);
    }

    public boolean isValidIngredient(ItemStack ingredient) {
        List<RecipeHolder<FluidExtractorRecipe>> recipes = this.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.FLUID_EXTRACTOR.get());
        return recipes.stream().map(RecipeHolder::value).anyMatch(recipe -> InventoryUtil.areItemStacksEqualIgnoreCount(ingredient, recipe.getIngredient()));
    }

    private void setExtracting(boolean state) {
        if (this.extracting != state) {
            this.extracting = state;
            this.level.setBlock(this.worldPosition, (BlockState)this.getBlockState().setValue(FluidMixerBlock.ENABLED, Boolean.valueOf(state)), 3);
        }
    }
}

