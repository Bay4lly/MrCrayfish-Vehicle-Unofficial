package com.mrcrayfish.vehicle.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/**
 * Author: MrCrayfish
 */
public class WorkstationIngredient
{
    public static final com.mojang.serialization.Codec<WorkstationIngredient> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(instance -> instance.group(
        net.minecraft.world.item.crafting.Ingredient.CODEC.fieldOf("ingredient").forGetter(WorkstationIngredient::getIngredient),
        com.mojang.serialization.Codec.INT.fieldOf("count").forGetter(WorkstationIngredient::getCount)
    ).apply(instance, WorkstationIngredient::new));

    public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, WorkstationIngredient> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.of(
        (buf, ingredient) -> {
            net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient.getIngredient());
            buf.writeVarInt(ingredient.getCount());
        },
        buf -> {
            net.minecraft.world.item.crafting.Ingredient ingredient = net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            int count = buf.readVarInt();
            return new WorkstationIngredient(ingredient, count);
        }
    );

    private final Ingredient ingredient;
    private final int count;

    public WorkstationIngredient(Ingredient ingredient, int count)
    {
        this.ingredient = ingredient;
        this.count = count;
    }

    public Ingredient getIngredient()
    {
        return this.ingredient;
    }

    public int getCount()
    {
        return this.count;
    }

    public boolean test(ItemStack stack)
    {
        return this.ingredient.test(stack);
    }

    public ItemStack[] getItems()
    {
        return this.ingredient.getItems();
    }

    public boolean isEmpty()
    {
        return this.ingredient.isEmpty();
    }

    public static WorkstationIngredient fromJson(JsonObject object)
    {
        Ingredient ingredient = net.minecraft.world.item.crafting.Ingredient.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, object.get("ingredient")).getOrThrow();
        int count = GsonHelper.getAsInt(object, "count", 1);
        return new WorkstationIngredient(ingredient, count);
    }

    public JsonElement toJson()
    {
        JsonObject object = new JsonObject();
        object.add("ingredient", net.minecraft.world.item.crafting.Ingredient.CODEC.encodeStart(com.mojang.serialization.JsonOps.INSTANCE, this.ingredient).getOrThrow());
        object.addProperty("count", this.count);
        return object;
    }

    public static WorkstationIngredient of(ItemLike provider, int count)
    {
        return new WorkstationIngredient(Ingredient.of(provider), count);
    }

    public static WorkstationIngredient of(ItemStack stack, int count)
    {
        return new WorkstationIngredient(Ingredient.of(stack), count);
    }

    public static WorkstationIngredient of(TagKey<Item> tag, int count)
    {
        return new WorkstationIngredient(Ingredient.of(tag), count);
    }

    public static WorkstationIngredient of(ResourceLocation id, int count)
    {
        return new WorkstationIngredient(Ingredient.of(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(id)), count);
    }
}
