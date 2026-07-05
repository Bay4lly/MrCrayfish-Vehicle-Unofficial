package com.mrcrayfish.vehicle.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Author: MrCrayfish
 */
public class FluidEntry
{
    public static final com.mojang.serialization.Codec<FluidEntry> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(instance -> instance.group(
        net.minecraft.core.registries.BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidEntry::getFluid),
        com.mojang.serialization.Codec.INT.fieldOf("amount").forGetter(FluidEntry::getAmount)
    ).apply(instance, FluidEntry::new));

    public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.FriendlyByteBuf, FluidEntry> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.of(
        (buf, entry) -> entry.write(buf),
        FluidEntry::read
    );

    private Fluid fluid;
    private int amount;

    public FluidEntry(Fluid fluid, int amount)
    {
        this.fluid = fluid;
        this.amount = amount;
    }

    public Fluid getFluid()
    {
        return this.fluid;
    }

    public int getAmount()
    {
        return this.amount;
    }

    public FluidStack createStack()
    {
        return new FluidStack(this.fluid, this.amount);
    }

    public static FluidEntry fromJson(JsonObject object)
    {
        if(!object.has("fluid") || !object.has("amount"))
        {
            throw new com.google.gson.JsonSyntaxException("Invalid fluid entry, missing fluid and amount");
        }
        ResourceLocation fluidId = ResourceLocation.parse(GsonHelper.getAsString(object, "fluid"));
        Fluid fluid = BuiltInRegistries.FLUID.get(fluidId);
        if(fluid == null)
        {
            throw new com.google.gson.JsonSyntaxException("Invalid fluid entry, unknown fluid: " + fluidId.toString());
        }
        int amount = GsonHelper.getAsInt(object, "amount");
        if(amount < 1)
        {
            throw new com.google.gson.JsonSyntaxException("Invalid fluid entry, amount must be more than zero");
        }
        return new FluidEntry(fluid, amount);
    }

    public JsonObject toJson()
    {
        JsonObject object = new JsonObject();
        object.addProperty("fluid", BuiltInRegistries.FLUID.getKey(this.fluid).toString()); // FIXME
        object.addProperty("amount", this.amount);
        return object;
    }

    public void write(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(BuiltInRegistries.FLUID.getKey(this.fluid).toString(), 256); // FIXME
        buffer.writeInt(this.amount);
    }

    public static FluidEntry read(FriendlyByteBuf buffer)
    {
        Fluid fluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(buffer.readUtf(256)));
        int amount = buffer.readInt();
        return new FluidEntry(fluid, amount);
    }

    public static FluidEntry of(Fluid fluid, int amount)
    {
        return new FluidEntry(fluid, amount);
    }
}
