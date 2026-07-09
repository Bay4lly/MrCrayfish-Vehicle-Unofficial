/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.mrcrayfish.vehicle.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidEntry {
    public static final Codec<FluidEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidEntry::getFluid), Codec.INT.fieldOf("amount").forGetter(FluidEntry::getAmount)).apply(instance, FluidEntry::new));
    public static final StreamCodec<FriendlyByteBuf, FluidEntry> STREAM_CODEC = StreamCodec.of((buf, entry) -> entry.write((FriendlyByteBuf)buf), FluidEntry::read);
    private Fluid fluid;
    private int amount;

    public FluidEntry(Fluid fluid, int amount) {
        this.fluid = fluid;
        this.amount = amount;
    }

    public Fluid getFluid() {
        return this.fluid;
    }

    public int getAmount() {
        return this.amount;
    }

    public FluidStack createStack() {
        return new FluidStack(this.fluid, this.amount);
    }

    public static FluidEntry fromJson(JsonObject object) {
        if (!object.has("fluid") || !object.has("amount")) {
            throw new JsonSyntaxException("Invalid fluid entry, missing fluid and amount");
        }
        ResourceLocation fluidId = ResourceLocation.parse((String)GsonHelper.getAsString((JsonObject)object, (String)"fluid"));
        Fluid fluid = (Fluid)BuiltInRegistries.FLUID.get(fluidId);
        if (fluid == null) {
            throw new JsonSyntaxException("Invalid fluid entry, unknown fluid: " + fluidId.toString());
        }
        int amount = GsonHelper.getAsInt((JsonObject)object, (String)"amount");
        if (amount < 1) {
            throw new JsonSyntaxException("Invalid fluid entry, amount must be more than zero");
        }
        return new FluidEntry(fluid, amount);
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("fluid", BuiltInRegistries.FLUID.getKey(this.fluid).toString());
        object.addProperty("amount", (Number)this.amount);
        return object;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(BuiltInRegistries.FLUID.getKey(this.fluid).toString(), 256);
        buffer.writeInt(this.amount);
    }

    public static FluidEntry read(FriendlyByteBuf buffer) {
        Fluid fluid = (Fluid)BuiltInRegistries.FLUID.get(ResourceLocation.parse((String)buffer.readUtf(256)));
        int amount = buffer.readInt();
        return new FluidEntry(fluid, amount);
    }

    public static FluidEntry of(Fluid fluid, int amount) {
        return new FluidEntry(fluid, amount);
    }
}

