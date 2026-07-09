/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.neoforged.neoforge.fluids.FluidType
 *  net.neoforged.neoforge.fluids.FluidType$Properties
 */
package com.mrcrayfish.vehicle.fluid;

import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;

public class SimpleFluidType
extends FluidType {
    private final ResourceLocation still;
    private final ResourceLocation flowing;

    public SimpleFluidType(FluidType.Properties properties, ResourceLocation still, ResourceLocation flowing) {
        super(properties);
        this.still = still;
        this.flowing = flowing;
    }

    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions(){

            public ResourceLocation getStillTexture() {
                return SimpleFluidType.this.still;
            }

            public ResourceLocation getFlowingTexture() {
                return SimpleFluidType.this.flowing;
            }
        });
    }
}

