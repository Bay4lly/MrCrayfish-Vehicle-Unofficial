package com.mrcrayfish.vehicle.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class SimpleFluidType extends FluidType
{
    private final ResourceLocation still, flowing;

    public SimpleFluidType(Properties properties, ResourceLocation still, ResourceLocation flowing) {
        super(properties);
        this.still = still;
        this.flowing = flowing;
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return still;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowing;
            }
        });
    }
}
