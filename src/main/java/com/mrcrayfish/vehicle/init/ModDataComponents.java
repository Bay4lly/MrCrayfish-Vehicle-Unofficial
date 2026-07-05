package com.mrcrayfish.vehicle.init;

import com.mojang.serialization.Codec;
import com.mrcrayfish.vehicle.Reference;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;
import java.util.function.UnaryOperator;

/**
 * Author: bay4lly (1.21.1 NeoForge port)
 * <p>
 * Replaces the old item NBT keys with typed {@link DataComponentType data components}.
 * Generic / structural item NBT that does not warrant a dedicated component is routed
 * through the vanilla {@code minecraft:custom_data} component instead (see CommonUtils).
 */
public class ModDataComponents
{
    public static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(Reference.MOD_ID);

    // Stores the paint/dye color of dyeable items (wheels, spray cans, ...).
    public static final net.neoforged.neoforge.registries.DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COLOR = register("color", builder -> builder
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.VAR_INT));

    // Remaining spray can charges.
    public static final net.neoforged.neoforge.registries.DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REMAINING_SPRAYS = register("remaining_sprays", builder -> builder
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.VAR_INT));

    // Fluid content for jerry cans.
    public static final net.neoforged.neoforge.registries.DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID_CONTENT = register("fluid_content", builder -> builder
            .persistent(SimpleFluidContent.CODEC)
            .networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    // A key's bound vehicle UUID.
    public static final net.neoforged.neoforge.registries.DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> VEHICLE_UUID = register("vehicle_uuid", builder -> builder
            .persistent(UUIDUtil.CODEC)
            .networkSynchronized(UUIDUtil.STREAM_CODEC));

    private static <T> net.neoforged.neoforge.registries.DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder)
    {
        return REGISTER.registerComponentType(name, builder);
    }
}
