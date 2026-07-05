package com.mrcrayfish.vehicle.crafting;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import com.mrcrayfish.vehicle.init.ModRecipeTypes;
import com.mrcrayfish.vehicle.util.InventoryUtil;
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
public class WorkstationRecipe implements Recipe<net.minecraft.world.item.crafting.RecipeInput>
{
    public static final com.mojang.serialization.MapCodec<WorkstationRecipe> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
        net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("vehicle").forGetter(WorkstationRecipe::getVehicle),
        com.mrcrayfish.vehicle.crafting.WorkstationIngredient.CODEC.listOf().fieldOf("materials").forGetter(WorkstationRecipe::getMaterials)
    ).apply(inst, WorkstationRecipe::new));

    public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, WorkstationRecipe> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.of(
        (buf, recipe) -> {
            buf.writeResourceLocation(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(recipe.getVehicle()));
            com.mrcrayfish.vehicle.crafting.WorkstationIngredient.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list()).encode(buf, recipe.getMaterials());
        },
        buf -> {
            EntityType<?> vehicle = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(buf.readResourceLocation());
            java.util.List<WorkstationIngredient> materials = com.mrcrayfish.vehicle.crafting.WorkstationIngredient.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list()).decode(buf);
            return new WorkstationRecipe(vehicle, materials);
        }
    );

    private EntityType<?> vehicle;
    private ImmutableList<WorkstationIngredient> materials;

    public WorkstationRecipe(EntityType<?> vehicle, java.util.List<WorkstationIngredient> materials)
    {
        this.vehicle = vehicle;
        this.materials = ImmutableList.copyOf(materials);
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
    public boolean matches(net.minecraft.world.item.crafting.RecipeInput inv, Level worldIn)
    {
        return false;
    }

    @Override
    public ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput inv, net.minecraft.core.HolderLookup.Provider registries)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registries)
    {
        return ItemStack.EMPTY;
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
