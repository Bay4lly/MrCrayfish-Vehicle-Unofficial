/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.entity.IEngineType;
import net.minecraft.resources.ResourceLocation;

public enum EngineType implements IEngineType
{
    NONE(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"none")),
    SMALL_MOTOR(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"small_motor")),
    LARGE_MOTOR(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"large_motor")),
    ELECTRIC_MOTOR(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"electric_motor"));

    private final ResourceLocation id;

    private EngineType(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }
}

