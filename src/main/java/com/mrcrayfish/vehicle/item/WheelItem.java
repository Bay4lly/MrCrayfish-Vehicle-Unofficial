/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item$Properties
 */
package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.entity.IWheelType;
import com.mrcrayfish.vehicle.item.IDyeable;
import com.mrcrayfish.vehicle.item.PartItem;
import net.minecraft.world.item.Item;

public class WheelItem
extends PartItem
implements IDyeable {
    private IWheelType wheelType;

    public WheelItem(IWheelType wheelType, Item.Properties properties) {
        super(properties);
        this.wheelType = wheelType;
    }

    public IWheelType getWheelType() {
        return this.wheelType;
    }
}

