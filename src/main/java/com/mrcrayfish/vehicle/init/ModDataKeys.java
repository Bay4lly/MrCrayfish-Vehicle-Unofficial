/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mrcrayfish.framework.api.FrameworkAPI
 *  com.mrcrayfish.framework.api.sync.DataSerializer
 *  com.mrcrayfish.framework.api.sync.Serializers
 *  com.mrcrayfish.framework.api.sync.SyncedClassKey
 *  com.mrcrayfish.framework.api.sync.SyncedDataKey
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 */
package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.sync.DataSerializer;
import com.mrcrayfish.framework.api.sync.SyncedClassKey;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import com.mrcrayfish.vehicle.common.data.Serializers;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ModDataKeys {
    public static final SyncedDataKey<Player, Integer> TRAILER = SyncedDataKey.builder((SyncedClassKey)SyncedClassKey.PLAYER, (DataSerializer)com.mrcrayfish.framework.api.sync.Serializers.INTEGER).id(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"trailer")).defaultValueSupplier(() -> -1).resetOnDeath().build();
    public static final SyncedDataKey<Player, Optional<BlockPos>> GAS_PUMP = SyncedDataKey.builder((SyncedClassKey)SyncedClassKey.PLAYER, Serializers.OPTIONAL_BLOCK_POS).id(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"gas_pump")).defaultValueSupplier(Optional::empty).resetOnDeath().build();
    private static boolean registered = false;

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        FrameworkAPI.registerSyncedDataKey(TRAILER);
        FrameworkAPI.registerSyncedDataKey(GAS_PUMP);
    }
}

