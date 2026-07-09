package com.mrcrayfish.vehicle.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapEncoder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class WorkstationIngredient {

    /**
     * Custom codec that handles the legacy format used by the recipe JSON files:
     *   { "item": "namespace:id", "count": N }
     *   { "tag":  "forge:ingots/iron", "count": N }
     *
     * Forge tags (forge:...) are remapped to the NeoForge c: convention.
     */
    public static final Codec<WorkstationIngredient> CODEC = buildCodec();

    private static Codec<WorkstationIngredient> buildCodec() {
        Decoder<WorkstationIngredient> decoder = new Decoder<WorkstationIngredient>() {
            @Override
            public <T> DataResult<com.mojang.datafixers.util.Pair<WorkstationIngredient, T>> decode(DynamicOps<T> ops, T input) {
                try {
                    JsonElement element = ops.convertTo(JsonOps.INSTANCE, input);
                    if (!element.isJsonObject()) {
                        return DataResult.error(() -> "WorkstationIngredient must be a JSON object");
                    }
                    JsonObject obj = element.getAsJsonObject();
                    int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
                    Ingredient ingredient;
                    if (obj.has("tag")) {
                        String tagStr = obj.get("tag").getAsString();
                        if (tagStr.startsWith("forge:")) {
                            tagStr = "c:" + tagStr.substring("forge:".length());
                        }
                        TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.parse(tagStr));
                        ingredient = Ingredient.of(tag);
                    } else if (obj.has("item")) {
                        String itemStr = obj.get("item").getAsString();
                        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemStr));
                        if (item == null) {
                            return DataResult.error(() -> "Unknown item: " + itemStr);
                        }
                        ingredient = Ingredient.of(item);
                    } else if (obj.has("ingredient")) {
                        DataResult<Ingredient> ingResult = Ingredient.CODEC.parse(JsonOps.INSTANCE, obj.get("ingredient"));
                        if (ingResult.isError()) {
                            return DataResult.error(() -> "Bad ingredient field: " + ingResult.error().get().message());
                        }
                        ingredient = ingResult.getOrThrow();
                    } else {
                        return DataResult.error(() -> "WorkstationIngredient must have 'item', 'tag', or 'ingredient'");
                    }
                    final Ingredient fi = ingredient;
                    final int fc = count;
                    return DataResult.success(com.mojang.datafixers.util.Pair.of(new WorkstationIngredient(fi, fc), input));
                } catch (Exception e) {
                    return DataResult.error(() -> "Failed to parse WorkstationIngredient: " + e.getMessage());
                }
            }
        };

        Encoder<WorkstationIngredient> encoder = new Encoder<WorkstationIngredient>() {
            @Override
            public <T> DataResult<T> encode(WorkstationIngredient input, DynamicOps<T> ops, T prefix) {
                JsonObject obj = new JsonObject();
                obj.add("ingredient", Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, input.getIngredient()).getOrThrow());
                obj.addProperty("count", input.getCount());
                T encoded = JsonOps.INSTANCE.convertTo(ops, obj);
                return DataResult.success(encoded);
            }
        };

        return Codec.of(encoder, decoder);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, WorkstationIngredient> STREAM_CODEC = StreamCodec.of(
        (buf, ingredient) -> {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient.getIngredient());
            buf.writeVarInt(ingredient.getCount());
        },
        buf -> {
            Ingredient ingredient = (Ingredient) Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            int count = buf.readVarInt();
            return new WorkstationIngredient(ingredient, count);
        }
    );

    private final Ingredient ingredient;
    private final int count;

    public WorkstationIngredient(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public int getCount() {
        return this.count;
    }

    public boolean test(ItemStack stack) {
        return this.ingredient.test(stack);
    }

    public ItemStack[] getItems() {
        return this.ingredient.getItems();
    }

    public boolean isEmpty() {
        return this.ingredient.isEmpty();
    }

    public static WorkstationIngredient fromJson(JsonObject object) {
        Ingredient ingredient = (Ingredient) Ingredient.CODEC.parse(JsonOps.INSTANCE, object.get("ingredient")).getOrThrow();
        int count = GsonHelper.getAsInt(object, "count", 1);
        return new WorkstationIngredient(ingredient, count);
    }

    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        object.add("ingredient", Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, this.ingredient).getOrThrow());
        object.addProperty("count", (Number) this.count);
        return object;
    }

    public static WorkstationIngredient of(ItemLike provider, int count) {
        return new WorkstationIngredient(Ingredient.of(new ItemLike[]{provider}), count);
    }

    public static WorkstationIngredient of(ItemStack stack, int count) {
        return new WorkstationIngredient(Ingredient.of(new ItemStack[]{stack}), count);
    }

    public static WorkstationIngredient of(TagKey<Item> tag, int count) {
        return new WorkstationIngredient(Ingredient.of(tag), count);
    }

    public static WorkstationIngredient of(ResourceLocation id, int count) {
        return new WorkstationIngredient(Ingredient.of(new ItemLike[]{(ItemLike) BuiltInRegistries.ITEM.get(id)}), count);
    }
}
