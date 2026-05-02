package com.mrcrayfish.vehicle.crafting;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import com.mrcrayfish.vehicle.init.ModRecipeTypes;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public class WorkstationRecipe implements Recipe<WorkstationTileEntity>
{
    private ResourceLocation id;
    private EntityType<?> vehicle;
    private ImmutableList<WorkstationIngredient> materials;

    public WorkstationRecipe(ResourceLocation id, EntityType<?> vehicle, ImmutableList<WorkstationIngredient> materials)
    {
        this.id = id;
        this.vehicle = vehicle;
        this.materials = materials;
    }

    public EntityType<?> getVehicle()
    {
        return this.vehicle;
    }

    public ImmutableList<WorkstationIngredient> getMaterials()
    {
        return this.materials;
    }

    @Override
    public boolean matches(WorkstationTileEntity inv, Level worldIn)
    {
        return false;
    }

    @Override
    public ItemStack assemble(WorkstationTileEntity inv, RegistryAccess registries)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.WORKSTATION.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return ModRecipeTypes.WORKSTATION.get();
    }

    public boolean hasMaterials(Player player)
    {
        for(WorkstationIngredient ingredient : this.getMaterials())
        {
            if(!InventoryUtil.hasWorkstationIngredient(player, ingredient))
            {
                return false;
            }
        }
        return true;
    }

    public void consumeMaterials(Player player)
    {
        for(WorkstationIngredient ingredient : this.getMaterials())
        {
            InventoryUtil.removeWorkstationIngredient(player, ingredient);
        }
    }
}
