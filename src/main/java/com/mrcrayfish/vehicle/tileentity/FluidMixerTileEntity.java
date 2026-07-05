package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.FluidMixerBlock;
import com.mrcrayfish.vehicle.block.RotatedObjectBlock;
import com.mrcrayfish.vehicle.crafting.FluidEntry;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipe;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModRecipeTypes;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.inventory.container.FluidMixerContainer;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class FluidMixerTileEntity extends TileEntitySynced implements Container, MenuProvider, IFluidTankWriter
{
    private NonNullList<ItemStack> inventory = NonNullList.withSize(7, ItemStack.EMPTY);

    private FluidTank tankBlaze = new FluidTank(Config.SERVER.mixerInputCapacity.get(), this::isValidFluid);
    private FluidTank tankEnderSap = new FluidTank(Config.SERVER.mixerInputCapacity.get(), this::isValidFluid);
    private FluidTank tankFuelium = new FluidTank(Config.SERVER.mixerOutputCapacity.get(), stack -> stack.getFluid() == ModFluids.FUELIUM.get());

    private static final int SLOT_FUEL = 0;
    public static final int SLOT_INGREDIENT = 1;

    private FluidMixerRecipe currentRecipe = null;
    private int remainingFuel;
    private int fuelMaxProgress;
    private int extractionProgress;
    private boolean mixing = false;

    private String customName;

    protected final ContainerData fluidMixerData = new ContainerData()
    {
        public int get(int index)
        {
            switch(index)
            {
                case 0:
                    return extractionProgress;
                case 1:
                    return remainingFuel;
                case 2:
                    return fuelMaxProgress;
                case 3:
                    return tankBlaze.getFluidAmount();
                case 4:
                    return tankEnderSap.getFluidAmount();
                case 5:
                    return tankFuelium.getFluidAmount();
                case 6:
                    return BuiltInRegistries.FLUID.getKey(tankBlaze.getFluid().getFluid()).hashCode(); // FIXME
                case 7:
                    return BuiltInRegistries.FLUID.getKey(tankEnderSap.getFluid().getFluid()).hashCode(); // FIXME
                case 8:
                    return BuiltInRegistries.FLUID.getKey(tankFuelium.getFluid().getFluid()).hashCode(); // FIXME
            }
            return 0;
        }

        public void set(int index, int value)
        {
            switch(index)
            {
                case 0:
                    extractionProgress = value;
                    break;
                case 1:
                    remainingFuel = value;
                    break;
                case 2:
                    fuelMaxProgress = value;
                    break;
                case 3:
                    if(!tankBlaze.isEmpty() || tankBlaze.getFluid().getFluid() != Fluids.EMPTY)
                    {
                        tankBlaze.getFluid().setAmount(value);
                    }
                    break;
                case 4:
                    if(!tankEnderSap.isEmpty() || tankEnderSap.getFluid().getFluid() != Fluids.EMPTY)
                    {
                        tankEnderSap.getFluid().setAmount(value);
                    }
                    break;
                case 5:
                    if(!tankFuelium.isEmpty() || tankFuelium.getFluid().getFluid() != Fluids.EMPTY)
                    {
                        tankFuelium.getFluid().setAmount(value);
                    }
                    break;
                case 6:
                    updateFluid(tankBlaze, value);
                    break;
                case 7:
                    updateFluid(tankEnderSap, value);
                    break;
                case 8:
                    updateFluid(tankFuelium, value);
                    break;
            }
        }

        public int getCount()
        {
            return 9;
        }
    };

    public FluidMixerTileEntity(BlockPos pos, BlockState state)
    {
        super(ModTileEntities.FLUID_MIXER.get(), pos, state);
    }

    @Override
    public int getContainerSize()
    {
        return 7;
    }

    @Override
    public boolean isEmpty()
    {
        for(ItemStack stack : this.inventory)
        {
            if(!stack.isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index)
    {
        return this.inventory.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        ItemStack stack = ContainerHelper.removeItem(this.inventory, index, count);
        if(!stack.isEmpty())
        {
            this.setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        return ContainerHelper.takeItem(this.inventory, index);
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        this.inventory.set(index, stack);
        if(stack.getCount() > this.getMaxStackSize())
        {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player)
    {
        return this.level.getBlockEntity(this.worldPosition) == this && player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    {
        if(index == 0)
        {
            return stack.getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING) > 0;
        }
        else if(index == 1)
        {
            return this.isValidIngredient(stack);
        }
        return false;
    }

    @Override
    public void clearContent()
    {
        this.inventory.clear();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FluidMixerTileEntity blockEntity)
    {
        if(blockEntity.level != null && !blockEntity.level.isClientSide())
        {
            ItemStack ingredient = blockEntity.getItem(SLOT_INGREDIENT);
            ItemStack fuel = blockEntity.getItem(SLOT_FUEL);

            if(blockEntity.currentRecipe == null && !ingredient.isEmpty())
            {
                blockEntity.currentRecipe = blockEntity.getRecipe().orElse(null);
            }
            else if(!blockEntity.canMix(blockEntity.currentRecipe))
            {
                blockEntity.currentRecipe = null;
                blockEntity.extractionProgress = 0;
            }

            if(blockEntity.canMix(blockEntity.currentRecipe))
            {
                blockEntity.updateFuel(fuel);

                if(blockEntity.remainingFuel > 0)
                {
                    blockEntity.setMixing(true);

                    if(blockEntity.extractionProgress++ == Config.SERVER.mixerMixTime.get())
                    {
                        FluidMixerRecipe recipe = blockEntity.currentRecipe;
                        blockEntity.tankFuelium.fill(recipe.getResult().createStack(), IFluidHandler.FluidAction.EXECUTE);
                        blockEntity.tankBlaze.drain(recipe.getFluidAmount(blockEntity.tankBlaze.getFluid().getFluid()), IFluidHandler.FluidAction.EXECUTE);
                        blockEntity.tankEnderSap.drain(recipe.getFluidAmount(blockEntity.tankEnderSap.getFluid().getFluid()), IFluidHandler.FluidAction.EXECUTE);
                        blockEntity.shrinkItem(SLOT_INGREDIENT);
                        blockEntity.extractionProgress = 0;
                        blockEntity.currentRecipe = null;
                    }
                }
                else
                {
                    blockEntity.extractionProgress = 0;
                    blockEntity.setMixing(false);
                }
            }
            else
            {
                blockEntity.extractionProgress = 0;
                blockEntity.setMixing(false);
            }

            if(blockEntity.remainingFuel > 0)
            {
                blockEntity.remainingFuel--;
                blockEntity.updateFuel(fuel);

                // Updates the enabled state of the fluid extractor
                if(blockEntity.remainingFuel == 0)
                {
                    blockEntity.setMixing(false);
                }
            }
        }
    }

    private void updateFuel(ItemStack fuel)
    {
        if(!fuel.isEmpty() && fuel.getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING) > 0 && this.remainingFuel == 0 && this.canMix(this.currentRecipe))
        {
            this.fuelMaxProgress = fuel.getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING);
            this.remainingFuel = this.fuelMaxProgress;
            this.shrinkItem(SLOT_FUEL);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean canMix()
    {
        ItemStack ingredient = this.getItem(SLOT_INGREDIENT);
        if(!ingredient.isEmpty() && !this.tankBlaze.getFluid().isEmpty() && !this.tankEnderSap.getFluid().isEmpty())
        {
            if(this.currentRecipe == null)
            {
                this.currentRecipe = this.getRecipe().orElse(null);
            }
        }
        else
        {
            this.currentRecipe = null;
        }
        return this.currentRecipe != null && this.canMix(this.currentRecipe) && this.remainingFuel >= 0;
    }

    private void shrinkItem(int index)
    {
        ItemStack stack = this.getItem(index);
        stack.shrink(1);
        if(stack.isEmpty())
        {
            this.setItem(index, ItemStack.EMPTY);
        }
    }

    private boolean canMix(@Nullable FluidMixerRecipe recipe)
    {
        if(recipe == null)
            return false;
        ItemStack ingredient = this.getItem(SLOT_INGREDIENT);
        if(ingredient.getItem() != recipe.getIngredient().getItem())
            return false;
        if(this.tankBlaze.getFluid().isEmpty())
            return false;
        if(this.tankEnderSap.getFluid().isEmpty())
            return false;
        if(this.tankBlaze.getFluidAmount() < recipe.getFluidAmount(this.tankBlaze.getFluid().getFluid()))
            return false;
        if(this.tankEnderSap.getFluidAmount() < recipe.getFluidAmount(this.tankEnderSap.getFluid().getFluid()))
            return false;
        if(this.tankFuelium.getFluidAmount() >= this.tankFuelium.getCapacity())
            return false;
        return this.tankFuelium.getFluidAmount() + recipe.getResult().getAmount() <= this.tankFuelium.getCapacity();
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries)
    {
        super.loadAdditional(compound, registries);
        if(compound.contains("Items", Tag.TAG_LIST))
        {
            this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(compound, this.inventory, registries);
        }
        if(compound.contains("CustomName", Tag.TAG_STRING))
        {
            this.customName = compound.getString("CustomName");
        }
        if(compound.contains("TankBlaze", Tag.TAG_COMPOUND))
        {
            CompoundTag tagCompound = compound.getCompound("TankBlaze");
            this.tankBlaze.readFromNBT(registries, tagCompound);
        }
        if(compound.contains("TankEnderSap", Tag.TAG_COMPOUND))
        {
            CompoundTag tagCompound = compound.getCompound("TankEnderSap");
            this.tankEnderSap.readFromNBT(registries, tagCompound);
        }
        if(compound.contains("TankFuelium", Tag.TAG_COMPOUND))
        {
            CompoundTag tagCompound = compound.getCompound("TankFuelium");
            this.tankFuelium.readFromNBT(registries, tagCompound);
        }
        if(compound.contains("RemainingFuel", Tag.TAG_INT))
        {
            this.remainingFuel = compound.getInt("RemainingFuel");
        }
        if(compound.contains("FuelMaxProgress", Tag.TAG_INT))
        {
            this.fuelMaxProgress = compound.getInt("FuelMaxProgress");
        }
        if(compound.contains("ExtractionProgress", Tag.TAG_INT))
        {
            this.extractionProgress = compound.getInt("ExtractionProgress");
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries)
    {
        super.saveAdditional(compound, registries);

        ContainerHelper.saveAllItems(compound, this.inventory, registries);

        if(this.hasCustomName())
        {
            compound.putString("CustomName", this.customName);
        }

        this.writeTanks(registries, compound);

        compound.putInt("RemainingFuel", this.remainingFuel);
        compound.putInt("FuelMaxProgress", this.fuelMaxProgress);
        compound.putInt("ExtractionProgress", this.extractionProgress);
    }

    @Override
    public void writeTanks(HolderLookup.Provider registries, CompoundTag compound)
    {
        CompoundTag tagTankBlaze = new CompoundTag();
        this.tankBlaze.writeToNBT(registries, tagTankBlaze);
        compound.put("TankBlaze", tagTankBlaze);

        CompoundTag tagTankEnderSap = new CompoundTag();
        this.tankEnderSap.writeToNBT(registries, tagTankEnderSap);
        compound.put("TankEnderSap", tagTankEnderSap);

        CompoundTag tagTankFuelium = new CompoundTag();
        this.tankFuelium.writeToNBT(registries, tagTankFuelium);
        compound.put("TankFuelium", tagTankFuelium);
    }

    @Override
    public boolean areTanksEmpty()
    {
        return this.tankBlaze.isEmpty() && this.tankEnderSap.isEmpty() && this.tankFuelium.isEmpty();
    }

    private String getName()
    {
        return this.hasCustomName() ? this.customName : "container.fluid_mixer";
    }

    public boolean hasCustomName()
    {
        return this.customName != null && !this.customName.isEmpty();
    }

    @Override
    public Component getDisplayName()
    {
        return this.hasCustomName() ? Component.literal(this.getName()) : Component.translatable(this.getName());
    }

    @Nullable
    public FluidStack getBlazeFluidStack()
    {
        return this.tankBlaze.getFluid();
    }

    @Nullable
    public FluidStack getEnderSapFluidStack()
    {
        return this.tankEnderSap.getFluid();
    }

    @Nullable
    public FluidStack getFueliumFluidStack()
    {
        return this.tankFuelium.getFluid();
    }

    public int getExtractionProgress()
    {
        return this.fluidMixerData.get(0);
    }

    public int getRemainingFuel()
    {
        return this.fluidMixerData.get(1);
    }

    public int getFuelMaxProgress()
    {
        return this.fluidMixerData.get(2);
    }

    public int getBlazeLevel()
    {
        return this.fluidMixerData.get(3);
    }

    public int getEnderSapLevel()
    {
        return this.fluidMixerData.get(4);
    }

    public int getFueliumLevel()
    {
        return this.fluidMixerData.get(5);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity)
    {
        return new FluidMixerContainer(windowId, playerInventory, this);
    }

    public ContainerData getFluidMixerData()
    {
        return fluidMixerData;
    }

    public void updateFluid(FluidTank tank, int fluidHash)
    {
        Optional<Fluid> optional = BuiltInRegistries.FLUID.stream().filter(fluid -> BuiltInRegistries.FLUID.getKey(fluid).hashCode() == fluidHash).findFirst();
        optional.ifPresent(fluid -> tank.setFluid(new FluidStack(fluid, tank.getFluidAmount())));
    }

    public Optional<FluidMixerRecipe> getRecipe()
    {
        FluidMixerRecipe.FluidMixerInput input = new FluidMixerRecipe.FluidMixerInput(
            this.getItem(SLOT_INGREDIENT),
            this.tankEnderSap.getFluid().getFluid(),
            this.tankBlaze.getFluid().getFluid()
        );
        return this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.FLUID_MIXER.get(), input, this.level).map(net.minecraft.world.item.crafting.RecipeHolder::value);
    }

    private boolean isValidIngredient(ItemStack ingredient)
    {
        java.util.Collection<net.minecraft.world.item.crafting.RecipeHolder<FluidMixerRecipe>> recipes = this.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.FLUID_MIXER.get());
        return recipes.stream().map(net.minecraft.world.item.crafting.RecipeHolder::value).anyMatch(recipe -> InventoryUtil.areItemStacksEqualIgnoreCount(ingredient, recipe.getIngredient()));
    }

    private boolean isValidFluid(FluidStack stack)
    {
        java.util.Collection<net.minecraft.world.item.crafting.RecipeHolder<FluidMixerRecipe>> recipes = this.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.FLUID_MIXER.get());
        return recipes.stream().map(net.minecraft.world.item.crafting.RecipeHolder::value).anyMatch(recipe ->
        {
            for(FluidEntry entry : recipe.getInputs())
            {
                if(entry.getFluid() == stack.getFluid())
                {
                    return true;
                }
            }
            return false;
        });
    }

    public FluidTank getEnderSapTank()
    {
        return tankEnderSap;
    }

    public FluidTank getBlazeTank()
    {
        return tankBlaze;
    }

    public FluidTank getFueliumTank()
    {
        return tankFuelium;
    }

    private void setMixing(boolean state)
    {
        if(this.mixing != state)
        {
            this.mixing = state;
            this.level.setBlock(this.worldPosition, this.getBlockState().setValue(FluidMixerBlock.ENABLED, state), Block.UPDATE_ALL);
        }
    }
}









