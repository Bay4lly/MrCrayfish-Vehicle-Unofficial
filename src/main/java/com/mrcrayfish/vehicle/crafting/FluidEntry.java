package com.mrcrayfish.vehicle.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: MrCrayfish
 */
public class FluidEntry
{
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
        ResourceLocation fluidId = new ResourceLocation(GsonHelper.getAsString(object, "fluid"));
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
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
        object.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(this.fluid).toString()); // FIXME
        object.addProperty("amount", this.amount);
        return object;
    }

    public void write(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(ForgeRegistries.FLUIDS.getKey(this.fluid).toString(), 256); // FIXME
        buffer.writeInt(this.amount);
    }

    public static FluidEntry read(FriendlyByteBuf buffer)
    {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(buffer.readUtf(256)));
        int amount = buffer.readInt();
        return new FluidEntry(fluid, amount);
    }

    public static FluidEntry of(Fluid fluid, int amount)
    {
        return new FluidEntry(fluid, amount);
    }
}
