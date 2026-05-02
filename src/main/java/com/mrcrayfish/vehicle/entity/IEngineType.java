package com.mrcrayfish.vehicle.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public interface IEngineType
{
    ResourceLocation getId();

    int hashCode();

    default Component getEngineName()
    {
        return Component.translatable(this.getId().getNamespace() + ".engine_type." + this.getId().getPath() + ".name");
    }
}
