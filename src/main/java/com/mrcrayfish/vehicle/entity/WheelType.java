/*
 * Decompiled with CFR 0.152.
 */
package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.entity.IWheelType;

public enum WheelType implements IWheelType
{
    STANDARD(0.9f, 0.8f, 0.5f),
    SPORTS(1.0f, 0.75f, 0.5f),
    RACING(1.1f, 0.7f, 0.5f),
    OFF_ROAD(0.75f, 1.0f, 0.85f),
    SNOW(0.75f, 0.85f, 0.95f),
    ALL_TERRAIN(0.85f, 0.85f, 0.85f),
    PLASTIC(0.5f, 0.5f, 0.5f);

    private final float roadMultiplier;
    private final float dirtMultiplier;
    private final float snowMultiplier;

    private WheelType(float roadMultiplier, float dirtMultiplier, float snowMultiplier) {
        this.roadMultiplier = roadMultiplier;
        this.dirtMultiplier = dirtMultiplier;
        this.snowMultiplier = snowMultiplier;
    }

    @Override
    public float getRoadMultiplier() {
        return this.roadMultiplier;
    }

    @Override
    public float getDirtMultiplier() {
        return this.dirtMultiplier;
    }

    @Override
    public float getSnowMultiplier() {
        return this.snowMultiplier;
    }
}

