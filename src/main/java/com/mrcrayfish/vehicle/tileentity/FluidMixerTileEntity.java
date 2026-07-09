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
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.Container
 *  net.minecraft.world.ContainerHelper
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ContainerData
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeType
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
import com.mrcrayfish.vehicle.crafting.FluidEntry;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipe;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModRecipeTypes;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.inventory.container.FluidMixerContainer;
import com.mrcrayfish.vehicle.tileentity.IFluidTankWriter;
import com.mrcrayfish.vehicle.tileentity.TileEntitySynced;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
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

public class FluidMixerTileEntity
extends TileEntitySynced
implements Container,
MenuProvider,
IFluidTankWriter {
    private NonNullList<ItemStack> inventory = NonNullList.withSize((int)7, ItemStack.EMPTY);
    private FluidTank tankBlaze;
    private FluidTank tankEnderSap;
    private FluidTank tankFuelium;
    private static final int SLOT_FUEL = 0;
    public static final int SLOT_INGREDIENT = 1;
    private FluidMixerRecipe currentRecipe;
    private int remainingFuel;
    private int fuelMaxProgress;
    private int extractionProgress;
    private boolean mixing;
    private String customName;
    protected final ContainerData fluidMixerData;

    public FluidMixerTileEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)ModTileEntities.FLUID_MIXER.get(), pos, state);
        this.tankBlaze = new FluidTank(((Integer)Config.SERVER.mixerInputCapacity.get()).intValue(), this::isValidFluid);
        this.tankEnderSap = new FluidTank(((Integer)Config.SERVER.mixerInputCapacity.get()).intValue(), this::isValidFluid);
        this.tankFuelium = new FluidTank(((Integer)Config.SERVER.mixerOutputCapacity.get()).intValue(), stack -> stack.getFluid() == ModFluids.FUELIUM.get());
        this.currentRecipe = null;
        this.mixing = false;
        this.fluidMixerData = new ContainerData(){

            public int get(int index) {
                switch (index) {
                    case 0: {
                        return FluidMixerTileEntity.this.extractionProgress;
                    }
                    case 1: {
                        return FluidMixerTileEntity.this.remainingFuel;
                    }
                    case 2: {
                        return FluidMixerTileEntity.this.fuelMaxProgress;
                    }
                    case 3: {
                        return FluidMixerTileEntity.this.tankBlaze.getFluidAmount();
                    }
                    case 4: {
                        return FluidMixerTileEntity.this.tankEnderSap.getFluidAmount();
                    }
                    case 5: {
                        return FluidMixerTileEntity.this.tankFuelium.getFluidAmount();
                    }
                    case 6: {
                        return BuiltInRegistries.FLUID.getKey(FluidMixerTileEntity.this.tankBlaze.getFluid().getFluid()).hashCode();
                    }
                    case 7: {
                        return BuiltInRegistries.FLUID.getKey(FluidMixerTileEntity.this.tankEnderSap.getFluid().getFluid()).hashCode();
                    }
                    case 8: {
                        return BuiltInRegistries.FLUID.getKey(FluidMixerTileEntity.this.tankFuelium.getFluid().getFluid()).hashCode();
                    }
                }
                return 0;
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0: {
                        FluidMixerTileEntity.this.extractionProgress = value;
                        break;
                    }
                    case 1: {
                        FluidMixerTileEntity.this.remainingFuel = value;
                        break;
                    }
                    case 2: {
                        FluidMixerTileEntity.this.fuelMaxProgress = value;
                        break;
                    }
                    case 3: {
                        if (FluidMixerTileEntity.this.tankBlaze.isEmpty() && FluidMixerTileEntity.this.tankBlaze.getFluid().getFluid() == Fluids.EMPTY) break;
                        FluidMixerTileEntity.this.tankBlaze.getFluid().setAmount(value);
                        break;
                    }
                    case 4: {
                        if (FluidMixerTileEntity.this.tankEnderSap.isEmpty() && FluidMixerTileEntity.this.tankEnderSap.getFluid().getFluid() == Fluids.EMPTY) break;
                        FluidMixerTileEntity.this.tankEnderSap.getFluid().setAmount(value);
                        break;
                    }
                    case 5: {
                        if (FluidMixerTileEntity.this.tankFuelium.isEmpty() && FluidMixerTileEntity.this.tankFuelium.getFluid().getFluid() == Fluids.EMPTY) break;
                        FluidMixerTileEntity.this.tankFuelium.getFluid().setAmount(value);
                        break;
                    }
                    case 6: {
                        FluidMixerTileEntity.this.updateFluid(FluidMixerTileEntity.this.tankBlaze, value);
                        break;
                    }
                    case 7: {
                        FluidMixerTileEntity.this.updateFluid(FluidMixerTileEntity.this.tankEnderSap, value);
                        break;
                    }
                    case 8: {
                        FluidMixerTileEntity.this.updateFluid(FluidMixerTileEntity.this.tankFuelium, value);
                    }
                }
            }

            public int getCount() {
                return 9;
            }
        };
    }

    public int getContainerSize() {
        return 7;
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, FluidMixerTileEntity blockEntity) {
        if (blockEntity.level != null && !blockEntity.level.isClientSide()) {
            ItemStack ingredient = blockEntity.getItem(1);
            ItemStack fuel = blockEntity.getItem(0);
            if (blockEntity.currentRecipe == null && !ingredient.isEmpty()) {
                blockEntity.currentRecipe = blockEntity.getRecipe().orElse(null);
            } else if (!blockEntity.canMix(blockEntity.currentRecipe)) {
                blockEntity.currentRecipe = null;
                blockEntity.extractionProgress = 0;
            }
            if (blockEntity.canMix(blockEntity.currentRecipe)) {
                blockEntity.updateFuel(fuel);
                if (blockEntity.remainingFuel > 0) {
                    blockEntity.setMixing(true);
                    if (blockEntity.extractionProgress++ == (Integer)Config.SERVER.mixerMixTime.get()) {
                        FluidMixerRecipe recipe = blockEntity.currentRecipe;
                        blockEntity.tankFuelium.fill(recipe.getResult().createStack(), IFluidHandler.FluidAction.EXECUTE);
                        blockEntity.tankBlaze.drain(recipe.getFluidAmount(blockEntity.tankBlaze.getFluid().getFluid()), IFluidHandler.FluidAction.EXECUTE);
                        blockEntity.tankEnderSap.drain(recipe.getFluidAmount(blockEntity.tankEnderSap.getFluid().getFluid()), IFluidHandler.FluidAction.EXECUTE);
                        blockEntity.shrinkItem(1);
                        blockEntity.extractionProgress = 0;
                        blockEntity.currentRecipe = null;
                    }
                } else {
                    blockEntity.extractionProgress = 0;
                    blockEntity.setMixing(false);
                }
            } else {
                blockEntity.extractionProgress = 0;
                blockEntity.setMixing(false);
            }
            if (blockEntity.remainingFuel > 0) {
                --blockEntity.remainingFuel;
                blockEntity.updateFuel(fuel);
                if (blockEntity.remainingFuel == 0) {
                    blockEntity.setMixing(false);
                }
            }
        }
    }

    private void updateFuel(ItemStack fuel) {
        if (!fuel.isEmpty() && fuel.getBurnTime(RecipeType.SMELTING) > 0 && this.remainingFuel == 0 && this.canMix(this.currentRecipe)) {
            this.remainingFuel = this.fuelMaxProgress = fuel.getBurnTime(RecipeType.SMELTING);
            this.shrinkItem(0);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean canMix() {
        ItemStack ingredient = this.getItem(1);
        if (!(ingredient.isEmpty() || this.tankBlaze.getFluid().isEmpty() || this.tankEnderSap.getFluid().isEmpty())) {
            if (this.currentRecipe == null) {
                this.currentRecipe = this.getRecipe().orElse(null);
            }
        } else {
            this.currentRecipe = null;
        }
        return this.currentRecipe != null && this.canMix(this.currentRecipe) && this.remainingFuel >= 0;
    }

    private void shrinkItem(int index) {
        ItemStack stack = this.getItem(index);
        stack.shrink(1);
        if (stack.isEmpty()) {
            this.setItem(index, ItemStack.EMPTY);
        }
    }

    private boolean canMix(@Nullable FluidMixerRecipe recipe) {
        if (recipe == null) {
            return false;
        }
        ItemStack ingredient = this.getItem(1);
        if (ingredient.getItem() != recipe.getIngredient().getItem()) {
            return false;
        }
        if (this.tankBlaze.getFluid().isEmpty()) {
            return false;
        }
        if (this.tankEnderSap.getFluid().isEmpty()) {
            return false;
        }
        if (this.tankBlaze.getFluidAmount() < recipe.getFluidAmount(this.tankBlaze.getFluid().getFluid())) {
            return false;
        }
        if (this.tankEnderSap.getFluidAmount() < recipe.getFluidAmount(this.tankEnderSap.getFluid().getFluid())) {
            return false;
        }
        if (this.tankFuelium.getFluidAmount() >= this.tankFuelium.getCapacity()) {
            return false;
        }
        return this.tankFuelium.getFluidAmount() + recipe.getResult().getAmount() <= this.tankFuelium.getCapacity();
    }

    public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        CompoundTag tagCompound;
        super.loadAdditional(compound, registries);
        if (compound.contains("Items", 9)) {
            this.inventory = NonNullList.withSize((int)this.getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems((CompoundTag)compound, this.inventory, (HolderLookup.Provider)registries);
        }
        if (compound.contains("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
        if (compound.contains("TankBlaze", 10)) {
            tagCompound = compound.getCompound("TankBlaze");
            this.tankBlaze.readFromNBT(registries, tagCompound);
        }
        if (compound.contains("TankEnderSap", 10)) {
            tagCompound = compound.getCompound("TankEnderSap");
            this.tankEnderSap.readFromNBT(registries, tagCompound);
        }
        if (compound.contains("TankFuelium", 10)) {
            tagCompound = compound.getCompound("TankFuelium");
            this.tankFuelium.readFromNBT(registries, tagCompound);
        }
        if (compound.contains("RemainingFuel", 3)) {
            this.remainingFuel = compound.getInt("RemainingFuel");
        }
        if (compound.contains("FuelMaxProgress", 3)) {
            this.fuelMaxProgress = compound.getInt("FuelMaxProgress");
        }
        if (compound.contains("ExtractionProgress", 3)) {
            this.extractionProgress = compound.getInt("ExtractionProgress");
        }
    }

    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        ContainerHelper.saveAllItems((CompoundTag)compound, this.inventory, (HolderLookup.Provider)registries);
        if (this.hasCustomName()) {
            compound.putString("CustomName", this.customName);
        }
        this.writeTanks(registries, compound);
        compound.putInt("RemainingFuel", this.remainingFuel);
        compound.putInt("FuelMaxProgress", this.fuelMaxProgress);
        compound.putInt("ExtractionProgress", this.extractionProgress);
    }

    @Override
    public void writeTanks(HolderLookup.Provider registries, CompoundTag compound) {
        CompoundTag tagTankBlaze = new CompoundTag();
        this.tankBlaze.writeToNBT(registries, tagTankBlaze);
        compound.put("TankBlaze", (Tag)tagTankBlaze);
        CompoundTag tagTankEnderSap = new CompoundTag();
        this.tankEnderSap.writeToNBT(registries, tagTankEnderSap);
        compound.put("TankEnderSap", (Tag)tagTankEnderSap);
        CompoundTag tagTankFuelium = new CompoundTag();
        this.tankFuelium.writeToNBT(registries, tagTankFuelium);
        compound.put("TankFuelium", (Tag)tagTankFuelium);
    }

    @Override
    public boolean areTanksEmpty() {
        return this.tankBlaze.isEmpty() && this.tankEnderSap.isEmpty() && this.tankFuelium.isEmpty();
    }

    private String getName() {
        return this.hasCustomName() ? this.customName : "container.fluid_mixer";
    }

    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public Component getDisplayName() {
        return this.hasCustomName() ? Component.literal((String)this.getName()) : Component.translatable((String)this.getName());
    }

    @Nullable
    public FluidStack getBlazeFluidStack() {
        return this.tankBlaze.getFluid();
    }

    @Nullable
    public FluidStack getEnderSapFluidStack() {
        return this.tankEnderSap.getFluid();
    }

    @Nullable
    public FluidStack getFueliumFluidStack() {
        return this.tankFuelium.getFluid();
    }

    public int getExtractionProgress() {
        return this.fluidMixerData.get(0);
    }

    public int getRemainingFuel() {
        return this.fluidMixerData.get(1);
    }

    public int getFuelMaxProgress() {
        return this.fluidMixerData.get(2);
    }

    public int getBlazeLevel() {
        return this.fluidMixerData.get(3);
    }

    public int getEnderSapLevel() {
        return this.fluidMixerData.get(4);
    }

    public int getFueliumLevel() {
        return this.fluidMixerData.get(5);
    }

    @Nullable
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        return new FluidMixerContainer(windowId, playerInventory, this);
    }

    public ContainerData getFluidMixerData() {
        return this.fluidMixerData;
    }

    public void updateFluid(FluidTank tank, int fluidHash) {
        Optional<Fluid> optional = BuiltInRegistries.FLUID.stream().filter(fluid -> BuiltInRegistries.FLUID.getKey(fluid).hashCode() == fluidHash).findFirst();
        optional.ifPresent(fluid -> tank.setFluid(new FluidStack(fluid, tank.getFluidAmount())));
    }

    public Optional<FluidMixerRecipe> getRecipe() {
        FluidMixerRecipe.FluidMixerInput input = new FluidMixerRecipe.FluidMixerInput(this.getItem(1), this.tankEnderSap.getFluid().getFluid(), this.tankBlaze.getFluid().getFluid());
        return this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.FLUID_MIXER.get(), input, this.level).map(RecipeHolder::value);
    }

    private boolean isValidIngredient(ItemStack ingredient) {
        List<RecipeHolder<FluidMixerRecipe>> recipes = this.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.FLUID_MIXER.get());
        return recipes.stream().map(RecipeHolder::value).anyMatch(recipe -> InventoryUtil.areItemStacksEqualIgnoreCount(ingredient, recipe.getIngredient()));
    }

    private boolean isValidFluid(FluidStack stack) {
        List<RecipeHolder<FluidMixerRecipe>> recipes = this.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.FLUID_MIXER.get());
        return recipes.stream().map(RecipeHolder::value).anyMatch(recipe -> {
            for (FluidEntry entry : recipe.getInputs()) {
                if (entry.getFluid() != stack.getFluid()) continue;
                return true;
            }
            return false;
        });
    }

    public FluidTank getEnderSapTank() {
        return this.tankEnderSap;
    }

    public FluidTank getBlazeTank() {
        return this.tankBlaze;
    }

    public FluidTank getFueliumTank() {
        return this.tankFuelium;
    }

    private void setMixing(boolean state) {
        if (this.mixing != state) {
            this.mixing = state;
            this.level.setBlock(this.worldPosition, (BlockState)this.getBlockState().setValue(FluidMixerBlock.ENABLED, Boolean.valueOf(state)), 3);
        }
    }
}

