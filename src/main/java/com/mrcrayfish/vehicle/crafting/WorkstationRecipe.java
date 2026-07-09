/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.Level
 */
package com.mrcrayfish.vehicle.crafting;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.vehicle.crafting.WorkstationIngredient;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import com.mrcrayfish.vehicle.init.ModRecipeTypes;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class WorkstationRecipe
implements Recipe<RecipeInput> {
    public static final MapCodec<WorkstationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("vehicle").forGetter(WorkstationRecipe::getVehicle), WorkstationIngredient.CODEC.listOf().fieldOf("materials").forGetter(WorkstationRecipe::getMaterials)).apply(inst, WorkstationRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, WorkstationRecipe> STREAM_CODEC = StreamCodec.of((buf, recipe) -> {
        buf.writeResourceLocation(BuiltInRegistries.ENTITY_TYPE.getKey(recipe.getVehicle()));
        WorkstationIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.getMaterials());
    }, buf -> {
        EntityType vehicle = (EntityType)BuiltInRegistries.ENTITY_TYPE.get(buf.readResourceLocation());
        List materials = (List)WorkstationIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        return new WorkstationRecipe(vehicle, materials);
    });
    private EntityType<?> vehicle;
    private ImmutableList<WorkstationIngredient> materials;

    public WorkstationRecipe(EntityType<?> vehicle, List<WorkstationIngredient> materials) {
        this.vehicle = vehicle;
        this.materials = ImmutableList.copyOf(materials);
    }

    public EntityType<?> getVehicle() {
        return this.vehicle;
    }

    public ImmutableList<WorkstationIngredient> getMaterials() {
        return this.materials;
    }

    public boolean matches(RecipeInput inv, Level worldIn) {
        return false;
    }

    public ItemStack assemble(RecipeInput inv, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    public RecipeSerializer<?> getSerializer() {
        return (RecipeSerializer)ModRecipeSerializers.WORKSTATION.get();
    }

    public RecipeType<?> getType() {
        return (RecipeType)ModRecipeTypes.WORKSTATION.get();
    }

    public boolean hasMaterials(Player player) {
        for (WorkstationIngredient ingredient : this.getMaterials()) {
            if (InventoryUtil.hasWorkstationIngredient(player, ingredient)) continue;
            return false;
        }
        return true;
    }

    public void consumeMaterials(Player player) {
        for (WorkstationIngredient ingredient : this.getMaterials()) {
            InventoryUtil.removeWorkstationIngredient(player, ingredient);
        }
    }
}

