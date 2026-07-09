/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package com.mrcrayfish.vehicle.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface IEngineType {
    public ResourceLocation getId();

    public int hashCode();

    default public Component getEngineName() {
        return Component.translatable((String)(this.getId().getNamespace() + ".engine_type." + this.getId().getPath() + ".name"));
    }
}

