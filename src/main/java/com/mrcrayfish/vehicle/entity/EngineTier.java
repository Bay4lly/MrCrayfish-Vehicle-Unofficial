/*
 * Decompiled with CFR 0.152.
 */
package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.entity.IEngineTier;

public enum EngineTier implements IEngineTier
{
    IRON(1.0f, 0.0f),
    GOLD(1.1f, 2.0f),
    DIAMOND(1.2f, 5.0f),
    NETHERITE(1.4f, 5.0f);

    private final float accelerationMultiplier;
    private final float additionalMaxSpeed;

    private EngineTier(float accelerationMultiplier, float additionalMaxSpeed) {
        this.accelerationMultiplier = accelerationMultiplier;
        this.additionalMaxSpeed = additionalMaxSpeed;
    }

    @Override
    public float getAccelerationMultiplier() {
        return this.accelerationMultiplier;
    }

    @Override
    public float getAdditionalMaxSpeed() {
        return this.additionalMaxSpeed;
    }
}

