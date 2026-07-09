/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.mrcrayfish.vehicle.network.message;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IMessage<T extends CustomPacketPayload>
extends CustomPacketPayload {
    public void encode(T var1, RegistryFriendlyByteBuf var2);

    public T decode(RegistryFriendlyByteBuf var1);

    public void handle(T var1, IPayloadContext var2);

    public static void enqueueTask(IPayloadContext context, Runnable runnable) {
        context.enqueueWork(runnable);
    }
}

