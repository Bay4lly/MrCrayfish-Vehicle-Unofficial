/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 */
package com.mrcrayfish.vehicle.item;

import net.minecraft.world.item.Item;

public class PartItem
extends Item {
    private boolean colored;

    public PartItem(Item.Properties properties) {
        super(properties);
    }

    public PartItem setColored() {
        this.colored = true;
        return this;
    }

    public boolean isColored() {
        return this.colored;
    }
}

