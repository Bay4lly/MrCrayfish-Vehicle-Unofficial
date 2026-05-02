package com.mrcrayfish.vehicle.recipe;

import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public class RecipeRefillSprayCan extends CustomRecipe
{
    public RecipeRefillSprayCan(ResourceLocation id, CraftingBookCategory category)
    {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level worldIn)
    {
        ItemStack sprayCan = ItemStack.EMPTY;
        ItemStack emptySprayCan = ItemStack.EMPTY;

        for(int i = 0; i < inventory.getContainerSize(); i++)
        {
            ItemStack stack = inventory.getItem(i);
            if(!stack.isEmpty())
            {
                if(stack.getItem() instanceof SprayCanItem)
                {
                    if(((SprayCanItem) stack.getItem()).hasColor(stack))
                    {
                        if(!sprayCan.isEmpty())
                        {
                            return false;
                        }
                        sprayCan = stack.copy();
                    }
                    else
                    {
                        if(!emptySprayCan.isEmpty())
                        {
                            return false;
                        }
                        emptySprayCan = stack.copy();
                    }
                }
            }
        }
        return !sprayCan.isEmpty() && !emptySprayCan.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer inventory, RegistryAccess registries)
    {
        ItemStack sprayCan = ItemStack.EMPTY;
        ItemStack emptySprayCan = ItemStack.EMPTY;

        for(int i = 0; i < inventory.getContainerSize(); i++)
        {
            ItemStack stack = inventory.getItem(i);
            if(!stack.isEmpty())
            {
                if(stack.getItem() instanceof SprayCanItem)
                {
                    if(((SprayCanItem) stack.getItem()).hasColor(stack))
                    {
                        if(!sprayCan.isEmpty())
                        {
                            return ItemStack.EMPTY;
                        }
                        sprayCan = stack.copy();
                    }
                    else
                    {
                        if(!emptySprayCan.isEmpty())
                        {
                            return ItemStack.EMPTY;
                        }
                        emptySprayCan = stack.copy();
                    }
                }
            }
        }

        if(!sprayCan.isEmpty() && !emptySprayCan.isEmpty())
        {
            ItemStack copy = sprayCan.copy();
            ((SprayCanItem) copy.getItem()).refill(copy);
            return copy;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.REFILL_SPRAY_CAN.get();
    }
}
