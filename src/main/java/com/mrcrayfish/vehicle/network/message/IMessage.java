package com.mrcrayfish.vehicle.network.message;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Author: MrCrayfish
 * <p>
 * Ported to the NeoForge 1.21.1 custom payload network system. Each message is a
 * {@link CustomPacketPayload} that supplies its own {@code TYPE} and {@code STREAM_CODEC}.
 */
public interface IMessage<T extends CustomPacketPayload> extends CustomPacketPayload
{
    void encode(T message, RegistryFriendlyByteBuf buffer);

    T decode(RegistryFriendlyByteBuf buffer);

    void handle(T message, IPayloadContext context);

    static void enqueueTask(IPayloadContext context, Runnable runnable)
    {
        context.enqueueWork(runnable);
    }
}
